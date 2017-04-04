package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This factory class create data messages from different steps in a brew crafting process.
 */

public class MessageHeaterFactory implements IMessageFactory
{
    @Override
    public MessageData createMessageData(float processId, int priority, Date date) {
        return new MessageDataHeater(processId, priority, date);
    }

    @Override
    public MessageInfo createMessageInfo(float processId, int priority, Date date, String info) {
        return new MessageInfoHeater(processId, priority, date, info);
    }

    @Override
    public MessageCommand createMessageCommand(float processId, int priority, Date date) {
        return new MessageCommandHeater(processId, priority, date);
    }
}
