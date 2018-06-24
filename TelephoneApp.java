import java.util.Scanner;
import java.util.regex.*;

/**
* Practice Telephone (etude 02) for COSC326 SS.
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

		while (scan.hasNextLine()){
			String numberLine = scan.nextLine();

			valid = numberChecker.isValid(numberLine);

			if (valid) {
				formattedNumber = numberChecker.format(numberLine);
				duplicate = numberChecker.getDuplicate();
				System.out.print(formattedNumber);
				
				if (duplicate) {
					System.out.print(" DUP\n");
				} else {
					System.out.print("\n");
				}
			} else if (!valid)  {
				System.out.println(numberLine + " INV");
			}
			count++;
		}
		System.out.println("COunt is: " + count);

	}

}


			
