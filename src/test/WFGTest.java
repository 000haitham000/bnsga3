/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import emo.Individual;
import emo.OptimizationProblem;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import javax.xml.stream.XMLStreamException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.WFG.Transitions;
import org.moeaframework.problem.WFG.WFG1;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author seadahai
 */
public class WFGTest {

    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException {
        int k = 2;
        int M = 3;
        int l = 2;
//        String inputFilePath = "F:\\IEEE-TEVC-DC-NSGA-III\\WFG\\wfg1\\z.txt";
//        String outputFilePathMoea = "F:\\IEEE-TEVC-DC-NSGA-III\\WFG\\wfg1\\F_MOEA_Java.txt";
//        String outputFilePathEvo = "F:\\IEEE-TEVC-DC-NSGA-III\\WFG\\wfg1\\F_EVO_Java.txt";
        String inputFilePath = "F:\\IEEE-TEVC-DC-NSGA-III\\test_wfg\\var_gen_0002.dat";
        String outputFilePathMoea = "F:\\IEEE-TEVC-DC-NSGA-III\\test_wfg\\var_gen_0002_moea.txt";
        String outputFilePathEvo = "F:\\IEEE-TEVC-DC-NSGA-III\\test_wfg\\var_gen_0002_evo.txt";
        double[][] z = MatlabIO.dlmread2D(inputFilePath);
        double[][] f_moea = wfg1MoeaFramework(k, l, M, z);
        double[][] f_evo = wfg1EvoMo(k, l, M, z);
        // Write f to a file
        MatlabIO.dlmwrite2D(outputFilePathMoea, f_moea);
        MatlabIO.dlmwrite2D(outputFilePathEvo, f_evo);
    }

    private static double[][] wfg1MoeaFramework(int k, int l, int M, double[][] z) {
        WFG1 wfg1 = new WFG1(k, l, M);
        Solution solution = new Solution(k + l, M);
        // Create an array to hold the objective functions
        double[][] f = new double[z.length][M];
        // Loop over all input vectors
        for (int i = 0; i < z.length; i++) {
            // Create a solution using an input z vector
            for (int j = 0; j < solution.getNumberOfVariables(); j++) {
                double uBound = 2 * (j + 1);
                solution.setVariable(j, new RealVariable(
                        z[i][j],
                        0,
                        uBound));
            }
            // Calculate the objective space of the solution
            wfg1.evaluate(solution);
            f[i] = solution.getObjectives();
        }
        return f;
    }

    private static double[][] wfg1EvoMo(int k, int l, int M, double[][] z)
            throws
            IOException,
            XMLStreamException,
            InvalidOptimizationProblemException {
        URL url = WFGTest.class.getResource("../samples/wfg1.xml");
        InputStream in = url.openStream();
        OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
        // Create Evaluator
        IndividualEvaluator individualEvaluator = new evaluators.wfg.WFG1(k, l, M);
        // Create Individual
        Individual individual = new Individual(optimizationProblem, individualEvaluator);
        // Create an array to hold the objective functions
        double[][] f = new double[z.length][M];
        // Loop over all input vectors
        for (int i = 0; i < z.length; i++) {
            // Create a solution using an input z vector
            for (int j = 0; j < individual.real.length; j++) {
                individual.real[j] = z[i][j];
            }
            // Calculate the objective space of the solution
            individualEvaluator.updateIndividualObjectivesAndConstraints(
                    optimizationProblem, 
                    individual);
            // Copy the results to f
            for (int j = 0; j < individual.getObjectivesCount(); j++) {
                f[i][j] = individual.getObjective(j);
            }
        }
        return f;
    }
}