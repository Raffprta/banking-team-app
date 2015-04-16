package uk.ac.ncl.team19.lloydsapp.api.datatypes;

/**
 * @author Dale Whinham
 *
 * A helper class to store secure characters as an object. These can then be packed into JSON.
 */
public class SecureChar {
    private final int index;
    private final char character;

    public SecureChar(int index, char character) {
        this.index = index;
        this.character = character;
    }
}