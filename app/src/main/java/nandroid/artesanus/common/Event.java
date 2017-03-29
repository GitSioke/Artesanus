package nandroid.artesanus.common;

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

    private int value;

    private String time;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
