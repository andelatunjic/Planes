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
 * AerodromiPolasciDAO.
 */
public class AerodromiPolasciDAO {

	/**
	 * Spremi polazak S aerodroma.
	 *
	 * @param konfig konfig
	 * @param polaziAvion polazi avion
	 * @return true, ako je spremljen
	 */
	public static boolean spremiPolazakSAerodroma(KonfiguracijaBP konfig, AvionLeti polaziAvion) {

		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		String upit = "INSERT INTO AERODROMI_POLASCI (icao24, firstSeen, estDepartureAirport, "
				+ "lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance, "
				+ "estDepartureAirportVertDistance, estArrivalAirportHorizDistance, estArrivalAirportVertDistance, "
				+ "departureAirportCandidatesCount, arrivalAirportCandidatesCount, `stored`) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, polaziAvion.getIcao24());
				s.setInt(2, polaziAvion.getFirstSeen());
				s.setString(3, polaziAvion.getEstDepartureAirport());
				s.setInt(4, polaziAvion.getLastSeen());
				s.setString(5, polaziAvion.getEstArrivalAirport());
				s.setString(6, polaziAvion.getCallsign());
				s.setInt(7, polaziAvion.getEstDepartureAirportHorizDistance());
				s.setInt(8, polaziAvion.getEstDepartureAirportVertDistance());
				s.setInt(9, polaziAvion.getEstArrivalAirportHorizDistance());
				s.setInt(10, polaziAvion.getEstArrivalAirportVertDistance());
				s.setInt(11, polaziAvion.getDepartureAirportCandidatesCount());
				s.setInt(12, polaziAvion.getArrivalAirportCandidatesCount());

				int brojAzuriranja = s.executeUpdate();
				s.close();
				con.close();
				return brojAzuriranja == 1;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromiPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
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
		String upit = "SELECT AERODROMI_POLASCI.icao24, AERODROMI_POLASCI.firstSeen, AERODROMI_POLASCI.estDepartureAirport, "
				+ "AERODROMI_POLASCI.lastSeen "
				+ "FROM AERODROMI_POLASCI WHERE icao24 = ? AND firstSeen = ? AND estDepartureAirport = ?";

		try {
			Class.forName(konfig.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpKorisnik, bpLozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, avion.getIcao24());
				s.setInt(2, avion.getFirstSeen());
				s.setString(3, avion.getEstDepartureAirport());

				ResultSet rs = s.executeQuery();
				boolean odgovor = rs.next();
				s.close();
				con.close();
				return odgovor;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromiPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

}
