package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a message belonging to mashing step.
 */

public class MessageMashingInfo extends MessageMashing
{
    public MessageMashingInfo(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
