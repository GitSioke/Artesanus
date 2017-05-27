package nandroid.artesanus.listener;

import java.util.ArrayList;
import nandroid.artesanus.common.Heat;

/**
 * This listener handle heat removes from list
 */
public interface OnHeatRemovedListener
{
    void onHeatRemoved(ArrayList<Heat> heats);
}
