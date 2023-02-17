package org.foi.nwtis.atunjic.aplikacija_4.mvc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Controller
@Path("kontrolerPutanje")
@RequestScoped
public class KontrolerPutanje {

	@Inject
	private Models model;

	@Context
	private ServletContext context;

	@GET
	@Path("pocetak")
	@View("index.jsp")
	public void pocetak() {
	}

	@GET
	@Path("registracija")
	@View("registracija.jsp")
	public void registracija() {
	}

	@GET
	@Path("prijava")
	@View("prijava.jsp")
	public void prijava() {
	}

}
