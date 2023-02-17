package org.foi.nwtis.atunjic.aplikacija_4.mvc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Controller
@Path("kontrolerPrijava")
@RequestScoped
public class KontrolerPrijava {

	@Inject
	private Models model;

	@Context
	private ServletContext context;

	@FormParam("korisnik")
	String korisnik;

	@FormParam("lozinka")
	String lozinka;

	@POST
	@Path("prijava")
	@View("prijava.jsp")
	public String prijava() {

		Klijent k = new Klijent();
		Response odgovorPosluzitelja = k.prijavi(korisnik, lozinka);

		if (odgovorPosluzitelja.getStatus() == 200) {
			String odgovor = odgovorPosluzitelja.readEntity(String.class);
			int token = vratiZeton(odgovor);
			// return
			// "redirect:kontrolerKorisnici/pregledKorisnika/"+korisnik+"/"+lozinka+"/"+token;
			return "redirect:kontrolerKorisnici/pregledKorisnika?zeton=" + token + "&korisnik=" + korisnik + "&lozinka="
					+ lozinka;
		}
		return "redirect:kontrolerPutanje/prijava";
	}

	private int vratiZeton(String odgovorPosluzitelja) {
		int odgovor = 0;

		String[] p1 = odgovorPosluzitelja.split(" ");

		String token = p1[0].replace(",", "");
		String token1 = token.replace("zeton:", "");
		odgovor = Integer.parseInt(token1);

		return odgovor;
	}

}
