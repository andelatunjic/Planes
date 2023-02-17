package org.foi.nwtis.atunjic.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.atunjic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * AerodromiDolasciDAO.
 */
public class AerodromiDolasciDAO {

	/**
	 * Spremi dolazak na aerodrom.
	 *
	 * @param konfig konfig
	 * @param dolaziAvion avion
	 * @return true, ako je spremljen
	 */
	public static boolean spremiDolazakNaAerodrom(KonfiguracijaBP konfig, AvionLeti dolaziAvion) {

		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		String upit = "INSERT INTO AERODROMI_DOLASCI (icao24, firstSeen, estDepartureAirport, "
				+ "lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance, "
				+ "estDepartureAirportVertDistance, estArrivalAirportHorizDistance, estArrivalAirportVertDistance, "
				+ "departureAirportCandidatesCount, arrivalAirportCandidatesCount, `stored`) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, dolaziAvion.getIcao24());
				s.setInt(2, dolaziAvion.getFirstSeen());
				s.setString(3, dolaziAvion.getEstDepartureAirport());
				s.setInt(4, dolaziAvion.getLastSeen());
				s.setString(5, dolaziAvion.getEstArrivalAirport());
				s.setString(6, dolaziAvion.getCallsign());
				s.setInt(7, dolaziAvion.getEstDepartureAirportHorizDistance());
				s.setInt(8, dolaziAvion.getEstDepartureAirportVertDistance());
				s.setInt(9, dolaziAvion.getEstArrivalAirportHorizDistance());
				s.setInt(10, dolaziAvion.getEstArrivalAirportVertDistance());
				s.setInt(11, dolaziAvion.getDepartureAirportCandidatesCount());
				s.setInt(12, dolaziAvion.getArrivalAirportCandidatesCount());

				int brojAzuriranja = s.executeUpdate();
				s.close();
				con.close();
				return brojAzuriranja == 1;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromiDolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiDolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Provjera duplikata.
	 *
	 * @param konfig konfig
	 * @param avion avion
	 * @return true, ako postoji duplikat
	 */
	public static boolean provjeraDuplikata(KonfiguracijaBP konfig, AvionLeti avion) {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpKorisnik = konfig.getUserUsername();
		String bpLozinka = konfig.getUserPassword();
		String upit = "SELECT AERODROMI_DOLASCI.icao24, AERODROMI_DOLASCI.lastSeen, AERODROMI_DOLASCI.estArrivalAirport, "
				+ "AERODROMI_DOLASCI.lastSeen "
				+ "FROM AERODROMI_DOLASCI WHERE icao24 = ? AND lastSeen = ? AND estArrivalAirport = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpKorisnik, bpLozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, avion.getIcao24());
				s.setInt(2, avion.getLastSeen());
				s.setString(3, avion.getEstArrivalAirport());

				ResultSet rs = s.executeQuery();

				boolean odgovor = rs.next();
				s.close();
				con.close();
				return odgovor;

			} catch (SQLException ex) {
				Logger.getLogger(AerodromiDolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiDolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
}
