/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import com.mathworks.toolbox.javabuilder.MWException;
import emo.DoubleAssignmentException;
import emo.Individual;
import emo.OptimizationProblem;
import engines.AbstractGeneticEngine;
import engines.NSGA3Engine;
import engines.UnifiedNSGA3Engine;
import evaluators.GeneralDTLZ1Evaluator;
import evaluators.OSYEvaluator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;
import utils.RandomNumberGenerator;

/**
 *
 * @author seadahai
 */
public class SingleRunTestScript {

    public static void main(String[] args) throws
            IOException,
            XMLStreamException,
            InvalidOptimizationProblemException,
            MWException,
            FileNotFoundException,
            DoubleAssignmentException {
        // OSY
        //optimizeOsy();
        // DLTZ1
        optimizeDtlz1();
    }

    private static void optimizeOsy() throws 
            IOException, 
            XMLStreamException, 
            InvalidOptimizationProblemException, 
            DoubleAssignmentException, MWException {
        // Define optimization parameters
        URL url = NSGA3Engine.class.getResource("../samples/osy.xml");
        InputStream in = url.openStream();
        OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
        // Define problem evaluator
        IndividualEvaluator evaluator = new OSYEvaluator();
        // Set seed
        RandomNumberGenerator.setSeed(0.7);
        // Create engine (algorithm)
        AbstractGeneticEngine geneticEngine = new UnifiedNSGA3Engine(
                optimizationProblem,
                evaluator);
        // Create output Directory
        File outputDir = new File("f:/optimizaion_results_osy");
        outputDir.mkdirs();
        // Start optimization
        Individual[] finalPopulation = geneticEngine.start(
                outputDir,
                0,
                0,
                null,
                null,
                Double.MAX_VALUE,
                Integer.MAX_VALUE);
    }

    private static void optimizeDtlz1() throws 
            IOException, 
            XMLStreamException, 
            InvalidOptimizationProblemException, 
            DoubleAssignmentException, MWException {
        // Define optimization parameters
        URL url = NSGA3Engine.class.getResource("../samples/dtlz1.xml");
        InputStream in = url.openStream();
        OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
        // Define problem evaluator
        IndividualEvaluator evaluator = new GeneralDTLZ1Evaluator(optimizationProblem);
        // Set seed
        RandomNumberGenerator.setSeed(0.7);
        // Create engine (algorithm)
        AbstractGeneticEngine geneticEngine = new UnifiedNSGA3Engine(
                optimizationProblem,
                evaluator);
        // Create output Directory
        File outputDir = new File("f:/optimizaion_results_dtlz1");
        outputDir.mkdirs();
        // Start optimization
        Individual[] finalPopulation = geneticEngine.start(
                outputDir,
                0,
                0,
                null,
                null,
                Double.MAX_VALUE,
                Integer.MAX_VALUE);
    }
}