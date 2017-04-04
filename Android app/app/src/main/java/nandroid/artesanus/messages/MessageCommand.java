package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a message created in the heating step.
 */

public class MessageCommand extends Message
{
    public MessageCommand(float processId, int priority, Date date) {
        super(processId, priority, date);
    }
}
