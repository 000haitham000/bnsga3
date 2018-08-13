/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkt.xmlbased;

import emo.Individual;
import engines.NSGA3Engine;
import evaluators.wfg.WFG1;
import evaluators.xmlbased.Wfg1Evaluator;
import exceptions.MisplacedTokensException;
import exceptions.TooManyDecimalPointsException;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsing.IndividualEvaluator;
import parsing.KKTPM;
import parsing.OptimizationProblem;
import parsing.StaXParser;
import parsing.XMLParser;
import test.MatlabIO;

/**
 *
 * @author Haitham
 */
public class Wfg1KKTPMCalculator extends KKTPM_CalculatorImpl {

    public Wfg1KKTPMCalculator() {
        // This class follows a Singleton design pattern to speed up the
        // calculations. Ther is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls should be the same as the
        // results of creating a new object for every call (has not been
        // verified independently yet though)
        if (problem == null) {
            try {
                problem = XMLParser.readXML(new File("E:/Dropbox/KKTPM/Java KKTPM/XML/wfg/wfg1-modified.xml"));
                problem.setConstant("pi", Math.PI);
                problem.setConstant("e", Math.E);
                problem.setDelta(0.00000001);
            } catch (Throwable ex) {
                Logger.getLogger(Wfg1KKTPMCalculator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.toString());
            }
        }
    }

    public static void main(String[] args) throws TooManyDecimalPointsException, MisplacedTokensException, Throwable {
        OptimizationProblem problem = XMLParser.readXML(new File("E:/Dropbox/KKTPM/Java KKTPM/XML/wfg/wfg1-modified.xml"));
        problem.setConstant("pi", Math.PI);
        problem.setConstant("e", Math.E);
        double[] x = {2.0000, 3.0000, 5.3479, 7.2087};
        problem.setAllVariables(x);
        double[] obj = {problem.getObjective(0), problem.getObjective(1), problem.getObjective(2)};
        System.out.println(Arrays.toString(obj));
        // Test the KKTPM Calculator
        URL url = NSGA3Engine.class.getResource("../samples/wfg1.xml");       // Modify Here
        emo.OptimizationProblem optimizationProblem = StaXParser.readProblem(url.openStream());
        IndividualEvaluator individualEvaluator = new WFG1(2, 2, 3);          // Modify Here
        Individual[] inds = {new Individual(optimizationProblem, individualEvaluator, x)};
        Wfg1KKTPMCalculator wfg1KKTPMCalculator = new Wfg1KKTPMCalculator();
        double[] ideal = {0, 0, 0};
        KKTPM[] kktpm = wfg1KKTPMCalculator.calculatePopulationKKT(inds, ideal);
        for (int i = 0; i < kktpm.length; i++) {
            System.out.println(kktpm[i].getKktpm() + " - " + kktpm[i].getFunEvalCount());
        }
    }
}