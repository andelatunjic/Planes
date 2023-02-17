package org.foi.nwtis.atunjic.aplikacija_3.rest;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.foi.nwtis.atunjic.aplikacija_3.podaci.Grupa;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.KorisnikDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.ProvjereDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.Token;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("provjere")
public class RestProvjere {

	@Inject
	ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response autenticirajIKreirajToken(@HeaderParam("korisnik") String korIme,
			@HeaderParam("lozinka") String lozinka) {

		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutenticiran(korIme, lozinka, konfig) && spremiToken(korIme)) {
			Token token = ProvjereDAO.dohvatiZadnjiToken(konfig);
			if (token != null) {
				SimpleDateFormat dateformatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				long mili = token.getTrajanje();
				String datum = dateformatter.format(mili * 1000);
				return Response.status(Response.Status.OK).entity("zeton:" + token.getToken() + ", vrijeme:" + datum)
						.build();
			}
		}
		return Response.status(Response.Status.UNAUTHORIZED).entity("Neuspješna autentikacija.").build();
	}

	private boolean spremiToken(String korIme) {

		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
		long trenutnoVrijeme = System.currentTimeMillis() / 1000;

		// u minutama, trenutno 60 min - pretvaramo sve u sekunde
		long trajanjeZetona = Long.parseLong(konfig.dajPostavku("zeton.trajanje")) * 60;
		long vrijemeDoIstekaTokena = trenutnoVrijeme + trajanjeZetona;

		if (ProvjereDAO.spremiToken(vrijemeDoIstekaTokena, korIme, konfig)) {
			return true;
		}
		return false;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response provjeriToken(@PathParam("token") int token, @HeaderParam("korisnik") String korIme,
			@HeaderParam("lozinka") String lozinka) {
		Response odgovor = null;
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutenticiran(korIme, lozinka, konfig)) {
			String provjera = ProvjereDAO.provjeriValjanostTokena(token, korIme, konfig);

			if (provjera.compareTo("aktivan") == 0) {
				odgovor = Response.status(Response.Status.OK).entity("Token je aktivan.").build();
			} else if (provjera.compareTo("nijeOdKorisnika") == 0) {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Token nije od korisnika:" + korIme)
						.build();
			} else if (provjera.compareTo("istekloVrijeme") == 0) {
				odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token više nije aktivan.").build();
			} else {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
			}

			return odgovor;
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response obrisiToken(@PathParam("token") int token, @HeaderParam("korisnik") String korIme,
			@HeaderParam("lozinka") String lozinka) {
		Response odgovor = null;
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutenticiran(korIme, lozinka, konfig)) {
			String provjera = ProvjereDAO.provjeriValjanostTokena(token, korIme, konfig);

			if (provjera.compareTo("aktivan") == 0) {
				if (ProvjereDAO.obrisiToken(token, konfig)) {
					odgovor = Response.status(Response.Status.OK).entity("Token je bio aktivan te je sad deaktiviran.")
							.build();
				}

			} else if (provjera.compareTo("nijeOdKorisnika") == 0) {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Token nije od korisnika:" + korIme)
						.build();
			} else if (provjera.compareTo("istekloVrijeme") == 0) {
				odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token više nije aktivan.").build();
			} else {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
			}

			return odgovor;
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("korisnik/{korisnik}")
	public Response obrisiTokeneKorisnika(@PathParam("korisnik") String korisnik,
			@HeaderParam("korisnik") String korIme, @HeaderParam("lozinka") String lozinka) {
		Response odgovor = null;
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutenticiran(korIme, lozinka, konfig)) {
			String provjera = "";
			if (korisnik == korIme) {
				provjera = ProvjereDAO.obrisiTokeneKorisnika(korisnik, konfig);
			} else {
				if (provjeriOvlast(korIme, konfig)) {
					provjera = ProvjereDAO.obrisiTokeneKorisnika(korisnik, konfig);
				} else {
					provjera = "nemaOvlastenje";
				}
			}

			if (provjera.compareTo("obrisaniTokeni") == 0) {
				odgovor = Response.status(Response.Status.OK).entity("Aktivni tokeni korisnika su deaktivirani.")
						.build();
			} else if (provjera.compareTo("korisnikNemaAktivanZeton") == 0) {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity("Korisnik nema aktivne tokene").build();
			} else if (provjera.compareTo("nemaOvlastenje") == 0) {
				odgovor = Response.status(Response.Status.UNAUTHORIZED)
						.entity("Korisnik nema ovlaštenje za brisanje tuđih tokena.").build();
			} else {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
			}

			return odgovor;
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
	}

	private boolean provjeriOvlast(String korimeIzZahtjeva, KonfiguracijaBP konfig) {
		String uloga = konfig.dajPostavku("sustav.administratori");

		List<Grupa> grupeKorisnika = KorisnikDAO.dohvatiGrupeKorisnika(korimeIzZahtjeva, konfig);
		for (Grupa g : grupeKorisnika) {
			if (g.getGrupa().compareTo(uloga) == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean korisnikAutenticiran(String korIme, String lozinka, KonfiguracijaBP konfig) {
		Korisnik korisnik = KorisnikDAO.dohvatiKorisnika(korIme, konfig);
		if (korisnik != null) {
			if (korisnik.getLozinka().compareTo(lozinka) == 0)
				return true;
		}
		return false;
	}
}
