package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This Factory class create MessageCommands from different steps in a brew crafting process.
 */

public class MessageCommandFactory implements MessagesFactory
{
    @Override
    public MessageHeating createMessageHeating(float processId, int priority, Date date) {
        return new MessageHeatingCommand(processId, priority, date);
    }

    @Override
    public MessageFermenter createMessageFermenter(float processId, int priority, Date date) {
        return new MessageFermenterCommand(processId, priority, date);
    }

    @Override
    public MessageMashing createMessageMashing(float processId, int priority, Date date) {
        return new MessageMashingCommand(processId, priority, date);
    }
}
