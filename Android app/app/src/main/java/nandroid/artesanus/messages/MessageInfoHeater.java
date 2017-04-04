package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a data message belonging to fermenting step.
 */

public class MessageInfoHeater extends MessageInfo
{
    public MessageInfoHeater(float processId, int priority, Date date,String info)
    {
        super(processId, priority, date, info);
    }
}
