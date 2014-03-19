package dk.drb.blacktiger.util;

import java.util.regex.Pattern;

/**
 *
 */
public class IpPhoneNumber {

    private static final Pattern IP_PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{4}$");  // 4 digits IP phone numbers
    private static final String IP_PHONE_PREFIX = "IP-";

    private IpPhoneNumber() {
    }

    public static boolean isIpPhoneNumber(String text) {
        if (text == null) {
            return false;
        }
        return IP_PHONE_NUMBER_PATTERN.matcher(text).matches();
    }

    public static String normalize(String number) {
        if (!number.startsWith(IP_PHONE_PREFIX)) {
            number = IP_PHONE_PREFIX + number;
        }
        return number;
    }
}
