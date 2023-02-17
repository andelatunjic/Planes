package org.foi.nwtis.atunjic.aplikacija_4.mvc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.atunjic.aplikacija_4.podaci.Grupa;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;

import com.google.gson.Gson;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class Klijent {

	public Response registriraj(Korisnik korisnik, String korime, String loz) {

		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/korisnici");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korime)
				.header("lozinka", loz).post(Entity.json(korisnik));

		return restOdgovor;
	}

	public Response prijavi(String korisnik, String lozinka) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/provjere");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).get();

		return restOdgovor;
	}

	public List<Korisnik> dajSveKorisnike(String korisnik, int zeton) {

		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/korisnici");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
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

	public List<Grupa> dajGrupeKorisnika(String korisnik, int zeton) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/korisnici/")
				.path(korisnik + "/grupe");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		List<Grupa> grupe = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			grupe = new ArrayList<>();
			grupe.addAll(Arrays.asList(gson.fromJson(odgovor, Grupa[].class)));
		}
		return grupe;
	}

	public Response obrisiTokene(String korIme, String korisnik, String lozinka) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/provjere/korisnik/")
				.path(korIme);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).delete();

		return restOdgovor;
	}

	public Response obrisiToken(int zeton, String korisnik, int lozinka) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/provjere/" + zeton);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).delete();

		return restOdgovor;
	}

	public String dohvatiStatus(String korisnik, int zeton, String adresa, int port) {
		String naredba = "STATUS";
		String odgovorPosluzitelja = posaljiKomandu(adresa, port, naredba);
		return odgovorPosluzitelja;
	}

	public String posaljiNaredbu(String korisnik, int zeton, String adresa, int port, String naredba) {
		String odgovorPosluzitelja = "";
		if (naredba.compareTo("LOAD") == 0) {
			Gson gson = new Gson();

			List<Aerodrom> aerodromi = dohvatiAerodromeZaPratiti(korisnik, zeton);

			String komanda = "LOAD " + gson.toJson(aerodromi);

			odgovorPosluzitelja = posaljiKomandu(adresa, port, komanda);
		} else {
			odgovorPosluzitelja = posaljiKomandu(adresa, port, naredba);
		}
		return odgovorPosluzitelja;
	}

	private List<Aerodrom> dohvatiAerodromeZaPratiti(String korisnik, int zeton) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/aerodromi?preuzimanje");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		String odgovor = restOdgovor.readEntity(String.class);
		Gson gson = new Gson();
		List<Aerodrom> aerodromi = new ArrayList<>();
		aerodromi.addAll(Arrays.asList(gson.fromJson(odgovor, Aerodrom[].class)));

		return aerodromi;
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

}
