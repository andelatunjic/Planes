package org.foi.nwtis.atunjic.aplikacija_3.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.foi.nwtis.atunjic.aplikacija_3.podaci.AerodromiDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.AerodromiDolasciDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.AerodromiPolasciDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.AerodromiPraceniDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.ProvjereDAO;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

import com.google.gson.Gson;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("aerodromi")
public class RestAerodromi {

	@Inject
	ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajSveAerodrome(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton,
			@QueryParam("preuzimanje") String preuzimanje) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
		List<Aerodrom> aerodromi;

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			if (preuzimanje == null) {
				aerodromi = AerodromiDAO.dohvatiAerodrome(konfig);
			} else {
				aerodromi = AerodromiPraceniDAO.dohvatiAerodromeZaPratiti(konfig);
			}

			if (aerodromi != null) {
				return Response.status(Response.Status.OK).entity(aerodromi).build();
			}
			return Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma.").build();
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response dodajAerodromZaPratiti(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton,
			String json) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
		Gson gson = new Gson();

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			Aerodrom noviAerodromZaPracenje = gson.fromJson(json, Aerodrom.class);

			if (AerodromiPraceniDAO.provjeraDuplikata(konfig, noviAerodromZaPracenje.getIcao()) == false) {
				if (AerodromiPraceniDAO.dodajAerodromZaPratiti(noviAerodromZaPracenje.getIcao(), konfig)) {
					return Response.status(Response.Status.OK).entity("OK").build();
				}
			}
			return Response.status(Response.Status.BAD_REQUEST).entity("Nije se zapisao aerodrom za praćenje u bazu.")
					.build();
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}")
	public Response dajAerodrom(@PathParam("icao") String icao, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") int zeton) {
		Response odgovor = null;
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			List<Aerodrom> aerodromi = AerodromiDAO.dohvatiAerodrome(konfig);

			for (Aerodrom a : aerodromi) {
				if (a.getIcao().compareTo(icao) == 0) {
					odgovor = Response.status(Response.Status.OK).entity(a).build();
					break;
				}
			}
			if (odgovor == null) {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma: " + icao).build();
			}
			return odgovor;
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/polasci")
	public Response dajPolaskeAerodoma(@PathParam("icao") String icao, @QueryParam("vrsta") String vrsta,
			@QueryParam("od") String vrijemeOd, @QueryParam("do") String vrijemeDo,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		Response odgovor = null;
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			List<AvionLeti> polasciAviona = AerodromiPolasciDAO.dajSvePolaske(konfig);
			List<AvionLeti> trazeniPolasci = new ArrayList<>();

			if (polasciAviona != null) {
				for (AvionLeti a : polasciAviona) {

					long datumPolaska = a.getFirstSeen();

					if (a.getEstDepartureAirport().compareTo(icao) == 0
							&& provjeriDatum(datumPolaska, vrsta, vrijemeOd, vrijemeDo) == true) {
						trazeniPolasci.add(a);
					}
				}

				if (!trazeniPolasci.isEmpty()) {
					odgovor = Response.status(Response.Status.OK).entity(trazeniPolasci).build();
				} else
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema polazaka.").build();

			} else
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Prazna tablica").build();

			return odgovor;
		} else
			return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/dolasci")
	public Response dajOdlaskeAerodoma(@PathParam("icao") String icao, @QueryParam("vrsta") String vrsta,
			@QueryParam("od") String vrijemeOd, @QueryParam("do") String vrijemeDo,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		Response odgovor = null;
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			List<AvionLeti> odlasciAviona = AerodromiDolasciDAO.dajSveDolaske(konfig);
			List<AvionLeti> trazeniDolasci = new ArrayList<>();

			if (odlasciAviona != null) {
				for (AvionLeti a : odlasciAviona) {

					long datumDolaska = a.getFirstSeen();
					if (a.getEstDepartureAirport().compareTo(icao) == 0) {
						if (provjeriDatum(datumDolaska, vrsta, vrijemeOd, vrijemeDo) == true) {
							trazeniDolasci.add(a);
						}
					}
				}

				if (trazeniDolasci.isEmpty() == false) {
					odgovor = Response.status(Response.Status.OK).entity(trazeniDolasci).build();
				} else
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema polazaka.").build();

			} else
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Prazna tablica").build();

			return odgovor;
		} else
			return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	private boolean provjeriDatum(long datum, String vrsta, String vrijemeOd, String vrijemeDo) {
		boolean provjera = false;
		if (vrsta.compareTo("1") == 0) {
			long vrijemeOdSekunde = Long.parseLong(vrijemeOd);
			long vrijemeDoSekunde = Long.parseLong(vrijemeDo);
			if (datum >= vrijemeOdSekunde && datum <= vrijemeDoSekunde) {
				provjera = true;
			}
		}
		if (vrsta.compareTo("0") == 0) {
			LocalDate datumOd = vratiDatum(vrijemeOd);
			LocalDate datumDo = vratiDatum(vrijemeDo);

			long vrijemeOdSekunde = datumOd.toEpochSecond(LocalTime.MAX, ZoneOffset.of("+02:00"));
			long vrijemeDoSekunde = datumDo.toEpochSecond(LocalTime.MAX, ZoneOffset.of("+02:00"));

			if (datum >= vrijemeOdSekunde && datum <= vrijemeDoSekunde) {
				provjera = true;
			}
		}
		return provjera;
	}

	private LocalDate vratiDatum(String vrijemeOd) {

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		LocalDate datum = LocalDate.from(timeFormatter.parse(vrijemeOd));

		return datum;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao1}/{icao2}")
	public Response dajUdaljenost(@PathParam("icao1") String icao1, @PathParam("icao2") String icao2,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		Response odgovor = null;

		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			String adresa = konfig.dajPostavku("aplikacija1.adresa");
			int port = Integer.parseInt(konfig.dajPostavku("aplikacija1.port"));

			String naredba = "DISTANCE " + icao1 + " " + icao2;

			String odgovorPosluzitelja = posaljiKomandu(adresa, port, naredba);
			String p[] = odgovorPosluzitelja.split(" ");

			if (p[0].compareTo("OK") == 0) {
				odgovor = Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build();
			} else
				odgovor = Response.status(Response.Status.NOT_FOUND).entity(odgovorPosluzitelja).build();

			return odgovor;
		} else
			return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	private String posaljiKomandu(String adresa, int port, String naredba) {
		try (Socket veza = new Socket(adresa, port);
				InputStreamReader isr = new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8"));
				OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), Charset.forName("UTF-8"));) {

			osw.write(naredba);
			osw.flush();
			veza.shutdownOutput();
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			veza.shutdownInput();
			veza.close();
			return tekst.toString();
		} catch (SocketException e) {
			ispis(e.getMessage());
		} catch (IOException ex) {
			ispis(ex.getMessage());
		}
		return null;
	}

	private void ispis(String message) {
		System.out.println("KORISNIK: " + message);
	}

	private Response korisnikAutoriziran(String korIme, int token, KonfiguracijaBP konfig) {

		String provjera = ProvjereDAO.provjeriValjanostTokena(token, korIme, konfig);

		if (provjera.compareTo("aktivan") == 0) {
			return Response.status(Response.Status.OK).entity("Token je aktivan.").build();
		} else if (provjera.compareTo("nijeOdKorisnika") == 0) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Token nije od korisnika:" + korIme).build();
		} else if (provjera.compareTo("istekloVrijeme") == 0) {
			return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token više nije aktivan.").build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
		}
	}
}
