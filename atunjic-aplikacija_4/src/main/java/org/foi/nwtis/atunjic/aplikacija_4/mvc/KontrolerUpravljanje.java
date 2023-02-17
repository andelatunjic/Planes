package org.foi.nwtis.atunjic.aplikacija_4.mvc;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

@Controller
@Path("kontrolerUpravljanje")
@RequestScoped
public class KontrolerUpravljanje {

	@Inject
	private Models model;

	@Context
	private ServletContext context;

	@GET
	@Path("upravljanje")
	@View("upravljanje.jsp")
	public void upravljanje(@QueryParam("zeton") int zeton, @QueryParam("korisnik") String korisnik) {
		KonfiguracijaBP konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		String adresa = konfig.dajPostavku("aplikacija1.adresa");
		int port = Integer.parseInt(konfig.dajPostavku("aplikacija1.port"));

		Klijent k = new Klijent();
		String odgovorPosluzitelja = k.dohvatiStatus(korisnik, zeton, adresa, port);

		model.put("status", odgovorPosluzitelja);
		model.put("korisnik", korisnik);
		model.put("zeton", zeton);
	}

	@POST
	@Path("upravljanjee")
	@View("upravljanje.jsp")
	public void upravljanjee(@QueryParam("zeton") int zeton, @QueryParam("korisnik") String korisnik,
			@FormParam("naredba") String naredba) {
		KonfiguracijaBP konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		String adresa = konfig.dajPostavku("aplikacija1.adresa");
		int port = Integer.parseInt(konfig.dajPostavku("aplikacija1.port"));

		Klijent k = new Klijent();
		String odgovorPosluzitelja = k.posaljiNaredbu(korisnik, zeton, adresa, port, naredba);
		String status = k.dohvatiStatus(korisnik, zeton, adresa, port);

		model.put("odgovor", odgovorPosluzitelja);
		model.put("status", status);
		model.put("korisnik", korisnik);
		model.put("zeton", zeton);
	}
}
