package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a command message belonged to a heating step.
 */

public class MessageCommandHeater extends MessageCommand
{
    public MessageCommandHeater(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
