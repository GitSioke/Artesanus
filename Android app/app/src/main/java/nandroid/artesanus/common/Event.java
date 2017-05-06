package nandroid.artesanus.common;

import java.util.Date;
import java.util.List;

/**
 * This class represents all events that happen inside a process
 */
public class Event
{
    private int id;

    private int id_process;

    private String type;

    private String message;

    private String data;

    private double value;

    private Date time;

    private String source;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_process() {
        return id_process;
    }

    public void setId_process(int id_process) {
        this.id_process = id_process;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setSource(String source) {this.source = source;}

    public String getSource(){return this.source;}

    public static class Builder {

        private int id;

        private int id_process;

        private String type;

        private String message;

        private String data;

        private double value;

        private Date time;

        private String source;


        public Builder message(String message)
        {
            this.message = message;
            return this;
        }

        public Builder data(String data)
        {
            this.data = data;
            return this;
        }

        public Builder time(Date time)
        {
            this.time = time;
            return this;
        }

        public Builder value(Double value)
        {
            this.value = value;
            return this;
        }

        public Builder source(String source)
        {
            this.source = source;
            return this;
        }


        public Builder type(String type)
        {
            this.type = type;
            return this;
        }

        public Builder id_process(int id_process)
        {
            this.id_process = id_process;
            return this;
        }

        public Builder id(int id)
        {
            this.id = id;
            return this;
        }

        public Event build()
        {
            return new Event(this);
        }
    }
    private Event(Builder builder)
    {
        id = builder.id;
        id_process = builder.id_process;
        type = builder.type;
        source = builder.source;
        time = builder.time;
        value = builder.value;
        message = builder.message;
        data = builder.data;
    }

}
