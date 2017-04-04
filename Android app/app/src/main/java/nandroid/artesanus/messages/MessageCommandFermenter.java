package nandroid.artesanus.messages;

import java.util.Date;

/**
 *  This class represents a data message coming from a heating step.
 */

public class MessageCommandFermenter extends MessageCommand
{
    public MessageCommandFermenter(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
