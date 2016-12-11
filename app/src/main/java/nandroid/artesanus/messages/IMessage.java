package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This interface declare methods of a message
 */

public interface IMessage
{
    Date getDate();
    float getProcess();
    int getPriority();
}
