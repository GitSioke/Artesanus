package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This factory class create data messages from different steps in a brew crafting process.
 */

public class MessageDataFactory implements MessagesFactory
{
        @Override
        public MessageHeating createMessageHeating(float processId, int priority, Date date) {
            return new MessageHeatingData(processId, priority, date);
        }

        @Override
        public MessageFermenter createMessageFermenter(float processId, int priority, Date date) {
            return new MessageFermenterData(processId, priority, date);
        }

        @Override
        public MessageMashing createMessageMashing(float processId, int priority, Date date) {
            return new MessageMashingData(processId, priority, date);
        }
}
