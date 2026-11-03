/**
 * Test Strategy for the Farm class.
 * This test strategy testing:
 *  - The default constructor
 *  - The non-default constructor (valid and invalid)
 *  - The toString/display method
 *  - The getter method (getName)
 *  - The setter method (setName), tested with valid and invalid inputs)
 *
 * @version 1.0
 * @author HSIN-I WU
 */

public class FarmTestStrategy
{
    public static void startFarmTest()
    {

        System.out.println("=======================================");
        System.out.println(" * Farm Class Test Strategy Starting ");
        System.out.println("=======================================");

        // testing default constructor
        System.out.println("[TESTING] the default constructor");
        Farm defaultFarm = new Farm();
        defaultFarm.display();

        // testing non-default constructor (valid)
        System.out.println
        ("[TESTING] the non-default constructor with valid field values");
        Farm validFarm = new Farm("Mildura", "VIC", 2, 1);
        validFarm.display();

        // testing non-default constructor (invalid)
        System.out.println
        ("[TESTING] the non-default constructor with invalid field values");
        Farm invalidFarm = new Farm("M", "ABC", -2, -1);
        invalidFarm.display();

        // testing getter method
        System.out.println("[TESTING] the getter method");
        System.out.println("Get Name: " + validFarm.getName());


        // testing setter method (valid)
        System.out.println
        ("[TESTING] the setter method with valid field values");
        validFarm.setName("AlpacaFarm");
        System.out.println("New Name: " + validFarm.getName());

        // testing setter method (invalid)
        System.out.println
        ("[TESTING] the setter method with invalid field values");
        invalidFarm.setName("1234");
        System.out.println("Invalid Name Set: " + invalidFarm.getName());

        System.out.println("\n All Farm class tests gooooooood!🤟");
    }
}
