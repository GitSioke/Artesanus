package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a CommandMessage created from Fermenting step.
 */

public class MessageInfoFermenter extends MessageInfo
{
    public MessageInfoFermenter(float processId, int priority, Date date, String info)
    {
        super(processId, priority, date, info);
    }
}
