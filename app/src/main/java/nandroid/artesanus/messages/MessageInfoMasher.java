package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents an info message belonged to Fermenting step.
 */

public class MessageInfoMasher extends MessageInfo
{
    public MessageInfoMasher(float processId, int priority, Date date, String info)
    {
        super(processId,priority, date, info);
    }
}
