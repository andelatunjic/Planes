package org.foi.nwtis.atunjic.aplikacija_3.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.List;

import org.foi.nwtis.atunjic.aplikacija_3.podaci.AerodromiPraceniDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.ProvjereDAO;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("serveri")
public class RestServeri {

	@Inject
	ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiStatus(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			String adresa = konfig.dajPostavku("aplikacija1.adresa");
			int port = Integer.parseInt(konfig.dajPostavku("aplikacija1.port"));

			String naredba = "STATUS";

			String odgovorPosluzitelja = posaljiKomandu(adresa, port, naredba);
			String p[] = odgovorPosluzitelja.split(" ");

			if (p[0].compareTo("OK") == 0) {
				return Response.status(Response.Status.OK).entity("adresa: " + adresa + ", port: " + port).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Krivi podaci").build();
			}
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{komanda}")
	public Response posaljiNaredbu(@PathParam("komanda") String komanda, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") int zeton) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			String adresa = konfig.dajPostavku("aplikacija1.adresa");
			int port = Integer.parseInt(konfig.dajPostavku("aplikacija1.port"));

			String odgovorPosluzitelja = posaljiKomandu(adresa, port, komanda);
			String p[] = odgovorPosluzitelja.split(" ");

			if (p[0].compareTo("OK") == 0) {
				return Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity(odgovorPosluzitelja).build();
			}
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/LOAD")
	public Response posaljiLoadNaredbu(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
		Gson gson = new Gson();

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			String adresa = konfig.dajPostavku("aplikacija1.adresa");
			int port = Integer.parseInt(konfig.dajPostavku("aplikacija1.port"));

			List<Aerodrom> aerodromi = AerodromiPraceniDAO.dohvatiAerodromeZaPratiti(konfig);

			String naredba = "LOAD " + gson.toJson(aerodromi);

			String odgovorPosluzitelja = posaljiKomandu(adresa, port, naredba);
			String p[] = odgovorPosluzitelja.split(" ");

			if (p[0].compareTo("OK") == 0 && aerodromi.size() == Integer.parseInt(p[1])) {
				return Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build();
			} else {
				return Response.status(Response.Status.CONFLICT).entity(odgovorPosluzitelja).build();
			}
		}
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
