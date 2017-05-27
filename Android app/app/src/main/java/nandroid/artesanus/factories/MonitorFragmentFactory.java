package nandroid.artesanus.factories;

import android.support.v4.app.Fragment;

import nandroid.artesanus.fragments.MonitorBoilTabFragment;
import nandroid.artesanus.fragments.MonitorFermentationTabFragment;
import nandroid.artesanus.fragments.MonitorMashingTabFragment;

/**
 * Created by Nando on 05/11/2016.
 */
public class MonitorFragmentFactory {


    public final static int MASHING = 0;
    public final static int HEAT = 1;
    public final static int FERMENTER = 2;

    public static Fragment getFragment(int type) {
        switch (type) {
            case MASHING:
                return new MonitorMashingTabFragment();
            case HEAT:
                return new MonitorBoilTabFragment();
            case FERMENTER:
                return new MonitorFermentationTabFragment();
        }
        return null;
    }
}
