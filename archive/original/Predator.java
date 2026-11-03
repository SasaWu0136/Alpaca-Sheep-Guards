/**
 * Represents a Predator in the simulation.
 * Each predator has a name, a danger factor (between 0 and 1),
 * and a kill count how many animals it has killed in the each round.
 * 
 * This class includes methods to get and set predator details, 
 * increment kills, reset kill count, and display the predator's information.
 * 
 * @version 1.0
 * @author HSIN-I WU
 */

public class Predator
{
    private String name;
    private double dangerFactor;
    private int kills; //calculate each round how many animal be killed


    /**
    * Default constructor.
    * Initializes the predator with
    * "unknown" name, danger factor 0.0, and 0 kills.
    */
    public Predator()
    {
        name = "unknown";
        dangerFactor = 0.0;
        kills = 0;
    }

    /**
    * Constructs a predator with the specified name
    * , danger factor, and initial kills.
    *
    * @param name - the name of the predator
    * @param dangerFactor - a value between 0 and 1 indicating danger level
    * @param kills - the number of animals killed so far
    */
    public Predator(String name, double dangerFactor, int kills)
    {
        this.name = name;
        this.dangerFactor = dangerFactor;
        this.kills = kills;
    }

    /**
    * Displays the predator's information using toString() method.
    */
    public void display()
    {
        System.out.println(toString());
    }

    /**
    * Returns the name of the predator.
    *
    * @return the predator's name
    */
    public String getName()
    {
        return name;
    }

    /**
    * Returns the danger factor of the predator.
    *
    * @return the predator's danger factor
    */
    public double getDangerFactor()
    {
        return dangerFactor;
    }

    /**
    * Returns the number of kills by this predator.
    *
    * @return the number of animals killed
    */
    public int getKills()
    {
        return kills;
    }

    /**
    * Sets the name of the predator.
    * The name must not be null or empty.
    *
    * @param name - the predator's name
    */
    public void setName(String name)
    {
        if (name != null && !name.trim().isEmpty())
        {
            this.name = name.trim();
        }
        else
        {
            System.out.println("Predator name can't be empty.");
        }
    }

    /**
    * Sets the danger factor of the predator.
    * The value must be between 0 and 1.
    *
    * @param dangerFactor - the danger level to set.
    */
    public void setDangerFactor(double dangerFactor)
    {
        if (dangerFactor >= 0 && dangerFactor <= 1)
        {
            this.dangerFactor = dangerFactor;
        }
        else
        {
            System.out.println("Danger factor need to between 0 and 1.");
        }
    }

    /**
    * Increments the kill count by one.
    */
    public void incrementKills()
    {
        this.kills = kills + 1;
    }

    /**
    * Resets the kill count to zero.
    */
    public void resetKills()
    {
        this.kills = 0;
    }

    /**
    * Returns a string representation of the predator.
    *
    * @return a formatted string with name, danger factor, and kill count
    */
    @Override
    public String toString()
    {
        return "Name: " + name + ", DangerFactor: " + dangerFactor
                + ", Kills: " + kills;
    }
}
