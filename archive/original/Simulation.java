import java.util.*;

/**
* This class is the simulation of predator in a farm and evaluates 
* the cost of using alpaca to protect sheep & lamb.
* Each simulation run calculates the animal lost and the total cost 
* which including stock loss, alpaca cost and maintenance cost.
* The results averages 10 runs
*
*
*
* @version 1.0
* @author HSIN-I WU
*/  

public class Simulation
{
    private Farm farm;
    private State state;
    private int alpacaCount;
    private Random random;
    private double[] totalCosts;
    private int[] sheepLost;
    private int[] lambsLost;
    private int[] alpacasLost;
    private int[] killsByFox;
    private int[] killsByDingo;
    private int[] killsByFeralPig;
    private int[] killsByWedgeTailedEagle;

    /**
     * Constructs a Simulation.
     *
     * @param farm the farm to simulate
     * @param state the state in which the farm is located
     * @param alpacaCount the number of alpacas used for protection
     */
    public Simulation(Farm farm,State state,int alpacaCount)
    {
        this.farm = farm;
        this.state = state;
        this.alpacaCount = alpacaCount;
        this.random = new Random();
        this.totalCosts = new double[10];
        this.sheepLost = new int[10];
        this.lambsLost = new int[10];
        this.alpacasLost = new int[10];
        this.killsByFox = new int[10];
        this.killsByDingo = new int[10];
        this.killsByFeralPig = new int[10];
        this.killsByWedgeTailedEagle = new int[10];
    }

    /**
     * Runs the simulation for 10 runs, simulation animal losses 
     * and cost for each time.
     */
    public void runSimulation()
    {
        for (int run = 0 ; run < 10 ; run ++)
        {
            // reset the predator kills
            for (Predator predator: state.getPredators())
            {
                predator.resetKills();
            }
        

            ArrayList<Animal> animals = new ArrayList<>();

            for (int i = 0 ; i < farm.getNumberSheeps(); i ++)
            {
                animals.add(new Sheep());
            }
            
            for (int i = 0 ; i < farm.getNumberLambs(); i ++)
            {
                animals.add(new Lamb());
            }

            for (int i = 0 ; i < alpacaCount; i ++)
            {
                animals.add(new Alpaca());
            }


            for (Animal animal : animals)
            {
                for (Predator predator : state.getPredators())
                {
                    double deathProbability = animal.getDeathProbability
                    (predator.getDangerFactor());

                    // apply protect multiplier
                    if (alpacaCount == 1)
                        deathProbability *= 0.5;
                    
                    else if (alpacaCount >= 2)
                        deathProbability *= 0.25;
                    

                    // alpaca adjustment
                    if (animal instanceof Alpaca)
                    {
                        deathProbability = animal.getDeathProbability(predator.getDangerFactor());

                        if (alpacaCount == 1)
                            deathProbability *= 0.5;
                    
                        else if (alpacaCount >= 2)
                            deathProbability *= 0.25;
                    }

                    double randomTest = random.nextDouble();
                    
                    if (randomTest < deathProbability)
                    {
                        animal.setAlive(false);
                        animal.setKilledBy(predator.getName());
                        predator.incrementKills();

                        break;
                    }
                }
            }
            
            int sheepLostCount = 0 , lambsLostCount = 0 , alpacasLostCount = 0;
            for(Animal animal : animals)
            {
                if(!animal.isAlive())
                {
                    // if (animal.getType().equals("Sheep"))
                    // {
                    //     sheepLostCount++;
                    // }
                    // else if (animal.getType().equals("Lamb"))
                    // {
                    //     lambsLostCount++;
                    // }
                    // else if (animal.getType().equals("Alpaca"))
                    // {
                    //     alpacasLostCount++;
                    // }

                    if (animal.getType().equals(TYPE_SHEEP)) 
                    {
                        sheepLostCount++;
                    } 
                    else if (animal.getType().equals(TYPE_LAMB)) 
                    {
                        lambsLostCount++;
                    }
                    else if (animal.getType().equals(TYPE_ALPACA)) 
                    {
                        alpacasLostCount++;
                    }
                }
            }
        
        
        double stockLoss = sheepLostCount * 150 + lambsLostCount * 250 +
        alpacasLostCount * 1000;
        double alpacaCost = alpacaCount * 500;
        double maintenanceTotal = 0;

        for (int i = 0 ; i < alpacaCount ; i++)
        {
            // want to get the random number between 400 to 600
            double maintenance = 400 + random.nextDouble() * 200 ; 

            if (i < alpacasLostCount)
            {
                maintenance *= 0.5; // if alpaca dies , get half
            }

            maintenanceTotal += maintenance;
        }

        double totalCost = stockLoss + alpacaCost + maintenanceTotal;

        // stored the results
        totalCosts[run] = totalCost;
        sheepLost[run] = sheepLostCount;
        lambsLost[run] = lambsLostCount;
        alpacasLost[run] = alpacasLostCount;
        killsByFox[run] = getKillsByPredator("Fox");
        killsByDingo[run] = getKillsByPredator("Dingo");
        killsByFeralPig[run] = getKillsByPredator("Feral Pig");
        killsByWedgeTailedEagle[run] = 
        getKillsByPredator("Wedge-tailed Eagle");
        }
}

        /**
        * Returns the total number of kills by predators.
        * @param predatorName - the name of the predator
        * @return the number of kills
        */
        public int getKillsByPredator(String predatorName)
        {
            for(Predator predator : state.getPredators())
            {
                if (predator.getName().equalsIgnoreCase(predatorName))
                {
                    return predator.getKills();
                }
            }
            return 0;
        }

        /**
        * Displays the average results of the simulation with cost and losses.
        */
        public void displayResults()
        {
            double avgCost = 0, minCost = totalCosts[0]
                 , maxCost = totalCosts[0];
            double avgSheepLost = 0, avgLambsLost = 0, avgAlpacasLost = 0;
            double avgFoxKills = 0, avgDingoKills = 0, avgFeralPigKills = 0,
            avgWedgeTailedEagleKills = 0;

            for (int i =0; i < 10; i++)
            {
                avgCost += totalCosts[i];
                avgSheepLost += sheepLost[i];
                avgLambsLost += lambsLost[i];
                avgAlpacasLost += alpacasLost[i];
                avgFoxKills += killsByFox[i];
                avgDingoKills += killsByDingo[i];
                avgFeralPigKills += killsByFeralPig[i];
                avgWedgeTailedEagleKills += killsByWedgeTailedEagle[i];

                if (totalCosts[i] < minCost)
                {
                    minCost = totalCosts[i];
                }
                if (totalCosts[i] > maxCost)
                {
                    maxCost = totalCosts[i];
                }
            }

            avgCost /= 10.0;
            avgSheepLost /= 10.0;
            avgLambsLost /= 10.0;
            avgAlpacasLost /= 10.0;
            avgFoxKills /= 10.0;
            avgDingoKills /= 10.0;
            avgFeralPigKills /= 10.0;
            avgWedgeTailedEagleKills /= 10.0;

            System.out.println("=========================================");
            System.out.println(" * Running simulation with " 
                               + alpacaCount + " alpaca *");
            System.out.println("=========================================");
            System.out.printf("    Lowest cost:  $%.2f\n", minCost);
            System.out.printf("    Highest cost: $%.2f\n", maxCost);
            System.out.printf("    Average cost: $%.2f\n\n", avgCost);

            System.out.println("    Average number of animal lost:");
            System.out.printf("       Sheep:  %.2f\n", avgSheepLost);
            System.out.printf("       Lamb:   %.2f\n", avgLambsLost);
            System.out.printf("       Alpaca: %.2f\n\n", avgAlpacasLost);

            System.out.println
            ("    Average number of animals killed by each predator:");
            System.out.printf
            ("       Fox:    %.2f\n", avgFoxKills);
            System.out.printf
            ("       Dingo:  %.2f\n", avgDingoKills);
            System.out.printf
            ("       Pig:    %.2f\n", avgFeralPigKills);
            System.out.printf
            ("       Eagle:  %.2f\n", avgWedgeTailedEagleKills);
        }

        /**
        * Returns the average cost over all simulation runs.
        * @return average cost
        */
        public double getAverageCost()
        {
            double sum = 0;
            for (double cost : totalCosts)
            {
                sum += cost;
            }
            return sum / 10.0;
        }

        /**
        * Returns the average number of killing by the predator type.
        * @return map of predator name to average kills
        */
        public Map<String , Double> getAverageKillsByPredator()
        {
            Map<String , Double> kills = new HashMap<>();
            double avgFox = 0, avgDingo = 0, avgFeralPig = 0, avgEagle = 0;

            for (int i= 0; i <10 ; i++)
            {
                avgFox += killsByFox[i];
                avgDingo += killsByDingo[i];
                avgFeralPig += killsByFeralPig[i];
                avgEagle += killsByWedgeTailedEagle[i];
            }
            
            kills.put("fox", avgFox / 10.0);
            kills.put("dingo", avgDingo / 10.0);
            kills.put("feral pig", avgFeralPig / 10.0);
            kills.put("wedge-tailed eagle", avgEagle / 10.0);
            return kills;
        }

        /**
        * Returns the average number of sheep lost.
        * @return average number of sheep lost
        */
        public double getAverageSheepLost()
        {
            double sum = 0;
            for (int count : sheepLost)
            {
                sum += count;
            }
            return sum / 10.0;
        }

        /**
        * Returns the average number of lambs lost.
        * @return average number of lambs lost
        */
        public double getAverageLambsLost()
        {
            double sum = 0;
            for (int count : lambsLost)
            {
                sum += count;
            }
            return sum / 10.0;
        }

        /**
        * Returns the average number of alpacas lost.
        * @return average number of alpacas lost
        */       
        public double getAverageAlpacasLost()
        {
            double sum = 0;
            for (int count : alpacasLost)
            {
                sum += count;
            }
            return sum / 10.0;
        }
}
