package org.foi.nwtis.atunjic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;

public class AerodromiPraceniDAO {

	/**
	 * Metoda vraća listu aerodroma koji se prate.
	 *
	 * @param konfig Konfiguracija baze podataka.
	 * @return Lista aerodroma koji se prate.
	 */
	public static List<Aerodrom> dohvatiAerodromeZaPratiti(KonfiguracijaBP konfig) {

		List<Aerodrom> aerodromi = new ArrayList<>();

		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		String upit = "SELECT airports.ident, airports.name, airports.iso_country, airports.coordinates "
				+ "FROM airports INNER JOIN AERODROMI_PRACENI ON " + "airports.ident = AERODROMI_PRACENI.ident";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {

				while (rs.next()) {
					String ICAO = rs.getString("ident");
					String naziv = rs.getString("name");
					String drzava = rs.getString("iso_country");
					String koordinate = rs.getString("coordinates");

					String[] p = koordinate.split(",");
					Lokacija lokacija = new Lokacija(p[0], p[1]);

					Aerodrom aerodrom = new Aerodrom(ICAO, naziv, drzava, lokacija);
					aerodromi.add(aerodrom);
				}

				s.close();
				con.close();
				return aerodromi;

			} catch (SQLException ex) {
				Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Metoda koja dodaje novi aerodrom za pratiti.
	 *
	 * @param icao   Icao novog aerodroma.
	 * @param konfig Konfiguracija baze podataka.
	 * @return true, ako je uspješno dodan aerodrom.
	 */
	public static boolean dodajAerodromZaPratiti(String icao, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpKorisnik = konfig.getUserUsername();
		String bpLozinka = konfig.getUserPassword();
		String upit = "INSERT INTO AERODROMI_PRACENI (ident, `stored`) " + "VALUES (?, now())";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpKorisnik, bpLozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, icao);

				int brojAzuriranja = s.executeUpdate();
				s.close();
				con.close();
				return brojAzuriranja == 1;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Metoda provjerava da li već postoji ICAO aerodroma koji se želi dodati u
	 * tablicu praćenih aerodroma.
	 *
	 * @param konfig Konfiguracija baze podataka.
	 * @param icao   Icao novog aerodroma.
	 * @return true, ako postoji duplikat.
	 */
	public static boolean provjeraDuplikata(KonfiguracijaBP konfig, String icao) {

		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpKorisnik = konfig.getUserUsername();
		String bpLozinka = konfig.getUserPassword();
		String upit = "SELECT AERODROMI_PRACENI.ident " + "FROM AERODROMI_PRACENI WHERE ident = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpKorisnik, bpLozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, icao);

				ResultSet rs = s.executeQuery();
				boolean odgovor = rs.next();
				s.close();
				con.close();
				return odgovor;

			} catch (SQLException ex) {
				Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
}
