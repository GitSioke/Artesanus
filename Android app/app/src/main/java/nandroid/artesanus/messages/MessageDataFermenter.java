package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a message belonging to mashing step.
 */

public class MessageDataFermenter extends MessageData
{
    public MessageDataFermenter(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
