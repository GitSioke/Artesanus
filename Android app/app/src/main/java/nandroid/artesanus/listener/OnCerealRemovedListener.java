package nandroid.artesanus.listener;

import java.util.ArrayList;

import nandroid.artesanus.common.Cereal;

/**
 * This listener handle cereal removes from list
 */

public interface OnCerealRemovedListener
{
    void onCerealRemoved(ArrayList<Cereal> cereals);
}