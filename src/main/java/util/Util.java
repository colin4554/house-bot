package util;

public class Util {

    public static int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isHouseManagerId(String userId) {
        return Constants.getHouseManagerIds().contains(userId);
    }

    public static String fixTimeString(String time) {
        if (time.split(":")[0].length() == 1) {
            return 0 + time;
        }

        return time;
    }
}
