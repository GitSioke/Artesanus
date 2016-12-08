package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class implements a factory of info messages which belong to a concrete brew crafting step.
 */

public class MessageInfoFactory implements MessagesFactory
{
    @Override
    public MessageHeating createMessageHeating(float processId, int priority, Date date)
    {
        return new MessageHeatingInfo(processId, priority, date);
    }

    @Override
    public MessageFermenter createMessageFermenter(float processId, int priority, Date date) {
        return new MessageFermenterInfo(processId, priority, date);
    }

    @Override
    public MessageMashing createMessageMashing(float processId, int priority, Date date) {
        return new MessageMashingInfo(processId, priority, date);
    }
}
