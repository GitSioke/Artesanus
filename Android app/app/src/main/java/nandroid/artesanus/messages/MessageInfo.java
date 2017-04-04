package nandroid.artesanus.messages;

import java.util.Date;

/**
 * This class represents a Message from Fermenting step.
 */

public class MessageInfo extends Message
{
    private String information;

    public MessageInfo(float processId, int priority, Date date, String information)
    {
        super(processId, priority, date);
        this.information = information;
    }

    public String getInformation()
    {
        return this.information;
    }
}
