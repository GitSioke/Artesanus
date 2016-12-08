package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents an info message coming from a heating step.
 */

public class MessageHeatingInfo extends MessageHeating
{
    public MessageHeatingInfo(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }
}
