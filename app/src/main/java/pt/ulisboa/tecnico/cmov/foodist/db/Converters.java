package pt.ulisboa.tecnico.cmov.foodist.db;

import androidx.room.TypeConverter;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

public class Converters {
    @TypeConverter
    public static LocalTime stringToLocalTime(String value) {
        return value == null ? null : LocalTime.parse(value);
    }

    @TypeConverter
    public static String localTimeToString(LocalTime localTime) {
        return localTime == null ? null : localTime.toString();
    }

    @TypeConverter
    public static DayOfWeek integerToDayOfWeek(Integer value) {
        return value == null ? null : DayOfWeek.of(value);
    }

    @TypeConverter
    public static Integer dayOfWeekToInteger(DayOfWeek dayOfWeek) {
        return dayOfWeek == null ? null : dayOfWeek.getValue();
    }
}