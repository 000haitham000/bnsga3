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
public class CollectMultiObjectiveParametersStatistics {

    /*
    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException, InvalidFileFormat {
        InputStream in = null;
        try {
            // Construct the optimization object
            String algorithmName = "EXTENDED-NSGA3";
            URL url = NSGA3Engine.class.getResource("../samples/zdt4-02-30.xml");
            in = url.openStream();
            OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
            int etaCStart = 10;
            int etaCEnd = 30;
            int etaCStep = 10;
            int etaMStart = 0;
            int etaMEnd = 30;
            int etaMStep = 10;
            // Collect hypervolume values from output files
            List<String> hvList = new ArrayList<String>();
            List<Double> gdList = new ArrayList<Double>();
            List<Double> igdList = new ArrayList<Double>();
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
                        do {
                            line = bReader.readLine();
                        } while (line != null && !isMeansLine(line));
                        if (line == null) {
                            throw new InvalidFileFormat("Invalid runs-summary file format");
                        } else {
                            double avgHV = Double.NaN, avgGD = Double.NaN, avgIGD = Double.NaN;
                            do {
                                line = bReader.readLine();
                                if(isMeanHvFile(line)) {
                                    avgHV = Double.parseDouble(line.split("=")[1].trim());
                                    hvList.add(String.format("%03d_%03d_%10.7f", etaC, etaM, avgHV));
                                }
                                if(isMeanGdFile(line)) {
                                    avgGD = Double.parseDouble(line.split("=")[1].trim());
                                    gdList.add(avgGD);
                                }
                                if(isMeanIgdFile(line)) {
                                    avgIGD = Double.parseDouble(line.split("=")[1].trim());
                                    igdList.add(avgIGD);
                                }
                            } while (line != null && !isStandardDeviationsLine(line));
                            if (avgHV == Double.NaN) {
                                throw new InvalidFileFormat("HV mean not found");
                            }
                            if (avgGD == Double.NaN) {
                                throw new InvalidFileFormat("GD mean not found");
                            }
                            if (avgIGD == Double.NaN) {
                                throw new InvalidFileFormat("IGD mean not found");
                            }
                        }
                    } catch(Exception ex) {
                        throw (InvalidFileFormat)ex;
                    } finally {
                        if (bReader != null) {
                            bReader.close();
                        }
                    }
                }
            }
            Mathematics.IdValuePair[] sortedHypervolumeParameterCombinations = Mathematics.getSortedHypervolumeParameterCombinations(hvList);
            String parametersFileName = String.format("D:/Dropbox/Work/NSGA/results/%s-%s-G%03d-P%03d-parameters.dat",
                    algorithmName,
                    optimizationProblem.getProblemID(),
                    optimizationProblem.getGenerationsCount(),
                    optimizationProblem.getPopulationSize());
            InputOutput.dumpHypervolumePerParamtersCombinations(parametersFileName, sortedHypervolumeParameterCombinations, gdList, igdList);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private static boolean isStandardDeviationsLine(String line) {
        if (line.equals("") || line.trim().charAt(0) != '-') {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (Character.isLetter(line.charAt(i))) {
                sb.append(line.charAt(i));
            }
        }
        if (sb.toString().toLowerCase().equals("standarddeviations")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isMeansLine(String line) {
        if (line.equals("") || line.trim().charAt(0) != '-') {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (Character.isLetter(line.charAt(i))) {
                sb.append(line.charAt(i));
            }
        }
        if (sb.toString().toLowerCase().equals("means")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isMeanHvFile(String line) {
        if(line.length() > 2 && line.trim().substring(0, 2).toLowerCase().equals("hv")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isMeanGdFile(String line) {
        if(line.length() > 2 && line.trim().substring(0, 2).toLowerCase().equals("gd")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isMeanIgdFile(String line) {
        if(line.length() > 2 && line.trim().substring(0, 3).toLowerCase().equals("igd")) {
            return true;
        } else {
            return false;
        }
    }
    */
}
