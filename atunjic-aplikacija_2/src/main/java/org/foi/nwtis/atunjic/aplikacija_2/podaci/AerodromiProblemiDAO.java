package org.foi.nwtis.atunjic.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;

// TODO: Auto-generated Javadoc
/**
 * The Class AerodromiProblemiDAO.
 */
public class AerodromiProblemiDAO {

	/**
	 * Spremi problem.
	 *
	 * @param problem problem
	 * @param konfig konfig
	 * @return true, ako je spremljen
	 */
	public static boolean spremiProblem(Problem problem, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpKorisnik = konfig.getUserUsername();
		String bpLozinka = konfig.getUserPassword();

		String upit = "INSERT INTO AERODROMI_PROBLEMI (ident, description, `stored`) " + "VALUES (?, ?, now())";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpKorisnik, bpLozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, problem.getIcao());
				s.setString(2, problem.getOpis());

				int brojAzuriranja = s.executeUpdate();
				s.close();
				con.close();
				return brojAzuriranja == 1;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Provjera duplikata.
	 *
	 * @param konfig konfig
	 * @param noviProblem novi problem
	 * @return true, ako postoji duplikat
	 */
	public static boolean provjeraDuplikata(KonfiguracijaBP konfig, Problem noviProblem) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpKorisnik = konfig.getUserUsername();
		String bpLozinka = konfig.getUserPassword();
		String upit = "SELECT AERODROMI_PROBLEMI.ident, AERODROMI_PROBLEMI.description "
				+ "FROM AERODROMI_PROBLEMI WHERE ident = ? AND description = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpKorisnik, bpLozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, noviProblem.getIcao());
				s.setString(2, noviProblem.getOpis());

				ResultSet rs = s.executeQuery();

				boolean odgovor = rs.next();
				s.close();
				con.close();
				return odgovor;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Dohvati probleme.
	 *
	 * @param konfig konfig
	 * @return list problemi
	 */
	public static List<Problem> dohvatiProbleme(KonfiguracijaBP konfig) {

		List<Problem> problemi = new ArrayList<>();

		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		String upit = "SELECT AERODROMI_PROBLEMI.ident, AERODROMI_PROBLEMI.description " + "FROM AERODROMI_PROBLEMI";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {

				while (rs.next()) {
					String ICAO = rs.getString("ident");
					String opis = rs.getString("description");

					Problem problem = new Problem(ICAO, opis);

					problemi.add(problem);
				}
				s.close();
				con.close();
				return problemi;

			} catch (SQLException ex) {
				Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Spremi problem aerodroma.
	 *
	 * @param konfig konfig
	 * @param icao icao
	 * @param polazak polazak
	 */
	public static void spremiProblemAerodroma(KonfiguracijaBP konfig, String icao, Boolean polazak) {
		String opis = "";

		if (polazak)
			opis = "Ne postoje polasci za ovaj aerodrom.";
		else
			opis = "Ne postoje dolasci za ovaj aerodrom.";

		Problem noviProblem = new Problem(icao, opis);
		if (!AerodromiProblemiDAO.provjeraDuplikata(konfig, noviProblem)) {
			if (AerodromiProblemiDAO.spremiProblem(noviProblem, konfig)) {
				System.out.println("Uspjesno dodan problem");
			} else {
				System.out.println("Neuspjesno dodan problem, moguce da se icao ne nalazi u tablici airports.");
			}
		} else {
			System.out.println("Problem već postoji.");
		}
	}

}
