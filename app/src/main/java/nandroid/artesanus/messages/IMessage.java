package nandroid.artesanus.messages;

import java.util.Date;

/**
 * Created by Nando on 06/12/2016.
 */

public interface IMessage
{
    Date getDate();
    float getProcess();
    int getPriority();
}
