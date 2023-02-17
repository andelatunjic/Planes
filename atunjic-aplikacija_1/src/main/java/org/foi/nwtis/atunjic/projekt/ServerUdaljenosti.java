package org.foi.nwtis.atunjic.projekt;

import com.google.gson.Gson;
import org.foi.nwtis.atunjic.vjezba_03.konfiguracije.Konfiguracija;
import org.foi.nwtis.atunjic.vjezba_03.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.atunjic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.podaci.Aerodrom;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa ServerUdaljenosti za računanje udaljenosti aerodroma.
 */
public class ServerUdaljenosti {

	/** Konfig. */
	static public Konfiguracija konfig = null;
	
	/** Status. */
	public static volatile StatusPosluzitelja status;
	
	/** Aerodromi. */
	private static volatile List<Aerodrom> aerodromi = new ArrayList<>();
	
	/** Broj dretvi. */
	private final int brojDretvi;
	
	/** Maks cekaca. */
	private final int maksCekaca;
	
	/** Port. */
	private final int port;
	
	/** Veza. */
	private Socket veza;

	/**
	 * Konstruktor.
	 *
	 * @param port port
	 * @param maksCekaca maks broj cekaca
	 * @param brojDretvi maks broj dretvi
	 * @param status status poslužitelja
	 */
	public ServerUdaljenosti(int port, int maksCekaca, int brojDretvi, StatusPosluzitelja status) {
		this.port = port;
		this.maksCekaca = maksCekaca;
		this.brojDretvi = brojDretvi;
		ServerUdaljenosti.status = status;
	}

	/**
	 * Main.
	 *
	 * @param args naziv datoteke konfiguracije
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Dozvoljen samo jedan argument datoteka konfiguracije!");
			return;
		}
		final String nazivDatoteke = args[0];
		if (!ucitajKonfiguraciju(nazivDatoteke))
			return;

		int port = Integer.parseInt(konfig.dajPostavku("port"));
		int brojDretvi = Integer.parseInt(konfig.dajPostavku("broj.dretvi"));
		int maksCekaca = Integer.parseInt(konfig.dajPostavku("maks.cekaca"));

		ServerUdaljenosti server = new ServerUdaljenosti(port, maksCekaca, brojDretvi, StatusPosluzitelja.hibernira);
		server.obradaZahtjeva();
	}

	/**
	 * Ucitaj konfiguraciju.
	 *
	 * @param nazivDatoteke naziv datoteke konfiguracije
	 * @return true, ako je uspješno učitana
	 */
	private static boolean ucitajKonfiguraciju(String nazivDatoteke) {
		try {
			konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
		} catch (NeispravnaKonfiguracija e) {
			System.out.println("Naziv datoteke: " + nazivDatoteke + " ne postoji.");
		}

		if (konfig == null) {
			System.out.println("Problem s učitavnjem konfiguracije!");
			return false;
		}
		return true;
	}

	/**
	 * Obrada zahtjeva.
	 */
	public void obradaZahtjeva() {
		try (ServerSocket ss = new ServerSocket(this.port, this.maksCekaca)) {
			while (true) {
				System.out.println("SERVER: Čekam korisnika.");
				this.veza = ss.accept();
				ServerUdaljenosti$DretvaUdaljenosti dretvaUdaljenosti = new ServerUdaljenosti$DretvaUdaljenosti(
						this.veza, this.brojDretvi);
				dretvaUdaljenosti.start();
			}
		} catch (IOException ex) {
			System.out.println("Port je zauzet.");
		}
	}

	/**
	 * DretvaUdaljenosti.
	 */
	private static class ServerUdaljenosti$DretvaUdaljenosti extends Thread {
		
		/** Veza. */
		private final Socket veza;
		
		/** Broj dretvi. */
		private final int brojDretvi;
		
		/** Semafor. */
		private final Semaphore semafor;

		/**
		 * Konstruktor.
		 *
		 * @param veza veza
		 * @param brojDretvi broj dretvi
		 */
		public ServerUdaljenosti$DretvaUdaljenosti(Socket veza, int brojDretvi) {
			this.veza = veza;
			this.brojDretvi = brojDretvi;
			this.semafor = new Semaphore(this.brojDretvi);
		}

		/**
		 * Start.
		 */
		@Override
		public synchronized void start() {
			super.start();
		}

		/**
		 * Run.
		 */
		@Override
		public synchronized void run() {
			try (InputStreamReader isr = new InputStreamReader(this.veza.getInputStream(), StandardCharsets.UTF_8);
					OutputStreamWriter osw = new OutputStreamWriter(this.veza.getOutputStream(),
							StandardCharsets.UTF_8)) {
				StringBuilder tekst = new StringBuilder();
				while (true) {
					int i = isr.read();
					if (i == -1) {
						break;
					}
					tekst.append((char) i);
				}
				this.veza.shutdownInput();

				semafor.acquire();
				String odgovor = izvrsiNaredbu(tekst);
				semafor.release();

				osw.write(odgovor);
				osw.flush();
				veza.shutdownOutput();
				interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Izvrsi naredbu.
		 *
		 * @param tekst naredba korisnika
		 * @return string odgovor servera
		 */
		private String izvrsiNaredbu(StringBuilder tekst) {
			Pattern pStatus = Pattern.compile("^STATUS$");
			Pattern pQuit = Pattern.compile("^QUIT$");
			Pattern pInit = Pattern.compile("^INIT$");
			Pattern pLoad = Pattern.compile("^.*LOAD.*$");
			Pattern pDistance = Pattern.compile("^DISTANCE ([A-Z]{4}) ([A-Z]{4})$");
			Pattern pDistanceClear = Pattern.compile("^CLEAR$");

			Matcher mStatus = pStatus.matcher(tekst.toString());
			Matcher mQuit = pQuit.matcher(tekst.toString());
			Matcher mInit = pInit.matcher(tekst.toString());
			Matcher mLoad = pLoad.matcher(tekst.toString());
			Matcher mDistance = pDistance.matcher(tekst.toString());
			Matcher mDistanceClear = pDistanceClear.matcher(tekst.toString());

			String odgovor = "ERROR 30 Format komande nije ispravan!";

			if (mStatus.matches()) {
				odgovor = izvrsiStatusNaredbu();
			} else if (mQuit.matches()) {
				odgovor = izvrsiQuitNaredbu();
				//System.exit(0);
			} else if (mInit.matches()) {
				odgovor = izvrsiInitNaredbu();
			} else if (mLoad.matches()) {
				odgovor = izvrsiLoadNaredbu(tekst.toString());
			} else if (mDistance.matches()) {
				odgovor = izvrsiDistanceNaredbu(tekst.toString());
			} else if (mDistanceClear.matches()) {
				odgovor = izvrsiDistanceClearNaredbu();
			}
			return odgovor;
		}

		/**
		 * Izvrsi status naredbu.
		 *
		 * @return string status servera
		 */
		private String izvrsiStatusNaredbu() {
			System.out.println("TRENUTNI STATUS: " + status.dajVrijednost());
			return "OK " + status.dajVrijednost();
		}

		/**
		 * Izvrsi quit naredbu.
		 *
		 * @return the string odgovor servera
		 */
		private String izvrsiQuitNaredbu() {
			status = StatusPosluzitelja.hibernira;
			System.out.println("TRENUTNI STATUS: " + status.dajVrijednost());
			return "OK";
		}

		/**
		 * Izvrsi init naredbu.
		 *
		 * @return the string odgovor servera
		 */
		private String izvrsiInitNaredbu() {
			if (status.dajVrijednost() == 1) {
				return "ERROR 02 Poslužitelj je inicijaliziran, a stigla je komanda (INIT, DISTANCE ili CLEAR).";
			}
			if (status.dajVrijednost() == 3) {
				return "ERROR 03 Poslužitelj je aktivan, a stigla je komanda (INIT ili LOAD).";
			}
			status = StatusPosluzitelja.inicijaliziran;
			return "OK";
		}

		/**
		 * Izvrsi load naredbu.
		 *
		 * @param json lista aerodroma
		 * @return string broj učitanih aerodroma
		 */
		private String izvrsiLoadNaredbu(String json) {
			if (status.dajVrijednost() == 0) {
				return "ERROR 01 Poslužitelj hibernira, a stigla je komanda (LOAD, DISTANCE ili CLEAR).";
			}
			if (status.dajVrijednost() == 3) {
				return "ERROR 03 Poslužitelj je aktivan, a stigla je komanda (INIT ili LOAD).";
			}
			Gson gson = new Gson();
			json = json.replace("LOAD ", "");
			aerodromi = List.of(gson.fromJson(json, Aerodrom[].class));
			status = StatusPosluzitelja.aktivan;
			return "OK " + aerodromi.size();
		}

		/**
		 * Izvrsi distance naredbu.
		 *
		 * @param komanda naredba korisnika
		 * @return string udaljenost
		 */
		private String izvrsiDistanceNaredbu(String komanda) {

			if (status.dajVrijednost() == 0) {
				return "ERROR 01 Poslužitelj hibernira, a stigla je komanda (LOAD, DISTANCE ili CLEAR).";
			}
			if (status.dajVrijednost() == 1) {
				return "ERROR 02 Poslužitelj je inicijaliziran, a stigla je komanda (INIT, DISTANCE ili CLEAR).";
			}

			String[] p = komanda.split(" ");
			String icao1 = p[1];
			String icao2 = p[2];

			Aerodrom aerodromIcao1 = pronadiAerodrom(icao1);
			Aerodrom aerodromIcao2 = pronadiAerodrom(icao2);

			if (aerodromIcao1 == null && aerodromIcao2 != null) {
				return "ERROR 11 Ne postoji prvi aerodrom.";
			}
			if (aerodromIcao2 == null && aerodromIcao1 != null) {
				return "ERROR 12 Ne postoji drugi aerodrom.";
			}
			if (aerodromIcao1 != null && aerodromIcao2 != null) {
				double udaljenost = izracunajUdaljenostAerodroma(aerodromIcao1.getLokacija().getLatitude(),
						aerodromIcao1.getLokacija().getLongitude(), aerodromIcao2.getLokacija().getLatitude(),
						aerodromIcao2.getLokacija().getLongitude());
				return "OK " + udaljenost;
			} else {
				return "ERROR 13 Ne postoje traženi aerodromi.";
			}
		}

		/**
		 * Pronadi aerodrom.
		 *
		 * @param icao icao
		 * @return aerodrom pronađeni aerodrom
		 */
		private Aerodrom pronadiAerodrom(String icao) {
			for (Aerodrom aerodrom : aerodromi) {
				if (icao.equals(aerodrom.getIcao())) {
					return aerodrom;
				}
			}
			return null;
		}

		/**
		 * Izracunaj udaljenost aerodroma.
		 *
		 * @param gpsGS1 gps GS 1
		 * @param gpsGD1 gps GD 1
		 * @param gpsGS2 gps GS 2
		 * @param gpsGD2 gps GD 2
		 * @return int udaljenost
		 */
		private int izracunajUdaljenostAerodroma(String gpsGS1, String gpsGD1, String gpsGS2, String gpsGD2) {

			double lat1 = Double.parseDouble(gpsGS1);
			double lon1 = Double.parseDouble(gpsGD1);
			double lat2 = Double.parseDouble(gpsGS2);
			double lon2 = Double.parseDouble(gpsGD2);

			double dlon = Math.toRadians(lon2) - Math.toRadians(lon1);
			double dlat = Math.toRadians(lat2) - Math.toRadians(lat1);
			double a = Math.pow(Math.sin(dlat / 2), 2)
					+ Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

			double c = 2 * Math.asin(Math.sqrt(a));

			double r = 6371 * c;
			return (int) r;
		}

		/**
		 * Izvrsi distance clear naredbu.
		 *
		 * @return string odgovor posluzitelja
		 */
		private String izvrsiDistanceClearNaredbu() {
			if (status.dajVrijednost() == 0) {
				return "ERROR 01 Poslužitelj hibernira, a stigla je komanda (LOAD, DISTANCE ili CLEAR).";
			}
			if (status.dajVrijednost() == 1) {
				return "ERROR 02 Poslužitelj je inicijaliziran, a stigla je komanda (INIT, DISTANCE ili CLEAR).";
			}
			status = StatusPosluzitelja.hibernira;

			aerodromi = new ArrayList<Aerodrom>();

			return "OK";
		}

		/**
		 * Interrupt.
		 */
		@Override
		public void interrupt() {
			super.interrupt();
		}
	}
}
