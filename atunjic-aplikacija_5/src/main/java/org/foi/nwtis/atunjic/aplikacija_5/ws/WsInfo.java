package org.foi.nwtis.atunjic.aplikacija_5.ws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.atunjic.aplikacija_5.wsock.Info;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;

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

@WebService(serviceName = "info")
public class WsInfo {

	@Resource
	private WebServiceContext wsContext;

	private KonfiguracijaBP konfig;

	@WebMethod
	public void dajMeteo() {
		String korisnik = konfig.dajPostavku("sustav.korisnik");
		String lozinka = konfig.dajPostavku("sustav.lozinka");
		int zeton = dohvatiNoviZeton(korisnik, lozinka);

		int brojAerdoroma = dohvatiAerodrome(korisnik, zeton).size();
		String poruka = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + ", " + brojAerdoroma;
		Info.informiraj(poruka);
		preuzmiPostavke();
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
		System.out.println(odgovorPosluzitelja);

		String token = p1[0].replace(",", "");
		String token1 = token.replace("zeton:", "");
		odgovor = Integer.parseInt(token1);
		System.out.println(token1);

		return odgovor;
	}

	private void preuzmiPostavke() {
		this.wsContext.getMessageContext();
		ServletContext context = (ServletContext) this.wsContext.getMessageContext()
				.get(MessageContext.SERVLET_CONTEXT);
		this.konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}
}