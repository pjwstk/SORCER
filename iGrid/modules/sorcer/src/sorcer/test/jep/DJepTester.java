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

import org.lsmp.djep.djep.DJep;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * @author algerma
 * 
 */
public class DJepTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> expressions = new ArrayList<String>();
        DJep myParser = new DJep();
        JEPTester test = new JEPTester();

        myParser.addStandardFunctions(); // add basic functions
        myParser.addStandardConstants(); // add basic constants
        myParser.addComplex();
        myParser.setAllowUndeclared(true);
        myParser.setAllowAssignment(true);
        myParser.setImplicitMul(true);
        // Sets up standard rules for differentiating sin(x) etc.
        myParser.addStandardDiffRules();

        Object result = null;

        System.out.println("Testing Java Math Expression Parser (JEP)");
        System.out.println("Let: x = 2, y = 5, z = 10, myStr = Hi!");

        Integer xValue = 2, yValue = 5, zValue = 10;
        String myStrValue = "Hi!";

        myParser.addVariable("x", xValue);
        myParser.addVariable("y", yValue);
        myParser.addVariable("z", zValue);
        myParser.addVariable("myVar", myStrValue);

        myParser.addFunction("myFunc", test.new MyFunc());

        expressions.add("5x^2 * y");
        expressions.add("2x + 2y + z");
        expressions.add("diff(x, x)");
        expressions.add("diff(5x^2 + x*y, x)");
        expressions.add("diff(2x^(x*y+1), x)");
        expressions.add("diff((2x^2)*y, x)");
        expressions.add("diff(diff(2x^3, x), x)");

        try {
            for (int i = 0; i < expressions.size(); i++) {
                // This time the differentiation is specified by the diff(eqn,var) function
                System.out.println("Evaluating Function: " + expressions.get(i));
                Node node2 = myParser.parse(expressions.get(i));

                // To actually make diff do its work the equation needs to be preprocessed
                Node processed = myParser.preprocess(node2);

                // finally simplify
                Node simp2 = myParser.simplify(processed);

                // print the node
                System.out.print("Simplified Function: ");
                myParser.println(simp2);

                // evaluate it for the result
                System.out.println("*Result: " + myParser.evaluate(simp2));
            }
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("\n***Symbol Table***\n" + myParser.getSymbolTable());
    }

    /**
     * Example how to create a user defiened function for JEP
     * 
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
                Integer r = ((Integer) param) * ((Integer) param);
                // push the result on the inStack
                inStack.push(new Integer(r));
            }
            else {
                throw new ParseException("Invalid parameter type");
            }
        }

    }
}
