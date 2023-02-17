package org.foi.nwtis.atunjic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
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

public class AerodromiDAO {

	public static List<Aerodrom> dohvatiAerodrome(KonfiguracijaBP konfig) {

		List<Aerodrom> aerodromi = new ArrayList<>();

		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		String upit = "SELECT airports.ident, airports.name, airports.iso_country, airports.coordinates "
				+ "FROM airports";

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
				Logger.getLogger(AerodromiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
}
