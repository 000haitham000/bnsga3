/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import emo.Individual;
import emo.OptimizationProblem;
import emo.VirtualIndividual;
import engines.NSGA3Engine;
import evaluators.GeneralDTLZ2Evaluator;
import evaluators.ZDT3ModifiedEvaluator;
import evaluators.ZDT2Evaluator;
import evaluators.ZDT4Evaluator;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;
import reference_directions.ReferenceDirection;
import reference_directions.ReferenceDirectionsFactory;

/**
 *
 * @author Haitham
 */
public class ParetoFrontGenerator {

    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException {
        // Read the Pareto front
        //List<ReferenceDirection> refDirs = new ReferenceDirectionsFactory(5).generateDirections(17);
        //ReferenceDirection[] refDirsArr = new ReferenceDirection[refDirs.size()];
        //refDirs.toArray(refDirsArr);
        VirtualIndividual[] paretoFront = PerformanceMetrics.getZDT4ParetoFront(new ZDT4Evaluator(), 1000);
        // Print the front
        PrintWriter dataFilePrinter = null;
        PrintWriter matlabFilePrinter = null;
        try {
            dataFilePrinter = new PrintWriter("D:/Matlab work/zdt4_02obj_1000points.dat");
            for (int i = 0; i < paretoFront.length; i++) {
                dataFilePrinter.format("%010.7f %010.7f%n",
                        paretoFront[i].getObjective(0), 
                        paretoFront[i].getObjective(1));
            }
        } finally {
            if(dataFilePrinter != null) {
                dataFilePrinter.close();
            }
            if(matlabFilePrinter != null) {
                matlabFilePrinter.close();
            }
        }
    }
}
