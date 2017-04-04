package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class implements a factory of info messages which belong to a concrete brew crafting step.
 */

public class MessageMasherFactory implements IMessageFactory
{
    @Override
    public MessageData createMessageData(float processId, int priority, Date date) {
        return new MessageDataMasher(processId, priority, date);
    }

    @Override
    public MessageCommand createMessageCommand(float processId, int priority, Date date)
    {
        return new MessageCommandMasher(processId, priority, date);
    }

    @Override
    public MessageInfo createMessageInfo(float processId, int priority, Date date, String info) {
        return new MessageInfoMasher(processId, priority, date, info);
    }
}
