package nandroid.artesanus.common;

/**
 * This class represents the cereal for the beer
 */
public class Cereal
{
    String name;
    int amount;

    public Cereal(String name)
    {
        this.amount = 0;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

}
