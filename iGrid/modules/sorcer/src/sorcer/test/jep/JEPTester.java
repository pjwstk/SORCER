/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.test.jep;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class JEPTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> expressions = new ArrayList<String>();
        JEP myParser = new org.nfunk.jep.JEP();
        JEPTester test = new JEPTester();

        myParser.addStandardFunctions(); // add basic functions
        myParser.addStandardConstants(); // add basic constants

        Object result = null;

        System.out.println("Testing Java Math Expression Parser (JEP)");
        System.out.println("Let: x = 10, y = 20, z = 30, myStr = Hi!");

        Integer xValue = 10, yValue = 20, zValue = 30;
        String myStrValue = "Hi!";

        myParser.addVariable("x", xValue);
        myParser.addVariable("y", yValue);
        myParser.addVariable("z", zValue);
        myParser.addVariable("myVar", myStrValue);
        
        myParser.addFunction("myFunc", test.new MyFunc());

        expressions.add("x + y + z");
        expressions.add("-x - y - z");
        expressions.add("x * y * z");
        expressions.add("x < y");
        expressions.add("x >= y");
        expressions.add("x == y");
        expressions.add("(x + 10) == y");
        expressions.add("(x+10) == y && (y <= z)");
        expressions.add("x == y && (y <= z)");
        expressions.add("sin(z) == 1");
        expressions.add("myVar == \"Hi!\"");
        expressions.add("myFunc(x) == (x*x)");
        expressions.add("(y = x + z) > 0");
        expressions.add("x/y");

        for (int i = 0; i < expressions.size(); i++) {
            myParser.parseExpression(expressions.get(i));
            result = myParser.getValue();
            System.out.println("Evaluating: " + expressions.get(i) + "\n");
            System.out.println("Result: " + result.toString() + "\n\n");
        }
        
        System.out.println("list symboltable: " + myParser.getSymbolTable());
    }

    
    /**
     * Example how to create a user defiened function for JEP
     * @author Michael Alger
     *
     */
    class MyFunc extends PostfixMathCommand {
        
        public MyFunc() {
            // sets the default number of parameters of your funcion
            // -1 for unlimited parameters
            numberOfParameters = 1; 
        }
        
        public void run(Stack inStack) throws ParseException {
            // check the stack
            checkStack(inStack);

            // get the parameter from the stack
            Object param = inStack.pop();

            // check whether the argument is of the right type
            if (param instanceof Integer) {
                // calculate the result
                Integer r = ((Integer)param) * ((Integer)param);
                // push the result on the inStack
                inStack.push(new Integer(r));
            }
            else {
                throw new ParseException("Invalid parameter type");
            }
        }

    }
}
