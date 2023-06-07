import java.util.ArrayList;

public class Parser {
	private static ArrayList<String> tokens;
	private static int tokPointer=0;

	enum State{object,operation,slot,priority,end}
	private static State currentState;
	private static CustomConstraint output;//:ID:    0:GR:OFF:WK:24:DY:5:DT:2:XW:22:FY:0

	public static void main(String[] args) {
		//		try {
		//			
		//			//CustomConstraint cp = parse("driver 567 must be after week 33 with high priority");
		//			//System.out.println(cp.getRegex());
		//			//System.out.println(cp.mustAppear());
		//			System.out.println(getRegexAfter("29"));//20
		//		} catch (Exception e) {
		//			System.out.println(e.getMessage());
		//		}
		for (int c=1; c<99;c++) {
			try {
				String str=""+c;
				if (c<10)
					str = "0"+c;
				System.out.println(str +"\t"+getRegexAfter(str));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	private static String getRegexBefore(String num) throws Exception {
		//Take <num> and return an expression that matches in the range 0-<num> (num can only be up to 99)

		if (num.length()>2)
			throw (new Exception("Number out of range (allowable range is 1-99)"));
		if (num.equals("00"))
			throw (new Exception("Number out of range (allowable range is 1-99)"));
		if(num.equals("01"))
			return "\\b([0][0])\\b";

		String expr = "\\b(";
		int digit1 = num.charAt(0)-48;//less 48 to convert ASCII to dec
		int digit2 = num.charAt(1)-48;

		if (digit1>0) {
			expr = expr +"[";
			for (int c=0; c < digit1; c++)
				expr = expr + c;
			expr = expr +"][0-9]";
		}

		if((digit1>0)&&(digit2>0))
			expr = expr +"|";

		if(digit2>0) {
			expr = expr + digit1;
			expr = expr +"[";
			for (int c=0; c < digit2; c++)
				expr = expr + c;
			expr = expr +"]";
		}
		expr = expr + ")\\b";

		return expr;
	}

	private static String getRegexAfter(String num) throws Exception {
		//Take <num> and return an expression that matches in the range <num>  to 99

		if (num.length()>2)
			throw (new Exception("Number out of range (allowable range is 0-99)"));
		String expr = "\\b(";
		int digit1 = num.charAt(0)-48;//less 48 to convert ASCII to dec
		int digit2 = num.charAt(1)-48;
		if(digit1<9) {
			expr = expr +"[";
			for (int c=digit1+1; c <= 9; c++)
				expr = expr + c;
			expr = expr +"][0-9]";
		}
		
	

		if (digit2<9) {
			expr = expr +"|";
			
			expr = expr + digit1;
			expr = expr +"[";
			for (int c=digit2+1; c <= 9; c++)
				expr = expr + c;
			expr = expr +"]";
		}
		expr = expr + ")\\b";
		return expr;
	}

	public static CustomConstraint parse(String input) throws Exception {
		input = stripComments(input);
		input = input.strip();
		if (input.length()>0)
			output = new CustomConstraint(input);
		else 
			output = null;
		tokenize(input);
		String tok = nextToken();
		currentState = State.object;

		while (tok!=null){
			processToken(tok);
			tok = nextToken();
		}
		return output;
	}

	private static String stripComments(String input) {
		String res = "";
		for (char c: input.toCharArray()) {
			if (c=='#')
				return res;
			else
				res = res + c;
		}
		return res;
	}

	private static void processToken(String tok) throws Exception {
		//State determines what we should expect...
		switch(currentState) {
		case object:
			if (tok.equals("driver")) {
				String id = nextToken();
				//to do - validate id
				if (id.length()>5)
					throw (new Exception("Invalid driver ID (max length should be 5 chars)!"));
				while (id.length()<5)
					id = " "+ id;
				output.setRegex(":ID:" +id+":GR:...");
			}

			else if (tok.equals("group")) {
				String group = nextToken();
				//to do validate group
				if (group.length()>3)
					throw (new Exception("Invalid group ID (max length should be 3 chars)!"));

				while(group.length()<3)
					group = " " + group;
				output.setRegex(":ID:.....:GR:"+group);
			}

			else
				throw (new Exception("You need to specify a driver or a group!"));

			currentState = State.operation;

			break;

		case operation:
			if (!tok.equals("must"))
				throw (new Exception("Expected 'must', found '" + tok +","));

			tok = nextToken();
			if (tok.equals("not")) {
				//output.setRegex("-" + output.getRegex());
				output.mustAppear(false);
				tok = nextToken();
			}else
				//output.setRegex("+" + output.getRegex());
				output.mustAppear(true);

			if (!tok.equals("be"))
				throw (new Exception("Expected 'be', found '" + tok+"'"));
			currentState = State.slot;
			break;

		case slot:
			if (tok.equals("in")) {
				//read week
				tok = nextToken();
				if (tok.equals("week")) {
					tok = nextToken();
					int week;
					try {
						week = Integer.parseInt(tok);
					}catch(Exception e) {
						throw (new Exception("Week must be a number"));
					}
					//To do - validate week range
					if (week <10)
						tok = tok +"0"+week;
					output.setRegex(output.getRegex() + ":WK:"+tok);
					//					output = output + ":WK:"+tok;
					//					output = output + ":DY:.:DT:.:XW:..:FY:.";
					output.setRegex(output.getRegex() + ":DY:.:DT:.:XW:..:FY:.");
				}else
					throw (new Exception("Expected 'week'  found '" + tok+"'"));	
			}
			else if (tok.equals("before")) {

				//read week
				tok = nextToken();
				if (tok.equals("week")) {
					tok = nextToken();
					int week;
					try {
						week = Integer.parseInt(tok);
					}catch(Exception e) {
						throw (new Exception("Week must be a number"));
					}
					//To do - validate week range
					if (week <10)
						tok = /*tok +*/ "0"+week;
					//					output = output + ":WK:"+getRegexBefore(tok);
					output.setRegex(output.getRegex()+ ":WK:"+getRegexBefore(tok));
					//					output = output + ":DY:.:DT:.:XW:..:FY:.";
					output.setRegex(output.getRegex()+ ":DY:.:DT:.:XW:..:FY:.");
				}
			}
			else if (tok.equals("after")) {

				//read week
				tok = nextToken();
				if (tok.equals("week")) {
					tok = nextToken();
					int week;
					try {
						week = Integer.parseInt(tok);
					}catch(Exception e) {
						throw (new Exception("Week must be a number"));
					}
					//To do - validate week range
					if (week <10)
						tok = /*tok +*/"0"+week;
					//					output = output + ":WK:"+getRegexAfter(tok);
					//					output = output + ":DY:.:DT:.:XW:..:FY:.";
					output.setRegex(output.getRegex()+ ":WK:"+getRegexAfter(tok));
					//					output = output + ":DY:.:DT:.:XW:..:FY:.";
					output.setRegex(output.getRegex()+ ":DY:.:DT:.:XW:..:FY:.");
				}
				else
					throw (new Exception("Expected 'week'  found '" + tok+"'"));	

			}
			else
				throw (new Exception("Expected 'in' or 'before', found '" + tok+"'"));

			currentState = State.priority;
			break;
		case priority:
			if (!tok.equals(tok))
				throw (new Exception("Expected'with' found '"+tok+"'"));

			tok = nextToken();
			if (tok.equals("high")) {
				output.setPriority(ConstraintPriority.high);
			}
			else if (tok.equals("medium")) {
				output.setPriority(ConstraintPriority.medium);
			} 
			else if (tok.equals("low")) {
				output.setPriority(ConstraintPriority.low);
			}
			else
				throw (new Exception("Expected 'low', 'medium' or 'hight' found '"+tok+"'"));

			tok = nextToken();
			currentState = State.end;
			break;
		}
	}
	private static void tokenize(String input) {
		tokens = new ArrayList<String>();
		String buffer = "";
		for (char c : input.toCharArray()) {
			if (c !=' ')
				buffer = buffer + c;
			else {
				if (buffer.length() >0)
					tokens.add(buffer);
				buffer = "";
			}
		}
		if (buffer.length() >0)
			tokens.add(buffer);
		tokPointer =0;

	}

	private static String nextToken() {
		String tok = null;
		if (tokPointer < tokens.size()) {
			tok = tokens.get(tokPointer);
			tokPointer ++;
		}
		return tok;
	}
}
