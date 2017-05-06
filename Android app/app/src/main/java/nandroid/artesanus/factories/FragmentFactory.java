package nandroid.artesanus.factories;

import android.support.v4.app.Fragment;

import nandroid.artesanus.fragments.KindTabFragment;
import nandroid.artesanus.fragments.MashingTabFragment;

/**
 * Created by Nando on 05/11/2016.
 */
public class FragmentFactory {

    public final static int KIND = 0;
    public final static int MASHING = 1;

    public static Fragment getFragment(int type) {
        switch (type) {
            case KIND:
                return new KindTabFragment();
            case MASHING:
                return new MashingTabFragment();
        }
        return null;
    }
}
