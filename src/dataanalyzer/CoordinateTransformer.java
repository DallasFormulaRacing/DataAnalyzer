/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 *
 * @author aribdhuka
 */
public class CoordinateTransformer {
    
    public static double[][] performTransformation() {
        //get grav
        double[] orig = new double[]{-.21316, .116, .91};
        //calculate unit vector and save to e3
        double[] e3 = getUnits(orig);

        //get xAccel data
        double[] origForward = new double[]{-.616, -.073, .652};
        double[] x = subtractVectors(origForward, scalarByVector((getDotProduct(origForward, orig) / (getNormalizer(orig) * getNormalizer(orig))), orig));

        double[] e1 = getUnits(x);

         //do cross product for e2
        double[] e2 = getCrossProduct(e3, e1);

        double[][] rotVector = new double[][]{e1, e2, e3};

        return rotVector;
        
    }
    
    public static double[] scalarByVector(double scaler, double[] vector) {
        double[] scaled = new double[vector.length];
        for(int i = 0; i < vector.length; i++) {
            scaled[i] = vector[i] * scaler;
        }
        return scaled;
    }

    public static double[] getCrossProduct(double[] ei, double[] ej) {
        double[] crossProduct = new double[] {
                ei[1]*ej[2] - ei[2]*ej[1],
                ei[2]*ej[0] - ei[0]*ej[2],
                ei[0]*ej[1] - ei[1]*ej[0]
        };

        return crossProduct;

    }

    public static double getDotProduct(double[] ei, double[] ej) {
        double finalSum = 0;
        for(int i = 0; i < ei.length; i++) {
            finalSum += ei[i] * ej[i];
        }
        return finalSum;
    }

    public static double[] subtractVectors(double[] x, double[] y) {
        double[] sub = new double[x.length];
        for(int i = 0; i < x.length; i++) {
            sub[i] = x[i] - y[i];
        }

        return sub;

    }

    public static double[] getUnits(double[] orig) {
        double[] units = new double[orig.length];
        double normalizer = getNormalizer(orig);
        for(int i = 0; i < orig.length; i++) {
            if(normalizer != 0)
               units[i] = orig[i] / normalizer;
        }
        return units;

    }

    public static double getNormalizer(double[] orig) {
        double normalizer = 0;
        for(int i = 0; i < orig.length; i++) {
            normalizer += (orig[i] * orig[i]);
        }
        return Math.sqrt(normalizer);
    }
    
}
