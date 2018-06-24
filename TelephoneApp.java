import java.util.Scanner;
import java.util.regex.*;

/**
* Practice Telephone (etude 02)
* Learning Regexes in Java
* January 2018
* This class sets up and handles the input to pass to the Telephone class.
* @author Nikolah Pearce
*/


public class TelephoneApp{

	public static void main(String[] args) {

		Telephone numberChecker = new Telephone();

		int count = 0;
		boolean valid;
		boolean duplicate;
		String formattedNumber;

		Scanner scan = new Scanner(System.in);
		String numberLine;

		// Continue while there is still a telephone number incoming
		while (scan.hasNextLine()) {

			numberLine = scan.nextLine();
			valid = numberChecker.isValid(numberLine);

			if (valid) {
				// Format the number
				formattedNumber = numberChecker.format(numberLine);

				// Check if it is a duplicate
				duplicate = numberChecker.isDuplicate();

				// Print it to the screen
				System.out.print(formattedNumber);
				
				if (duplicate) {
					System.out.print(" DUP\n");
				} else {
					System.out.print("\n");
				}
			} else if (!valid)  {
				// Print and don't worry about formatting
				System.out.println(numberLine + " INV");
			}
			count++;
		}
		
		//System.out.println("Count is: " + count);
	}
}


			
