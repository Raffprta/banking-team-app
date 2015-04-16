package uk.ac.ncl.team19.lloydsapp.api.datatypes;

/**
 * Created by Dale Whinham on 25/03/15.
 */
public class SecureChar {
    private final int index;
    private final char character;

    public SecureChar(int index, char character) {
        this.index = index;
        this.character = character;
    }
}