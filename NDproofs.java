import java.util.*;

// Proofs by Natural Deduction
// Made by Oh

class Op {

    // Operators
    // VAR - Variable. No operator.
    // CON - Conjunction - ^
    // DIS - Disjunction - v
    // IMP - Implication - >
    // NOT - Negation - ~

    public static final int VAR = 0;
    public static final int CON = 1;
    public static final int DIS = 2;
    public static final int IMP = 3;
    public static final int NOT = 4;


    public static final int IT = 0;
    public static final int IC = 1;
    public static final int EC = 2;
    public static final int ID = 3;
    public static final int ED = 4;
    public static final int II = 5;
    public static final int EI = 6;
    public static final int IN = 7;
    public static final int EN = 8;

	public static final String[] rule = {"IT", "I^", "E^", "Iv", "Ev", "I>", "E>", "I~", "E~"};
    public static final char[] opChar = {' ', '^', 'v', '>', '~'};

    public static boolean orderMattersCONDIS = true;
    public static boolean hideUpperLayers = false;
	public static boolean keepLooping = true;
}


class ReadArguments {

    public int intA, intB, intC;
    public int numInts;
    public Logic logicA;
    public boolean error;

    // LineMaker.isLogic(String stateString)
    public static boolean isInteger(String argString) {
	if (argString.length() > 8) return false;

        boolean isInteger = true;
        for (int i=0; i<argString.length(); i++) {
            char curChar = argString.charAt(i);
            if (curChar < '0' || curChar > '9')
                isInteger = false;
        }
        return isInteger;
    }

    private int analyseSubstring(String argString) {
        // Returns 0 if integer.
        // Returns 1 if Logic.
        // Returns -1 if neither.

        if(argString == null)
            return -1; // Error. no string.

        if(argString.length() == 0)
            return -1; // error. empty string.

        if(isInteger(argString))
            return 0; // Is Integer

        if(LineMaker.isLogic(argString))
            return 1; // Is Logic

        return -1; // Neither Integer or logic - Reject.
    }

    public ReadArguments(String inputString, int type1, int type2, int type3) {
        // Reads arguments in a certain format type1,type2,type3	

        // For type1, type2, type3,
        // 0 means integer required
        // 1 means logic required
        // -1 represents no input needed.
        numInts = 0;

        String[] argString = inputString.split(",");

        /*for (int i=0; i<argString.length; i++) { // DEBUGGING
          System.out.printf("|%s|",argString[i]);
          }System.out.println(argString.length);*/

        // Check if number of arguments correspond.
	if (type3 != -1){
		if (argString.length != 3) {error=true;return;}}
	else if (type2 != -1){
		if (argString.length != 2) {error=true;return;}}
	else{
		if (argString.length != 1) {error=true;return;}}

        if (type3 != -1){ // Check third argument.
                int stringType = analyseSubstring(argString[2]);

                if (type3 != stringType)
                {error=true;return;}
                else {
                    if (stringType == 0)
                        intC = Integer.parseInt(argString[2]);
                    if (stringType == 1)
                        logicA = LineMaker.readStatement(argString[2]);
                }	
        }

        if (type2 != -1){ // Check second argument. 
                int stringType = analyseSubstring(argString[1]);

                if (type2 != stringType)
	                {error=true;return;}
                else {
                    if (stringType == 0)
                        intB = Integer.parseInt(argString[1]);
                    if (stringType == 1)
                        logicA = LineMaker.readStatement(argString[1]);
                }	
        }

        { // type1 != -1 for sure. Check first argument. 
            int stringType = analyseSubstring(argString[0]);

            if (type1 != stringType)
            {error=true;return;}
            else {
                if (stringType == 0)
                    intA = Integer.parseInt(argString[0]);
                if (stringType == 1)
                    logicA = LineMaker.readStatement(argString[0]);
            }	
        }

    }
} // ReadArguments class - END



class Logic {

    // operator
    // VAR uses no a or b
    // NOT uses only a
    // IMP uses a => b
    // CON and DIS use a,b

    public int optr;

    public Logic a, b;
    public String varName;

    public Logic(){}

    public Logic(String varName) {

        this.varName = varName;
        //No operator. a variable itself or a truth.
        optr = Op.VAR;
    }

    public Logic(int varOptr, Logic varA) {

        // For negation operator.
        optr = varOptr;
        a = varA;

    }

    public Logic(int varOptr, Logic varA, Logic varB) {

        optr = varOptr;
        a = varA;
        b = varB;

    }

    public boolean compare(Logic logic2) {

        // If operators are different, immediately return false.
        if (optr != logic2.optr)
            return false;

        // Operators the same.
        if (optr == Op.VAR) // VAR: Compare variable names
            return varName.equals(logic2.varName); 

        if (optr == Op.NOT) // NOT: Compare statement A with statement A of logic2.
            return a.compare(logic2.a);

        // Operator is IMP
        if (optr == Op.IMP) // IMP: Both must be true.
            return a.compare(logic2.a) && b.compare(logic2.b);

        // Operator is DIS OR CON
        if (Op.orderMattersCONDIS) {
            // Order of Conjunction and Disjunction matters. AvB != BvA
            return a.compare(logic2.a) && b.compare(logic2.b);
        }
        else {
            // Order of Conjunction and Disjunction does not matter. AvB == BvA
            return (a.compare(logic2.a) && b.compare(logic2.b)) ||
                (a.compare(logic2.b) && b.compare(logic2.a));
        }

    }


    public String toStringBrackets() {
        //Outputs the line as a string. For all later iterations
        if (optr == Op.VAR)
            return varName;
        else if (optr == Op.NOT)
            return "~" + a.toStringBrackets();
        else //optr = v ^ >  one of these
            return "(" + a.toStringBrackets() + Op.opChar[optr] + b.toStringBrackets() + ")";

    }

    public String toString() {
        //Outputs the line as a string. For the topmost level, where brackets are not needed.
        if (optr == Op.VAR)
            return varName;
        else if (optr == Op.NOT)
            return "~" + a.toStringBrackets();
        else //optr = v ^ >  one of these
            return a.toStringBrackets() + Op.opChar[optr] + b.toStringBrackets();

    }

}



class LineClass {
    public int lineNum;
    public int hypoLine;
    public int layer;
    Logic statement;
    public String justification;

    public LineClass(int lineNum, Logic varStatement, int hypoLine, int layer, String justification) {

        this.lineNum = lineNum;
        statement = varStatement;
        this.hypoLine = hypoLine;
        this.layer = layer;
        this.justification = justification;
    }

}


class LineMaker {

    public static int _curLine = 0;
    public static int[] _lastHypo = new int[100]; // Maximum 100 layers.
    public static int _curLayer = 0;

    public static LineClass[] line = new LineClass[1000];

    public static void makeLine(Logic varStatement, String reason) {

        _curLine++;
        line[_curLine] = new LineClass(_curLine, varStatement, _lastHypo[_curLayer], _curLayer, reason);

    }//m.a.d.e.b.y.o.h

    public static void makeHypo(Logic hypoStatement) {

        _curLayer++;

        _lastHypo[_curLayer] = _curLine+1;
        makeLine(hypoStatement, "H");

    }

    public static void undo() {

        // If already at starting line, return.
        if (_curLine == 0) return;

        //If curLayer < layer of last line, then curLayer++
        //Else delete the last line
        if (_curLayer != line[_curLine].layer) {
            _curLayer = line[_curLine].layer;
            return;
        }
        else { // Same layer. So undo means delete line.
            // Removing line;
            _curLine--;
            if (_curLine != 0) _curLayer = line[_curLine].layer; // Set layer to layer of last line
            else _curLayer = 0; // Set layer to 0 cause no last line.
        }
    }

    public static void upOneLayer() {

        if (_curLayer > 0)
            _curLayer--;

    }

    public static boolean read(String inputString) {
        // Returns false to not print all lines after read.
        // Returns true to print all lines after read. Generally when a statement is accepted.

        //possible inputs:
        // a,b,c,L,1,2 are possible parameters.
        // a,b,c represent line numbers, L represents a logical statement.
        // 1 or 2 in E^ represents taking the left or right Statement respectively.

	if (inputString == null) return false;
	if (inputString.length() == 0) return false;

	if (inputString.equals("help")) {
		printHelp();
		return false;
	}
	if (inputString.equals("print")) {
		printAllLines();
		return false;
	}
	if (inputString.equals("hide")) {
		Op.hideUpperLayers = true;
		return true;
	}
	if (inputString.equals("show")) {
		Op.hideUpperLayers = false;
		return true;
	}
	if (inputString.equals("quit")) {
		System.out.println("PROGRAM TERMINATED. Made By Oh");
		Op.keepLooping = false;
		return false;
	}

        // 1) Up one Level - DISABLED
        // <
        /*if (inputString.equals("<")) {
            upOneLayer();
            return true;
        }*/

        // 2) Undo
        // u (include int parameter to run multiple numes)
        else if (inputString.equalsIgnoreCase("u")) {
            undo();
            return true;
        }

        // 3) Make a Premise
        // P L
        else if (inputString.substring(0,1).equalsIgnoreCase("P")) {

            if (inputString.length() >= 3) {
		if (inputString.charAt(1) != ' ') return error("Invalid input");

                String argString = inputString.substring(2,inputString.length());
                if (isLogic(argString)) {
                    if (_curLine != 0 && !line[_curLine].justification.equals("P"))
			return error("Unable to make additional premises at this point");
			// Last line is not premise.
                    // Generally, you can't make premises after any non-premise statement has been made.

                    makeLine(readStatement(argString), "P");
                    return true;
                }
                return error("Invalid logic");
            }
            else {
                return error("No premise entered");
            }
        }

        // 4) Make a Hypothesis
        // H L
        else if (inputString.substring(0,1).equalsIgnoreCase("A")) {
            if (inputString.length() >= 3) {
		if (inputString.charAt(1) != ' ') return error("Invalid input");

                String argString = inputString.substring(2,inputString.length());
                if (isLogic(argString)) {
                    makeHypo(readStatement(argString));
                    return true;
                }
                return error("Invalid logic");
            }
            else {
                return error("No hypothesis entered");
            }
        }

        // 5) Make a Line
        // IT, I^, E^, Iv, Ev, I>, E>, I~, E~
        // IT a
        // I^ a,b or b,a (different result)
        // E^ a,1
        // Iv a,L or L,a (different result)
        // Ev a,b,c (a must be a AvB statement. b and c interchangable)  
        // I> a,b   (a must be a hypothesis)
        // E> a,b   (a must be A, b must be A>B) 
        // I~ a,b   (b is an implication to a contradiction)
        // E~ a

        if (inputString.length() >= 4)
	{
		for (int i=0; i<9; i++) {
			String ruleString = inputString.substring(0,2);

			if (ruleString.equals(Op.rule[i])) {
				return makeDeduction(i, inputString.substring(3,inputString.length()), false);
			}
		}
	}

	boolean deductionTesting = false;
	for (int i=0; i<9; i++) {
		
		if (makeDeduction(i, inputString, true)) {
			deductionTesting = true;
			if (i == Op.EC)
				System.out.println("TRY: " + Op.rule[i]
						+ " " + inputString + ",1 OR "
						+ inputString + ",2");
			else if (i == Op.ID)
				System.out.println("TRY: " + Op.rule[i]
						+ " L," + inputString + " OR " 
						+ inputString + ",L");
			else
				System.out.println("TRY: " + Op.rule[i] + " " + inputString);
		}
	}
	if (deductionTesting)
		return false;

        return error("Invalid Input"); // Doesn't read anything. error
    } // LineReader.read(String inputString) - END


	public static boolean isActualLine(int lineNum) {
        	// Checks whether line refers to an actual statement
        	if (lineNum == 0) return false;
        	if (_curLine < lineNum) return false;

		return true;
	}

    public static boolean isEstablished(int lineNum) {
        // Checks whether 1) It refers to an actual statement
        // 2) Whether that statement is established. (Not a hypothesis)
	if (!isActualLine(lineNum)) return false;

        // 0 or greater than _curLine means not an actual statement.

        if (line[lineNum].layer > _curLayer) return false;
        // Error. trying to iterate result from hypothesis.

        if (line[lineNum].layer == _curLayer &&
                line[lineNum].hypoLine != _lastHypo[line[lineNum].layer]) return false;
        // hypo of the current line must be lasthypo of the current layer.
        // Error. Same layer but from wrong thread.

        return true; // No issues.
    }

	public static boolean makeDeduction(int rule, String argString, boolean testOnly){
//System.out.println(rule);
//System.out.println(argString);
	String inputString = testOnly ? null :  Op.rule[rule] + " " + argString;

        // IT a
        if (rule == Op.IT) {
            ReadArguments arg = new ReadArguments(argString,0,-1,-1);
            if (arg.error) return testOnly ? false : error("Invalid Arguments");

            if (isEstablished(arg.intA)) {
                if (!testOnly) makeLine(line[arg.intA].statement, inputString);
                return true;
            }
            else
                return testOnly ? false : error("Invalid line referenced");
        }

        // I^ a,b or b,a (different result)
        if (rule == Op.IC) {
            ReadArguments arg = new ReadArguments(argString,0,0,-1);
            if (arg.error) return testOnly ? false : error("Invalid Arguments");

            if (isEstablished(arg.intA) && isEstablished(arg.intB)) {
                if (!testOnly) makeLine(new Logic(Op.CON, line[arg.intA].statement,
					line[arg.intB].statement), inputString);
                return true;
            }
            else
                return testOnly ? false : error("Invalid line referenced");
        }

        // E^ a,1
        if (rule == Op.EC) {
		// For testOnly, only read one argument, the line.
            ReadArguments arg;
		if (testOnly) arg = new ReadArguments(argString,0,-1,-1);
		else arg = new ReadArguments(argString,0,0,-1);

            if (arg.error) return testOnly ? false : error("Invalid Arguments");


            if (!testOnly && arg.intB != 1 && arg.intB != 2)
		return testOnly ? false : error("Second argument needs to be 1 or 2");
            // 1: left input
            // 2: right input

            if (isEstablished(arg.intA)) {
                if (line[arg.intA].statement.optr != Op.CON)
                    return testOnly ? false : error("Line is not a conjunction");

                if (arg.intB == 1) {
                    if (!testOnly) makeLine(line[arg.intA].statement.a, inputString);}
                else { // arg.intB == 2
                    if (!testOnly) makeLine(line[arg.intA].statement.b, inputString);}
                return true;
            }
            else
                return testOnly ? false : error("Invalid line referenced");
        }

        // Iv a,L or L,a (different result)
        if (rule == Op.ID) {
		if (testOnly) {
			// Testing cases only. Not actual commadn 
			ReadArguments arg = new ReadArguments(argString,0,-1,-1);
			if (arg.error) return false;
			if (isEstablished(arg.intA)) return true;
			return false;
			} // testOnly - END

            ReadArguments arg = new ReadArguments(argString,1,0,-1);
            if (!arg.error) {
                // Choice 1: Logic, line
                if (isEstablished(arg.intB)) {
                    if (!testOnly) makeLine(new Logic(Op.DIS, arg.logicA, line[arg.intB].statement), inputString);
                    return true;
                }
                else
                	return testOnly ? false : error("Invalid line referenced");
            }
            else {
                // Choice 2: line, Logic
                arg = new ReadArguments(argString,0,1,-1);
            	if (arg.error) return testOnly ? false : error("Invalid Arguments");

                if (isEstablished(arg.intA)) {
                    if (!testOnly) makeLine(new Logic(Op.DIS, line[arg.intA].statement, arg.logicA), inputString);
                    return true;
                }
                else
                	return testOnly ? false : error("Invalid line referenced");
            } 

        }

        // Ev a,b,c (a must be a AvB statement. b and c interchangable)  
        if (rule == Op.ED) {
            ReadArguments arg = new ReadArguments(argString,0,0,0);
            if (arg.error) return testOnly ? false : error("Invalid Arguments");

            if (isEstablished(arg.intA) && isEstablished(arg.intB) && isEstablished(arg.intC)) {
                if (line[arg.intA].statement.optr != Op.DIS)
			return testOnly ? false : error("First line not disjunction");
                if (line[arg.intB].statement.optr != Op.IMP)
			return testOnly ? false : error("Second line not implication");
                if (line[arg.intC].statement.optr != Op.IMP)
			return testOnly ? false : error("Third line not implication");

                // A1vA2, B1>B2, C1>C2.
                // Show A1==B1, A2==C1, B2==C2; 
                Logic logicA1 = line[arg.intA].statement.a;
                Logic logicA2 = line[arg.intA].statement.b;

                Logic logicB1 = line[arg.intB].statement.a;
                Logic logicB2 = line[arg.intB].statement.b;

                Logic logicC1 = line[arg.intC].statement.a;
                Logic logicC2 = line[arg.intC].statement.b;

                if (logicA1.compare(logicB1) == false)
			return testOnly ? false : error("First and Second lines don't match");
                if (logicA2.compare(logicC1) == false)
			return testOnly ? false : error("First and Third lines don't match");
                if (logicB2.compare(logicC2) == false)
			return testOnly ? false : error("Second and Third lines don't match");

                // Fits all conditions.
                if (!testOnly) makeLine(logicB2, inputString);
                return true;
            }
            else
                return testOnly ? false : error("Invalid line referenced");

        }

        // I> a,b   (a must be a hypothesis)
        if (rule == Op.II) {
            ReadArguments arg = new ReadArguments(argString,0,0,-1);
            if (arg.error) return testOnly ? false : error("Invalid Arguments");

		if (!isActualLine(arg.intA) || !isActualLine(arg.intB))
			return testOnly? false : error("Invalid line referenced");

            // intA > intB
            // a and b are both in higher layers. Must be one layer above.
            // a must be the hypo of b.

            //if (line[arg.intA].statement.layer != line[arg.intB].statement.layer) return false; // different layer
            if (arg.intA != line[arg.intB].hypoLine)
		return testOnly ? false : error("First line is not the hypothesis of the second");

            // A being the hypo of B is sufficient.
            // It means that A and B must be of the same layer.
            if (line[arg.intA].layer == _curLayer+1) {
                if (!testOnly) makeLine(new Logic(Op.IMP, line[arg.intA].statement, line[arg.intB].statement), inputString);
                return true;
            }
            if (line[arg.intA].layer == _curLayer) {
                // Same layer. auto upOneLayer.
                if (!testOnly) upOneLayer();
                if (!testOnly) makeLine(new Logic(Op.IMP, line[arg.intA].statement, line[arg.intB].statement), inputString);
                return true;
            }
            return testOnly ? false : error("Lines from invalid layer"); // too many layers apart.	
        }

        // E> a,b   (a must be A, b must be A>B) 
        if (rule == Op.EI) {
            ReadArguments arg = new ReadArguments(argString,0,0,-1);
            if (arg.error) return testOnly ? false : error("Invalid Arguments");

            if (isEstablished(arg.intA) && isEstablished(arg.intB)) {
                if (line[arg.intB].statement.optr != Op.IMP)
			return testOnly ? false : error("Second line not implication");

                if (!line[arg.intA].statement.compare(line[arg.intB].statement.a))
			return testOnly ? false : error("Statements don't match");
                // A1 and A2>B, A1 and A2 not equal.

                // all conditions met.
                if (!testOnly) makeLine(line[arg.intB].statement.b, inputString);
                return true;
            }
            else
                return testOnly ? false : error("Invalid line referenced");
        }

        // I~ a   (a is an implication to a contradiction)
        if (rule == Op.IN) {
            ReadArguments arg = new ReadArguments(argString,0,-1,-1);
            if (arg.error) return testOnly ? false : error("Invalid Arguments");

            if (isEstablished(arg.intA)) {
                if (line[arg.intA].statement.optr != Op.IMP)
			return testOnly ? false : error("Not implication");
                if (line[arg.intA].statement.b.optr != Op.CON)
			return testOnly ? false : error("Does not imply a conjunction");

                //orderMattersCONDIS
                // A>(C1^C2). Show that C2 = ~C1. if orderMatters == false, alt C1 == ~C2
                Logic C1 = line[arg.intA].statement.b.a;
                Logic C2 = line[arg.intA].statement.b.b;

                if (C2.optr == Op.NOT && C2.a.compare(C1)) {
                    if (!testOnly) makeLine(new Logic(Op.NOT, line[arg.intA].statement.a), inputString);
                    return true;
                }
                if (Op.orderMattersCONDIS)
			return testOnly ? false : error("Does not imply something of the form A^~A");
		 // End here if the order matters. a^~a

                // If order doesn't matter, try ~a^a
                if (C1.optr == Op.NOT && C1.a.compare(C2)) {
                    if (!testOnly) makeLine(new Logic(Op.NOT, line[arg.intA].statement.a), inputString);
                    return true;
                }
                return testOnly ? false : error("Does not imply something of the form A^~A nor ~A^A");
            }
            else
                return testOnly ? false : error("Invalid line referenced");

        }

        // E~ a
        if (rule == Op.EN) {
            ReadArguments arg = new ReadArguments(argString,0,-1,-1);
            if (arg.error) return testOnly ? false : error("Invalid Arguments");

            if (isEstablished(arg.intA)) {
                Logic logicA = line[arg.intA].statement;

                // A == ~~B for some logic B
                if (logicA.optr == Op.NOT && logicA.a.optr == Op.NOT) {
                    if (!testOnly) makeLine(logicA.a.a, inputString);
                    return true;
                }
                return testOnly ? false : error("Statement not of the form ~~B");
            }
            else
                return testOnly ? false : error("Invalid line referenced");
        }
	return false;
	}

    public static boolean isChar(char checkChar) {
	if (checkChar == 'v' || checkChar == 'V')
		return false;

        if ((checkChar >= 'a') && (checkChar <= 'z'))
            return true;

        if ((checkChar >= 'A') && (checkChar <= 'Z'))
            return true;

        return false;
    }

    public static boolean isLogicSymbol(char checkChar) {

        if ((checkChar == '(') || (checkChar == ')'))
            return true;
        for (int i=1; i<5; i++)
            if (checkChar == Op.opChar[i])
                return true;

        return isChar(checkChar);
    }

    public static boolean isLogic(String stateString) {

        // A. Reject wrong statements here.

        // Rules (Fundamental):
        // Accepted symbols: ( ) ^ v > ~
        // And all characters.
        for (int i=0; i<stateString.length(); i++) {
            if (!isLogicSymbol(stateString.charAt(i)))
                return false; // unacceptable symbol detected
        }

        // Rules (Precedence):
        // Line must start with (, A or ~
        // Precede (: Any operators
        // Precede ): No operator other than )
        // Precede A: A or operator other than )
        // Precede ~: Any operator other than )
        // Precede v ^ >: ) or A

        {
            char prevChar = '@'; // @ Character used to represent start of line.

            for (int i=0; i<stateString.length(); i++) {
                char curChar = stateString.charAt(i);

                // Line must start with (, A or ~
                // Precede (: Any operators
		if (curChar == '(') {
                        if (isChar(prevChar)) return false;
		}

                // Precede ): No operator other than )
		if (curChar == ')') {
                        if (prevChar == '@') return false;
                        if (prevChar == '(') return false;
                        if (prevChar == Op.opChar[Op.CON]) return false;
                        if (prevChar == Op.opChar[Op.DIS]) return false;
                        if (prevChar == Op.opChar[Op.IMP]) return false;
                        if (prevChar == Op.opChar[Op.NOT]) return false;
		}

                // Precede ~: Any operator other than )
		if (curChar == Op.opChar[Op.NOT]) {
                        if (prevChar == ')') return false;
                        if (isChar(prevChar)) return false;
                }

                // Precede v ^ >: ) or A
                if (curChar == Op.opChar[Op.CON] || curChar == Op.opChar[Op.DIS]
				|| curChar == Op.opChar[Op.IMP]) {
                    if (prevChar == '@') return false;
                    if (prevChar == '(') return false;
                    if (prevChar == Op.opChar[Op.CON]) return false;
                    if (prevChar == Op.opChar[Op.DIS]) return false;
                    if (prevChar == Op.opChar[Op.IMP]) return false;
                    if (prevChar == Op.opChar[Op.NOT]) return false;
                }

                // Precede A: A or operator other than )
                if (isChar(curChar)) {
                    if (prevChar == ')') return false;
                }

                prevChar = curChar;
            }
            
        // Line must end with ) or A
	// now prevChar is the last character of the string.
        	if (prevChar == Op.opChar[Op.CON]) return false;
        	if (prevChar == Op.opChar[Op.DIS]) return false;
        	if (prevChar == Op.opChar[Op.IMP]) return false;
        	if (prevChar == Op.opChar[Op.NOT]) return false;
        	if (prevChar == '(') return false;
	}

        // Rules (Logical):
        // 1. Every ( must correspond to a )
        // 2. There cannot be two > symbols on the same bracket level.
        // 3. There cannot be both v or ^ on the same bracket level.

        //1.2.3.
        {
            boolean[] hasIMP = new boolean[100];
            boolean[] hasCONDIS = new boolean[100];

            int bracketLevel = 0;
            for (int i=0; i<stateString.length(); i++) {
                char curChar = stateString.charAt(i);

                if (curChar == '(')
                    bracketLevel++;

                else if (curChar == ')') {
                    // Exit bracket level. Clear counter for hasIMP and hasCONDIS
                    hasIMP[bracketLevel] = false;
                    hasCONDIS[bracketLevel] = false;
                    bracketLevel--;
                }

                else if (curChar == '>') {
                    if (hasIMP[bracketLevel])
                        return false; // Error. More than one > in bracket Level.
                    hasIMP[bracketLevel] = true;
                }

                else if (curChar == 'v' || curChar == '^') {
                    if (hasCONDIS[bracketLevel])
                        return false; // Error. More than one v/^ in bracket Level.
                    hasCONDIS[bracketLevel] = true;
                }

                if (bracketLevel < 0)
                    return false; // bracketLevel should never go below zero.
            }
            if (bracketLevel != 0)
                return false; // brackets not balanced.
        }
        return true;
    }

    public static Logic readStatement(String stateString) {
        // System.out.println(stateString); // DEBUGGING

        // B. Assuming statement is correct

        // Find the logic statement with the lowest priority.
        // Then split it into two substrings.

        // Creates a logical statement based on the string.

        // Operators: ( ) ^ v > ~
        // If anything is unreadable, it will taken as a variable name...


        // WE LOOK FOR THE LOWEST PRIORITY OPERATOR.
        // Lowest to highest:  1) >  2) ^,v  3) ~

        // WE IGNORE EVERYTHING WITHIN BRACKETS UNLESS EVERYTHING IS WITHIN BRACKETS.

        // ( adds one bracket level,  ) removes one bracket level. Bracket level 0 files are read.
        int bracketLevel = 0;

        // CASE 1:
        // Search for > symbol first.
        for (int i=0; i<stateString.length(); i++) {

            // Only search for chars at the highest bracket level
            if (stateString.charAt(i) == '(')
                bracketLevel++;
            else if (stateString.charAt(i) == ')')
                bracketLevel--;

            if ((bracketLevel == 0) && (stateString.charAt(i) == Op.opChar[Op.IMP])) {
                // Break up into two logical statements.
                return new Logic(Op.IMP, readStatement(stateString.substring(0,i)),
                        readStatement(stateString.substring(i+1,stateString.length())));
            }

        }
        
	// CASE  2:
        // Search for v or ^ symbol next.
        for (int i=0; i<stateString.length(); i++) {

            // Only search for chars at the highest bracket level
            if (stateString.charAt(i) == '(')
                bracketLevel++;
            else if (stateString.charAt(i) == ')')
                bracketLevel--;

            else {
                if ((bracketLevel == 0) && (stateString.charAt(i) == Op.opChar[Op.CON])) {
                    // Break up into two logical statements.
                    return new Logic(Op.CON, readStatement(stateString.substring(0,i)),
                            readStatement(stateString.substring(i+1,stateString.length())));
                }

                if ((bracketLevel == 0) && (stateString.charAt(i) == Op.opChar[Op.DIS])) {
                    // Break up into two logical statements.
                    return new Logic(Op.DIS, readStatement(stateString.substring(0,i)),
                            readStatement(stateString.substring(i+1,stateString.length())));
                }
            }

        }

        // CASE 3:
        //If we have reached this point, it means there are no >,v,^ characters at the highest bracket level.
        // The only possibility left is a statement of the form "~(statements)" or "variable"

        if (stateString.charAt(0) == Op.opChar[Op.NOT]) {
            return new Logic(Op.NOT, readStatement(stateString.substring(1,stateString.length())));
        }


        // CASE 4:
        // Next possibility. Everything within brackets.
        if ((stateString.charAt(0) == '(') && (stateString.charAt(stateString.length()-1) == ')'))
            return readStatement(stateString.substring(1,stateString.length()-1));


        // CASE 5:
        // Final possibility. No operators, nothing surrounded by brackets...
        // The rest must be a variable name.

        return new Logic(stateString);



    } // Read Statement - END

	public static void printHelp() {

		System.out.println("\n\n\n\n\n\n");

		System.out.println("----------------------");
		System.out.println("<<COMMAND LIST>>");
		System.out.println("----------------------");

		System.out.println("help - Help Window");
		System.out.println("print - Prints all lines.");
		System.out.println("hide - hide upper layers.");
		System.out.println("show - show upper layers.");
		System.out.println("quit - Exit Program");
		System.out.println("----------------------");

		System.out.println("<<COMMAND ARGUMENTS>>");
		System.out.println("L = Any Logical Statement");
	 	System.out.println("a,b,c = Line Numbers");
		System.out.println("1,2 = 1 or 2"); 
		System.out.println("----------------------");

		System.out.println("<<OPERATIONS>>  L,a,b,c,1,2 represent command arguments.");
		//System.out.println("1) < - Up one level");
		System.out.println("1) u - Undo");
		System.out.println("2) P L - Make a Premise");
		System.out.println("3) A L - Make a");
		System.out.println("4) Make a line. Commands below.");
		System.out.println("  a) IT a");
		System.out.println("  b) I^ a,b  OR  I^ b,a");
		System.out.println("  c) E^ a,1  OR  E^ a,2  (1 gets left element, 2 gets right)");
		System.out.println("  d) Iv a,L  OR  Iv L,a");
		System.out.println("  e) Ev a,b,c  (a must be AvB. b must be A>C and c must be B>C)");
		System.out.println("  f) I> a,b    (a must be a hypothesis leading to b.)");
		System.out.println("  g) E> a,b    (a must be A, b must be A>B)");
		System.out.println("  h) I~ a      (a must be an implication to a contradiction.)");
		System.out.println("  i) E~ a      (a must be of the form ~~A)");

		System.out.println("\nExtra) Key in line numbers (e.g. 1,4) for prompts on possible operations");
		System.out.println("----------------------");

	}


	public static boolean error(String message) {
		// Returns false with an error message.
		System.out.printf("error: %s\n", message);
		return false;
	}

    public static void printLine(int lineNum, int labelSpacing, boolean hypoHide) {

	String pFormat = "%" + labelSpacing + "d|";

        System.out.printf(pFormat, lineNum);

        for (int i=0; i<line[lineNum].layer; i++)
            System.out.print(" |");

        System.out.printf("%s          %s\n", line[lineNum].statement, line[lineNum].justification);

    }

	public static void printAllLines() {
		int n = 1;
		for (int i=_curLine; i>9; i/=10)
			n++;

            System.out.println("-----------");
            for (int i=1; i<=_curLine; i++) {
		if (Op.hideUpperLayers) {
			if (line[i].layer > _curLayer) {
				if (i == _lastHypo[_curLayer+1]) {
                			printLine(i,n,false);
					String pFormat = "%" + n + "d|";
        				System.out.printf(pFormat, i);
					System.out.println(" .............. ");
				}
			}
			else
                		printLine(i,n,false);
		}
		else
                	printLine(i,n,false);
		}
	}


}


public class NDproofs {

    public static void main(String [] args) {
	System.out.println("Natural Deduction Prover. Made by Oh");

        Scanner sc = new Scanner(System.in);

        LineMaker._lastHypo[0] = 0;

        while(Op.keepLooping && sc.hasNext()) {

            String input = sc.nextLine();
		//System.out.println(input);
            if(LineMaker.read(input))
            	LineMaker.printAllLines();

        }
    }

}
