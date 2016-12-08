package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a Message created from Mashing step.
 */

public class MessageMashing extends Message
{
    public MessageMashing(float processId, int priority, Date date) {
        super(processId, priority, date);
    }
}
