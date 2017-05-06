package nandroid.artesanus.common;

import java.util.List;

/**
 * This class represents a BrewProcess inside a Beer Crafting
 */

public class BrewProcess
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




    public static class Builder {

        private int id;

        private int id_brew;

        private String type;

        private long startTime;

        private long endTime;

        private List<Event> events;

        public Builder events(List<Event> events) {
            this.events = events;
            return this;
        }

        public Builder endTime(long endTime)
        {
            this.endTime = endTime;
            return this;
        }

        public Builder startTime(long startTime)
        {
            this.startTime = startTime;
            return this;
        }

        public Builder type(String type)
        {
            this.type = type;
            return this;
        }

        public Builder id_brew(int id_brew)
        {
            this.id_brew = id_brew;
            return this;
        }

        public Builder id(int id)
        {
            this.id = id;
            return this;
        }

        public BrewProcess build()
        {
            return new BrewProcess(this);
        }
    }
    private BrewProcess(Builder builder)
    {
        id = builder.id;
        id_brew = builder.id_brew;
        type = builder.type;
        startTime = builder.startTime;
        endTime = builder.endTime;
        events = builder.events;
    }
}
