package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This Factory class create MessageCommands from different steps in a brew crafting process.
 */

public class MessageFermenterFactory implements IMessageFactory
{
    @Override
    public MessageCommand createMessageCommand(float processId, int priority, Date date) {
        return new MessageCommandFermenter(processId, priority, date);
    }

    @Override
    public MessageInfo createMessageInfo(float processId, int priority, Date date, String info) {
        return new MessageInfoFermenter(processId, priority, date, info);
    }

    @Override
    public MessageData createMessageData(float processId, int priority, Date date) {
        return new MessageDataFermenter(processId, priority, date);
    }
}
