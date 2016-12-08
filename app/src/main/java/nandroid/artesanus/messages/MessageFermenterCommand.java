package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a CommandMessage created from Fermenting step.
 */

public class MessageFermenterCommand extends MessageFermenter
{
    public MessageFermenterCommand(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
