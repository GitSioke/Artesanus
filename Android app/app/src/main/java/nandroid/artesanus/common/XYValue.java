package nandroid.artesanus.common;

import java.util.Date;

/**
 * Graph values
 */

public class XYValue
{
    private Date _X;
    private int _Y;

    public XYValue(Date x, int y)
    {
        _X = x;
        _Y = y;
    }

    public Date getX()
    {return _X;}

    public int getY()
    {
        return _Y;
    }
}
