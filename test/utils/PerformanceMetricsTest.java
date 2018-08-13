/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import utils.PerformanceMetrics;
import utils.InputOutput;
import emo.Individual;
import emo.OptimizationProblem;
import engines.NSGA2Engine;
import evaluators.ZDT3ModifiedEvaluator;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;

/**
 *
 * @author toshiba
 */
public class PerformanceMetricsTest {
    
    public PerformanceMetricsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of calculateHyperVolumeForTwoObjectivesOnly method, of class PerformanceMetrics.
     */
    @Test
    public void testCalculateHyperVolumeForTwoObjectivesOnly() throws
            XMLStreamException,
            InvalidOptimizationProblemException {
        System.out.println("calculateHyperVolumeForTwoObjectivesOnly");
        // Load ZDT1 test problem (or any other two objectives problem)
        String problemFilePath = "../samples/zdt1-02-30.xml";
        OptimizationProblem optimizationProblem = InputOutput.getProblem(problemFilePath);
        IndividualEvaluator evaluator = new ZDT3ModifiedEvaluator();
        NSGA2Engine geneticEngine = new NSGA2Engine(optimizationProblem, evaluator);
        // Create an array of individuals
        Individual[] individuals = {
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator)
        };
        // Override the original objective values
        individuals[0].setObjective(0, 3);
        individuals[0].setObjective(1, 4);
        individuals[1].setObjective(0, 2);
        individuals[1].setObjective(1, 5);
        individuals[2].setObjective(0, 1);
        individuals[2].setObjective(1, 7);
        individuals[3].setObjective(0, 5);
        individuals[3].setObjective(1, 2);
        // Create the reference point
        double[] referencePoint = {7, 8};
        double expResult = 24;
        double result = PerformanceMetrics.calculateHyperVolumeForTwoObjectivesOnly(geneticEngine, individuals, referencePoint, null/*MUST BE MOFDIFIED*/, 0);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of calculateGenerationalDistance method, of class PerformanceMetrics.
     */
    @Test
    public void testCalculateGenerationalDistance() throws
            InvalidOptimizationProblemException,
            XMLStreamException {
        System.out.println("calculateGenerationalDistance");
        // Load ZDT1 test problem (or any other problem)
        String problemFilePath = "../samples/zdt1-02-30.xml";
        OptimizationProblem optimizationProblem = InputOutput.getProblem(problemFilePath);
        IndividualEvaluator evaluator = new ZDT3ModifiedEvaluator();
        // Prepare the parameters
        // Population
        Individual[] individuals = new Individual[6];
        for(int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, individuals[i]);
        }
        individuals[0].setObjective(0, 2);
        individuals[0].setObjective(1, 11);
        individuals[1].setObjective(0, 3.5);
        individuals[1].setObjective(1, 9);
        individuals[2].setObjective(0, 4);
        individuals[2].setObjective(1, 6);
        individuals[3].setObjective(0, 5);
        individuals[3].setObjective(1, 5);
        individuals[4].setObjective(0, 7);
        individuals[4].setObjective(1, 1.5);
        individuals[5].setObjective(0, 8);
        individuals[5].setObjective(1, 1);
        // Hypothetical Pareto-front
        Individual[] paretoFrontMembers = new Individual[8];
        for(int i = 0; i < paretoFrontMembers.length; i++) {
            paretoFrontMembers[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, paretoFrontMembers[i]);
        }
        paretoFrontMembers[0].setObjective(0, 1);
        paretoFrontMembers[0].setObjective(1, 9);
        paretoFrontMembers[1].setObjective(0, 1.5);
        paretoFrontMembers[1].setObjective(1, 7);
        paretoFrontMembers[2].setObjective(0, 2);
        paretoFrontMembers[2].setObjective(1, 4);
        paretoFrontMembers[3].setObjective(0, 3);
        paretoFrontMembers[3].setObjective(1, 3);
        paretoFrontMembers[4].setObjective(0, 3.5);
        paretoFrontMembers[4].setObjective(1, 2);
        paretoFrontMembers[5].setObjective(0, 5);
        paretoFrontMembers[5].setObjective(1, 1.5);
        paretoFrontMembers[6].setObjective(0, 6);
        paretoFrontMembers[6].setObjective(1, 1);
        paretoFrontMembers[7].setObjective(0, 9);
        paretoFrontMembers[7].setObjective(1, 0.5);
        int power = 2;
        double expResult = 0.8975274678557507;
        double result = PerformanceMetrics.calculateGenerationalDistance(optimizationProblem.objectives.length, individuals, paretoFrontMembers, power);
        System.out.println(result);
        assertEquals("Unit Test Failed", expResult, result, 0.000001);
    }

    /**
     * Test 2 of calculateGenerationalDistance method, of class PerformanceMetrics.
     */
    @Test
    public void testCalculateGenerationalDistance2() throws
            InvalidOptimizationProblemException,
            XMLStreamException {
        System.out.println("calculateGenerationalDistance2");
        // Load ZDT1 test problem (or any other problem)
        String problemFilePath = "../samples/zdt1-02-30.xml";
        OptimizationProblem optimizationProblem = InputOutput.getProblem(problemFilePath);
        IndividualEvaluator evaluator = new ZDT3ModifiedEvaluator();
        // Prepare the parameters
        // Population
        Individual[] individuals = new Individual[9];
        for(int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, individuals[i]);
        }
        individuals[0].setObjective(0, 3);
        individuals[0].setObjective(1, 19);
        individuals[1].setObjective(0, 4);
        individuals[1].setObjective(1, 18);
        individuals[2].setObjective(0, 5);
        individuals[2].setObjective(1, 15);
        individuals[3].setObjective(0, 7);
        individuals[3].setObjective(1, 14);
        individuals[4].setObjective(0, 8);
        individuals[4].setObjective(1, 12);
        individuals[5].setObjective(0, 9);
        individuals[5].setObjective(1, 9);
        individuals[6].setObjective(0, 12);
        individuals[6].setObjective(1, 5);
        individuals[7].setObjective(0, 15);
        individuals[7].setObjective(1, 3);
        individuals[8].setObjective(0, 17);
        individuals[8].setObjective(1, 2);
        // Hypothetical Pareto-front
        Individual[] paretoFrontMembers = new Individual[10];
        for(int i = 0; i < paretoFrontMembers.length; i++) {
            paretoFrontMembers[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, paretoFrontMembers[i]);
        }
        paretoFrontMembers[0].setObjective(0, 0.5);
        paretoFrontMembers[0].setObjective(1, 20);
        paretoFrontMembers[1].setObjective(0, 1);
        paretoFrontMembers[1].setObjective(1, 16);
        paretoFrontMembers[2].setObjective(0, 2);
        paretoFrontMembers[2].setObjective(1, 12);
        paretoFrontMembers[3].setObjective(0, 4);
        paretoFrontMembers[3].setObjective(1, 8);
        paretoFrontMembers[4].setObjective(0, 6);
        paretoFrontMembers[4].setObjective(1, 5);
        paretoFrontMembers[5].setObjective(0, 8);
        paretoFrontMembers[5].setObjective(1, 3);
        paretoFrontMembers[6].setObjective(0, 10);
        paretoFrontMembers[6].setObjective(1, 2);
        paretoFrontMembers[7].setObjective(0, 12);
        paretoFrontMembers[7].setObjective(1, 1.5);
        paretoFrontMembers[8].setObjective(0, 15);
        paretoFrontMembers[8].setObjective(1, 1);
        paretoFrontMembers[9].setObjective(0, 20);
        paretoFrontMembers[9].setObjective(1, 0.5);
        int power = 2;
        double expResult = 1.336;
        double result = PerformanceMetrics.calculateGenerationalDistance(optimizationProblem.objectives.length, individuals, paretoFrontMembers, power);
        System.out.println(result);
        assertEquals("Unit Test Failed", expResult, result, 0.001);
    }

    /**
     * Test 2 of calculateInvertedGenerationalDistance method, of class PerformanceMetrics.
     */
    @Test
    public void testCalculateInvertedGenerationalDistance2() throws
            InvalidOptimizationProblemException,
            XMLStreamException {
        System.out.println("calculateInvertedGenerationalDistance2");
        // Load ZDT1 test problem (or any other problem)
        String problemFilePath = "../samples/zdt1-02-30.xml";
        OptimizationProblem optimizationProblem = InputOutput.getProblem(problemFilePath);
        IndividualEvaluator evaluator = new ZDT3ModifiedEvaluator();
        // Prepare the parameters
        // Population
        Individual[] individuals = new Individual[9];
        for(int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, individuals[i]);
        }
        individuals[0].setObjective(0, 3);
        individuals[0].setObjective(1, 19);
        individuals[1].setObjective(0, 4);
        individuals[1].setObjective(1, 18);
        individuals[2].setObjective(0, 5);
        individuals[2].setObjective(1, 15);
        individuals[3].setObjective(0, 7);
        individuals[3].setObjective(1, 14);
        individuals[4].setObjective(0, 8);
        individuals[4].setObjective(1, 12);
        individuals[5].setObjective(0, 9);
        individuals[5].setObjective(1, 9);
        individuals[6].setObjective(0, 12);
        individuals[6].setObjective(1, 5);
        individuals[7].setObjective(0, 15);
        individuals[7].setObjective(1, 3);
        individuals[8].setObjective(0, 17);
        individuals[8].setObjective(1, 2);
        // Hypothetical Pareto-front
        Individual[] paretoFrontMembers = new Individual[10];
        for(int i = 0; i < paretoFrontMembers.length; i++) {
            paretoFrontMembers[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, paretoFrontMembers[i]);
        }
        paretoFrontMembers[0].setObjective(0, 0.5);
        paretoFrontMembers[0].setObjective(1, 20);
        paretoFrontMembers[1].setObjective(0, 1);
        paretoFrontMembers[1].setObjective(1, 16);
        paretoFrontMembers[2].setObjective(0, 2);
        paretoFrontMembers[2].setObjective(1, 12);
        paretoFrontMembers[3].setObjective(0, 4);
        paretoFrontMembers[3].setObjective(1, 8);
        paretoFrontMembers[4].setObjective(0, 6);
        paretoFrontMembers[4].setObjective(1, 5);
        paretoFrontMembers[5].setObjective(0, 8);
        paretoFrontMembers[5].setObjective(1, 3);
        paretoFrontMembers[6].setObjective(0, 10);
        paretoFrontMembers[6].setObjective(1, 2);
        paretoFrontMembers[7].setObjective(0, 12);
        paretoFrontMembers[7].setObjective(1, 1.5);
        paretoFrontMembers[8].setObjective(0, 15);
        paretoFrontMembers[8].setObjective(1, 1);
        paretoFrontMembers[9].setObjective(0, 20);
        paretoFrontMembers[9].setObjective(1, 0.5);
        int power = 2;
        double expResult = 1.22;
        double result = PerformanceMetrics.calculateInvertedGenerationalDistance(optimizationProblem.objectives.length, individuals, paretoFrontMembers, power);
        System.out.println(result);
        assertEquals("Unit Test Failed", expResult, result, 0.001);
    }

    /**
     * Test of calculateInvertedGenerationalDistance method, of class PerformanceMetrics.
     */
    @Test
    public void testCalculateInvertedGenerationalDistance() throws InvalidOptimizationProblemException, XMLStreamException {
        System.out.println("calculateInvertedGenerationalDistance");
        // Load ZDT1 test problem (or any other problem)
        String problemFilePath = "../samples/zdt1-02-30.xml";
        OptimizationProblem optimizationProblem = InputOutput.getProblem(problemFilePath);
        IndividualEvaluator evaluator = new IndividualEvaluator(optimizationProblem);
        // Prepare the parameters
        // Population
        Individual[] individuals = new Individual[6];
        for(int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, individuals[i]);
        }
        individuals[0].setObjective(0, 2);
        individuals[0].setObjective(1, 11);
        individuals[1].setObjective(0, 3.5);
        individuals[1].setObjective(1, 9);
        individuals[2].setObjective(0, 4);
        individuals[2].setObjective(1, 6);
        individuals[3].setObjective(0, 5);
        individuals[3].setObjective(1, 5);
        individuals[4].setObjective(0, 7);
        individuals[4].setObjective(1, 1.5);
        individuals[5].setObjective(0, 8);
        individuals[5].setObjective(1, 1);
        // Hypothetical Pareto-front
        Individual[] paretoFrontMembers = new Individual[8];
        for(int i = 0; i < paretoFrontMembers.length; i++) {
            paretoFrontMembers[i] = new Individual(optimizationProblem, evaluator);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, paretoFrontMembers[i]);
        }
        paretoFrontMembers[0].setObjective(0, 1);
        paretoFrontMembers[0].setObjective(1, 9);
        paretoFrontMembers[1].setObjective(0, 1.5);
        paretoFrontMembers[1].setObjective(1, 7);
        paretoFrontMembers[2].setObjective(0, 2);
        paretoFrontMembers[2].setObjective(1, 4);
        paretoFrontMembers[3].setObjective(0, 3);
        paretoFrontMembers[3].setObjective(1, 3);
        paretoFrontMembers[4].setObjective(0, 3.5);
        paretoFrontMembers[4].setObjective(1, 2);
        paretoFrontMembers[5].setObjective(0, 5);
        paretoFrontMembers[5].setObjective(1, 1.5);
        paretoFrontMembers[6].setObjective(0, 6);
        paretoFrontMembers[6].setObjective(1, 1);
        paretoFrontMembers[7].setObjective(0, 9);
        paretoFrontMembers[7].setObjective(1, 0.5);
        int power = 2;
        double expResult = 0.8477912478906585;
        double result = PerformanceMetrics.calculateInvertedGenerationalDistance(optimizationProblem.objectives.length, individuals, paretoFrontMembers, power);
        System.out.println(result);
        assertEquals("Unit Test Failed", expResult, result, 0.000001);
    }

    /**
     * Test of getZDT1ParetoFront method, of class PerformanceMetrics.
     */
    @Test
    public void testGetZDT1ParetoFront() throws Exception {
        System.out.println("getZDT1ParetoFront");
        // Load ZDT1 test problem
        String problemFilePath = "../samples/zdt1-02-30.xml";
        OptimizationProblem optimizationProblem = InputOutput.getProblem(problemFilePath);
        IndividualEvaluator evaluator = new ZDT3ModifiedEvaluator();
        // Create an array of individuals
        Individual[] expResult = {
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator),
            new Individual(optimizationProblem, evaluator)
        };
        // The expected number of pareto-optimal points
        int n = 10;
        // Override the original design variables values
        expResult[0].real[0] = 0.0;
        expResult[1].real[0] = 1.0/9;
        expResult[2].real[0] = 2.0/9;
        expResult[3].real[0] = 3.0/9;
        expResult[4].real[0] = 4.0/9;
        expResult[5].real[0] = 5.0/9;
        expResult[6].real[0] = 6.0/9;
        expResult[7].real[0] = 7.0/9;
        expResult[8].real[0] = 8.0/9;
        expResult[9].real[0] = 1.0;
        for(Individual individual : expResult) {
            for(int j = 1; j < individual.real.length; j++) {
                individual.real[j] = 0.0;
            }
        }
        Individual[] result = PerformanceMetrics.getZDT1ParetoFront(evaluator, n);
        assertArrayEquals(expResult, result);
    }
}
