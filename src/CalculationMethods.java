package radiotherapysuite;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Mickey Cyberkid
 */
public class CalculationMethods {
    
    // Calculate Equivalent Field Size
    public static double EquivalentFieldSize(double X, double Y){
        double eqfs = 0;
        if(X > 50 || Y > 50){
            JOptionPane.showMessageDialog(null, "Field Size Is Too Large!");
        }
        else{
            double calc1 = (2.015)*(X * Y);
            double calc2 = (1.015* X)+Y;
            eqfs = calc1/calc2;
            
        }
        return Double.parseDouble(toDP(eqfs,"1"));
    }
    
    //Calculat Reduced Field
    public static double ReducedFieldSize(double X, double Y, double area){
        double reducedField = Math.sqrt((X * Y) - area);
        return Double.parseDouble(toDP(reducedField,"1"));
    }
    // Calculate Dmax
    public static float calculate_dmax(float dosep, float TMRd){
        float DMAX = (dosep / TMRd);
        return DMAX;

    }
  
    public static int getIndexOf(double field, double[] dataset){
        int index = -1;
        for(int i = 0; i < dataset.length; i++){
            if(dataset[i] == field){
                return i;
            }
        }
        return index;
    }
    
    public static int[] getMinMax(double val, double[] dSet){
        int lower = 0;
        int higher = 0;
        Arrays.sort(dSet);
        for(double num : dSet){
            if(num < val){
                lower = (int)num;
            }else if(num  > val){
                higher = (int)num;
                break;
            }
        }
        System.out.println("Lower : "+lower+" - higher " + higher);
        return new int[]{lower, higher};

    }
    
    public static double getDecayFactor(){
        HashMap<Integer, Double> decayFactors = new HashMap<Integer, Double>();
        decayFactors.put(5,1.0000);
        decayFactors.put(6,0.9890);
        decayFactors.put(7,0.9781);
        decayFactors.put(8,0.9674);
        decayFactors.put(9,0.9567);
        decayFactors.put(10,0.9462);
        decayFactors.put(11,0.9358);
        decayFactors.put(12,0.9255);
        decayFactors.put(1,0.9153);
        decayFactors.put(2, 0.9052);
        decayFactors.put(3,0.8953);
        decayFactors.put(4, 0.8854);
        // Get current date
        // get month of year
        LocalDate date = LocalDate.now();
        int stored_month = date.getMonthValue();
        int dayOfMonth = date.getDayOfMonth();
        
        if(dayOfMonth < 7){
            stored_month = (stored_month - 2 + 12) % 12 + 1;
            double decay_factor = decayFactors.get(stored_month);
            return decay_factor;
        }
        else{
            stored_month = (stored_month - 1) % 12 + 1;
            double decay_factor = decayFactors.get(stored_month);
            return decay_factor;
        }
    
    }
    
    //Treatment Time Calculation
    public static double calculate_time(double DMAX, double scpValue){
        double shutter_time = 0.01;
        double dose_rate = 179.47;
        double sad_factor = 1.01;
        double decay_factor = getDecayFactor();
        double TIME_CALC = (float)((DMAX / (dose_rate * sad_factor * scpValue * decay_factor)) - shutter_time); 
        return TIME_CALC;
    }
    
    public static String toDP(double value, String dp){
        return String.format("%."+dp+"f", value);
    }
    // check if value exists
    public static boolean exists(double target, double[] array) {
        for (double num : array) {
            if (num == target) {
                return true;
            }
        }
        return false;
    }
    
    // get value per index of field
    public static double getValue(double field, String depth,HashMap<String, double[]> DataSheet){
        // uneccessray but I'd like to do it
        // for clarification
        double[] data = DataSheet.get("FS");
        double[] Depth_TMR = DataSheet.get(depth);
        double Value = 0.0;
        //check if field size is in Dataset
        if(exists(field, data)){
            // get TMR directly
            int index_field = getIndexOf(field, data);
            Value = Depth_TMR[index_field];
            return Value;
        }else{
            int[] X_vals = getMinMax(field, data);
            int x1 = X_vals[0];
            int x2 = X_vals[1];
            
            int x1_index = getIndexOf(x1, data);
            int x2_index = getIndexOf(x2, data);
            
            double y1 = Depth_TMR[x1_index];
            double y2 = Depth_TMR[x2_index];
            double delY = (y2 - y1);
            double delX = (x2 - x1);
            double eqn1 = delY/delX;
            double eqn2 = (field - x1) ;
            Value = (eqn1 * eqn2) + y1;
            return Value;
        }
    }
   
    // TODO: Make the function below generic (for scp pdd and tmr)
    // Get TMR | PDD directly | by INTERPOLATION
    public static double lookupValue(double field, String depth, HashMap<String, double[]> DataSheet){
        double[] data = DataSheet.get("FS");
        double[] Depth_TMR;
        if(DataSheet.containsKey(depth)){
            Depth_TMR = DataSheet.get(depth);
        }else{
            int depthOne = Integer.parseInt(depth) -1;
            int depthTwo = Integer.parseInt(depth) + 1;
            // Call get value method to get Values of the field at different depths
            double valueOne = getValue(field, String.valueOf(depthOne), DataSheet);
            double valueTwo = getValue(field, String.valueOf(depthTwo), DataSheet);
            // Take average of two values
            double Avg_Value = (valueOne + valueTwo)/2;
            return Avg_Value;
        }
        
        System.out.println(DataSheet);
        double Value = 0.0;
        //check if field size is in Dataset
        if(exists(field, data)){
            // get TMR directly
            int index_field = getIndexOf(field, data);
            Value = Depth_TMR[index_field];
            return Value;
        }else{
            int[] X_vals = getMinMax(field, data);
            int x1 = X_vals[0];
            int x2 = X_vals[1];
            
            int x1_index = getIndexOf(x1, data);
            int x2_index = getIndexOf(x2, data);
            
            double y1 = Depth_TMR[x1_index];
            double y2 = Depth_TMR[x2_index];
            double delY = (y2 - y1);
            double delX = (x2 - x1);
            double eqn1 = delY/delX;
            double eqn2 = (field - x1) ;
            Value = (eqn1 * eqn2) + y1;
            return Value;
        }

    }
    
    
    public static double scpValue(double field,HashMap<String, double[]> DataSheet){
        System.out.println("Gettiing values ...");
        double[] fields = DataSheet.get("FS");
        double[] data = DataSheet.get("SCP_SHEET");
        
        double Value = 0.0;
        //check if field size is in Dataset
        if(exists(field, fields)){
            // get SCP directly
            int index_field = getIndexOf(field, fields);
            Value = data[index_field];
            return Value;
        }else{
            
            int[] X_vals = getMinMax(field, fields);
            int x1 = X_vals[0];
            int x2 = X_vals[1];
            
            int x1_index = getIndexOf(x1, fields);
            int x2_index = getIndexOf(x2, fields);
            
            double y1 = data[x1_index];
            double y2 = data[x2_index];
            
            double delY = (y2 - y1);
            double delX = (x2 - x1);
            double eqn1 = delY/delX;
            double eqn2 = (field - x1) ;
            Value = (eqn1 * eqn2) + y1;
            return Value;
        }

    }
    
    public static double scatterValue(double field,HashMap<String, double[]> DataSheet, String source){
        System.out.println("Gettiing values ...");
        double[] fields = DataSheet.get("FS");
        double[] data = DataSheet.get(source);
        
        double Value = 0.0;
        //check if field size is in Dataset
        if(exists(field, fields)){
            // get SCP directly
            int index_field = getIndexOf(field, fields);
            Value = data[index_field];
            return Value;
        }else{
            
            int[] X_vals = getMinMax(field, fields);
            int x1 = X_vals[0];
            int x2 = X_vals[1];
            
            int x1_index = getIndexOf(x1, fields);
            int x2_index = getIndexOf(x2, fields);
            
            double y1 = data[x1_index];
            double y2 = data[x2_index];
            
            double delY = (y2 - y1);
            double delX = (x2 - x1);
            double eqn1 = delY/delX;
            double eqn2 = (field - x1) ;
            Value = (eqn1 * eqn2) + y1;
            return Value;
        }

    }
}
