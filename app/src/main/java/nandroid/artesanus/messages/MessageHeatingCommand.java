package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a command message belonged to a heating step.
 */

public class MessageHeatingCommand extends MessageHeating
{
    public MessageHeatingCommand(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
