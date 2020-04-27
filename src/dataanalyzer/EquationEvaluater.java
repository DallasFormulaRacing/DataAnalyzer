package dataanalyzer;

import com.arib.toast.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * equations used on raw data for graphing
 * @author aribdhuka
 */
//TODO: Need to display errors, need a parent obj
public class EquationEvaluater {
    
    //if vehicleData is not provided, provide an empty copy
    public static void evaluate(String eq, CategoricalHashMap dataMap, String channelTag) {
        evaluate(eq, dataMap, new VehicleData(), channelTag);
    }
    //if vehicleData is not provided, provide an empty copy
    public static void evaluate(String eq, CategoricalHashMap dataMap, String channelTag, double lowBound, double upBound) {
        evaluate(eq, dataMap, new VehicleData(), channelTag, lowBound, upBound);
    }
    //if vehicleData is not provided, provide an empty copy
    public static void summate(String eq, CategoricalHashMap dataMap, String channelTag) {
        summate(eq, dataMap, new VehicleData(), channelTag);
    }
    //if vehicleData is not provided, provide an empty copy
    public static void summate(String eq, CategoricalHashMap dataMap, String channelTag, double lowBound, double upBound) {
        summate(eq, dataMap, new VehicleData(), channelTag, lowBound, upBound);
    }
    
    
    public static void evaluate(String eq, CategoricalHashMap dataMap, VehicleData vehicleData, String channelTag) {
        String equationsStr = fixBuffers(eq);
        if(equationsStr.contains("asFunctionOf")) {
            createFunctionOf(equationsStr, dataMap, vehicleData, channelTag);
            return;
        }
        int varCount = validateEquation(equationsStr, dataMap, vehicleData);
        int index = 0;
        LinkedList<LogObject> dataList = new LinkedList<>();
        while(index < varCount) {
            long time = 0;
            Stack<Double> values = new Stack<>();
            Stack<String> operators = new Stack<>();
            String[] equation = equationsStr.split(" ");
            for(String element : equation) {
                if(isANumber(element.charAt(0)) || element.charAt(0) == '.') {
                    values.push(Double.parseDouble(element));
                }
                //if the element is a variable
                else if(element.charAt(0) == '$') {
                    String tag = element.substring(2, element.length() - 1);
                    LogObject lo = dataMap.getList(tag).get(index); //oh no get index of linked list, maybe make use of an iterator here to keep position to save some time.
                    time = lo.time;
                    if(lo instanceof SimpleLogObject)
                        values.push(((SimpleLogObject) lo).value);
                    else
                        break;
                }
                //if the element is a vehicle variables
                else if(element.charAt(0) == '&') {
                    //get the key
                    String key = element.substring(2, element.length() - 1);
                    //ask vehicle data for associating value
                    double value = vehicleData.get(key);
                    //push that value to the stack
                    values.push(value);
                }
                //if its open parentheses
                else if(element.charAt(0) == '(')
                    operators.push(element);
                //if closing
                else if(element.charAt(0) == ')') {
                    //do operations until we find open
                    while(!operators.peek().equals("(")) {
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //discard open
                    operators.pop();
                }
                //if its an operator
                else if(isAOperator(element.charAt(0))) {
                    //where operators is not empty and the next operator is greater than or the same operator
                    while(!operators.isEmpty() && precendenceCheck(operators.peek().charAt(0), element.charAt(0)) >= 0) {
                        //calc value
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //add current operator to stack
                    operators.push(element);
                }
            }
            //while operators are left
            while(!operators.isEmpty()) {
                //do operations
                values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
            }
            index++;
            if(channelTag.contains("Time,")) {
                SimpleLogObject slo = new SimpleLogObject(channelTag, values.pop(), time);
                slo.setCreationMethod(eq);
                dataList.add(slo);
            }
            else {
                SimpleLogObject slo = new SimpleLogObject("Time," + channelTag, values.pop(), time);
                slo.setCreationMethod(eq);
                dataList.add(slo);
            }
        }
        if(!dataList.isEmpty())
            dataMap.put(dataList);
        //TODO: Error message. No parent to send Toast or MessageBox to
        
    }
    
    public static void evaluate(String eq, CategoricalHashMap dataMap, VehicleData vehicleData, String channelTag, double lowBound, double upBound) {
        String equationsStr = fixBuffers(eq);
        if(equationsStr.contains("asFunctionOf")) {
            createFunctionOf(equationsStr, dataMap, vehicleData, channelTag);
            return;
        }
        int varCount = validateEquation(equationsStr, dataMap, vehicleData);
        int index = 0;
        LinkedList<LogObject> dataList = new LinkedList<>();
        double lastVal = 0;
        while(index < varCount) {
            long time = 0;
            Stack<Double> values = new Stack<>();
            Stack<String> operators = new Stack<>();
            String[] equation = equationsStr.split(" ");
            for(String element : equation) {
                if(isANumber(element.charAt(0)) || element.charAt(0) == '.') {
                    values.push(Double.parseDouble(element));
                }
                //if the element is a variable
                else if(element.charAt(0) == '$') {
                    String tag = element.substring(2, element.length() - 1);
                    LogObject lo = dataMap.getList(tag).get(index); //oh no get index of linked list, maybe make use of an iterator here to keep position to save some time.
                    time = lo.time;
                    if(lo instanceof SimpleLogObject)
                        values.push(((SimpleLogObject) lo).value);
                    else
                        break;
                }
                //if the element is a vehicle variables
                else if(element.charAt(0) == '&') {
                    //get the key
                    String key = element.substring(2, element.length() - 1);
                    //ask vehicle data for associating value
                    double value = vehicleData.get(key);
                    //push that value to the stack
                    values.push(value);
                }
                //if its open parentheses
                else if(element.charAt(0) == '(')
                    operators.push(element);
                //if closing
                else if(element.charAt(0) == ')') {
                    //do operations until we find open
                    while(!operators.peek().equals("(")) {
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //discard open
                    operators.pop();
                }
                //if its an operator
                else if(isAOperator(element.charAt(0))) {
                    //where operators is not empty and the next operator is greater than or the same operator
                    while(!operators.isEmpty() && precendenceCheck(operators.peek().charAt(0), element.charAt(0)) >= 0) {
                        //calc value
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //add current operator to stack
                    operators.push(element);
                }
            }
            //while operators are left
            while(!operators.isEmpty()) {
                //do operations
                values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
            }
            index++;
            
            //check if the current value is within the bounds. If not, set curr value to be whatever the last value within the bounds was.
            double currValue = values.pop();
            if(currValue < lowBound || currValue > upBound) {
                currValue = lastVal;
            }
            else {
                lastVal = currValue;
            }
            if(channelTag.contains("Time,")) {
                SimpleLogObject slo = new SimpleLogObject(channelTag, currValue, time);
                slo.setCreationMethod(eq+"%"+upBound+"%"+lowBound);
                dataList.add(slo);
            }
            else {
                SimpleLogObject slo = new SimpleLogObject("Time," + channelTag, currValue, time);
                slo.setCreationMethod(eq+"%"+upBound+"%"+lowBound);
                dataList.add(slo);
            }
        }
        if(!dataList.isEmpty())
            dataMap.put(dataList);
        //TODO: Error message. No parent to send Toast or MessageBox to

        
    }
    
    private static void createFunctionOf(String eq, CategoricalHashMap dataMap, VehicleData vehicleData, String channelTag) {
        //get the new equation and tag of the data set we are creating a function opf
        String[] equationAndVar = getFunctionOfTag(eq);
        //store those locally
        String equationsStr = equationAndVar[0];
        String functionOfTag = equationAndVar[1];
        //if the equation is replaced with error, something went wrong, do not continue;
        if(equationsStr.equals("ERROR")) {
            //TODO: Error message. No parent to send Toast or MessageBox to
            return;
        }
        //get the dataset we are getting the function of
        LinkedList<LogObject> functionOfList = dataMap.getList(functionOfTag);
        //if the data set is empty, do not continue
        if(functionOfList == null) {
            return;
        }
        int varCount = validateEquation(equationsStr, dataMap, vehicleData);
        int index = 0;
        LinkedList<LogObject> dataList = new LinkedList<>();
        while(index < varCount) {
            long time = 0;
            Stack<Double> values = new Stack<>();
            Stack<String> operators = new Stack<>();
            String[] equation = equationsStr.split(" ");
            for(String element : equation) {
                if(isANumber(element.charAt(0)) || element.charAt(0) == '.') {
                    values.push(Double.parseDouble(element));
                }
                //if the element is a variable
                else if(element.charAt(0) == '$') {
                    String tag = element.substring(2, element.length() - 1);
                    LogObject lo = dataMap.getList(tag).get(index); //oh no get index of linked list, maybe make use of an iterator here to keep position to save some time.
                    time = lo.time;
                    if(lo instanceof SimpleLogObject)
                        values.push(((SimpleLogObject) lo).value);
                    else
                        break;
                }
                //if its open parentheses
                else if(element.charAt(0) == '(')
                    operators.push(element);
                //if closing
                else if(element.charAt(0) == ')') {
                    //do operations until we find open
                    while(!operators.peek().equals("(")) {
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //discard open
                    operators.pop();
                }
                //if its an operator
                else if(isAOperator(element.charAt(0))) {
                    //where operators is not empty and the next operator is greater than or the same operator
                    while(!operators.isEmpty() && precendenceCheck(operators.peek().charAt(0), element.charAt(0)) >= 0) {
                        //calc value
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //add current operator to stack
                    operators.push(element);
                }
            }
            //while operators are left
            while(!operators.isEmpty()) {
                //do operations
                values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
            }
            index++;
            
            double functionOfValue = -1;
            for(LogObject lo : functionOfList) {
                if(lo.getTime() == time) {
                    if(lo instanceof SimpleLogObject) {
                        functionOfValue = ((SimpleLogObject) lo).getValue();
                    }
                    break;
                }
            }
            
            if(functionOfValue != -1) {
                FunctionOfLogObject folo = new FunctionOfLogObject(functionOfTag.substring(functionOfTag.indexOf(",") + 1, functionOfTag.length()) + "," + channelTag, values.pop(), functionOfValue);
                folo.setCreationMethod(eq);
                dataList.add(folo);
            }
        }
        dataMap.put(dataList);
    }
    
    public static void summate(String eq, CategoricalHashMap dataMap, VehicleData vehicleData, String channelTag) {
        String equationsStr = fixBuffers(eq);
        if(equationsStr.contains("asFunctionOf")) {
            createFunctionOf(equationsStr, dataMap, vehicleData, channelTag);
            return;
        }
        int varCount = validateEquation(equationsStr, dataMap, vehicleData);
        int index = 0;
        LinkedList<LogObject> dataList = new LinkedList<>();
        while(index < varCount) {
            long time = 0;
            Stack<Double> values = new Stack<>();
            Stack<String> operators = new Stack<>();
            String[] equation = equationsStr.split(" ");
            for(String element : equation) {
                if(isANumber(element.charAt(0)) || element.charAt(0) == '.') {
                    values.push(Double.parseDouble(element));
                }
                //if the element is a variable
                else if(element.charAt(0) == '$') {
                    String tag = element.substring(2, element.length() - 1);
                    LogObject lo = dataMap.getList(tag).get(index); //oh no get index of linked list, maybe make use of an iterator here to keep position to save some time.
                    time = lo.time;
                    if(lo instanceof SimpleLogObject)
                        values.push(((SimpleLogObject) lo).value);
                    else
                        break;
                }
                //if the element is a vehicle variables
                else if(element.charAt(0) == '&') {
                    //get the key
                    String key = element.substring(2, element.length() - 1);
                    //ask vehicle data for associating value
                    double value = vehicleData.get(key);
                    //push that value to the stack
                    values.push(value);
                }
                //if its open parentheses
                else if(element.charAt(0) == '(')
                    operators.push(element);
                //if closing
                else if(element.charAt(0) == ')') {
                    //do operations until we find open
                    while(!operators.peek().equals("(")) {
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //discard open
                    operators.pop();
                }
                //if its an operator
                else if(isAOperator(element.charAt(0))) {
                    //where operators is not empty and the next operator is greater than or the same operator
                    while(!operators.isEmpty() && precendenceCheck(operators.peek().charAt(0), element.charAt(0)) >= 0) {
                        //calc value
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //add current operator to stack
                    operators.push(element);
                }
            }
            //while operators are left
            while(!operators.isEmpty()) {
                //do operations
                values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
            }
            index++;
            if(channelTag.contains("Time,")) {
                if(!dataList.isEmpty()) {
                    SimpleLogObject slo = new SimpleLogObject(channelTag, values.pop() + ((SimpleLogObject) dataList.getLast()).getValue(), time);
                    slo.creationMethod = eq;
                    dataList.add(slo);
                }
                else {
                    SimpleLogObject slo = new SimpleLogObject(channelTag, values.pop(), time);
                    slo.creationMethod = eq;
                    dataList.add(slo);
                }

            }
            else {
                if(!dataList.isEmpty()) {
                    SimpleLogObject slo = new SimpleLogObject("Time," + channelTag, values.pop() + ((SimpleLogObject) dataList.getLast()).getValue(), time);
                    slo.setCreationMethod(eq+"%SUMM!");
                    dataList.add(slo);
                }
                else {
                    SimpleLogObject slo = new SimpleLogObject("Time," + channelTag, values.pop(), time);
                    slo.setCreationMethod(eq+"%SUMM!");
                    dataList.add(slo);
                }
            }
        }
        if(!dataList.isEmpty())
            dataMap.put(dataList);
        //TODO: Error message. No parent to send Toast or MessageBox to
        
    }
    
    public static void summate(String eq, CategoricalHashMap dataMap, VehicleData vehicleData, String channelTag, double lowBound, double upBound) {
        String equationsStr = fixBuffers(eq);
        if(equationsStr.contains("asFunctionOf")) {
            createFunctionOf(equationsStr, dataMap, vehicleData, channelTag);
            return;
        }
        int varCount = validateEquation(equationsStr, dataMap, vehicleData);
        int index = 0;
        LinkedList<LogObject> dataList = new LinkedList<>();
        double lastVal = 0;
        while(index < varCount) {
            long time = 0;
            Stack<Double> values = new Stack<>();
            Stack<String> operators = new Stack<>();
            String[] equation = equationsStr.split(" ");
            for(String element : equation) {
                if(isANumber(element.charAt(0)) || element.charAt(0) == '.') {
                    values.push(Double.parseDouble(element));
                }
                //if the element is a variable
                else if(element.charAt(0) == '$') {
                    String tag = element.substring(2, element.length() - 1);
                    LogObject lo = dataMap.getList(tag).get(index); //oh no get index of linked list, maybe make use of an iterator here to keep position to save some time.
                    time = lo.time;
                    if(lo instanceof SimpleLogObject)
                        values.push(((SimpleLogObject) lo).value);
                    else
                        break;
                }
                //if the element is a vehicle variables
                else if(element.charAt(0) == '&') {
                    //get the key
                    String key = element.substring(2, element.length() - 1);
                    //ask vehicle data for associating value
                    double value = vehicleData.get(key);
                    //push that value to the stack
                    values.push(value);
                }
                //if its open parentheses
                else if(element.charAt(0) == '(')
                    operators.push(element);
                //if closing
                else if(element.charAt(0) == ')') {
                    //do operations until we find open
                    while(!operators.peek().equals("(")) {
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //discard open
                    operators.pop();
                }
                //if its an operator
                else if(isAOperator(element.charAt(0))) {
                    //where operators is not empty and the next operator is greater than or the same operator
                    while(!operators.isEmpty() && precendenceCheck(operators.peek().charAt(0), element.charAt(0)) >= 0) {
                        //calc value
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //add current operator to stack
                    operators.push(element);
                }
            }
            //while operators are left
            while(!operators.isEmpty()) {
                //do operations
                values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
            }
            index++;
            
            //check if the current value is within the bounds. If not, set curr value to be whatever the last value within the bounds was.
            double currValue = values.pop();
            if(currValue < lowBound || currValue > upBound) {
                currValue = lastVal;
            }
            else {
                lastVal = currValue;
            }
            if(channelTag.contains("Time,")) {
                if(!dataList.isEmpty()) {
                    SimpleLogObject slo = new SimpleLogObject(channelTag, values.pop() + ((SimpleLogObject) dataList.getLast()).getValue(), time);
                    slo.creationMethod = eq;
                    dataList.add(slo);
                }
                else {
                    SimpleLogObject slo = new SimpleLogObject(channelTag, values.pop(), time);
                    slo.creationMethod = eq;
                    dataList.add(slo);
                }

            }
            else {
                if(!dataList.isEmpty()) {
                    SimpleLogObject slo = new SimpleLogObject("Time," + channelTag, values.pop() + ((SimpleLogObject) dataList.getLast()).getValue(), time);
                    slo.setCreationMethod(eq+"%"+upBound+"%"+lowBound+"%SUMM!");
                    dataList.add(slo);
                }
                else {
                    SimpleLogObject slo = new SimpleLogObject("Time," + channelTag, values.pop(), time);
                    slo.setCreationMethod(eq+"%"+upBound+"%"+lowBound+"%SUMM!");
                    dataList.add(slo);
                }
            }
        }
        if(!dataList.isEmpty())
            dataMap.put(dataList);
        //TODO: Error message. No parent to send Toast or MessageBox to

        
    }
    
    private static void createFunctionOfSummation(String eq, CategoricalHashMap dataMap, VehicleData vehicleData, String channelTag) {
        //get the new equation and tag of the data set we are creating a function opf
        String[] equationAndVar = getFunctionOfTag(eq);
        //store those locally
        String equationsStr = equationAndVar[0];
        String functionOfTag = equationAndVar[1];
        //if the equation is replaced with error, something went wrong, do not continue;
        if(equationsStr.equals("ERROR")) {
            //TODO: Error message. No parent to send Toast or MessageBox to
            return;
        }
        //get the dataset we are getting the function of
        LinkedList<LogObject> functionOfList = dataMap.getList(functionOfTag);
        //if the data set is empty, do not continue
        if(functionOfList == null) {
            return;
        }
        int varCount = validateEquation(equationsStr, dataMap, vehicleData);
        int index = 0;
        LinkedList<LogObject> dataList = new LinkedList<>();
        while(index < varCount) {
            long time = 0;
            Stack<Double> values = new Stack<>();
            Stack<String> operators = new Stack<>();
            String[] equation = equationsStr.split(" ");
            for(String element : equation) {
                if(isANumber(element.charAt(0)) || element.charAt(0) == '.') {
                    values.push(Double.parseDouble(element));
                }
                //if the element is a variable
                else if(element.charAt(0) == '$') {
                    String tag = element.substring(2, element.length() - 1);
                    LogObject lo = dataMap.getList(tag).get(index); //oh no get index of linked list, maybe make use of an iterator here to keep position to save some time.
                    time = lo.time;
                    if(lo instanceof SimpleLogObject)
                        values.push(((SimpleLogObject) lo).value);
                    else
                        break;
                }
                //if its open parentheses
                else if(element.charAt(0) == '(')
                    operators.push(element);
                //if closing
                else if(element.charAt(0) == ')') {
                    //do operations until we find open
                    while(!operators.peek().equals("(")) {
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //discard open
                    operators.pop();
                }
                //if its an operator
                else if(isAOperator(element.charAt(0))) {
                    //where operators is not empty and the next operator is greater than or the same operator
                    while(!operators.isEmpty() && precendenceCheck(operators.peek().charAt(0), element.charAt(0)) >= 0) {
                        //calc value
                        values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
                    }
                    //add current operator to stack
                    operators.push(element);
                }
            }
            //while operators are left
            while(!operators.isEmpty()) {
                //do operations
                values.push(doOperation(operators.pop().charAt(0), values.pop(), values.pop()));
            }
            index++;
            
            double functionOfValue = -1;
            for(LogObject lo : functionOfList) {
                if(lo.getTime() == time) {
                    if(lo instanceof SimpleLogObject) {
                        functionOfValue = ((SimpleLogObject) lo).getValue();
                    }
                    break;
                }
            }
            
            if(functionOfValue != -1) {
                String tagToUse = "";
                if(channelTag.split(",").length == 2) {
                    tagToUse = channelTag;
                } else {
                    tagToUse = functionOfTag.substring(functionOfTag.indexOf(",") + 1, functionOfTag.length()) + "," + channelTag;
                }
                if(!dataList.isEmpty()) {
                    FunctionOfLogObject folo = new FunctionOfLogObject(tagToUse, values.pop() + ((FunctionOfLogObject) dataList.getLast()).getValue(), functionOfValue);
                    folo.setCreationMethod(eq);
                    dataList.add(folo);
                } else {
                    FunctionOfLogObject folo = new FunctionOfLogObject(tagToUse, values.pop(), functionOfValue);
                    folo.setCreationMethod(eq);
                    dataList.add(folo);
                }
                
            }
        }
        dataMap.put(dataList);
    }

    
    //Validate Equation String
    private static int validateEquation(String equation, CategoricalHashMap dataMap, VehicleData vehicleData) {
        int doVariablesExist = validateVars(equation, dataMap, vehicleData);
        if(doVariablesExist < 0)
            return doVariablesExist;
        
        return doVariablesExist;
        
    }
    
    private static String[] getFunctionOfTag(String equation) {
        String[] str = equation.split("asFunctionOf");
        if(str.length > 2) {
            return new String[] {"ERROR", "ERROR"};
        }
        String var = str[1];
        var = var.substring(var.indexOf('(', 2) + 1, var.indexOf(')'));
        return new String[] {equation.substring(0, equation.indexOf("asFunctionOf")), var};   
    }
    
    private static String fixBuffers(String equation) {
        //firstly remove all spaces, so we have a clean sheet to work with
        String eq = "";
        for(int i = 0; i < equation.length(); i++) {
            if(equation.charAt(i) != ' ')
                eq += equation.charAt(i);
        }
        
        String addedBuffer = "";
        for(int i = 0; i < eq.length(); i++) {
            //if its a $ or & keep it and add a space
            if(eq.charAt(i) == '$' || eq.charAt(i) == '&') {
                int jumpTo = eq.indexOf(")", i);
                addedBuffer += eq.substring(i, jumpTo+1) + " ";
                i = jumpTo;
            }
            //if its a number
            else if(isANumber(eq.charAt(i)) || eq.charAt(i) == '.') {
                //add it
                addedBuffer += eq.charAt(i);
                //if its not followed by another number, add a space
                if(i != eq.length() - 1 && !isANumber(eq.charAt(i+1)) && eq.charAt(i+1) != '.')
                    addedBuffer += " ";
            }
            //if its a operator, output and add a space;
            else if(isAOperator(eq.charAt(i))) {
                addedBuffer += eq.charAt(i) + " ";
            }
            //if current is a character
            else if(isACharacter(eq.charAt(i))) {
                //if next is a character
                if(i != eq.length() - 1 && isACharacter(eq.charAt(i+1)))
                    //add without space
                    addedBuffer += eq.charAt(i);
                else
                    addedBuffer += eq.charAt(i) + " ";
            }
        }
        return addedBuffer;
        
    }
    
    /**
     * Checks if all variables inputted by user exist and are of same size, if variables are usable, returns the length of elements. returns negative number on error
     * @param equation equation as the string inputted by user
     * @param dataMap log data to get variables from.
     * @return -1 if log data is empty, -2 if the variable does not exist, -3 if the variables are of different sizes
     */
    private static int validateVars(String equation, CategoricalHashMap dataMap, VehicleData vehicleData) {
        //get all string variables from equation string
        ArrayList<String> vars = new ArrayList<>();
        ArrayList<String> vehicleVars = new ArrayList<>();
        for(int i = 0; i < equation.length(); i++) {
            if(equation.charAt(i) == '$')
                vars.add(equation.substring(i+2, equation.indexOf(")", i)));
            if(equation.charAt(i) == '&')
                vehicleVars.add(equation.substring(i+2, equation.indexOf(")", i)));
        }
        
        //see if all variables exist, we can make assumption tags list is valid if not empty
        if(dataMap.tags.isEmpty())
            if(vars.size() > 0)
                return -1;
        else {
            for(String s : vars) {
                if(!dataMap.tags.contains(s))
                    return -2;
            }
        }
        
        //see if all vehicle data variables exist
        if(vehicleData.getKeySet().isEmpty()) {
            if(vehicleVars.size() > 0)
                return -1;
        } else {
            for(String s : vehicleVars) {
                if(!vehicleData.getKeySet().contains(s))
                    return -2;
            }
        }
        
        //if all variables are of same length, we must make assumption they are valid, more complex validity of checking each index for having the same time value
        int sizeToBe = -1;
        for(String s : vars) {
            if(sizeToBe == -1)
                sizeToBe = dataMap.getList(s).size();
            else
                if(dataMap.getList(s).size() != sizeToBe)
                    return -3;
        }
        
        return sizeToBe;
        
    }
    
    private static boolean isANumber(char c) {
        return (c >= 48 && c <= 57);
    }
    
    private static boolean isAOperator(char c) {
        return (c == '/' || c == '*' || c == '+' || c == '-' || c == '(' || c == ')');
    }
    
    private static boolean isACharacter(char c) {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }
    
    private static double doOperation(char operator, double val1, double val2) {
        switch (operator) {
            case '+' : return val1 + val2;
            case '-' : return val2 - val1;
            case '*' : return val1 * val2;
            case '/' : return (val1 == 0 ? 0 : (val2 / val1));
            default : return -1;
        }
    }
    
    //checks if operator1 is >= operator2
    private static int precendenceCheck(char operator1, char operator2) {        
        if(operator2 == '(' || operator2 == ')') 
            return -1;
        if(operator1 == '*' || operator1 == '/') 
            return 1; 
        if((operator1 == '+' || operator1 == '-') && (operator2 == '+' || operator2 == '-'))
            return 1;
        else
            return -1; 
    }
}
