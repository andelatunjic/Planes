package org.foi.nwtis.atunjic.aplikacija_5.wsock;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Klasa Info.
 */
@ServerEndpoint("/info")
public class Info {

	/** Kolekcija sesija. */
	private static Set<Session> sesije = new HashSet<Session>();

	/**
	 * Otvora se sesija.
	 *
	 * @param sesija sesija
	 * @param konfig konfig.
	 */
	@OnOpen
	public void otvori(Session sesija, EndpointConfig konfig) {
		sesije.add(sesija);
		System.out.println("Veza otvorena: " + sesija.getId());
	}

	/**
	 * Zatvora se sesija.
	 *
	 * @param sesija sesija
	 * @param razlog razlog zatvaranja.
	 */
	@OnClose
	public void zatvori(Session sesija, CloseReason razlog) {
		sesije.remove(sesija);
		System.out.println("Veza zatvorena: " + sesija.getId() + " razglo: " + razlog.getReasonPhrase());
	}

	/**
	 * Stigla poruka.
	 *
	 * @param sesija sesija
	 * @param poruka poruka
	 */
	@OnMessage
	public void stiglaPoruka(Session sesija, String poruka) {
		System.out.println("Veza: " + sesija.getId() + " poruka: " + poruka);
	}

	/**
	 * Greska.
	 *
	 * @param sesija sesija
	 * @param greska greska
	 */
	@OnError
	public void greska(Session sesija, Throwable greska) {
		System.out.println("Veza: " + sesija.getId() + " greska: " + greska.getMessage());
	}

	/**
	 * Informiraj.
	 *
	 * @param poruka poruka
	 */
	public static void informiraj(String poruka) {
		for (Session sesija : sesije) {
			if (sesija.isOpen()) {
				try {
					sesija.getBasicRemote().sendText(poruka);
				} catch (IOException e) {
					System.out.println("Veza greska: " + sesija.getId() + " " + e.getMessage());
				}
			}
		}
	}
}
