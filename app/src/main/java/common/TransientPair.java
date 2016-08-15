package common;

import android.util.Pair;

import java.io.Serializable;

/**
 * Created by nando on 15/08/2016.
 */
public class TransientPair implements Serializable {
    private transient Pair _pair;

    public TransientPair(String key, Object value)
    {
        _pair = new Pair(key, value);
    }

    @Override
    public String toString()
    {
        return _pair.toString();
    }
}
