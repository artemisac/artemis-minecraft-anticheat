package ac.artemis.core.v4.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * @apiNote Feel free to change this format at any given time. Keep in mind it will slowly change the MySQL ones.
     * However, if you change it without properly migrating, you'll most likely have a parsing exception due to the formats
     * being different
     */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    /**
     * @param date Input the string which you need the date to be parsed from using the above format
     * @return Date which corresponds to the string with the format above
     * @apiNote Use this wisely. It's not the most standard form to do it. If you want to change the format, don't
     * change this but instead the format above
     */
    public static Date toDate(String date) {
        try {
            return FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param date Input the date to format it to a string.
     * @return The string formatted date
     * @apiNote I use this function to be able to serialize dates without making the size too large. This formula is
     * stupid simple. Don't modify it if you wish to change the format.
     */
    public static String fromDate(Date date) {
        return FORMAT.format(date);
    }
}
