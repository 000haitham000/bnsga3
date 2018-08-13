/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import utils.RandomNumberGenerator;
import utils.Mathematics;
import utils.InputOutput;
import emo.Individual;
import emo.OptimizationProblem;
import engines.NSGA2Engine;
import engines.NSGA3Engine;
import evaluators.ZDT3ModifiedEvaluator;
import evaluators.ZDT2Evaluator;
import evaluators.ZDT3Evaluator;
import evaluators.ZDT4Evaluator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Random;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author toshiba
 */
public class XMLvsEvaluatorTest {

    public XMLvsEvaluatorTest() {
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
    public final static double[] ZDT1_NADIR_POINT = {1.0, 1.0};
    public final static double[] ZDT2_NADIR_POINT = {1.0, 1.0};
    public final static double[] ZDT3_NADIR_POINT = {0.8518328654, 1.0};
    public final static double[] ZDT4_NADIR_POINT = {1.0, 1.0};

    /**
     * Test of calculateHyperVolumeForTwoObjectivesOnly method, of class
     * PerformanceMetrics.
     */
    @Test
    public void testXMLvsEvaluator() throws XMLStreamException, InvalidOptimizationProblemException, IOException {
        System.out.println("XML vs. Evaluator");
        InputStream in = null;
        try {
            URL url = NSGA3Engine.class.getResource("../samples/zdt1-02-30.xml");
            in = url.openStream();
            OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
            IndividualEvaluator individualEvaluator1 = new IndividualEvaluator(optimizationProblem);
            IndividualEvaluator individualEvaluator2 = new ZDT3ModifiedEvaluator();
            optimizationProblem.setPopulationSize(1);
            double seed = new Random().nextDouble();
            // Using XML input file
            RandomNumberGenerator.setSeed(seed);
            NSGA2Engine geneticEngine1 = new NSGA2Engine(optimizationProblem, individualEvaluator1);
            Individual[] initialPopulation1 = geneticEngine1.generateInitialPopulation(null);
            InputOutput.displayPopulation(optimizationProblem, "POP(1)", initialPopulation1);
            // Using Evaluator
            RandomNumberGenerator.setSeed(seed);
            NSGA2Engine geneticEngine2 = new NSGA2Engine(optimizationProblem, individualEvaluator2);
            Individual[] initialPopulation2 = geneticEngine2.generateInitialPopulation(null);
            InputOutput.displayPopulation(optimizationProblem, "POP(2)", initialPopulation2);
            // Compare Results and assert them
            String expResultStr = "0-0";
            String actualResultStr = "";
            DecimalFormat decimalFormat = new DecimalFormat("######.######");
            for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                if (Mathematics.compare(initialPopulation1[0].getObjective(i), initialPopulation2[0].getObjective(i), 0.000001) != 0) {
                    actualResultStr += decimalFormat.format(Math.abs(initialPopulation1[0].getObjective(i) - initialPopulation2[0].getObjective(i)));
                } else {
                    actualResultStr += "0";
                }
                if (i != optimizationProblem.objectives.length - 1) {
                    actualResultStr += "-";
                }
            }
            assertEquals(expResultStr, actualResultStr);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Test
    public void testXMLvsEvaluator2() throws XMLStreamException, InvalidOptimizationProblemException, IOException {
        InputStream in = null;
        try {
            URL url = NSGA3Engine.class.getResource("../samples/zdt4-02-30.xml");
            in = url.openStream();
            OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
            IndividualEvaluator individualEvaluator1 = new IndividualEvaluator(optimizationProblem);
            IndividualEvaluator individualEvaluator2 = new ZDT4Evaluator();
            double seed = 0.00001;
            RandomNumberGenerator.setSeed(seed);
            Individual individual1 = new Individual(optimizationProblem, individualEvaluator1);
            RandomNumberGenerator.setSeed(seed);
            Individual individual2 = new Individual(optimizationProblem, individualEvaluator2);
            for (int i = 5; i < individual1.real.length; i++) {
                individual1.real[i] = 0;
                individual2.real[i] = 0;
            }
            individualEvaluator1.updateIndividualObjectivesAndConstraints(optimizationProblem, individual1);
            individualEvaluator2.updateIndividualObjectivesAndConstraints(optimizationProblem, individual2);
            // Compare Results and assert them
            String expResultStr = "0-0";
            String actualResultStr = "";
            DecimalFormat decimalFormat = new DecimalFormat("######.######");
            for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                if (Mathematics.compare(individual1.getObjective(i), individual2.getObjective(i), 0.000001) != 0) {
                    actualResultStr += decimalFormat.format(Math.abs(individual1.getObjective(i) - individual2.getObjective(i)));
                } else {
                    actualResultStr += "0";
                }
                if (i != optimizationProblem.objectives.length - 1) {
                    actualResultStr += "-";
                }
            }
            Individual[] pop1 = {individual1};
            Individual[] pop2 = {individual2};
            InputOutput.displayPopulation(optimizationProblem, "IND(1)", pop1);
            InputOutput.displayPopulation(optimizationProblem, "IND(2)", pop2);
            assertEquals(expResultStr, actualResultStr);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
