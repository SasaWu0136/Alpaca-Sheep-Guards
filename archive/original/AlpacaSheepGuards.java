import java.util.*;
import java.io.*;

/**
* AlpacaSheepGuards is the main class for simulation the cost 
* and effectiveness to use alpacas to guard sheeps for farm from predators.
* It asks from the user for farm data,running simulations and outputs results.
*
* @version 1.0
* @author HSIN-I WU 
*/  


public class AlpacaSheepGuards
{
    private static Input input = new Input();

    /**
    * Main entry of the progarm with user input, running simulation,
    * and display results.
    */ 
    public static void main(String[] args)
    {
        Random random = new Random();

        displayWelcomeMessage();

        String farmName = input.acceptFarmName("What is your farm's name: ");

        String farmState = input.acceptChoice
        ("Which state?", new String[]{"VIC", "NSW", "SA", "WA"});      

        int[] number = getSheepAndLambNumbers();
        int farmNumberSheep = number[0];
        int farmNumberLamb = number[1];

        FileIO fileIO = new FileIO();
        ArrayList<State> states = fileIO.readPredatorFile();
        State pickedState = findStateName(farmState, states);
        if (pickedState == null)
        {
            return;
        }

        Farm farm = new Farm
        (farmName, farmState, farmNumberSheep, farmNumberLamb);
        List<Simulation> simulations = new ArrayList<>();

        for (int alpacaCount = 0; alpacaCount <= 2; alpacaCount++)
        {
            Simulation simulation = new Simulation
            (farm, pickedState, alpacaCount);

            simulation.runSimulation();
            simulation.displayResults();
            simulations.add(simulation);
        }

        // get the finish report detail
        double lowAvgCost = simulations.get(0).getAverageCost();
        int protectNumber = 0;
        for (int i = 1; i < simulations.size(); i++)
        {
            if(simulations.get(i).getAverageCost() < lowAvgCost)
            {
                lowAvgCost = simulations.get(i).getAverageCost();
                protectNumber = i;
            }
        }

        Simulation simNumber = simulations.get(protectNumber);
        Map<String, Double> predatorKills 
        = simNumber.getAverageKillsByPredator();

        List<String> troublesome = new ArrayList<>();
        List<String> unharm = new ArrayList<>();
        for (String killer : predatorKills.keySet())
        {
            if (predatorKills.get(killer) > 0.0)
                troublesome.add(killer + ", kill average: " + 
                String.format("%.1f", predatorKills.get(killer)));
            else
                unharm.add(killer);
        }

        // display the finish report
        System.out.println("==============================");
        System.out.println(" * End of Simulation Report * ");
        System.out.println("==============================");
        System.out.printf("  🦊Lowest average cost: $%.2f%n", lowAvgCost);
        System.out.printf("  🐶Protection: " + protectNumber + " alpaca\n");
        System.out.printf("      🐷Troublesome predators:\n");
        for (String line : troublesome)
        {
            System.out.printf("      " + line + "\n");
        }
        if (!unharm.isEmpty())
        {
            System.out.println("      🐥" + unharm.size() + 
                               " predators that had no kill: ");
            for(String s : unharm)
            {
                System.out.println("      " + s);
            }
        }

        // write the simulation report
        fileIO.writeFinalReport(farmName, farmNumberSheep, farmNumberLamb,
                         protectNumber, lowAvgCost, simNumber, troublesome);

        //
        // Testing Strategy Part
        // FarmTestStrategy.startFarmTest();

    }

        /**
        * Display the welcome message to the user.
        */

        public static void displayWelcomeMessage()
        {
            System.out.println("Welcome to the Alpaca Sheep Guards Program");
        }

        /**
        * Prompts the user to enter both sheep and lamb counts.
        * Total must be more than 0 and less/equal to 100.
        *
        * @return int array: [sheep, lamb]
        */

        public static int[] getSheepAndLambNumbers()
        {
            int sheep = -1;
            int lamb = -1;
            boolean valid = false;

            while(!valid)
            {
                sheep = input.acceptPositiveInteger("How many sheep? ");
                lamb = input.acceptPositiveInteger("How many lamb? ");

                if(sheep + lamb == 0)
                {
                    System.out.println
                    ("Error: the total number of sheep"
                      + " and lambs must be more than 0\n");
                }
                else if(sheep + lamb > 100)
                {
                    System.out.println
                    ("Error: the total number of sheep"
                     + " and lambs must be less than 100\n");
                }
                else
                {
                    valid = true;
                }             
                
            }
            return new int[]{sheep, lamb};
        } 

        /**
        * Searching the State object in the list which matches the given name.
        * 
        *
        * @param name the name of the state to search for.
        * @param states the list of valid States objects
        * @return the matching State object if not found return null
        */

        public static State findStateName
        (String name, ArrayList<State> states)
        {
            for(State s: states)
            {
                if (s.getName().equalsIgnoreCase(name))
                {
                    return s;
                }
            }
            return null;
        }
}
