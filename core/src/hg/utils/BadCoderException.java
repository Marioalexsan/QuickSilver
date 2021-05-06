package hg.utils;

public class BadCoderException extends RuntimeException {

    /** As the name implies, BadCoderException is thrown where I don't know how to handle stuff (yet).
     * These should get replaced later on with proper behavior.
     */
    public BadCoderException(String msg) {
        super(msg);
    }
}
