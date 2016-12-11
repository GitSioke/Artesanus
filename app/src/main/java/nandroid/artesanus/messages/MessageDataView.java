package nandroid.artesanus.messages;

import android.util.Pair;
import android.view.View;

import java.util.Date;

import nandroid.artesanus.gui.R;

/**
 * This class represents a Message created from Mashing step.
 */

public class MessageDataView implements IMessageDataDrawing
{
    View view;

    public MessageDataView(View view)
    {
        this.view = view;
    }

    public void show(MessageData messageData)
    {
        this.view.findViewById(R.id.monitoring_button_initilice);
        Pair<String, Object> valueData = messageData.getValueData();
        String data = valueData.first;
        Object value = valueData.second;

    }
}
