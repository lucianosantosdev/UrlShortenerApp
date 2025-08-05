package android.util;

public class Log {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    public static boolean isLoggable(String tag, int level) {
        return true;
    }

    public static int v(String tag, String msg) {
        return println(VERBOSE, tag, msg);
    }

    public static int d(String tag, String msg) {
        return println(DEBUG, tag, msg);
    }

    public static int i(String tag, String msg) {
        return println(INFO, tag, msg);
    }

    public static int w(String tag, String msg) {
        return println(WARN, tag, msg);
    }

    public static int e(String tag, String msg) {
        return println(ERROR, tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return println(ERROR, tag, msg);
    }

    public static int wtf(String tag, String msg) {
        return println(ASSERT, tag, msg);
    }

    public static int println(int priority, String tag, String msg) {
        // Simple stub: print to System.out or System.err
        String level;
        switch (priority) {
            case VERBOSE:
                level = "V";
                break;
            case DEBUG:
                level = "D";
                break;
            case INFO:
                level = "I";
                break;
            case WARN:
                level = "W";
                break;
            case ERROR:
                level = "E";
                break;
            case ASSERT:
                level = "A";
                break;
            default:
                level = "?";
                break;
        }
        System.out.println(level + "/" + tag + ": " + msg);
        return 0;
    }
}