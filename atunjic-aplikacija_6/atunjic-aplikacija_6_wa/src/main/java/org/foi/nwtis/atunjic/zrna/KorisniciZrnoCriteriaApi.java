package org.foi.nwtis.atunjic.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.atunjic.jpa.criteriaapi.GrupeJpa;
import org.foi.nwtis.atunjic.jpa.criteriaapi.KorisniciJpa;
import org.foi.nwtis.atunjic.jpa.entiteti.Grupe;
import org.foi.nwtis.atunjic.jpa.entiteti.Korisnici;
import org.foi.nwtis.podaci.Korisnik;

import com.google.gson.Gson;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

//@ConversationScoped
@SessionScoped
@Named("korisniciZrnoCriteriaApi")
public class KorisniciZrnoCriteriaApi implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	KorisniciJpa korisniciJpa;

	@EJB
	GrupeJpa grupeJpa;

	List<Korisnik> korisniciPosluzitelj = new ArrayList<>();
	Korisnik korisnikPosluzitelj = new Korisnik();

	List<Korisnici> korisnici = new ArrayList<>();
	Korisnici korisnik = new Korisnici();
	Grupe grupa = new Grupe();

	boolean traziGrupe = false;

	public List<Korisnici> getKorisnici() {
		if (!traziGrupe) {
			korisnici = this.dajSveKorisnike();
		}
		return korisnici;
	}

	public void setKorisnici(List<Korisnici> korisnici) {
		this.korisnici = korisnici;
	}

	public Korisnici getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnici korisnik) {
		this.korisnik = korisnik;
	}

	public List<Korisnik> getKorisniciPosluzitelj() {
		if (!traziGrupe) {
			korisniciPosluzitelj = this.dajSveKorisnikePosluzitelj();
		}
		return korisniciPosluzitelj;
	}

	public void setKorisniciPosluzitelj(List<Korisnik> korisniciPosluzitelj) {
		this.korisniciPosluzitelj = korisniciPosluzitelj;
	}

	public Korisnik getKorisnikPosluzitelj() {
		return korisnikPosluzitelj;
	}

	public void setKorisnikPosluzitelj(Korisnik korisnikPosluzitelj) {
		this.korisnikPosluzitelj = korisnikPosluzitelj;
	}

	public Grupe getGrupa() {
		return grupa;
	}

	public void setGrupa(Grupe grupa) {
		this.grupa = grupa;
	}

	public List<Korisnici> dajSveKorisnike() {
		List<Korisnici> lKorisnicii = (List<Korisnici>) korisniciJpa.findAll();

		return lKorisnicii;
	}

	public String odabirKorisnika(String korisnikId) {
		this.korisnik = korisniciJpa.find(korisnikId);
		return "";
	}

	public String odabirGrupe(String grupaId) {
		this.grupa = grupeJpa.find(grupaId);
		traziGrupe = true;
		this.korisnici = this.grupa.getKorisnicis();
		return "";
	}

	String korIme = "";
	String lozinka = "";
	String obavijest = "";
	String obavijestOdjava = "";
	int zeton = 0;

	public String getKorIme() {
		return korIme;
	}

	public void setKorIme(String korIme) {
		this.korIme = korIme;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public String getObavijest() {
		return obavijest;
	}

	public void setObavijest(String obavijest) {
		this.obavijest = obavijest;
	}

	public String getObavijestOdjava() {
		return obavijestOdjava;
	}

	public void setObavijestOdjava(String obavijestOdjava) {
		this.obavijestOdjava = obavijestOdjava;
	}

	public int getZeton() {
		return zeton;
	}

	public void setZeton(int zeton) {
		this.zeton = zeton;
	}

	public void prijaviKorisnika() {
		System.out.println(korIme + " i " + lozinka);
		boolean lokalnaProvjera = provjeriKorisnikaLokalno(korIme, lozinka);

		if (lokalnaProvjera) {
			Response odgovorPosluzitelja = prijavi(korIme, lozinka);
			String odgovor = odgovorPosluzitelja.readEntity(String.class);
			if (odgovorPosluzitelja.getStatus() == 200) {
				int token = vratiZeton(odgovor);
				setZeton(token);
				setObavijest("Uspjesna prijava!");
			} else {
				setObavijest(odgovor);
			}
		}
	}

	private int vratiZeton(String odgovorPosluzitelja) {
		int zeton = 0;

		String[] p1 = odgovorPosluzitelja.split(" ");

		String token = p1[0].replace(",", "");
		String token1 = token.replace("zeton:", "");
		zeton = Integer.parseInt(token1);

		return zeton;
	}

	private Response prijavi(String korIme2, String lozinka2) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/provjere");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korIme2)
				.header("lozinka", lozinka2).get();

		return restOdgovor;
	}

	private boolean provjeriKorisnikaLokalno(String korIme2, String lozinka2) {
		List<Korisnici> korisnici = dajSveKorisnike();
		for (Korisnici k : korisnici) {
			if (k.getKorisnik().compareTo(korIme) == 0 && k.getLozinka().compareTo(lozinka) == 0) {
				System.out.println("Korisnik postoji");
				return true;
			} else {
				setObavijest("Korisnik ne postoji lokalno");
			}
		}
		return false;
	}

	public void odjaviKorisnika() {
		System.out.println("Došao sam do odjave");
		if (zeton != 0) {
			Response odgovorPosluzitelja = obrisiToken(zeton, korIme, lozinka);
			String odgovor = odgovorPosluzitelja.readEntity(String.class);
			setObavijestOdjava("Uspjesna odjava! " + odgovor);
			setZeton(0);
			setKorIme("");
			setLozinka("");
		} else {
			setObavijestOdjava("Prvo se prijavite");
		}
	}

	private Response obrisiToken(int zeton2, String korIme2, String lozinka2) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/provjere/" + zeton2);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korIme2)
				.header("lozinka", lozinka2).delete();

		return restOdgovor;
	}

	public List<Korisnik> dajSveKorisnikePosluzitelj() {

		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/korisnici");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korIme)
				.header("zeton", zeton).get();

		List<Korisnik> korisnici = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			korisnici = new ArrayList<>();
			korisnici.addAll(Arrays.asList(gson.fromJson(odgovor, Korisnik[].class)));
		}
		return korisnici;
	}
}