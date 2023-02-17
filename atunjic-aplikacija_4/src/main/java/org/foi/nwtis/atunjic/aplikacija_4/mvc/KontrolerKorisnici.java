package org.foi.nwtis.atunjic.aplikacija_4.mvc;

import java.util.List;

import org.foi.nwtis.atunjic.aplikacija_4.podaci.Grupa;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Controller
@Path("kontrolerKorisnici")
@RequestScoped
public class KontrolerKorisnici {

	@Inject
	private Models model;

	@Context
	private ServletContext context;

	@GET
	@Path("pregledKorisnika")
	@View("pregledKorisnika.jsp")
	public void pregledKorisnika(@QueryParam("zeton") int zeton, @QueryParam("korisnik") String korisnik,
			@QueryParam("lozinka") String lozinka) {
		Klijent k = new Klijent();
		List<Korisnik> korisnici = k.dajSveKorisnike(korisnik, zeton);
		model.put("korisnici", korisnici);

		List<Grupa> grupeKorisnika = k.dajGrupeKorisnika(korisnik, zeton);

		KonfiguracijaBP konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		String uloga = konfig.dajPostavku("sustav.administratori");
		boolean provjera = false;
		for (Grupa g : grupeKorisnika) {
			if (g.getGrupa().compareTo(uloga) == 0) {
				provjera = true;
			}
		}
		model.put("provjera", provjera);
		model.put("korisnik", korisnik);
		model.put("lozinka", lozinka);
		model.put("zeton", zeton);
	}

	@GET
	@Path("obrisi")
	@View("obavijest.jsp")
	public void obrisi(@QueryParam("zeton") int zeton, @QueryParam("korisnik") String korisnik,
			@QueryParam("lozinka") int lozinka) {
		Klijent k = new Klijent();
		Response odgovorPosluzitelja = k.obrisiToken(zeton, korisnik, lozinka);
		String odgovor = odgovorPosluzitelja.readEntity(String.class);
		model.put("odgovor", odgovor);
	}

	@GET
	@Path("obrisiTokene")
	@View("obavijest.jsp")
	public void obrisiTokene(@QueryParam("korIme") String korIme, @QueryParam("korisnik") String korisnik,
			@QueryParam("lozinka") String lozinka) {
		Klijent k = new Klijent();
		Response odgovorPosluzitelja = k.obrisiTokene(korIme, korisnik, lozinka);
		String odgovor = odgovorPosluzitelja.readEntity(String.class);
		model.put("odgovor", odgovor);
	}
}
