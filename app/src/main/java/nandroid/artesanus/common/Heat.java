package nandroid.artesanus.common;

/**
 * Created by Nando on 13/11/2016.
 */
public class Heat
{
    int temperature;
    int duration;
    int start;

    public Heat(int temperature, int duration, int start)
    {
        this.duration = duration;
        this.temperature = temperature;
        this.start = start;
    }

    public int getTemperature()
    {
        return this.temperature;
    }

    public int getDuration()
    {
        return this.duration;
    }

    public int getStart()
    {
        return this.start;
    }
}
