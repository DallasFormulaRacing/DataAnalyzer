/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.mathchannel;


/**
 *
 * @author uli
 * @url https://ulrichbuschbaum.wordpress.com/2015/01/09/aligning-an-accelerometer-to-the-axes-of-a-vehicle/
 */
public class Mathematics {


    /**
     * Maps the vector to the skew-symmetric cross-product matrix
     *
     * @param vector a 3x1 array
     * @return a 3x3 matrix
     */
    public static double[][] skewSymmetriCrossProductMatrix3(double[] vector) {
        double[][] skewSymmetricMatrix = new double[3][3];

        skewSymmetricMatrix[0][0] = 0d;
        skewSymmetricMatrix[1][0] = vector[2];
        skewSymmetricMatrix[2][0] = -vector[1];

        skewSymmetricMatrix[0][1] = -vector[2];
        skewSymmetricMatrix[1][1] = 0d;
        skewSymmetricMatrix[2][1] = vector[0];

        skewSymmetricMatrix[0][2] = vector[1];
        skewSymmetricMatrix[1][2] = -vector[0];
        skewSymmetricMatrix[2][2] = 0d;

        return skewSymmetricMatrix;
    }

    /**
     * Returns the 3x3 identity matrix
     *
     * @return
     */
    public static double[][] identityMatrix3() {
        double[][] identityMatrix = new double[3][3];

        identityMatrix[0][0] = 1;
        identityMatrix[1][1] = 1;
        identityMatrix[2][2] = 1;

        return identityMatrix;
    }

    /**
     * Returns the cross product of two 3x1 vectors
     *
     * @param a
     * @param b
     * @return
     */
    public static double[] crossProduct3(double[] a, double[] b) {
        double[] result = new double[3];
        result[0] = a[1] * b[2] - a[2] * b[1];
        result[1] = a[2] * b[0] - a[0] * b[2];
        result[2] = a[0] * b[1] - a[1] * b[0];
        return result;
    }

    /**
     * Multiplies a 3x1 vector with a 3x3 matrix
     *
     * @param vector
     * @param matrix
     * @return
     */
    public static double[] multiplyVector3(double[] vector, double[][] matrix) {
        double[] result = new double[3];
        result[0] = vector[0] * matrix[0][0] + vector[1] * matrix[0][1] + vector[2] * matrix[0][2];
        result[1] = vector[0] * matrix[1][0] + vector[1] * matrix[1][1] + vector[2] * matrix[1][2];
        result[2] = vector[0] * matrix[2][0] + vector[1] * matrix[2][1] + vector[2] * matrix[2][2];
        return result;
    }

    /**
     * Returns the dot product of two 3x1 vectors
     *
     * @param a
     * @param b
     * @return
     */
    public static double dotProduct3(double[] a, double[] b) {
        double result = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return result;
    }

    public static double[] normalize3(double[] vector) {
        double length = length3(vector);

        vector[0] /= length;
        vector[1] /= length;
        vector[2] /= length;

        return vector;
    }

    /**
     * Calculates the length of a 3x1 vector
     *
     * @param vector
     * @return
     */
    public static double length3(double[] vector) {
        double result = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
        return result;
    }

    /**
     * Calculates the 3x3 rotation matrix for two given 3x1 vectors
     *
     * @param a
     * @param b
     * @return
     */
    @SuppressWarnings("AssignmentToMethodParameter")
    public static double[][] rotationMatrix3(double[] a, double[] b) {

        a = normalize3(a);
        b = normalize3(b);

        double[][] i = identityMatrix3(); //eye(3)
        double[][] v = skewSymmetriCrossProductMatrix3(crossProduct3(a, b)); //ssc(cross(A,B))

        double[][] v2 = multiplyMatrices3(v, v);//ssc(cross(A,B))^2
        double dotProduct = 1 - dotProduct3(a, b); //(1-dot(A,B))
        double crossProduct2 = length3(crossProduct3(a, b)) * length3(crossProduct3(a, b));//(norm(cross(A,B))^2)

        double[][] summand1 = addMatrices3(i, v);
        double[][] summand2 = divideMatrix3(
                multiplyMatrix3(v2, dotProduct),
                crossProduct2);

        double[][] result = addMatrices3(summand1, summand2);
        //eye(3) + ssc(cross(A,B)) + ssc(cross(A,B))^2*(1-dot(A,B))/(norm(cross(A,B))^2)
        return result;
    }

    /**
     * Adds to 3x3 matrics
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] addMatrices3(double[][] a, double[][] b) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    /**
     * Multiplies two 3x3 matrices
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] multiplyMatrices3(double[][] a, double[][] b) {
        double[][] result = new double[3][3];

        result[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0] + a[0][2] * b[2][0];
        result[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1] + a[0][2] * b[2][1];
        result[0][2] = a[0][0] * b[0][2] + a[0][1] * b[1][2] + a[0][2] * b[2][2];

        result[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0] + a[1][2] * b[2][0];
        result[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1] + a[1][2] * b[2][1];
        result[1][2] = a[1][0] * b[0][2] + a[1][1] * b[1][2] + a[1][2] * b[2][2];

        result[2][0] = a[2][0] * b[0][0] + a[2][1] * b[1][0] + a[2][2] * b[2][0];
        result[2][1] = a[2][0] * b[0][1] + a[2][1] * b[1][1] + a[2][2] * b[2][1];
        result[2][2] = a[2][0] * b[0][2] + a[2][1] * b[1][2] + a[2][2] * b[2][2];

        return result;
    }

    /**
     * Multiplies a 3x3 matrices with a factor
     *
     * @param matrix
     * @param factor
     * @return
     */
    public static double[][] multiplyMatrix3(double[][] matrix, double factor) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = matrix[i][j] * factor;
            }
        }
        return result;
    }

    /**
     * Divides a 3x3 matrix by the given value
     *
     * @param matrix
     * @param divider
     * @return
     */
    public static double[][] divideMatrix3(double[][] matrix, double divider) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = matrix[i][j] / divider;
            }
        }
        return result;
    }

}