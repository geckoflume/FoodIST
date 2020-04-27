package pt.ulisboa.tecnico.cmov.foodist.model;

import androidx.annotation.NonNull;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

public interface OpeningHours {
    DayOfWeek getDayOfWeek();
    void setDayOfWeek(@NonNull DayOfWeek dayOfWeek);

    LocalTime getFrom();
    void setFrom(@NonNull LocalTime from);

    LocalTime getTo();
    void setTo(@NonNull LocalTime to);

    int getCafeteriaId();
    void setCafeteriaId(int cafeteriaId);
}
