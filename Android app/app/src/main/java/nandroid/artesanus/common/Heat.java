package nandroid.artesanus.common;

/**
 * This class represents a heat during mashing process.
 */
public class Heat
{
    private int temperature;
    private int duration;
    private int startTime;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Heat(int temperature, int duration, int start)
    {
        this.duration = duration;
        this.temperature = temperature;
        this.startTime = start;
    }

    public int getTemperature()
    {
        return this.temperature;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration()
    {
        return this.duration;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}
