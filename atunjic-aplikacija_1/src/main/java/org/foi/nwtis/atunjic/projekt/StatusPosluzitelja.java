package org.foi.nwtis.atunjic.projekt;

/**
 * Enum Status posluzitelja.
 */
public enum StatusPosluzitelja {
	
	hibernira(0), 

	inicijaliziran(1), 

	aktivan(2);

    /** Vrijednost statusa. */
    private final int vrijednost;
    
    /**
     * Konstruktor.
     *
     * @param vrijednost vrijednost
     */
    private StatusPosluzitelja(int vrijednost) {
        this.vrijednost = vrijednost;
    }

    /**
     * Daj vrijednost.
     *
     * @return int vrijednost statusa
     */
    public int dajVrijednost() {
        return vrijednost;
    }
}
