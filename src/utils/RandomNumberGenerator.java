/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author toshiba
 */
public class RandomNumberGenerator {

    /*
     private final static int seed = 123456789;
     private final static Random random;

     static {
     random = new Random(seed);
     }

     public static double nextDouble() {
     return random.nextDouble();
     }

     public static double nextDoubleWithin(double lowerLimit, double upperLimit) {
     return (lowerLimit + (upperLimit - lowerLimit) * nextDouble());
     }

     public static int nextIntegerWithin(int lowerLimit, int upperLimit) {
     return (int) (lowerLimit + (upperLimit - lowerLimit) * nextDouble());
     }
     */
    private static double seed = 0.5;
    static double oldrand[] = new double[55];
    public static int jrand;

    private static Random rand = new Random((long) (seed * 1000000));

    private static SecureRandom sr;

    private static double[] randArr;
    private static int randArrIndex = 0;
    /* Get seed number for random and start it up */

    public static void randomize() {
        /*
         int j1;
         for (j1 = 0; j1 <= 54; j1++) {
         oldrand[j1] = 0.0;
         }
         jrand = 0;
         warmup_random(seed);
         return;
         */
        /* JAVA RANDOM */
        rand = new Random((long) (seed * Long.MAX_VALUE));
        /* MORE PRECISE RANDOM */
        //sr = new SecureRandom();
        //sr.setSeed((long) (seed * Long.MAX_VALUE));
        /* READ FROM FILE */
        /*
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("d:/rand_file.out"));
            randArr = new double[10000000];
            for (int i = 0; i < randArr.length; i++) {
                randArr[i] = Double.parseDouble(reader.readLine());
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
            System.exit(-1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(RandomNumberGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        */
    }

    /* Get randomize off and running */
    public static void warmup_random(double seed) {
        int j1, ii;
        double new_random, prev_random;
        oldrand[54] = seed;
        new_random = 0.000000001;
        prev_random = seed;
        for (j1 = 1; j1 <= 54; j1++) {
            ii = (21 * j1) % 54;
            oldrand[ii] = new_random;
            new_random = prev_random - new_random;
            if (new_random < 0.0) {
                new_random += 1.0;
            }
            prev_random = oldrand[ii];
        }
        advance_random();
        advance_random();
        advance_random();
        jrand = 0;
        return;
    }

    /* Create next batch of 55 random numbers */
    public static void advance_random() {
        int j1;
        double new_random;
        for (j1 = 0; j1 < 24; j1++) {
            new_random = oldrand[j1] - oldrand[j1 + 31];
            if (new_random < 0.0) {
                new_random = new_random + 1.0;
            }
            oldrand[j1] = new_random;
        }
        for (j1 = 24; j1 < 55; j1++) {
            new_random = oldrand[j1] - oldrand[j1 - 24];
            if (new_random < 0.0) {
                new_random = new_random + 1.0;
            }
            oldrand[j1] = new_random;
        }
    }

    /* Fetch a single random number between 0.0 and 1.0 */
    public static double randomperc() {
        //System.out.println(n_rand++);
        /*
         jrand++;
         if (jrand >= 55) {
         jrand = 1;
         advance_random();
         }
         double randResult = (double) oldrand[jrand];
         return randResult;
        */
        /* JAVA RANDOM */
        return rand.nextDouble();
        /* JAVA MORE PRECISE RANDOM */
        //return sr.nextDouble();
        /* READ FROM FILE */
        /*
         if(randArrIndex == randArr.length) {
         randArrIndex = 0;
         System.out.println("Rand Index Restarted");
         }
         double r = randArr[randArrIndex];
         randArrIndex++;
         return r;
         */
    }

    /* Fetch a single random integer between low and high including the bounds */
    public static int rnd(int low, int high) {
        int res;
        if (low >= high) {
            res = low;
        } else {
            res = (int) (low + (randomperc() * (high - low + 1)));
            if (res > high) {
                res = high;
            }
        }
        return (res);
    }

    /* Fetch a single random real number between low and high including the bounds */
    public static double rndreal(double low, double high) {
        return (low + (high - low) * randomperc());
    }

    /**
     * @param aSeed the seed to set
     */
    public static void setSeed(double aSeed) {
        seed = aSeed;
        randomize();
    }
}
