package hg.utils;

/** Exception thrown for absurd scenarios that can only be explained by poor coding skills. */
public class BadCoderException extends RuntimeException {
    public BadCoderException(String msg) {
        super(msg);
    }
}
