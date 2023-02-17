package org.foi.nwtis.atunjic.aplikacija_4.mvc;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Controller
@Path("kontrolerRegistracije")
@RequestScoped
public class KontrolerRegistracija {

	@Inject
	private Models model;

	@Context
	private ServletContext context;

	@FormParam("korisnik")
	String korisnik;

	@FormParam("ime")
	String ime;

	@FormParam("prezime")
	String prezime;

	@FormParam("lozinka")
	String lozinka;

	@FormParam("email")
	String email;

	@POST
	@Path("registracija")
	@View("registracija.jsp")
	public String registracija() {
		KonfiguracijaBP konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		String korime = konfig.dajPostavku("sustav.korisnik");
		String loz = konfig.dajPostavku("sustav.lozinka");

		Klijent k = new Klijent();
		Korisnik noviKorisnik = new Korisnik(korisnik, ime, prezime, lozinka, email);
		Response odgovorPosluzitelja = k.registriraj(noviKorisnik, korime, loz);
		if (odgovorPosluzitelja.getStatus() == 200) {
			return "redirect:kontrolerPutanje/prijava";
		}
		return "redirect:kontrolerPutanje/registracija";
	}

}
