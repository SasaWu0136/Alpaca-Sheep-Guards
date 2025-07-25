import java.util.Scanner;

/**
 *
 * The Input class provides the methods to read and validate types
 * of user input from the console
 *
 * @version 1.0
 * @author HSIN-I WU
 */

public class Input
{
    private Scanner scanner;

    /**
     * Initializes the Scanner.
     */
    public Input()
    {
        scanner = new Scanner(System.in);
    }

    /**
     * Prompts the user for a string input.
     * 
     * @param message the prompts message to display
     * @return the user input as a String
     */   
    public String acceptStringInput(String message) 
    {
        System.out.print(message);
        return scanner.nextLine().trim(); 
    }

    /**
     * Prompts the user for a positive integer input with validation.
     * Display error message if input is invalid.
     *
     * @param message the prompt message to display
     * @return the valid positive integer the user entered 
     */
    public int acceptPositiveInteger(String message)
    {
        int result = -1;
        boolean valid = false;
        while (!valid)
        {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) 
            {
                System.out.println("Error: value is not a number\n");
            } 
            else if (!input.matches("-?\\d+")) 
            {
                System.out.println
                ("Error: value is not a number: " + "'" + input + "'\n");
            } 
            else 
            {
                result = Integer.parseInt(input);
                if (result < 0) 
                {
                    System.out.println("Error: number must not be less than 0\n");
                }
                else 
                {
                    valid = true;
                }
            }
        }    
        return result;
    }

    /**
     * Prompts the user to select the valid choice from a list.
     * Keeping prompt until the valid choice is entered
     *
     * @param message the prompt message to display 
     * @param choices the valid choice of array 
     * @return the chosen option in uppercase
     */
    public String acceptChoice(String message, String[] choices)
    {
        boolean valid = false;
        String input = "";
        while (true)
        {
            System.out.println(message);
            for (String c : choices)
            {
                System.out.println(" - " + c);
            }
        
            System.out.print("Choice: ");
            input = scanner.nextLine().trim().toUpperCase();
            for (String c : choices)
            {
                if (input.equalsIgnoreCase(c)) return c.toUpperCase();
            }          
            System.out.println("Error: invalid state\n");
        }
    }

    /**
     * Prompts the user to enter a valid farm name.
     * The name must be at least 6 characters, not blank, 
     * and no leading/trailing spaces.
     * 
     * @param message the prompt message to display
     * @return the valid farm name 
     */
    public String acceptFarmName(String message)
    {
        String name;
        do
        {
        name = acceptStringInput(message);
        if (name.length() >= 6 && !name.startsWith(" ")
        && !name.endsWith(" ") && !name.trim().isEmpty())
            return name.trim();
        System.out.println
        ("Error: at least 6 characters is required.\n");
        }
        while(true);
    }
}
