package gov.socialdev.nic.util;

import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DateUtil {

    public static String normalizeDate(String inputDate) {

        if (inputDate == null || inputDate.isBlank()) {
            return null;
        }

        // Drop any trailing time-of-day/timezone junk, regardless of which of the
        // supported date shapes (yyyy-MM-dd, yyyy/MM/dd, dd-MM-yyyy, dd/MM/yyyy) it's
        // attached to and whether or not it's separated by "T"/space, e.g.
        // "1990-01-01T00:00:00.000Z", "01/01/1990 00:00:00" and "01-01-1990Z" all become
        // just the date portion.
        String trimmedDate = inputDate.trim();
        java.util.regex.Matcher dateShapeMatcher = java.util.regex.Pattern
                .compile("^(\\d{4}[-/]\\d{2}[-/]\\d{2}|\\d{2}[-/]\\d{2}[-/]\\d{4})")
                .matcher(trimmedDate);
        if (dateShapeMatcher.find()) {
            trimmedDate = dateShapeMatcher.group();
        } else {
            trimmedDate = trimmedDate.split("[T ]", 2)[0];
        }

        List<DateTimeFormatter> hijriFormatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd").withChronology(HijrahChronology.INSTANCE),
                DateTimeFormatter.ofPattern("yyyy/MM/dd").withChronology(HijrahChronology.INSTANCE),
                DateTimeFormatter.ofPattern("dd-MM-yyyy").withChronology(HijrahChronology.INSTANCE),
                DateTimeFormatter.ofPattern("dd/MM/yyyy").withChronology(HijrahChronology.INSTANCE)
        );

        List<DateTimeFormatter> gregorianFormatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Try Hijri first
        for (DateTimeFormatter formatter : hijriFormatters) {
            try {
                HijrahDate hijrahDate = HijrahDate.from(formatter.parse(trimmedDate));
                // Format via LocalDate to use ISO chronology for output
                return outputFormatter.format(LocalDate.from(hijrahDate));
            } catch (DateTimeParseException ignored) {
            }
        }

        // Fall back to Gregorian
        for (DateTimeFormatter formatter : gregorianFormatters) {
            try {
                LocalDate gregorianDate = LocalDate.parse(trimmedDate, formatter);
                return outputFormatter.format(gregorianDate);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Unable to parse date (Hijri or Gregorian): " + inputDate);
    }
}
