import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities  {

    private static final String IPADDRESS_PATTERN =
                    "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static boolean verify_ip(String ip) {
        Pattern pattern = Pattern.compile(Utilities.IPADDRESS_PATTERN);

        if(ip == null || ip.isEmpty()) {
            return false;
        }

        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches()) {
            return true;
        }
        // Might be IPV6 but ... its ok
        return false;
    }

    public static boolean verify_port(String port) {
        try {
            Integer.parseInt(port);
            return !(Integer.parseInt(port) < 0 && Integer.parseInt(port) > 65535);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
