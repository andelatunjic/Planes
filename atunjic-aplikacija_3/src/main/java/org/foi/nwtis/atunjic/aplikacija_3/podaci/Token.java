package org.foi.nwtis.atunjic.aplikacija_3.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor()
public class Token {

	@Getter
	@Setter
	int token;

	@Getter
	@Setter
	int trajanje;

	@Getter
	@Setter
	String korisnik;

	@Getter
	@Setter
	String status;
}
