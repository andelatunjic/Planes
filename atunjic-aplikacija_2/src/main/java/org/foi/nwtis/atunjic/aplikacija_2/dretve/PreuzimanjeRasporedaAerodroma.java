package org.foi.nwtis.atunjic.aplikacija_2.dretve;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.foi.nwtis.atunjic.aplikacija_2.podaci.AerodromiDolasciDAO;
import org.foi.nwtis.atunjic.aplikacija_2.podaci.AerodromiPolasciDAO;
import org.foi.nwtis.atunjic.aplikacija_2.podaci.AerodromiPraceniDAO;
import org.foi.nwtis.atunjic.aplikacija_2.podaci.AerodromiProblemiDAO;
import org.foi.nwtis.atunjic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Klasa Preuzimanje rasporeda aerodroma.
 */
public class PreuzimanjeRasporedaAerodroma extends Thread {

	/** Konfig. */
	private KonfiguracijaBP konfig;

	/** Vrijeme ciklusa. */
	private int vrijemeCiklusa;
	
	/** Korekcija ciklusa. */
	private int korekcijaCiklusa;
	
	/** Preuzimanje odmak. */
	private int preuzimanjeOdmak;
	
	/** Preuzimanje pauza. */
	private long preuzimanjePauza;
	
	/** Preuzimanje od. */
	private long preuzimanjeOd;
	
	/** Preuzimanje do. */
	private long preuzimanjeDo;
	
	/** Preuzimanje vrijeme. */
	private long preuzimanjeVrijeme;

	/** Korisnik. */
	private String korisnik;
	
	/** Lozinka. */
	private String lozinka;

	/** os klijent. */
	private OSKlijent osKlijent;

	/**
	 * Konstruktor.
	 *
	 * @param konfig the konfig
	 */
	public PreuzimanjeRasporedaAerodroma(KonfiguracijaBP konfig) {
		this.konfig = konfig;
	}

	/**
	 * Start.
	 */
	@Override
	public synchronized void start() {
		this.preuzmiPostavke();
		super.start();
	}

	/**
	 * Preuzmi postavke.
	 */
	private void preuzmiPostavke() {
		try {
			konfig.ucitajKonfiguraciju();
		} catch (NeispravnaKonfiguracija e1) {
			e1.printStackTrace();
		}

		// u sekundama
		this.vrijemeCiklusa = Integer.parseInt(konfig.dajPostavku("ciklus.vrijeme")) * 1000;
		// broj
		this.korekcijaCiklusa = Integer.parseInt(konfig.dajPostavku("ciklus.korekcija"));
		// u danima
		this.preuzimanjeOdmak = Integer.parseInt(konfig.dajPostavku("preuzimanje.odmak")) * 24 * 60 * 60;
		// u ms
		this.preuzimanjePauza = Integer.parseInt(konfig.dajPostavku("preuzimanje.pauza"));
		// dd.mm.gggg
		this.preuzimanjeOd = vratiMilisekunde(konfig.dajPostavku("preuzimanje.od")) / 1000;
		this.preuzimanjeDo = vratiMilisekunde(konfig.dajPostavku("preuzimanje.do")) / 1000;
		// u satima
		this.preuzimanjeVrijeme = Integer.parseInt(konfig.dajPostavku("preuzimanje.vrijeme")) * 60 * 60;

		// OpenSky
		this.korisnik = konfig.dajPostavku("OpenSkyNetwork.korisnik");
		this.lozinka = konfig.dajPostavku("OpenSkyNetwork.lozinka");
		this.osKlijent = new OSKlijent(korisnik, lozinka);
	}

	/**
	 * Vrati milisekunde iz datuma.
	 *
	 * @param datum datum
	 * @return long milisekunde
	 */
	private long vratiMilisekunde(String datum) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		try {
			return simpleDateFormat.parse(datum).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		

		int stvarniBrojacCiklusa = 1;
		float virtualniBrojacCiklusa = 1;
		long preuzimanjeAerodromaDo = preuzimanjeOd + preuzimanjeVrijeme;
		long pocetakPrvogCiklusa = System.currentTimeMillis();
		long pocetakSljedecegCiklusa = pocetakPrvogCiklusa;
		while (preuzimanjeOd < preuzimanjeDo) {

			List<Aerodrom> aerodromi = AerodromiPraceniDAO.dohvatiAerodromeZaPratiti(konfig);
//			long trenutnoVrijeme = System.currentTimeMillis() / 1000;
//			if (preuzimanjeOd > (trenutnoVrijeme - preuzimanjeOdmak)) {
//				stvarniBrojacCiklusa += 1;
//				virtualniBrojacCiklusa += (86400000 / vrijemeCiklusa);
//				try {
//					sleep(86400000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}

			for (Aerodrom a : aerodromi) {

				List<AvionLeti> avioniPolasci;
				try {
					avioniPolasci = osKlijent.getDepartures(a.getIcao(), preuzimanjeOd, preuzimanjeAerodromaDo);
					if (avioniPolasci != null) {
						for (AvionLeti avion : avioniPolasci) {
							if (avion.getEstArrivalAirport() != null) {
								spremiPolazakAviona(konfig, avion);
							}
						}
					}
				} catch (NwtisRestIznimka e) {
					AerodromiProblemiDAO.spremiProblemAerodroma(konfig, a.getIcao(), true);
					e.printStackTrace();
				}

				List<AvionLeti> avioniDolasci;
				try {

					avioniDolasci = osKlijent.getArrivals(a.getIcao(), preuzimanjeOd, preuzimanjeAerodromaDo);

					if (avioniDolasci != null) {
						for (AvionLeti avion : avioniDolasci) {
							if (avion.getEstDepartureAirport() != null) {
								spremiDolazakAviona(konfig, avion);
							}
						}
					}
				} catch (NwtisRestIznimka e) {
					AerodromiProblemiDAO.spremiProblemAerodroma(konfig, a.getIcao(), false);
					e.printStackTrace();
				}

				// Pauza prije sljedećeg preuzimanja
				try {
					sleep(preuzimanjePauza);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			preuzimanjeOd = preuzimanjeAerodromaDo;
			preuzimanjeAerodromaDo = preuzimanjeAerodromaDo + preuzimanjeVrijeme;

			long krajRadaCiklusa = System.currentTimeMillis();
			long efektivniRadDretve = krajRadaCiklusa - pocetakSljedecegCiklusa;
			long vrijemeSpavanja;
			if (efektivniRadDretve > vrijemeCiklusa) {
				long n = 2;
				while (true) {
					if (n * vrijemeCiklusa < efektivniRadDretve)
						n++;
					else {
						vrijemeSpavanja = (n * vrijemeCiklusa) - efektivniRadDretve;
						virtualniBrojacCiklusa += n;
						stvarniBrojacCiklusa += 1;
						break;
					}
				}
			} else {
				vrijemeSpavanja = vrijemeCiklusa - efektivniRadDretve;
				virtualniBrojacCiklusa += 1;
				stvarniBrojacCiklusa += 1;
			}

			if (stvarniBrojacCiklusa % korekcijaCiklusa == 0) {
				vrijemeCiklusa = Integer.parseInt(konfig.dajPostavku("ciklus.vrijeme")) * 1000;
			}

			pocetakSljedecegCiklusa = pocetakPrvogCiklusa + (stvarniBrojacCiklusa * vrijemeCiklusa);

			try {
				sleep(vrijemeSpavanja);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Spremi polazak aviona.
	 *
	 * @param konfig konfiguracija
	 * @param avion avion
	 */
	private void spremiPolazakAviona(KonfiguracijaBP konfig, AvionLeti avion) {
		if (!AerodromiPolasciDAO.provjeraDuplikata(konfig, avion)) {
			if (AerodromiPolasciDAO.spremiPolazakSAerodroma(konfig, avion)) {
				System.out.println("Uspjesno dodan polazak");
			} else {
				System.out.println("Neuspjesno dodan polazak");
			}
		} else {
			System.out.println("Polazak je vec dodan prije.");
		}
	}

	/**
	 * Spremi dolazak aviona.
	 *
	 * @param konfig konfiguracija
	 * @param avion avion
	 */
	private void spremiDolazakAviona(KonfiguracijaBP konfig, AvionLeti avion) {
		if (!AerodromiDolasciDAO.provjeraDuplikata(konfig, avion)) {
			if (AerodromiDolasciDAO.spremiDolazakNaAerodrom(konfig, avion)) {
				System.out.println("Uspjesno dodan dolazak");
			} else {
				System.out.println("Neuspjesno dodan dolazak");
			}
		} else {
			System.out.println("Dolazak je vec dodan prije.");
		}
	}

	/**
	 * Interrupt.
	 */
	@Override
	public void interrupt() {
		super.interrupt();
	}

}
