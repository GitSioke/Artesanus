package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents an info message coming from a heating step.
 */

public class MessageCommandMasher extends MessageCommand
{
    public MessageCommandMasher(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
