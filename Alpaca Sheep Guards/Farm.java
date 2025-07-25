/**
 * Represents a Farm containing sheep and lambs 
 * and located in a specific state.
 * This class provides basic attributes 
 * and mutator/accessor methods for farm name.
 * state, number of sheep, and number of lambs.
 * 
 * Valid states include VIC, NSW, SA, and WA.
 * 
 * @version 1.0
 * @author HSIN-I WU
 */

public class Farm 
{
    private String name;
    private String state;
    private int numberSheeps;
    private int numberLambs;

    /**
    * Default constructor 
    * initializing name as "unknown" and initializing state as "unknown".
    * sheeps number and lambs number counts to 0.
    */
    public Farm()
    {
        name = "unknown";
        state = "unknown";
        numberSheeps = 0;
        numberLambs = 0;
    }


    /**
    * Constructs a Farm object with specified name, state,
    * and numbers of sheep and lambs.
    *
    * @param name the name of the farm
    * @param state the state where the farm is located (VIC, NSW, SA, WA)
    * @param numberSheeps the number of sheep on the farm
    * @param numberLambs the number of lambs on the farm
    */
    public Farm (String name, String state, int numberSheeps , int numberLambs)
    {
        this.name = name;
        this.state = state;
        this.numberSheeps = numberSheeps;
        this.numberLambs = numberLambs;
    }

    /**
    * Displays the farm's details using the toString() method.
    */
    public void display()
    { 
        System.out.print(toString());
    }

    /**
    * Returns the name of the farm.
    *
    * @return the farm's name
    */
    public String getName()
    {
        return name;
    }

    /**
    * Returns the state where the farm is located.
    *
    * @return the farm's state
    */
    public String getState()
    {
        return state;
    }

    /**
    * Returns the number of sheep on the farm.
    *
    * @return the number of sheep
    */
    public int getNumberSheeps()
    {
        return numberSheeps;
    }

    /**
    * Returns the number of lambs on the farm.
    *
    * @return the number of lambs
    */
    public int getNumberLambs()
    {
        return numberLambs;
    }

    /**
    * Sets the name of the farm.
    * The name must not be null and must be at least 6 characters long.
    *
    * @param name the new name for the farm
    */
    public void setName(String name)
    {
        if (name != null && name.trim().length() >= 6)
        {
            this.name = name.trim();
        }
        else
        {
            System.out.println("Farm name must be more and equal to 6 words");
        }
    }

    /**
    * Sets the state of the farm.
    * Valid states are VIC, NSW, SA, and WA.
    *
    * @param state the state to set
    */
    public void setState(String state)
    {
        if (state != null && (state.equals("VIC") || 
                              state.equals("NSW") || 
                              state.equals("SA") || 
                              state.equals("WA")))
        {
            this.state = state;
        }
        else
        {
            System.out.println("Valid states must be VIC,NSW,SA,WA.");
        }
    }

    /**
    * Sets the number of sheep on the farm.
    * The number must be non-negative.
    *
    * @param numberSheeps the number of sheep
    */
    public void setNumberSheeps(int numberSheeps)
    {
        if (numberSheeps >= 0)
        {
            this.numberSheeps = numberSheeps;
        }
        else
        {
            System.out.println("Number of sheep must to be positive number! ");
        }
    }

    /**
    * Sets the number of lambs on the farm.
    * The number must be non-negative.
    *
    * @param numberLambs the number of lambs
    */
    public void setNumberLambs(int numberLambs)
    {
        if (numberLambs >= 0)
        {
            this.numberLambs = numberLambs;
        }
        else
        {
            System.out.println("Number of lamb must to be positive number! ");
        }
    }

    /**
    * Returns a string representation of the farm's information.
    *
    * @return a string containing the name, state, sheep and lamb counts
    */
    @Override
    public String toString()
    {
        return "Name: " + name + "\n" + "State: " + state + "\n" 
          + "NumberSheeps: " + numberSheeps + "\n" 
          + "NumberLambs: " + numberLambs + "\n" ;
    }
}
