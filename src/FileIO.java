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
        fileName = "data/predators.txt";
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
                    throw new IllegalArgumentException("Expected state and four danger factors: " + line);
                }

                String stateName = parts[0].trim();
                ArrayList<Predator> predators = new ArrayList<>();

                for (int i = 0; i < predatorNames.length; i++)
                {
                    try
                    {
                        double dangerFactor =
                        Double.parseDouble(parts[i+1].trim());
                        if (!Double.isFinite(dangerFactor) || dangerFactor < 0 || dangerFactor > 1)
                            throw new IllegalArgumentException("Danger factor must be between 0 and 1: " + line);
                        predators.add
                        (new Predator(predatorNames[i],dangerFactor, 0));
                    }
                    catch (NumberFormatException e)
                    {
                        throw new IllegalArgumentException("Invalid danger factor: " + line, e);
                    }
                }

                states.add(new State(stateName, predators));
                stateIndex++;
            }
        }
        catch (FileNotFoundException e)
        {
            throw new IllegalStateException("Cannot read predator data: " + fileName, e);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Cannot read predator data: " + fileName, e);
        }
        if (states.isEmpty()) throw new IllegalArgumentException("Predator data is empty");
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
     * "output/alpacaSheepGuardViability.txt".
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
        new File("output").mkdirs();
        try (FileWriter writer =
        new FileWriter("output/alpacaSheepGuardViability.txt"))
        {
            writer.write("=== Alpaca Sheep Guard Simulation Report ===\n");
            writer.write("Educational model; assumptions are not field-validated.\n");
            writer.write("Farm name: " + farmName + "\n");
            writer.write("Number of sheep: " + farmNumberSheep + "\n");
            writer.write("Number of lamb: " + farmNumberLamb + "\n");
            writer.write("Recommended level of protection: " + protectNumber
                          + " alpaca\n");
            writer.write
            (String.format("Estimated total cost: $%.2f\n" , lowAvgCost));
            double avgAnimalLost = simNumber.getAverageSheepLost() +
                                   simNumber.getAverageLambsLost() +
                                   simNumber.getAverageAlpacasLost();
            writer.write("Average animals lost: " +
                         String.format("%.2f" , avgAnimalLost) + "\n");
            writer.write("Most troublesome predators:\n");
            for (String line : troublesome)
            {
                writer.write(" " + line + "\n");
            }

            System.out.println
            ("\nA report has been written to: output/alpacaSheepGuardViability.txt");
            System.out.println("Goodbye");
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Cannot write simulation report", e);
        }
    }

}
