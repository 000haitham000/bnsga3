/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import utils.InputOutput;
import utils.Mathematics;
import utils.PerformanceMetrics;
import reference_directions.DasDennis;
import emo.Individual;
import emo.VirtualIndividual;
import reference_directions.ReferenceDirection;
import reference_directions.ReferenceDirectionsFactory;
import evaluators.ZDT3ModifiedEvaluator;
import haitham.Utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.fastHypervolume.wfg.WFGHV;
import jmetal.qualityIndicator.util.MetricsUtil;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import reference_directions.NestedReferenceDirectionsFactory;

/**
 *
 * @author toshiba
 */
public class temp {

    public static void main(String[] args) throws FileNotFoundException, InvalidOptimizationProblemException, XMLStreamException, IOException {
        //printParetoFront();
        //refDirTest();
        //System.out.println("TEST START");
        //DTLZ4Evaluator dTLZ4Evaluator = new DTLZ4Evaluator();
        //VirtualIndividual[] paretoFront = dTLZ4Evaluator.getParetoFront(3, 15);
        //System.out.println("TEST END");
        //displaySelectedDirs("D:/Extra/r1_dtlz1_gen_0499_obj.dat");
        testHypervolumeAlgorithms("D:\\results\\ZDT1\\additional_individuals_kept\\zdt1-03-30-000-000_P016_HV_0.6400\\EXTENDED-NSGA3\\zzz.dat");
    }

    private static void refDirTest() {
        // ReferenceDirectionsFactory dirGenerator = new ReferenceDirectionsFactory(3);
        // List<ReferenceDirection> dirs = dirGenerator.generateDirections(15);
        NestedReferenceDirectionsFactory dirGenerator = new NestedReferenceDirectionsFactory(10);
        int[] divisions = {3, 2};
        List<ReferenceDirection> dirs = dirGenerator.generateDirections(divisions);
        for (ReferenceDirection dir : dirs) {
            System.out.println(dir.toString());
        }
        ReferenceDirection[] refDirArr = new ReferenceDirection[dirs.size()];
        dirs.toArray(refDirArr);
        VirtualIndividual[] dtlZ2ParetoFront = PerformanceMetrics.getDTLZ2ParetoFront(refDirArr);
        dirs.clear();
        for (int i = 0; i < dtlZ2ParetoFront.length; i++) {
            double[] arr = new double[3];
            for (int j = 0; j < arr.length; j++) {
                arr[j] = dtlZ2ParetoFront[i].getObjective(j);
            }
            dirs.add(new ReferenceDirection(arr));
        }
        InputOutput.printDirectionMatlabCode(3, dirs, new PrintWriter(System.out));
    }

    private static void printParetoFront() throws XMLStreamException, FileNotFoundException, InvalidOptimizationProblemException {
        IndividualEvaluator evaluator = new ZDT3ModifiedEvaluator();
        Individual[] zdT1ParetoFront = PerformanceMetrics.getZDT1ParetoFront(evaluator, 500);
        PrintWriter printer = null;
        try {
            File dir = new File("d:/test");
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            printer = new PrintWriter("d:/test/ZDT1ParetoFront.dat");
            for (Individual individual : zdT1ParetoFront) {
                printer.println(individual.getObjective(0) + " " + individual.getObjective(1));
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }

    private static void displaySelectedDirs(String directionsFilePath) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        try {
            List<ReferenceDirection> referenceDirectionsList = new ArrayList<ReferenceDirection>();
            referenceDirectionsList = new ArrayList<ReferenceDirection>();
            reader = new BufferedReader(new FileReader(directionsFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.trim().split(" ");
                if (!line.isEmpty()) {
                    double[] dirValues = new double[splits.length];
                    for (int i = 0; i < splits.length; i++) {
                        dirValues[i] = Double.parseDouble(splits[i]);
                    }
                    referenceDirectionsList.add(new ReferenceDirection(dirValues));
                }
            }
            InputOutput.printDirectionMatlabCode(3, referenceDirectionsList, new PrintWriter(System.out));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static void testHypervolumeAlgorithms(String filePath) throws IOException {
        VirtualIndividual[] individuals = InputOutput.loadIndividualsFromFile(new File(filePath));
        SolutionSet population = Utilities.haithamReadNonDominatedSolutionSet(filePath);
        // Ideal Point = [0 ... 0]
        double[] minValues = new double[individuals[0].getObjectivesCount()];
        // Reference Point = [1.01 ... 1.01]
        double[] maxValues = new double[individuals[0].getObjectivesCount()];
        for (int i = 0; i < maxValues.length; i++) {
            maxValues[i] = 1.01;
        }
        // Exact Hypervolume
        double exactHypervolume = new Hypervolume().haithamHypervolume(
                population.writeObjectivesToMatrix(),
                minValues,
                maxValues,
                maxValues.length);
        // WFG Hypervolume
        double wfgHypervolume = WFGHV.haithamHypervolume(filePath, maxValues);
        // Print the two hypervolumes
        System.out.format("* Exact HV (Ziztler algorithm) = %10.8f%n", exactHypervolume);
        System.out.format("* Approx. HV   (WFG algorithm) = %10.8f%n", wfgHypervolume);
    }
}
