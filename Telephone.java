import java.util.*;
import java.util.regex.*;

/**
* Practice Telephone (etude 02)
* Learning Regexes in Java
* January 2018
* @author Nikolah Pearce
*/

public class Telephone{

	private List<Pattern> patterns = new ArrayList<>();
	private List<String> usedNumbers = new ArrayList<String>();
	private boolean duplicate = false;

	/* The eight different regexes for 8 different telephone number types */
	private String initial1 = "^\\(?0800\\)? ?((\\w{6}[a-zA-Z]{0,3})|(\\d{3} ?-?\\d{3,4}))$";
	private String initial2 = "^\\(?0508\\)? ?((\\w{6}[a-zA-Z]{0,3})|(\\d{3} ?-?\\d{3,4}))$";
	private String initial3 = "^\\(?0900\\)? ?((\\w{5}[a-zA-Z]{0,4})|(\\d{5}))$";
	
	private String area1 = "^\\(?02\\)? ?409 ?-?\\d{4}$";
	private String area2 = "^\\(?0[34679]\\)? ?([2-8]|9(?!(00|11|99)))\\d{2} ?-?\\d{4}$";
	
	private String mobile1 = "^\\(?021\\)? ?((\\d{3} ?-?\\d{3,4})|(\\d{4} ?-?\\d{4}))$";
	private String mobile2 = "^\\(?02[27]\\)? ?\\d{3} ?-?\\d{4}$";
	private String mobile3 = "^\\(?025\\)? ?\\d{3} ?-?\\d{3}$";
	

	/** 
	 * Constructor that sets up and compiles the 8 regex patterns.
	 */
	public Telephone() {
		patterns.add(Pattern.compile(initial1));
		patterns.add(Pattern.compile(initial2));
		patterns.add(Pattern.compile(initial3));
		patterns.add(Pattern.compile(area1));
		patterns.add(Pattern.compile(area2));
		patterns.add(Pattern.compile(mobile1));
		patterns.add(Pattern.compile(mobile2));
		patterns.add(Pattern.compile(mobile3));
	}

	/**
	 * Checks to see if the current number has been seen before.
	 * @return true if the number is a duplicate.
	 * Duplicate being of the currently searched list from STDIN.
	 */
	public boolean isDuplicate() {
		return this.duplicate;
	}

	/** 
	 * Checks to see if the number is in a valid acceptable input format.
	 * @param numberLine the unfromatted telephone number as a String.
	 * @return true if the number is one of the valid accepted numbers defined.
	 */
	public boolean isValid(String numberLine) {
		for (Pattern pattern : patterns) {
			Matcher m = pattern.matcher(numberLine);
			boolean b = m.matches();
 			if (b) {
				// If it matches any of our regexes, return true
 				return true;
 			}
		}
		// No match found
		return false;
	}

	/**
	 * Format the telephone number string into acceptable output format.
	 * @param numberLine the unformatted telephone number as a String.
	 * @return the formatted telephone number as a String
	 */
	public String format(String numberLine) {
		StringBuilder fixedNumber = new StringBuilder("");
		this.duplicate = false;

		// The prefix e.g. 03 or 0800
		String code = "";
		String part1 = "";
		String part2 = "";

		// Wipe out any unnecessary punctuation 
		numberLine = numberLine.replaceAll("-"," ");
		numberLine = numberLine.replaceAll("\\(","");
		numberLine = numberLine.replaceAll("\\)","");
		
		// Check for our special 025 cases, changing to 0274 as per requirements
		if (numberLine.charAt(1) == '2' && numberLine.charAt(2) == '5') {
			numberLine = numberLine.replaceAll(" ","");
			String changedNumber = "0274" + numberLine.substring(3);
			numberLine = changedNumber;
		}

		int firstSpace = numberLine.indexOf(" ");
		int lastSpace = numberLine.lastIndexOf(" ");

		// Call the helper method to find the prefix
		code = pullCodeOut(numberLine, firstSpace);

		// Get out the number parts next
		if (lastSpace != firstSpace) {
			// Telephone number has > 1 space, easy to separate out the two number parts
			part1 = numberLine.substring(firstSpace + 1, lastSpace);
			part2 = numberLine.substring(lastSpace + 1);

		} else if (firstSpace < 0) { 
			// Telephone number has no spaces at all, let's add some in to help us later
			if ((code.equals("0800")) | (code.equals("0508"))) {
				part1 = numberLine.substring(4,7);
				part2 = numberLine.substring(7);
			} else if (code.equals("0900")) {
				part1 = numberLine.substring(4);
			} else if (code.equals("022") | code.equals("027")) {
				part1 = numberLine.substring(3,6);
				part2 = numberLine.substring(6);
			} else if ((code.length() == 2)) {
				// We have an area code e.g. 03 or 09
				part1 = numberLine.substring(2,5);
				part2 = numberLine.substring(5);
			} else {
				if (numberLine.length() == 3+8) {
					// We have an 8 digit cellphone number e.g. 021
					part1 = numberLine.substring(3,7);
					part2 = numberLine.substring(7);
				} else {
					// We have a 6 digit cellphone number
					part1 = numberLine.substring(3,6);
					part2 = numberLine.substring(6);
				}
			}
		} else {
			// Has exactly one space, must be a INTITIAL
			String part = numberLine.substring(5);
			if (part.length() == 5) {
				// Exactly 5 digit toll number, keep as is
				part1 = part;
			} else {
				// More than 5 digits, convert the letters by digits by helper method
				String result = letterToNumber(part);

				if (code.equals("0900")) {
					part1 = result.substring(0,5);
				} else if (result.length() > 7) { 
					// 8 digits or more, need to reduce ensure only 8
					part1 = result.substring(0,3);
					part2 = result.substring(3,7);
				} else {
					// 7 digits 
					part1 = result.substring(0,3);
					part2 = result.substring(3);
				}
			}
		}

		// Append all the code and parts back together into a String
		fixedNumber.append(code + " ");
		fixedNumber.append(part1);

		if (part2.length() > 0) {
			// Only append part2 if it has been set, else ignore
			fixedNumber.append(" " +part2);
		}
		String fixedString = fixedNumber.toString();
		
		if (usedNumbers.contains(fixedString)) {
			// Check if this number has been previously seen
			this.duplicate = true;
		} else {
			// Add this number to the list of previously seen numbers
			usedNumbers.add(fixedString);
		}
		return fixedString;
	}

	/**
	 * Pulls out and returns the prefix to a given telephone number. 
	 * @param numberLine the telephone number as a String.
	 * @param firstSpace the index of the first space in the numberLine.
	 * @return the code/prefix found e.g. 0800 or 027.
	 */
	public String pullCodeOut(String numberLine, int firstSpace) {
		String code;
		// Get out the code first
		if (firstSpace < 0) {
			// code isn't spaced so get manually
			if (numberLine.charAt(1) == '5' | numberLine.charAt(1) ==  '8' | numberLine.charAt(1) == '9') {
				// An initial number e.g. 0508, 0800 or 0900
				code = numberLine.substring(0, 4);
			} else if (numberLine.charAt(1) == '2' && numberLine.charAt(2) != '4' && numberLine.charAt(2) != '5') {
				// A cellphone number e.g. 027
				code = numberLine.substring(0, 3);
			} else {
				// An area number e.g. 03 
				code = numberLine.substring(0, 2);
			}
		} else { 
			// Code is before the first space, simply break on whitespace
			code = numberLine.substring(0, firstSpace);
		}
		return code;
	}

	/**
	 * Converts alphabet characters to their corresponding digits.
	 * As per a regular NZ telephone keypad.
	 * @param number the telephone number as a String, without its prefix/code.
	 * @return the telephone number with only numerical digits.
	 */
	private String letterToNumber(String number) {
		char ch;
		String result = "";
		for (int i = 0; i < number.length(); i++) {
			// Loop through each character, appending its number to result
			ch = number.charAt(i);
			switch(ch) {  
				case 'A': case 'B': case 'C':
	  				result += "2";
	  				break;  
	  			case 'D': case 'E': case 'F':  
	  			  	result += "3";
	  				break;  
	  			case 'G': case 'H': case 'I':  
	  			  	result += "4";
	  				break; 
	  			case 'J': case 'K': case 'L':  
	  			  	result += "5";
	  				break; 
	  			case 'M': case 'N': case 'O':  
	  			  	result += "6";
	  				break;
	  			case 'P': case 'Q': case 'R': case 'S': 
	  			  	result += "7";
	  				break; 
	  			case 'T': case 'U': case 'V':  
	  			  	result += "8";
	  				break; 
				case 'W': case 'X': case 'Y': case 'Z':  
	  			  	result += "9";
	  				break;  
	  			default: 
	  				result += ch;
	  		} 
	  	}
	  	return result;
	}

}
