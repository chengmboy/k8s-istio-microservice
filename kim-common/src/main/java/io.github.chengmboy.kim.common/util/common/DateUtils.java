package io.github.chengmboy.kim.common.util.common;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author cheng_mboy
 */
public class DateUtils {

    public static Date toDate(LocalDateTime dateTime) {
        Instant instant = dateTime
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Date.from(instant);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Duration between(Date startInclusive, Date endExclusive) {
        return Duration.between(toLocalDateTime(startInclusive),
                toLocalDateTime(endExclusive));
    }
}
