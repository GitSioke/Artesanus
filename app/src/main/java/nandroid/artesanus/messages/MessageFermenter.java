package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a Message from Fermenting step.
 */

public class MessageFermenter extends Message
{
    public MessageFermenter(float processId, int priority, Date date) {
        super(processId, priority, date);
    }
}
