/*
package org.foi.nwtis.atunjic.zadaca_1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.foi.nwtis.atunjic.zadaca_1.ServerGlavni;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerGlavniTest {
	ServerGlavni testniObjekt = null;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		testniObjekt = new ServerGlavni(8003, 10);
	}

	@AfterEach
	void tearDown() throws Exception {
		testniObjekt = null;
	}

	@Test
	void testMain() {
	}

	@Test
	void testUcitajKonfiguraciju() {
		assertNull(ServerGlavni.konfig);
		boolean odgovor = ServerGlavni.ucitajKonfiguraciju("NWTiS_atunjic_1.txt");
		assertNotNull(ServerGlavni.konfig);
		boolean ocekujem = true;
		assertEquals(ocekujem, odgovor);
		assertNotEquals(0, ServerGlavni.konfig.dajSvePostavke().size());
		int portOdgovor = Integer.parseInt(ServerGlavni.konfig.dajPostavku("port"));
		int portOcekivano = 8000;
		assertEquals(portOcekivano, portOdgovor);
	}

	@Test
	void testServerGlavni() {
	}

	@Test
	void testPripremiKorisnike() {
		assertEquals(0, testniObjekt.getKorisnici().size());
		testniObjekt.pripremiKorisnike("korisnici.csv");
		assertNotEquals(0, testniObjekt.getKorisnici().size());

	}

	@Test
	void testObradaZahtjeva() {
		boolean odgovorKonfig = ServerGlavni.ucitajKonfiguraciju("NWTiS_atunjic_4.txt");
		assertNotNull(ServerGlavni.konfig);
		
		PomocnaDretva pd = new PomocnaDretva();
		pd.start();
		String komanda = "METEO LDZA";
		String odgovor = null;

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try (Socket veza = new Socket("localhost", 8003);
				InputStreamReader isr = new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8"));
				OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), Charset.forName("UTF-8"));) {

			osw.write(komanda);
			osw.flush();
			veza.shutdownOutput();
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			veza.shutdownInput();
			veza.close();
			odgovor = tekst.toString();
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println(odgovor);
		assertEquals("OK ", odgovor.substring(0, 3));
	}

	@Test
	void testIzvrsiNaredbu() {
	}

	public class PomocnaDretva extends Thread {

		@Override
		public synchronized void start() {
			super.start();
		}

		@Override
		public void run() {
			testniObjekt.obradaZahtjeva();
		}

		@Override
		public boolean isInterrupted() {
			return super.isInterrupted();
		}

	}
}
*/