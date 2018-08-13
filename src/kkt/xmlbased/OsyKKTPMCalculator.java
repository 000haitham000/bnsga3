/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkt.xmlbased;

import emo.Individual;
import emo.OptimizationProblem;
import evaluators.OSYEvaluator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.KKTPM;
import parsing.StaXParser;
import parsing.XMLParser;
import test.MatlabIO;

/**
 *
 * @author Haitham
 */
public class OsyKKTPMCalculator extends KKTPM_CalculatorImpl {

    public OsyKKTPMCalculator() {
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
                problem = XMLParser.readXML(new File("E:/Dropbox/KKTPM/Java KKTPM/XML/osy.xml"));
            } catch (Throwable ex) {
                Logger.getLogger(OsyKKTPMCalculator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.toString());
            }
        }
    }
    
    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException {
        OsyKKTPMCalculator kktpmCalc = new OsyKKTPMCalculator();
        double[][] x = MatlabIO.dlmread2D("F:\\IEEE-TEVC-DC-NSGA-III\\Results\\to Mohamed\\osy_test5_var.dat");
        double[] z = new double[]{-300, -0.05};
        Individual[] individuals = new Individual[x.length];
        URL url = OsyKKTPMCalculator.class.getResource("../../samples/osy.xml");
        InputStream in = url.openStream();
        // Parse the optimization problem
        OptimizationProblem problem = StaXParser.readProblem(in);
        // Create Evaluator
        IndividualEvaluator individualEvaluator = new OSYEvaluator();
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(problem, individualEvaluator, x[i]);
        }
        KKTPM[] kktpm = kktpmCalc.calculatePopulationKKT(individuals, z);
        double[][] kktpmValues = new double[kktpm.length][1];
        for (int i = 0; i < kktpmValues.length; i++) {
            kktpmValues[i][0] = kktpm[i].getKktpm();
        }
        MatlabIO.dlmwrite2D("F:\\IEEE-TEVC-DC-NSGA-III\\Results\\to Mohamed\\osy_test5_kktpm_java_fix_1.dat", kktpmValues);
    }
}
