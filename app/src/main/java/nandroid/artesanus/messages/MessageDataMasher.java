package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a command message belonging to mashing step
 */

public class MessageDataMasher extends MessageData
{

    public MessageDataMasher(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
