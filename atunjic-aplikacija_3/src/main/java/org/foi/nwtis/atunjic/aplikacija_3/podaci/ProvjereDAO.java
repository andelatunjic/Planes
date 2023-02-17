package org.foi.nwtis.atunjic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;

public class ProvjereDAO {

	public static String provjeriValjanostTokena(int token, String korimeIzZahtjeva, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "SELECT token, korisnik, trajanje, status FROM tokeni WHERE token = ? AND korisnik = ?";
		String odgovor = "nijeOdKorisnika";
		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setInt(1, token);
				s.setString(2, korimeIzZahtjeva);

				ResultSet rs = s.executeQuery();

				Token t = null;
				while (rs.next()) {
					int tok = rs.getInt("token");
					int trajanje = rs.getInt("trajanje");
					String korisnik = rs.getString("korisnik");
					String status = rs.getString("status");

					t = new Token(tok, trajanje, korisnik, status);
				}

				if (t == null) {
					return "nijeOdKorisnika";
				} else {
					long sec = t.getTrajanje();
					long datum = sec * 1000;
					if (datum < System.currentTimeMillis() || t.getStatus().compareTo("0") == 0) {
						return "istekloVrijeme";
					} else
						return "aktivan";
				}

			} catch (SQLException ex) {
				Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return odgovor;
	}

	public static boolean obrisiToken(int token, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "UPDATE tokeni SET status = ? WHERE token = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, "0");
				s.setInt(2, token);

				int brojAzuriranja = s.executeUpdate();

				return brojAzuriranja == 1;

			} catch (SQLException ex) {
				Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public static void brisiToken(int token, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "UPDATE tokeni SET status = ? WHERE token = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, "0");
				s.setInt(2, token);

				int brojAzuriranja = s.executeUpdate();

			} catch (SQLException ex) {
				Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static String obrisiTokeneKorisnika(String korisnik, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "SELECT token, korisnik, trajanje, status FROM tokeni WHERE korisnik = ? AND status = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, korisnik);
				s.setString(2, "1");

				ResultSet rs = s.executeQuery();

				List<Token> tokeniKorisnika = new ArrayList<>();
				while (rs.next()) {
					int tok = rs.getInt("token");
					int trajanje = rs.getInt("trajanje");
					String korIme = rs.getString("korisnik");
					String status = rs.getString("status");

					Token noviToken = new Token(tok, trajanje, korIme, status);
					tokeniKorisnika.add(noviToken);
				}

				if (tokeniKorisnika.isEmpty() == true) {
					return "korisnikNemaAktivanZeton";
				} else {
					for (Token t : tokeniKorisnika) {
						brisiToken(t.getToken(), konfig);
					}
					return "obrisaniTokeni";
				}

			} catch (SQLException ex) {
				Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static Token dohvatiZadnjiToken(KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "SELECT token, trajanje, korisnik, status FROM tokeni ORDER BY token DESC LIMIT 1";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					int token = rs.getInt("token");
					int trajanje = rs.getInt("trajanje");
					String korisnik = rs.getString("korisnik");
					String status = rs.getString("status");

					Token t = new Token(token, trajanje, korisnik, status);
					return t;
				}

			} catch (SQLException ex) {
				Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static boolean spremiToken(long vrijemeDoIstekaTokena, String korIme, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		String upit = "INSERT INTO tokeni (trajanje, korisnik, status) " + "VALUES (?, ?, ?)";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				String trajanje = String.valueOf(vrijemeDoIstekaTokena);
				s.setInt(1, Integer.parseInt(trajanje));
				s.setString(2, korIme);
				s.setString(3, "1");

				int brojAzuriranja = s.executeUpdate();
				return brojAzuriranja == 1;
			} catch (SQLException ex) {
				Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ProvjereDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

}
