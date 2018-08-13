/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

/**
 *
 * @author seadahai
 */
public class RandomNumberGeneratorTest {

    public static void main(String[] args) {
        int n = 3;
        RandomNumberGenerator.setSeed(0.5);
        displaySample(n);
        RandomNumberGenerator.setSeed(0.8);
        displaySample(n);
        RandomNumberGenerator.setSeed(0.9);
        displaySample(n);
    }

    private static void displaySample(int n) {
        for (int i = 0; i < n; i++) {
            System.out.format("%3.2f ", RandomNumberGenerator.randomperc());
        }
        System.out.println();
    }
}
