package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a command message belonging to mashing step
 */

public class MessageMashingCommand extends MessageMashing
{

    public MessageMashingCommand(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
