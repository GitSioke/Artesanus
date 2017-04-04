package nandroid.artesanus.common;

/**
 * This class represents the cereal for the beer
 */
public class Cereal
{
    String name;
    int quantity;

    public Cereal(String name)
    {
        this.quantity = 0;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public int getQuantity()
    {
        return this.quantity;
    }

    public void setQuantity(int amount)
    {
        this.quantity = amount;
    }

}
