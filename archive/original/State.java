/**
 * Represents an Australian state and the predators found in it.
 * let setting and retrieving the name and predator list.
 *
 * @version 1.0
 * @author HSIN-I WU
 */

import java.util.*;

/**
 * The State class represents a state with a name 
 * and a list of predators who found in that state.
 * It with validation for acceptable state names 
 * and ensures the predator list is not empty or null.
 */

public class State
{
    private String name;
    private ArrayList<Predator> predators;

    /**
    * Default constructor initializing name as "unknown" 
    * and empty predator list.
    */
    public State()
    {
        name = "unknown";
        predators = new ArrayList<>();
    }

    /**
    * Constructs a State object with the specified name and list of predators.
    *
    * @param name     
    *   the name of the state (should be one of VIC, NSW, SA, WA)
    * @param predators the list of predators found in the state
    */
    public State(String name,ArrayList<Predator> predators)
    {
        this.name = name;
        this.predators = predators;
    }

    /**
    * Displays the string representation of the state and its predators.
    */
    public void display()
    {
        System.out.println(toString());
    }

    /**
    * Returns the name of the state.
    *
    * @return the state's name
    */
    public String getName()
    {
        return name;
    }

    /**
    * Returns the list of predators in the state.
    *
    * @return a list of Predator objects
    */
    public ArrayList<Predator> getPredators()
    {
        return predators;
    }

    /**
    * Sets the name of the state if it is valid (VIC, NSW, SA, WA).
    * Otherwise, prints an error message.
    *
    * @param name the new name of the state
    */
    public void setName(String name)
    {
        if (name != null && (name.equals("VIC") || 
                             name.equals("NSW") || 
                             name.equals("SA") || 
                             name.equals("WA")))
        {
            this.name = name;
        }
        else
        {
            System.out.println("Valid state names must be VIC,NSW,SA,WA.");
        }
    }

    /**
    * Sets the list of predators for the state  
    * if the list is not null or empty and contains no null entries.
    * Prints an error message.
    *
    * @param predators a list of Predator objects
    */
    public void setPredators(ArrayList<Predator> predators)
    {
        
        //check is predator list empty or not
        if (predators == null || predators.isEmpty())
        {
            System.out.println("Predator list can't be empty.");
            return;
        }

        //check the value in predator list is empty or not
        for (Predator p : predators)
        {
            if (p == null)
            {
                System.out.println("Predator list can't be empty.");
                return;
            }
        }

        this.predators = predators;
    }

    /**
    * Returns a string representation of the state 
    * including its name and predators.
    *
    * @return a formatted string with state information
    */
    @Override
    public String toString()
    {
        return "Name: " + name + ",Predators: " + predators;
    }
    
}
