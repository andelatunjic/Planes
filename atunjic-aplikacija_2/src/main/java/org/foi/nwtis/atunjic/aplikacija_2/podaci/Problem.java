package org.foi.nwtis.atunjic.aplikacija_2.podaci;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;

/**
 * Problem.
 */
public class Problem {

	/**
	 * Konstruktor.
	 *
	 * @param icao icao aerodroma
	 * @param opis opis problema
	 */
	public Problem(@NonNull String icao, @NonNull String opis) {
		super();
		this.icao = icao;
		this.opis = opis;

	}

	/**
	 * Gets the icao.
	 *
	 * @return the icao
	 */
	@Getter

	/**
	 * Sets the icao.
	 *
	 * @param icao the new icao
	 */
	@Setter
	@NonNull
	private String icao;

	/**
	 * Gets the opis.
	 *
	 * @return the opis
	 */
	@Getter

	/**
	 * Sets the opis.
	 *
	 * @param opis the new opis
	 */
	@Setter
	@NonNull
	private String opis;
}
