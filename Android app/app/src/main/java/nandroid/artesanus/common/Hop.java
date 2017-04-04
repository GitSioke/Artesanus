package nandroid.artesanus.common;

/**
 * This class represents a model for a hop.
 */
public class Hop
{
    String name;

    int quantity;

    int minutes;

    public Hop(String name)
    {
        this.quantity = 0;
        this.name = name;
        this.minutes = 0;
    }

    public String getName()
    {
        return this.name;
    }

    public int getMinutes()  { return this.minutes; }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {

        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setMinutes(int minutes)
    {
        this.minutes = minutes;
    }

}
