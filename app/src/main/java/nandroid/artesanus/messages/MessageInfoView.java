package nandroid.artesanus.messages;

import android.util.Pair;
import android.view.View;

import nandroid.artesanus.gui.R;

/**
 * This class represents a view and a message relation */

public class MessageInfoView implements IMessageInfoDrawing
{
    View view;

    public MessageInfoView(View view)
    {
        this.view = view;
    }

    public void show(MessageInfo msg)
    {
        this.view.findViewById(R.id.monitoring_button_initilice);
        int priority = msg.getPriority();

    }
}
