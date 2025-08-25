package com.kenyajug.core;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
public class DateTimeManager {
    public static final DateTimeFormatter ZONED_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    public static final ZoneId UTC_TIME_ZONE_ID = ZoneId.of("UTC");
    /**
     * Parses a date-time string with a time zone into a {@link LocalDateTime} in UTC.
     * <p>
     * The input string must match the {@link #ZONED_DATE_TIME_FORMATTER} pattern
     * ({@code yyyy-MM-dd HH:mm:ss z}).
     * </p>
     *
     * @param timestampText the date-time string to parse (including a zone, e.g. {@code "2025-08-25 10:15:30 UTC"})
     * @return the parsed {@link LocalDateTime} in UTC
     * @throws java.time.format.DateTimeParseException if the text cannot be parsed
     */
    public static LocalDateTime dateTextToUTCTimestamp(String timestampText) {
        var temporalValue = ZONED_DATE_TIME_FORMATTER.parse(timestampText);
        return LocalDateTime.from(temporalValue);
    }
    /**
     * Converts the given {@link ZonedDateTime} to a formatted UTC string using
     * {@link #ZONED_DATE_TIME_FORMATTER}.
     *
     * @param zonedDateTime the {@link ZonedDateTime} to convert
     * @return the UTC time as a formatted string
     */
    public static String convertUTCZonedDateTimeToString(ZonedDateTime zonedDateTime) {
        var _zonedDateTime = zonedDateTime.withZoneSameInstant(UTC_TIME_ZONE_ID);
        return _zonedDateTime.format(ZONED_DATE_TIME_FORMATTER);
    }
    /**
     * Converts a {@link LocalDateTime} in UTC to a formatted UTC string using
     * {@link #convertUTCZonedDateTimeToString(ZonedDateTime)}.
     *
     * @param timestamp the {@link LocalDateTime} in UTC to format
     * @return the UTC time as a formatted string
     */
    public static String timestampToUTCText(LocalDateTime timestamp) {
        var zonedDateTime = ZonedDateTime.of(timestamp, ZoneId.of("UTC"));
        return convertUTCZonedDateTimeToString(zonedDateTime);
    }
}
