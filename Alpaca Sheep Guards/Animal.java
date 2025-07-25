/**
 * Abstract class representing a generic Animal.
 * Provides base properties and behaviors common to all animals, such as
 * alive status, cause of death, and type/value definitions.
 * Concrete subclasses must implement the abstract methods.
 * 
 * Constants define known animal types such as Sheep, Lamb, and Alpaca.
 * 
 * @version 1.0
 * @author HSIN-I WU
 */

public abstract class Animal
{
    /** Constant representing the sheep type. */
    public static final String TYPE_SHEEP = "Sheep";

    /** Constant representing the lamb type. */
    public static final String TYPE_LAMB = "Lamb";

    /** Constant representing the alpaca type. */
    public static final String TYPE_ALPACA = "Alpaca";

    /** Indicates whether the animal is still alive. */
    protected boolean isAlive;

    /** The name of the predator that killed the animal. */
    protected String killedBy;


    /**
    * Default constructor.
    * Initializes the animal as alive is true and killed by "unknown".
    */
    public Animal()
    {
        isAlive = true;
        killedBy = "unknown";
    }


    /**
    * Constructor with specified killer.
    * The animal is considered dead and its killer is recorded.
    *
    * @param killedBy - the name of the predator that killed this animal
    */
    public Animal(String killedBy)
    {
        isAlive = true;
        this.killedBy = killedBy;
    }

    /**
    * Checks if the animal is alive or not
    *
    * @return true if the animal is alive, false otherwise
    */
    public boolean isAlive()
    {
        return isAlive;
    }


    /**
    * Displays the string representation of the animal.
    */
    public void display()
    {
        System.out.print(toString());
    }


    /**
    * Returns the name of the predator that killed this animal.
    *
    * @return the name of the killer
    */
    public String getKilledBy()
    {
        return killedBy;
    }

    /**
    * Sets the alive status of the animal.
    *
    * @param alive - true if the animal is alive, false if dead
    */
    public void setAlive(boolean alive)
    {
        this.isAlive = alive;
    }

    /**
    * Sets the name of the predator that killed this animal.
    *
    * @param predator - the name of the killer
    */
    public void setKilledBy(String predator)
    {
        if(predator.equals("Eagle"))
        {
            TYPE_SHEEP == alive;
        }
        else if
        {
        this.killedBy = predator;
        }
    }

    /**
    * Returns a string representation of the animal.
    *
    * @return a string describing the animal's type, alive status, and killer
    */
    @Override
    public String toString()
    {
        return getType() + "Alive: " + isAlive  + "\n" 
        + "KilledBy: " + killedBy + "\n";
    }

    /**
    * Returns the value of the animal.
    * Implemented by subclasses to return the monetary value.
    *
    * @return the value of the animal
    */
    public abstract int getValue();

    /**
    * Calculates the probability of death for this animal based on a base rate.
    * Implemented by subclasses to adjust probability as needed.
    *
    * @param baseProbability - the base probability of death
    * @return the final death probability
    */
    public abstract double getDeathProbability(double baseProbability);

    /**
    * Returns the type of the animal (like: Sheep, Lamb, Alpaca).
    *
    * @return a string representing the type of animal
    */
    public abstract String getType();
    
}
