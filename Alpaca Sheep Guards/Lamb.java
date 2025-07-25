/**
 * Represents a Lamb, which is a type of Animal.
 * Lamb have a fixed value and use double the base death probability.
 * This class overrides the abstract methods from the Animal class.
 * 
 * @version 1.0
 * @author HSIN-I WU
 */

public class Lamb extends Animal
{
    /**
     * Constructs a Lamb object and initializes it as alive.
     */
    public Lamb()
    {
        super();
    }

    /**
    * Returns the market value of the lamb.
    *
    * @return the value of the lamb, which is 250
    */
    @Override
    public int getValue()
    {
        return 250;
    }

    /**
    * Returns the death probability of the lamb.
    * Lambs are more vulnerable, so their death probability
    * is double the base probability.
    *
    * @param baseProbability the base probability of death
    * @return the adjusted death probability
    */ 
    @Override
    public double getDeathProbability(double baseProbability)
    {
        return baseProbability * 2.0;
    }

    /**
    * Returns the type of this animal.
    *
    * @return the type string representing a lamb
    */
    @Override
    public String getType()
    {
        return TYPE_LAMB;
    }
}
