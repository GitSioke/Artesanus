package nandroid.artesanus.messages;

import android.util.Pair;

import java.util.Date;

/**
 * This class represents a Message created from Mashing step.
 */

public class MessageData extends Message
{
    private Pair<String, Object> valueData;

    public MessageData(float processId, int priority, Date date)
    {
        super(processId, priority, date);
    }

    public Pair<String, Object> getValueData()
    {
        return this.valueData;
    }
}
