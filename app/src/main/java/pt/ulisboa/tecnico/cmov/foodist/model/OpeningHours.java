package pt.ulisboa.tecnico.cmov.foodist.model;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

public interface OpeningHours {
    int getId();

    void setId(int id);

    DayOfWeek getDayOfWeek();

    void setDayOfWeek(DayOfWeek dayOfWeek);

    LocalTime getFromTime();

    void setFromTime(LocalTime fromTime);

    LocalTime getToTime();

    void setToTime(LocalTime toTime);

    int getCafeteriaId();

    void setCafeteriaId(int cafeteriaId);

    int getStatus();

    void setStatus(int status);
}
