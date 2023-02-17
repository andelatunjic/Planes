package org.foi.nwtis.atunjic.aplikacija_5.ws;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
//import org.foi.nwtis.atunjic.aplikacija_5.ws.WsInfo;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

import com.google.gson.Gson;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceContext;

@WebService(serviceName = "aerodromi")
public class WsAerodromi {

	@Resource
	private WebServiceContext wsContext;

	@WebMethod
	public List<AvionLeti> dajPolaskeDan(String korisnik, int zeton, String icao, String danOd, String danDo) {
		// dd.mm.gggg
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/aerodromi/" + icao
				+ "/polasci?vrsta=0&od=" + danOd + "&do=" + danDo);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		List<AvionLeti> polasciAerodroma = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			polasciAerodroma = new ArrayList<>();
			polasciAerodroma.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLeti[].class)));
		}
		return polasciAerodroma;
	}

	@WebMethod
	public List<AvionLeti> dajPolaskeVrijeme(String korisnik, int zeton, String icao, String vrijemeOd,
			String vrijemeDo) {
		// dd.mm.gggg hh:mm:ss
		LocalDate datumOd = vratiDatum(vrijemeOd);
		LocalDate datumDo = vratiDatum(vrijemeDo);

		long vrijemeOdSekunde = datumOd.toEpochSecond(LocalTime.MAX, ZoneOffset.of("+02:00"));
		long vrijemeDoSekunde = datumDo.toEpochSecond(LocalTime.MAX, ZoneOffset.of("+02:00"));

		List<AvionLeti> polasciAerodroma = dohvatiPolaske(korisnik, zeton, icao, vrijemeOdSekunde, vrijemeDoSekunde);

		return polasciAerodroma;
	}

	private List<AvionLeti> dohvatiPolaske(String korisnik, int zeton, String icao, long vrijemeOdSekunde,
			long vrijemeDoSekunde) {

		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/aerodromi/" + icao
				+ "/polasci?vrsta=1&od=" + vrijemeOdSekunde + "&do=" + vrijemeDoSekunde);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		List<AvionLeti> polasciAerodroma = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			polasciAerodroma = new ArrayList<>();
			polasciAerodroma.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLeti[].class)));
		}
		return polasciAerodroma;
	}

	private LocalDate vratiDatum(String vrijeme) {

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		LocalDate datum = LocalDate.from(timeFormatter.parse(vrijeme));

		return datum;
	}

	@WebMethod
	public boolean dodajAerodromPreuzimanje(String korisnik, String zeton, String icao) {
		boolean odgovor = false;

		int token = Integer.parseInt(zeton);
		Aerodrom aerodrom = dohvatiAerodromZaSlanje(icao, korisnik, token);

		if (aerodrom != null) {
			Client client = ClientBuilder.newClient();
			WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/aerodromi");
			Response restOdgovor = webResource.request().header("Accept", "application/json")
					.header("korisnik", korisnik).header("zeton", zeton).post(Entity.json(aerodrom));

			if (restOdgovor.getStatus() == 200) {
				odgovor = true;
//				WsInfo info = new WsInfo();
//				info.dajMeteo();
			}
		}

		return odgovor;
	}

	private Aerodrom dohvatiAerodromZaSlanje(String icao, String korisnik, int zeton) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/aerodromi").path(icao);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		Aerodrom aerodrom = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();

			aerodrom = gson.fromJson(odgovor, Aerodrom.class);
		}
		return aerodrom;
	}
}