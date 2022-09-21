package utils;

/**
 * This class is responsible for any utility methods the application may use, but are not tied to a specific pages functionality and are used throughout the program.
 */
public class Utils {

    /**
     * This method checks if a given String is a whole number i.e. contains no other characters other than numbers.
     * @param s The string to check whether it is a number or not.
     * @return Returns true for a String s that is a number, false if it contains any characters other than numbers.
     */
    public static boolean isNumber(String s) {

        for (int i = 0; i < s.length(); i++) {

            if (!Character.isDigit(s.charAt(i))) return false;

        }

        return true;
    }

}
