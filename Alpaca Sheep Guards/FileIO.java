import java.io.*;
import java.util.*;

/**
* Handling files reading and writing operations
* which related to predator data and simulation reports 
* for alpacaSheepGuard program.
* Assumes the file(predators.txt) has exactly 4 lines
* , each state with predator danger factors.
*
* @version 1.0
* @author HSIN-I WU
*/

public class FileIO
{
    private String fileName;

    /**
     * Default constructor. 
     * Initializes fileName to "predators.txt".
     */
    public FileIO()
    {
        fileName = "predators.txt";
    }

    /**
     * Non-default constructor a FileIO object with a custom file name.
     *
     * @param fileName the name of the file to be used
     */
    public FileIO(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Returns the file name currently in use.
     *
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Sets the file name to be used for file operations.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Reads predator danger factor data from the file and
     * creates a list of State with predator details.
     *
     * @return a list of State objects read from the file
     */
    public ArrayList<State> readPredatorFile()
    {
        ArrayList<State> states = new ArrayList<>();
        String[] predatorNames = 
        {
            "fox", "dingo", "feral pig", "wedge-tailed eagle"
        };

        try (FileReader reader = new FileReader(fileName);
            Scanner scanner = new Scanner(reader))
        {
            int stateIndex = 0;

            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine().trim();

                if(line.isEmpty())
                {
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length != 5)
                {
                    System.out.println("Invalid line : " + line);
                    continue;
                }

                String stateName = parts[0].trim();
                ArrayList<Predator> predators = new ArrayList<>();

                for (int i = 0; i < predatorNames.length; i++)
                {
                    try
                    {
                        double dangerFactor = 
                        Double.parseDouble(parts[i+1].trim());
                        predators.add
                        (new Predator(predatorNames[i],dangerFactor, 0));
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println
                        ("Invalid danger factor in line: " + line);
                         // Default value
                        predators.add(new Predator(predatorNames[i], 0.0, 0));
                    }
                }

                states.add(new State(stateName, predators));
                stateIndex++;
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found: " + e );
        }
        catch (IOException e)
        {
            System.out.println("Error reading predator data: " + e);
        }
        return states;
    }


    /**
     * Writes a single string of content to the current file.
     *
     * @param content the string to be written to the file
     */
    public void writeFile(String content)
    {
        try(PrintWriter writer = new PrintWriter(fileName);)
        {
            writer.println(content);
        }
        catch (IOException e)
        {
            System.out.println("Failed to write file: " + e);
        }
    }


    /**
     * Writes a detailed simulation report to the file
     * "alpacaSheepGuardViability.txt".
     *
     * @param farmName         the name of the farm
     * @param farmNumberSheep  the number of sheep on the farm
     * @param farmNumberLamb   the number of lambs on the farm
     * @param protectNumber    the recommended alpaca count for protection
     * @param lowAvgCost       the lowest average cost found in simulation
     * @param simNumber        the Simulation class with detailed results
     * @param troublesome      list of strings representing top predators
     */
    public void writeFinalReport(String farmName, int farmNumberSheep
                                ,int farmNumberLamb, int protectNumber
                                ,double lowAvgCost, Simulation simNumber
                                ,List<String> troublesome)
                                {
        try (FileWriter writer = 
        new FileWriter("alpacaSheepGuardViability.txt"))
        {
            writer.write("=== Alpaca Sheep Guard Simulation Report ===\n");
            writer.write("Farm name: " + farmName + "\n");
            writer.write("Number of sheep: " + farmNumberSheep + "\n");
            writer.write("Number of lamb: " + farmNumberLamb + "\n");
            writer.write("Recommended level of protection: " + protectNumber
                          + " alpaca\n");
            writer.write
            (String.format("Protection cost: $%.2f\n" , lowAvgCost));
            double avgAnimalLost = simNumber.getAverageSheepLost() +
                                   simNumber.getAverageLambsLost() +
                                   simNumber.getAverageAlpacasLost();
            writer.write("Average predicted number of animal: " + 
                         String.format("%.2f" , avgAnimalLost) + "\n");
            writer.write("Most troublesome predators:\n");
            for (String line : troublesome)
            {
                writer.write(" " + line + "\n");
            }

            System.out.println
            ("\nA report has been written to: alpacaSheepGuardViability.txt");
            System.out.println("Goodbye");
        }
        catch (IOException e)
        {
            System.out.println("error");
        }
    }

}
