package nandroid.artesanus.messages;


import java.util.Date;

/**
 * This Abstract Factory defines creation methods for Message child classes.
 */

public interface IMessageFactory
{
    MessageCommand createMessageCommand(float processId, int priority, Date date);
    MessageData createMessageData(float processId, int priority, Date date);
    MessageInfo createMessageInfo(float processId, int priority, Date date, String info);
}
