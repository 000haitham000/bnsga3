/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author toshiba
 */
public class Mathematics {

    /**
     * Default Precision
     */
    public static final double EPSILON = 1e-10;//1e-100;

    public static double approximate(double x) {
        int decimalPlaces = 0;
        if (Mathematics.compare(x, 0) != 0) {
            decimalPlaces = getPrecisionDecimalPlacesCount();
        }
        return approximate(x, decimalPlaces);
    }

    public static int getPrecisionDecimalPlacesCount() {
        int decimalPlaces = 0;
        double tempEpsilon = EPSILON;
        while (Math.abs(tempEpsilon) < 1) {
            tempEpsilon *= 10;
            decimalPlaces++;
        }
        return decimalPlaces;
    }

    public static int getPrecisionDecimalPlacesCount(double epsilon) {
        int decimalPlaces = 0;
        while (Math.abs(epsilon) < 1) {
            epsilon *= 10;
            decimalPlaces++;
        }
        return decimalPlaces;
    }

    public static double approximate(double x, int decimalPlaces) {
        if (decimalPlaces == 0) {
            return x;
        }
//        double temp = Math.pow(10, decimalPlaces-1);
//        double temp2 = x * temp;
//        double numerator = Math.round(temp2);
//        double denominator = Math.pow(10, decimalPlaces-1);
//        return numerator / denominator;
        String approxString = "#.";
        for (int i = 0; i < decimalPlaces; i++) {
            approxString += "#";
        }
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(df.format(x));
    }

    public static int nchoosek(int n, int k) {
        int i;
        double prod;
        prod = 1.0;

        if (n == 0 && k == 0) {
            return 1;
        } else {
            for (i = 1; i <= k; i++) {
                prod = prod * (double) ((double) (n + 1 - i) / (double) i);
            }

            return (int) (prod + 0.5);
        }
    }

    public static int compare(double num1, double num2) {
        return compare(num1, num2, EPSILON);
    }

    public static int compare(double num1, double num2, double delta) {
        if (Math.abs(num1 - num2) < delta) {
            return 0;
        }
        if (num1 > num2) {
            return 1;
        } else {
            return -1;
        }
    }

    // Gaussian elimination with partial pivoting
    public static double[] gaussianElimination(double[][] A, double[] b) throws SingularMatrixException {
        int N = b.length;
        for (int p = 0; p < N; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p];
            A[p] = A[max];
            A[max] = temp;
            double t = b[p];
            b[p] = b[max];
            b[max] = t;

            // singular or nearly singular
            if (Math.abs(A[p][p]) <= EPSILON) {
                throw new SingularMatrixException();
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < N; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        // back substitution
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }

    /*
     public static IdValuePair[] getSortedHypervolumeParameterCombinations(List<String> hypervolumesParametersList) {
     IdValuePair[] idValuePairs = new IdValuePair[hypervolumesParametersList.size()];
     for (int i = 0; i < hypervolumesParametersList.size(); i++) {
     String[] splits = hypervolumesParametersList.get(i).split("_");
     String id = "(" + splits[0] + "," + splits[1] + ")";
     double hypervolume = Double.parseDouble(splits[2]);
     idValuePairs[i] = new IdValuePair(id, hypervolume);
     }
     Arrays.sort(idValuePairs);
     return idValuePairs;
     }
     */
    static double getAverage(double[] values) {
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum / values.length;
    }

    public static double[] getApproximateCopy(double[] arr, int decimalPlaces) {
        double[] arrCopy = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < Math.pow(10, -1 * decimalPlaces)) {
                arrCopy[i] = 0;
            } else {
                arrCopy[i] = arr[i];
            }
        }
        return arrCopy;
    }

    public static double[] getVector(double[] p1, double[] p2) {
        double[] v = new double[p1.length];
        for (int i = 0; i < p1.length; i++) {
            v[i] = p2[i] - p1[i];
        }
        return v;
    }

    public static class SingularMatrixException extends Exception {

        public SingularMatrixException() {
            super("Matrix is singular or nearly singular");
        }

        public SingularMatrixException(String message) {
            super(message);
        }

        public String toString() {
            return getMessage();
        }
    }

    public static double getNonNegativesAverage(double[] arr) {
        double average = 0.0;
        int count = 0;
        for (double num : arr) {
            if (num >= 0) {
                average += num;
                count++;
            }
        }
        if (count == 0) {
            return -1;
        }
        return average / count;
    }

    public static double getNonNegativesAverage(int[] arr) {
        double[] dArr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            dArr[i] = arr[i];
        }
        return getNonNegativesAverage(dArr);
    }

    public static int getMinIndex(double[] arr) {
        int counter = 0;
        while (counter < arr.length && Double.isNaN(arr[counter])) {
            counter++;
        }
        if (counter == arr.length) {
            // All array elements are NANs
            return -1;
        } else {
            int index = counter++;
            while (counter < arr.length) {
                if (arr[counter] < arr[index]) {
                    index = counter;
                }
                counter++;
            }
            return index;
        }
    }

    public static int getMinIndex(int[] arr) {
        double[] dArr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            dArr[i] = arr[i];
        }
        return getMinIndex(dArr);
    }

    public static int getMaxIndex(double[] arr) {
        int counter = 0;
        while (counter < arr.length && Double.isNaN(arr[counter])) {
            counter++;
        }
        if (counter == arr.length) {
            // All array elements are NANs
            return -1;
        } else {
            int index = counter++;
            while (counter < arr.length) {
                if (arr[counter] > arr[index]) {
                    index = counter;
                }
                counter++;
            }
            return index;
        }
    }

    public static int getMaxIndex(int[] arr) {
        double[] dArr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            dArr[i] = arr[i];
        }
        return getMaxIndex(dArr);
    }

    public static int getMedianIndex(double[] arr) {
        List<IndexValuePair> indexValuePairs = new ArrayList<IndexValuePair>();
        for (int i = 0; i < arr.length; i++) {
            if (Double.isNaN(arr[i])) {
                // Ignore NaN values
                continue;
            }
            indexValuePairs.add(new IndexValuePair(i, arr[i]));
        }
        if (indexValuePairs.isEmpty()) {
            return -1;
        }
        Collections.sort(indexValuePairs);
        return indexValuePairs.get(indexValuePairs.size() / 2).index;
    }

    public static int getMedianIndex(int[] arr) {
        double[] dArr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            dArr[i] = arr[i];
        }
        return getMedianIndex(dArr);
    }

    public static int getNonNegativesMedianIndex(double[] arr) {
        List<IndexValuePair> indexValuePairs = new ArrayList<IndexValuePair>();
        for (int i = 0; i < arr.length; i++) {
            if (Double.isNaN(arr[i])) {
                // Ignore NaN values
                continue;
            }
            if (arr[i] >= 0) {
                // Consider onle non-negative values (i.e. exclude -ve values
                // and consider only Zero and positive values)
                indexValuePairs.add(new IndexValuePair(i, arr[i]));
            }
        }
        if (indexValuePairs.isEmpty()) {
            return -1;
        }
        Collections.sort(indexValuePairs);
        return indexValuePairs.get(indexValuePairs.size() / 2).index;
    }

    public static int getNonNegativesMedianIndex(int[] arr) {
        double[] dArr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            dArr[i] = arr[i];
        }
        return getNonNegativesMedianIndex(dArr);
    }

    public static double getMin(double[] arr) {
        int index = getMinIndex(arr);
        if (index == -1) {
            return Double.NaN;
        } else {
            return arr[index];
        }
    }

    public static int getMin(int[] arr) {
        return arr[getMinIndex(arr)];
    }

    public static double getMax(double[] arr) {
        int index = getMaxIndex(arr);
        if (index == -1) {
            return Double.NaN;
        } else {
            return arr[index];
        }
    }

    public static int getMax(int[] arr) {
        return arr[getMaxIndex(arr)];
    }

    public static double getMedian(double[] arr) {
        int index = getMedianIndex(arr);
        if (index == -1) {
            return Double.NaN;
        } else {
            return arr[index];
        }
    }

    public static int getMedian(int[] arr) {
        return arr[getMedianIndex(arr)];
    }

    public static double getNonNegativesMedian(double[] arr) {
        int index = getNonNegativesMedianIndex(arr);
        if (index == -1) {
            return Double.NaN;
        } else {
            return arr[index];
        }
    }

    public static int getNonNegativesMedian(int[] arr) {
        return arr[getNonNegativesMedianIndex(arr)];
    }

    public static double getStandardDeviation(double[] values) {
        double mean = 0.0;
        for (int i = 0; i < values.length; i++) {
            mean += values[i];
        }
        mean /= values.length;
        return getStandardDeviation(values, mean);
    }

    public static double getStandardDeviation(int[] values) {
        double[] dArr = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            dArr[i] = values[i];
        }
        return getStandardDeviation(dArr);
    }

    public static double getStandardDeviation(double[] values, double mean) {
        double stdDev = 0.0;
        for (int i = 0; i < values.length; i++) {
            stdDev += Math.pow(values[i] - mean, 2);
        }
        stdDev /= values.length;
        stdDev = Math.sqrt(stdDev);
        return stdDev;
    }

    public static class IndexValuePair implements Comparable<IndexValuePair> {

        private int index;
        private double value;

        public IndexValuePair(int index, double value) {
            this.index = index;
            this.value = value;
        }

        /**
         * @return the index
         */
        public int getIndex() {
            return index;
        }

        /**
         * @param index the index to set
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * @return the value
         */
        public double getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public int compareTo(IndexValuePair indexValuePair) {
            return compare(this.value, indexValuePair.value, 1e-100);
        }
    }

    public static double[] getUnitVector(double[] v) {
        double[] weightVector = new double[v.length];
        for (int j = 0; j < v.length; j++) {
            //weightVector[j] = individuals[i].getObjective(j) - individualEvaluator.getIdealPoint()[j];
            weightVector[j] = v[j] == 0 ? 0.0000000001 : v[j];
        }
        double norm = 0;
        for (int j = 0; j < weightVector.length; j++) {
            norm += Math.pow(weightVector[j], 2);
        }
        norm = Math.sqrt(norm);
        for (int j = 0; j < weightVector.length; j++) {
            weightVector[j] /= norm;
        }
        return weightVector;
    }

    public static double getEuclideanDistance(
            double[] v1,
            double[] v2) {
        double powerSum = 0;
        for (int i = 0; i < v1.length; i++) {
            powerSum += Math.pow(v1[i] - v2[i], 2);
        }
        return Math.sqrt(powerSum);
    }

    public static double getDotProduct(double[] v1, double[] v2) {
        double d = 0.0;
        for (int i = 0; i < v1.length; i++) {
            d += v1[i] * v2[i];
        }
        return d;
    }

    public static double getNorm(double[] v) {
        double refDirNorm;
        refDirNorm = 0.0; // Eventually will be the NORM of the direction
        for (int k = 0; k < v.length; k++) {
            refDirNorm += Math.pow(v[k], 2);
        }
        refDirNorm = Math.sqrt(refDirNorm); // After this line refDirNorm will be the NORM of the reference direction
        return refDirNorm;
    }

    public static double[] multiply(double s, double[] v) {
        double[] sv = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            sv[i] = s * v[i];
        }
        return sv;
    }

    public static double[] add(double s, double[] v) {
        double[] sv = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            sv[i] = s + v[i];
        }
        return sv;
    }

    public static double[] add(double[] v1, double[] v2) {
        double[] sum = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            sum[i] = v1[i] + v2[i];
        }
        return sum;
    }

    /**
     * Get the perpendicular distance from point v to vector u
     *
     * @param v
     * @param u
     * @return
     */
    public static double getPerpendicularDistance(double[] v, double[] u) {
        double[] proj = getProjection(v, u);
        return getDistance(proj, v);
    }

    public static double[] getProjection(double[] v, double[] u) {
        double scalarProjection = getScalarProjection(v, u);
        return Mathematics.multiply(scalarProjection, Mathematics.getUnitVector(u));
    }

    public static double getScalarProjection(double[] v, double[] u) {
        double dot = Mathematics.getDotProduct(v, u);
        double refDirNorm = Mathematics.getNorm(u);
        return dot / refDirNorm;
    }

    public static double getDistance(double[] v1, double[] v2) {
        return getNorm(add(v1, multiply(-1, v2)));
    }

    public static double[] medianEachRow(double[][] matrix) {
        double[] arr = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            arr[i] = Mathematics.getMedian(matrix[i]);
        }
        return arr;
    }

    public static double[] meanEachRow(double[][] matrix) {
        double[] arr = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            arr[i] = Mathematics.getAverage(matrix[i]);
        }
        return arr;
    }

    public static void main(String[] args) {

        // TEST CASE (1)
        double[] arrD = {0.5, -2.1, 1.5, 3.01, -7.1, 8.6, 9.5, 4.88, 5.02, 6.0, 10};
        // >> Correct answers
        // Min = -7.1
        // Min Index = 4
        // Max = 10
        // Max Index = 10
        // Median = 4.88
        // Median Index = 7
        // Non-negatives Median = 5.02
        // Non-negatives Median Index = 8
        // >> Test
        System.out.println("Min = " + getMin(arrD));
        System.out.println("Min Index = " + getMinIndex(arrD));
        System.out.println("Man = " + getMax(arrD));
        System.out.println("Man Index = " + getMaxIndex(arrD));
        System.out.println("Median = " + getMedian(arrD));
        System.out.println("Median Index = " + getMedianIndex(arrD));
        System.out.println("Non-negatives Median = " + getNonNegativesMedian(arrD));
        System.out.println("Non-negatives Median Index = " + getNonNegativesMedianIndex(arrD));

        // TEST CASE (2)
        double[] arrI = {0, 0, 2, 1, 3, -7, -8, -9, 4, 5, -6, -10};
        // >> Correct answers
        // Min = -10
        // Min Index = 11
        // Max = 5
        // Max Index = 9
        // Median = 0
        // Median Index = 1
        // Non-negatives Median = 2
        // Non-negatives Median Index = 2
        // >> Test
        System.out.println("Min = " + getMin(arrI));
        System.out.println("Min Index = " + getMinIndex(arrI));
        System.out.println("Man = " + getMax(arrI));
        System.out.println("Man Index = " + getMaxIndex(arrI));
        System.out.println("Median = " + getMedian(arrI));
        System.out.println("Median Index = " + getMedianIndex(arrI));
        System.out.println("Non-negatives Median = " + getNonNegativesMedian(arrI));
        System.out.println("Non-negatives Median Index = " + getNonNegativesMedianIndex(arrI));

        // TEST CASE (3)
        double[] arrDwithNans = {Double.NaN, 0.5, -2.1, 1.5, 3.01, -7.1, 8.6, 9.5, 4.88, 5.02, 6.0, 10, Double.NaN};
        // >> Correct answers
        // Min = -7.1
        // Min Index = 5
        // Max = 10
        // Max Index = 11
        // Median = 4.88
        // Median Index = 8
        // Non-negatives Median = 5.02
        // Non-negatives Median Index = 9
        // >> Test
        System.out.println("Min = " + getMin(arrDwithNans));
        System.out.println("Min Index = " + getMinIndex(arrDwithNans));
        System.out.println("Man = " + getMax(arrDwithNans));
        System.out.println("Man Index = " + getMaxIndex(arrDwithNans));
        System.out.println("Median = " + getMedian(arrDwithNans));
        System.out.println("Median Index = " + getMedianIndex(arrDwithNans));
        System.out.println("Non-negatives Median = " + getNonNegativesMedian(arrDwithNans));
        System.out.println("Non-negatives Median Index = " + getNonNegativesMedianIndex(arrDwithNans));

        // >> Test precision
        double x = 0;
        double y = 0.0000000001;
        System.out.println(compare(x, y));
        y = 0.00000000009;
        System.out.println(compare(x, y));
        
        
        // >> Test NaN values with getMinIndex
        System.out.println("---");
        double[] arr2 = {4.856735325965714E-6, 4.1895877528047383E-7, 0.00992861470031564, 0.08568158429197138, 0.006699608754079388, 
            0.36429645901648583, 0.02890625894646863, /*Double.NaN,*/ 1.2205352502947448E-10, 1.2781828756653672E-6, 0.17998373326580608,
            
            0.008640180744116557, 0.39768098256728335, 1.624365886030851E-9, 9.488864800337724E-10, 2.715135983766521E-5, 
            
            0.016616813299221295, 0.07642507729341613, 0.009960379439334451, 0.006411855489266737, 0.009101886663799855, 

            0.009422522057756596, 8.439698611211303E-9, 1.003748019792701E-10, 0.018786232477744157, 8.342932798538062E-11, 

            1.976322388357676E-12, 0.005420910840777983, 0.00979311941534942, 3.5997095188231422E-12, 0.416335780728617, 

            5.253758319488653E-9, 3.917347947320176E-4, /*Double.NaN,*/ 2.292853421645462E-5, 0.020782299595071992, 2.8607710440305822E-8, 
            0.008386171151004812, 9.124575014747903E-8, 0.0010583282861377338, 1.0239726663836935E-10, 0.007706092768045827, 
            0.009213803451620007, 6.220598144587533E-11, 1.0229012611149217E-7, 0.034941237047089214, 0.04561717489774028, 
            2.2796261946563718E-10, 0.006025240637838938, 0.022487735298686973, 8.794444246397285E-12, 3.313145957678273E-7, 
            0.007867206318372575, 0.030683369932689903, 1.624944063873375E-4, 0.014150631498672865, 1.4137280493552055E-9, 
            0.009421860702607523, 0.008100169909967371, 0.02079405535364617, /*Double.NaN,*/ 2.4115598345397485E-10, 4.0950079339010393E-10, 
            8.97129812549495E-6, /*Double.NaN,*/ 2.676602928852323E-5, 4.2501908149319654E-10, 0.009086292127241347, 4.228463038017759E-7, 
            1.993947814357373E-9, 9.518163400934805E-7, 1.0143605983675315E-10, 0.48213834149756113, 1.8813753184127247E-8, 
            1.1972824854804537E-10, 0.009957305667056522, 1.6053544969934232E-12, /*Double.NaN,*/ 0.006349546029433461, 
            3.7623571051730516E-10, 5.1318205560273734E-8, 1.3236623619321262E-10, 0.010048943075032351, 0.008961187608513497, 
            1.9186138818282694E-5, 2.895948655662186E-9, 4.602653936946698E-9, 0.02024567134801752, 0.009861127900860358, 
            0.008758046874226117, 9.381427371453387E-10, 5.121067468653927E-8};
        System.out.println(getMedianIndex(arr2));
    }
}
