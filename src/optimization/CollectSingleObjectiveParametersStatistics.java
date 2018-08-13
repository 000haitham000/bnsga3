/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import utils.InputOutput;
import utils.Mathematics;
import emo.OptimizationProblem;
import engines.NSGA3Engine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author toshiba
 */
public class CollectSingleObjectiveParametersStatistics {

    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException, InvalidFileFormat {
        InputStream in = null;
        try {
            // Construct the optimization object
            String algorithmName = "EXTENDED-NSGA3";
            URL url = NSGA3Engine.class.getResource("../samples/single-objective-g06.xml");
            in = url.openStream();
            OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
            int etaCStart = 0;
            int etaCEnd = 100;
            int etaCStep = 10;
            int etaMStart = 0;
            int etaMEnd = 100;
            int etaMStep = 10;

            PrintWriter printer = null;
            try {
                String parametersFileName = String.format("D:/Dropbox/Work/NSGA/results/%s-%s-G%03d-P%03d-parameters.dat",
                        algorithmName,
                        optimizationProblem.getProblemID(),
                        optimizationProblem.getGenerationsCount(),
                        optimizationProblem.getPopulationSize());
                printer = new PrintWriter(parametersFileName);
                for (int etaC = etaCStart; etaC <= etaCEnd; etaC += etaCStep) {
                    for (int etaM = etaMStart; etaM <= etaMEnd; etaM += etaMStep) {
                        optimizationProblem.setRealCrossoverDistIndex(etaC);
                        optimizationProblem.setRealMutationDistIndex(etaM);
                        // Read the stored average HV from the metrics file
                        String metricsFileName = String.format("D:/Dropbox/Work/NSGA/results/%s-%03d-%03d/%s/%s-G%03d-P%03d-metrics.dat",
                                optimizationProblem.getProblemID(),
                                optimizationProblem.getRealCrossoverDistIndex(),
                                optimizationProblem.getRealMutationDistIndex(),
                                algorithmName,
                                optimizationProblem.getProblemID(),
                                optimizationProblem.getGenerationsCount(),
                                optimizationProblem.getPopulationSize());
                        BufferedReader bReader = null;
                        try {
                            bReader = new BufferedReader(new FileReader(metricsFileName));
                            String line = null;
                            line = bReader.readLine();
                            String[] splits = line.split(" ");
                            double bestValue = Double.parseDouble(splits[splits.length - 1]);
                            printer.format("(%03d,%03d) > %10.5f%n", etaC, etaM, bestValue);
                        } catch (Exception ex) {
                            throw (InvalidFileFormat) ex;
                        } finally {
                            if (bReader != null) {
                                bReader.close();
                            }
                        }
                    }
                }
            } finally {
                if (printer != null) {
                    printer.close();
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
