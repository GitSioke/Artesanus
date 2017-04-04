package nandroid.artesanus.common;

import java.util.Date;

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
}
