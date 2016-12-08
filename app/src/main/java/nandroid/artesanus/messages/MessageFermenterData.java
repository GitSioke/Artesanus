package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a data message belonging to fermenting step.
 */

public class MessageFermenterData extends MessageFermenter
{
    public MessageFermenterData(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
