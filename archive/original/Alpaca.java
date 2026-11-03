/**
 * Represents an Alpaca, which is a type of Animal.
 * Alpacas have a fixed value and use very low death probability,
 * which is a small percentage of the base danger.
 * This class overrides the abstract methods from the Animal class.
 * 
 * @version 1.0
 * @author HSIN-I WU
 */

public class Alpaca extends Animal
{
    /**
     * Constructs an Alpaca object and initializes it as alive.
     */
    public Alpaca()
    {
        super();
    }

    /**
    * Returns the market value of the alpaca.
    *
    * @return the value of the alpaca, which is 1000
    */
    
    @Override
    public int getValue()
    {
        return 1000;
    }

    /**
    * Returns the death probability of the alpaca.
    * In alpaca, the death probability is only 1% of the base danger.
    *
    * @param baseProbability the base probability of death
    * @return the death probability reduced to 1% of the base probability
    */
    @Override
    public double getDeathProbability(double baseProbability)
    {
        return baseProbability * 0.01;
    }

    /**
    * Returns the type of this animal.
    *
    * @return the type string representing an alpaca
    */
    @Override
    public String getType()
    {
        return TYPE_ALPACA;
    }
}
