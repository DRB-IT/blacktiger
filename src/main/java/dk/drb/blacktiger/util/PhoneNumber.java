package dk.drb.blacktiger.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 *
 * @author michael
 */
public class PhoneNumber {

    private static final Logger LOG = LoggerFactory.getLogger(PhoneNumber.class);
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\+?\\d{2,15}$");
    private static final String DEFAULT_COUNTRY_CODE = "+45";
    private static final PhoneNumberUtil.PhoneNumberFormat FORMAT = PhoneNumberUtil.PhoneNumberFormat.E164;

    public static String normalize(String number, String region) {
        Assert.hasLength(number, "Number is null or empty");
        Phonenumber.PhoneNumber parseNumber;
        try {
            parseNumber = PhoneNumberUtil.getInstance().parse(number, region);
            return PhoneNumberUtil.getInstance().format(parseNumber, FORMAT);
        } catch (NumberParseException ex) {
            LOG.info("Unable to parse number. [" + number + "]", ex);
            return number;
        }
        
        
    }

    public static boolean isPhoneNumber(String text, String region) {
        return PHONE_NUMBER_PATTERN.matcher(text).matches();
    }
}
