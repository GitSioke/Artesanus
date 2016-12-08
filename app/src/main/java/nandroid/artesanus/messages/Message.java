package nandroid.artesanus.messages;

import java.util.Calendar;
import java.util.Date;

/**
 * This class represents a Message sent to this device
 */

public class  Message implements IMessage
{

    private Date date; //Date when this message was created
    private float processId; // Id brew process where message belongs to
    private int priority; // Priority of this message

    public Message(float processId, int priority, Date date)
    {
        this.processId = processId;
        this.date = date;
        this.priority = priority;
    }

    @Override
    public Date getDate() {
        return this.date;
    }


    @Override
    public float getProcess() {
        return this.processId;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }


}
