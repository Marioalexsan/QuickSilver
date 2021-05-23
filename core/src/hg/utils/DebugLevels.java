package hg.utils;

/** Holds debug levels for ChatSystem.
 * ChatSystem will output messages that have a debug level equal or lower than the current one. */
public class DebugLevels {
    public static final int Fatal = -1;
    public static final int Error = 0;
    public static final int Warn = 1;
    public static final int Info = 2;

    public static final int WORST = Fatal;
    public static final int DEFAULT = Error;
    public static final int ALL = Info;
}
