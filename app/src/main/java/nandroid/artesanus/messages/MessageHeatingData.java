package nandroid.artesanus.messages;

import java.util.Date;

/**
 *  This class represents a data message coming from a heating step.
 */

public class MessageHeatingData extends MessageHeating
{
    public MessageHeatingData(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
