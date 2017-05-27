package nandroid.artesanus.listener;

import java.util.ArrayList;
import nandroid.artesanus.common.Hop;

/**
 * This listener handle hop removes from list
 */

public interface OnHopRemovedListener
{
    void onHopRemoved(ArrayList<Hop> hops);
}
