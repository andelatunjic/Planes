package org.foi.nwtis.atunjic.aplikacija_3.rest;

import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.atunjic.aplikacija_3.podaci.Grupa;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.KorisnikDAO;
import org.foi.nwtis.atunjic.aplikacija_3.podaci.ProvjereDAO;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;

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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("korisnici")
public class RestKorisnici {

	@Inject
	ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajSveKorisnike(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
		List<Korisnik> korisnici = new ArrayList<>();

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			korisnici = KorisnikDAO.dohvatiSveKorisnike(konfig);

			if (korisnici != null) {
				return Response.status(Response.Status.OK).entity(korisnici).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Nema korisnika.").build();
			}
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response dodajKorisnika(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka,
			String json) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");
		Gson gson = new Gson();

		if (korisnikAutenticiran(korisnik, lozinka, konfig)) {
			Korisnik korisnikNovi = gson.fromJson(json, Korisnik.class);

			if (KorisnikDAO.dodajKorisnika(korisnikNovi, konfig)) {
				return Response.status(Response.Status.OK).entity("OK").build();
			}
			return Response.status(Response.Status.BAD_REQUEST).entity("Nije dodan korisnik.").build();
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Krivi podaci.").build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}")
	public Response dajKorisnika(@PathParam("korisnik") String korisnikTrazeni,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			Korisnik trazeniKorisnik = KorisnikDAO.dohvatiKorisnika(korisnikTrazeni, konfig);
			if (trazeniKorisnik != null) {
				return Response.status(Response.Status.OK).entity(trazeniKorisnik).build();
			}
			return Response.status(Response.Status.NOT_FOUND).entity("Korisnik: " + korisnikTrazeni + " ne postoji.")
					.build();
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}/grupe")
	public Response dajGrupeKorisnika(@PathParam("korisnik") String korisnikTrazeni,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") int zeton) {
		KonfiguracijaBP konfig = (KonfiguracijaBP) context.getAttribute("Postavke");

		if (korisnikAutoriziran(korisnik, zeton, konfig).getStatus() == 200) {
			List<Grupa> grupeKorisnika = KorisnikDAO.dohvatiGrupeKorisnika(korisnikTrazeni, konfig);

			if (grupeKorisnika != null) {
				return Response.status(Response.Status.OK).entity(grupeKorisnika).build();
			}
			return Response.status(Response.Status.NOT_FOUND).entity("Korisnik ne pripada ni jednoj grupi.").build();
		}
		return korisnikAutoriziran(korisnik, zeton, konfig);
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

	private boolean korisnikAutenticiran(String korIme, String lozinka, KonfiguracijaBP konfig) {
		Korisnik korisnik = KorisnikDAO.dohvatiKorisnika(korIme, konfig);
		if (korisnik != null) {
			if (korisnik.getLozinka().compareTo(lozinka) == 0)
				return true;
		}
		return false;
	}
}
