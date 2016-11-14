package nandroid.artesanus.common;

/**
 * Created by Nando on 12/11/2016.
 */
public class Hop
{
    String name;
    int amount;
    int minutes;

    public Hop(String name)
    {
        this.amount = 0;
        this.name = name;
        this.minutes = 0;
    }

    public String getName()
    {
        return this.name;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public int getMinutes()  { return this.minutes; }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public void setMinutes(int minutes)
    {
        this.minutes = minutes;
    }

}
