import java.util.*;
import java.util.regex.*;

/**
* Practice Telephone (etude 02) Ori.
* @author Nikolah Pearce
*/


public class Telephone{

	private List<Pattern> patterns = new ArrayList<>();
	private List<String> usedNumbers = new ArrayList<String>();
	private boolean duplicate = false;

	private String initial1 = "^\\(?0800\\)? ?((\\w{6}[a-zA-Z]{0,3})|(\\d{3} ?-?\\d{3,4}))$";
	private String initial2 = "^\\(?0508\\)? ?((\\w{6}[a-zA-Z]{0,3})|(\\d{3} ?-?\\d{3,4}))$";
	private String initial3 = "^\\(?0900\\)? ?((\\w{5}[a-zA-Z]{0,4})|(\\d{5}))$";
	
	private String area1 = "^\\(?02\\)? ?409 ?-?\\d{4}$";
	private String area2 = "^\\(?0[34679]\\)? ?([2-8]|9(?!(00|11|99)))\\d{2} ?-?\\d{4}$";
	
	private String mobile1 = "^\\(?021\\)? ?((\\d{3} ?-?\\d{3,4})|(\\d{4} ?-?\\d{4}))$";
	private String mobile2 = "^\\(?02[27]\\)? ?\\d{3} ?-?\\d{4}$";
	private String mobile3 = "^\\(?025\\)? ?\\d{3} ?-?\\d{3}$";
	
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

	public boolean getDuplicate() {
		return this.duplicate;
	}

	public boolean isValid(String numberLine) {
		for (Pattern pattern : patterns) {
			Matcher m = pattern.matcher(numberLine);
			boolean b = m.matches();
 			if (b) {
 				return true;
 			}
		}
		return false;
	}

	public String format(String numberLine) {
		StringBuilder fixed = new StringBuilder("");
		this.duplicate = false;

		String code = "";
		String part1 = "";
		String part2 = "";

		numberLine = numberLine.replaceAll("-"," ");
		numberLine = numberLine.replaceAll("\\(","");
		numberLine = numberLine.replaceAll("\\)","");
		
		if (numberLine.charAt(1) == '2' && numberLine.charAt(2) == '5') {
			//special case need to change 025 over to 0274
			numberLine = numberLine.replaceAll(" ","");
			String change = "0274" + numberLine.substring(3);
			numberLine = change;
		}

		int firstSpace = numberLine.indexOf(" ");
		int lastSpace = numberLine.lastIndexOf(" ");

		code = pullCodeOut(numberLine, firstSpace);

		//Get out the number parts next
		if (lastSpace != firstSpace) {
			//code is nicely spaced, easy pull out the sectoin
			part1 = numberLine.substring(firstSpace + 1, lastSpace);
			part2 = numberLine.substring(lastSpace + 1);

		} else if (firstSpace < 0) { 
			//No spaces at all, MAKE SOME
			if ((code.equals("0800")) | (code.equals("0508"))) {
				part1 = numberLine.substring(4,7);
				part2 = numberLine.substring(7);
			} else if (code.equals("0900")) {
				part1 = numberLine.substring(4);
			} else if (code.equals("022") | code.equals("027")) {
				part1 = numberLine.substring(3,6);
				part2 = numberLine.substring(6);
			} else if ((code.length() == 2)) {
				part1 = numberLine.substring(2,5);
				part2 = numberLine.substring(5);
			} else {
				if (numberLine.length() == 3+8) {
					part1 = numberLine.substring(3,7);
					part2 = numberLine.substring(7);
				} else {
					part1 = numberLine.substring(3,6);
					part2 = numberLine.substring(6);
				}
			}
		} else {
			// Has exactly one space, must be a INTITIAL
			String part = numberLine.substring(5);
			
			if (part.length() == 5) {
				part1 = part;
			} else {
				String result = letterToNumber(part);
				if (code.equals("0900")) {
					part1 = result.substring(0,5);
				} else if (result.length() > 7) { 
					part1 = result.substring(0,3);
					part2 = result.substring(3,7);
				} else {
					part1 = result.substring(0,3);
					part2 = result.substring(3);
				}
			}
		}

		//Next part build string
		fixed.append(code + " ");
		fixed.append(part1);
		if (part2.length() > 0) {
			fixed.append(" " +part2);
		}
		String fixedString = fixed.toString();
		
		if (usedNumbers.contains(fixedString)) {
			this.duplicate = true;
		} else {
			usedNumbers.add(fixedString);
		}
		return fixedString;
	}

	public String pullCodeOut(String numberLine, int firstSpace) {
		String code;
		// Get out the code first
		if (firstSpace < 0) {
			//code isn't spaced so get manually
			if (numberLine.charAt(1) == '5' | numberLine.charAt(1) ==  '8' | numberLine.charAt(1) == '9') {
				//it is initial
				code = numberLine.substring(0, 4);
			} else if (numberLine.charAt(1) == '2' && numberLine.charAt(2) != '4' && numberLine.charAt(2) != '5') {
				// it is cellphone
				code = numberLine.substring(0, 3);
			} else {
				// it must be area
				code = numberLine.substring(0, 2);
			}
		} else { 
			//code is before a space break on whitespace
			code = numberLine.substring(0, firstSpace);
		}
		return code;
	}

	private String letterToNumber(String number) {
		char ch;
		String result = "";
		for (int i = 0; i < number.length(); i++) {
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
