package org.foi.nwtis.atunjic.aplikacija_3.podaci;

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
import org.foi.nwtis.podaci.Korisnik;

public class KorisnikDAO {

	public static List<Korisnik> dohvatiSveKorisnike(KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "SELECT korisnik, ime, prezime, lozinka, email FROM korisnici";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			List<Korisnik> korisnici = new ArrayList<>();

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {

				while (rs.next()) {
					String korisnik1 = rs.getString("korisnik");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String email = rs.getString("email");

					Korisnik k = new Korisnik(korisnik1, ime, prezime, "******", email);
					korisnici.add(k);
				}
				return korisnici;

			} catch (SQLException ex) {
				Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static boolean dodajKorisnika(Korisnik k, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "INSERT INTO korisnici (korisnik, ime, prezime, lozinka, email) " + "VALUES (?, ?, ?, ?, ?)";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, k.getKorIme());
				s.setString(2, k.getIme());
				s.setString(3, k.getPrezime());
				s.setString(4, k.getLozinka());
				s.setString(5, k.getEmail());

				int brojAzuriranja = s.executeUpdate();

				return brojAzuriranja == 1;

			} catch (Exception ex) {
				Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public static Korisnik dohvatiKorisnika(String korisnik, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "SELECT korisnik, ime, prezime, lozinka, email FROM korisnici WHERE korisnik = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, korisnik);

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String korisnik1 = rs.getString("korisnik");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String lozinka = rs.getString("lozinka");
					String email = rs.getString("email");

					Korisnik k = new Korisnik(korisnik1, ime, prezime, lozinka, email);
					return k;
				}

			} catch (SQLException ex) {
				Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static List<Grupa> dohvatiGrupeKorisnika(String korisnik, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "SELECT korisnik, grupa FROM uloge WHERE korisnik = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			List<Grupa> grupe = new ArrayList<>();
			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, korisnik);

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String grupa = rs.getString("grupa");
					String naziv = dohvatiNazivGrupe(grupa, konfig);

					Grupa g = new Grupa(grupa, naziv);
					grupe.add(g);
				}

				return grupe;

			} catch (SQLException ex) {
				Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private static String dohvatiNazivGrupe(String grupa, KonfiguracijaBP konfig) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();
		String upit = "SELECT naziv FROM grupe WHERE grupa = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, grupa);

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String naziv = rs.getString("naziv");
					return naziv;
				}

			} catch (SQLException ex) {
				Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
}
