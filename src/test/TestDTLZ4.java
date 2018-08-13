/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import emo.Individual;
import emo.OptimizationProblem;
import engines.NSGA3Engine;
import evaluators.GeneralDTLZ4Evaluator;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author seadahai
 */
public class TestDTLZ4 {

    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException {
        double[][] x = new double[][]{
            {0.0003084, 0.9727804, 0.1676794, 0.3469239, 0.2906483, 0.1977228, 0.5082953, 0.2448202, 0.3907513, 0.4355022, 0.1914456, 0.7208344},
            {0.6475506, 0.8725817, 0.3123098, 0.3608465, 0.0431948, 0.6121961, 0.3408283, 0.1502790, 0.8612957, 0.9130734, 0.5004638, 0.5611531},
            {0.5327817, 0.8217609, 0.3827592, 0.4992735, 0.7260710, 0.7996684, 0.6481877, 0.3053845, 0.3230458, 0.1146552, 0.6537853, 0.3825610},
            {0.9212577, 0.1174487, 0.0247174, 0.6118183, 0.3050233, 0.5306642, 0.2241403, 0.3148343, 0.4177841, 0.0871685, 0.8615440, 0.4710643},
            {0.9838754, 0.0137645, 0.7886105, 0.1460522, 0.3963076, 0.4012077, 0.4372436, 0.9787672, 0.1416787, 0.8080695, 0.8502297, 0.9786482}
        };
        URL url = NSGA3Engine.class.getResource("../samples/dtlz4.xml");     // Modify Here
        OptimizationProblem optimizationProblem = StaXParser.readProblem(url.openStream());
        IndividualEvaluator evaluator = new GeneralDTLZ4Evaluator(optimizationProblem); //new WFG1(2,2,3);   // Modify Here
        for (int i = 0; i < x.length; i++) {
            Individual individual = new Individual(optimizationProblem, evaluator, x[i]);
            evaluator.updateIndividualObjectivesAndConstraints(optimizationProblem, individual);
            System.out.print(individual.getObjectiveSpace());
        }
    }
}
