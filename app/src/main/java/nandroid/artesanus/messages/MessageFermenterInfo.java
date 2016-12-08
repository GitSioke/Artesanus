package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents an info message belonged to Fermenting step.
 */

public class MessageFermenterInfo extends MessageFermenter
{
    public MessageFermenterInfo(float processId, int priority, Date date)
    {
        super(processId,priority, date);
    }
}
