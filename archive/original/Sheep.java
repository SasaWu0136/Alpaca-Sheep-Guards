/**
 * Represents a Sheep, which is a type of Animal.
 * Sheep have a fixed value 
 * and use the base death probability without modification.
 * This class overrides the abstract methods from the Animal class.
 * 
 * @version 1.0
 * @author HSIN-I WU
 */

public class Sheep extends Animal
{
    /**
     * Constructs a Sheep object and initializes it as alive.
     */
    public Sheep()
    {
        super();
    }

    /**
    * Returns the market value of the sheep.
    *
    * @return the value of the sheep, which is 150
    */
    @Override
    public int getValue()
    {
        return 150;
    }

    /**
    * Returns the death probability of the sheep.
    * In sheep, this is the same as the base probability (no change).
    *
    * @param baseProbability - the base probability of death
    * @return the unmodified death probability
    */
    @Override
    public double getDeathProbability(double baseProbability)
    {
        return baseProbability * 1.0;
    }

    /**
    * Returns the type of this animal.
    *
    * @return the type string representing a sheep
    */
    @Override
    public String getType()
    {
        return TYPE_SHEEP;
    }
}
