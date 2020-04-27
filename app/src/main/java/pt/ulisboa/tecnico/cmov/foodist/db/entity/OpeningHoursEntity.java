package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import pt.ulisboa.tecnico.cmov.foodist.model.OpeningHours;

public class OpeningHoursEntity implements OpeningHours {
    private DayOfWeek dayOfWeek;
    private LocalTime from;
    private LocalTime to;
    private int cafeteriaId;

    @Override
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public LocalTime getFrom() {
        return from;
    }

    @Override
    public void setFrom(LocalTime from) {
        this.from = from;
    }

    @Override
    public LocalTime getTo() {
        return to;
    }

    @Override
    public void setTo(LocalTime to) {
        this.to = to;
    }

    @Override
    public int getCafeteriaId() {
        return cafeteriaId;
    }

    @Override
    public void setCafeteriaId(int cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }
}
