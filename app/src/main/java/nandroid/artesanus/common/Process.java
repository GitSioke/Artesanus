package nandroid.artesanus.common;

import java.util.List;

/**
 * This class represents a Process inside a Beer Crafting
 */

public class Process
{
    private int id;

    private int id_brew;

    private String type;

    private long startTime;

    private long endTime;

    private List<Event> events;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_brew() {
        return id_brew;
    }

    public void setId_brew(int id_brew) {
        this.id_brew = id_brew;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
