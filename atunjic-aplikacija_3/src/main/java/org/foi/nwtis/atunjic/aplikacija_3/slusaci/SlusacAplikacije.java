package org.foi.nwtis.atunjic.aplikacija_3.slusaci;

import java.io.File;

import org.foi.nwtis.atunjic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

	public SlusacAplikacije() {

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		String nazivDatoteke = context.getInitParameter("konfiguracija");
		String putanja = context.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;

		KonfiguracijaBP konfig = new PostavkeBazaPodataka(nazivDatoteke);
		try {
			konfig.ucitajKonfiguraciju();
		} catch (NeispravnaKonfiguracija e) {
			e.printStackTrace();
			return;
		}

		context.setAttribute("Postavke", konfig);

		ServletContextListener.super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("postavke");
		System.out.println("Postavke obrisane!");
		ServletContextListener.super.contextDestroyed(sce);
	}

}
