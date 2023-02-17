package org.foi.nwtis.atunjic.aplikacija_5.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

import com.google.gson.Gson;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

@WebService(serviceName = "meteo")
public class WsMeteo {

	@Resource
	private WebServiceContext wsContext;

	private KonfiguracijaBP konfig;

	@WebMethod
	public MeteoPodaci dajMeteo(String icao) {
		this.preuzmiPostavke();

		String korisnik = konfig.dajPostavku("sustav.korisnik");
		String lozinka = konfig.dajPostavku("sustav.lozinka");
		int zeton = dohvatiNoviZeton(korisnik, lozinka);
		List<Aerodrom> aerodromi = dohvatiAerodrome(korisnik, zeton);

		Aerodrom trazeni = null;

		for (Aerodrom a : aerodromi) {
			if (a.getIcao().compareTo(icao) == 0) {
				trazeni = a;
				break;
			}
		}

		Lokacija l = trazeni.getLokacija();

		OWMKlijent owmKlijent = new OWMKlijent(konfig.dajPostavku("OpenWeatherMap.apikey"));

		MeteoPodaci mp = null;
		try {
			mp = owmKlijent.getRealTimeWeather(l.getLatitude(), l.getLongitude());
		} catch (NwtisRestIznimka e) {
			e.printStackTrace();
		}

		return mp;
	}

	private List<Aerodrom> dohvatiAerodrome(String korisnik, int zeton) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/aerodromi?preuzimanje");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		List<Aerodrom> aerodromi = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodromi = new ArrayList<>();
			aerodromi.addAll(Arrays.asList(gson.fromJson(odgovor, Aerodrom[].class)));
		}
		return aerodromi;
	}

	private int dohvatiNoviZeton(String korisnik, String lozinka) {
		int odgovor = 0;

		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target("http://localhost:8080/atunjic-aplikacija_3/api/provjere");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).get();

		if (restOdgovor.getStatus() == 200) {
			String odgovorPosluzitelja = restOdgovor.readEntity(String.class);
			odgovor = vratiZeton(odgovorPosluzitelja);
		}
		return odgovor;
	}

	private int vratiZeton(String odgovorPosluzitelja) {
		int odgovor = 0;

		String[] p1 = odgovorPosluzitelja.split(" ");

		String token = p1[0].replace(",", "");
		String token1 = token.replace("zeton:", "");
		odgovor = Integer.parseInt(token1);

		return odgovor;
	}

	private void preuzmiPostavke() {
		this.wsContext.getMessageContext();
		ServletContext context = (ServletContext) this.wsContext.getMessageContext()
				.get(MessageContext.SERVLET_CONTEXT);
		this.konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}
}