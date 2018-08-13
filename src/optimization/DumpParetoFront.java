/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import utils.PerformanceMetrics;
import emo.Individual;
import evaluators.ZDT2Evaluator;
import evaluators.ZDT3ModifiedEvaluator;
import evaluators.ZDT3Evaluator;
import evaluators.ZDT4Evaluator;
import evaluators.ZDT6Evaluator;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import javax.xml.stream.XMLStreamException;
import parsing.InvalidOptimizationProblemException;

/**
 *
 * @author toshiba
 */
public class DumpParetoFront {

    public static void main(String[] args) throws InvalidOptimizationProblemException, XMLStreamException,  FileNotFoundException {
        //dumpZDT1ParetoFront("D:/Dropbox/Work/NSGA/results/zdt1ParetoFront.dat");
        //dumpLinePlot("D:/Dropbox/Work/NSGA/results/plotZdt1ParetoFront.dat", "D:/Dropbox/Work/NSGA/results/zdt1ParetoFront.dat");
        //dumpZDT2ParetoFront("D:/Dropbox/Work/NSGA/results/zdt2ParetoFront.dat");
        //dumpLinePlot("D:/Dropbox/Work/NSGA/results/plotZdt2ParetoFront.dat", "D:/Dropbox/Work/NSGA/results/zdt2ParetoFront.dat");
        //dumpZDT3ParetoFront("D:/Dropbox/Work/NSGA/results/zdt3ParetoFront.dat");
        //dumpLinePlot("D:/Dropbox/Work/NSGA/results/plotZdt3ParetoFront.dat", "D:/Dropbox/Work/NSGA/results/zdt3ParetoFront.dat");
        //dumpZDT4ParetoFront("D:/Dropbox/Work/NSGA/results/zdt4ParetoFront.dat");
        //dumpLinePlot("D:/Dropbox/Work/NSGA/results/plotZdt4ParetoFront.dat", "D:/Dropbox/Work/NSGA/results/zdt4ParetoFront.dat");
        dumpZDT6ParetoFront("D:/Dropbox/Work/NSGA/results/zdt6ParetoFront.dat");
        //dumpLinePlot("D:/Dropbox/Work/NSGA/results/plotZdt6ParetoFront.dat", "D:/Dropbox/Work/NSGA/results/zdt6ParetoFront.dat");
    }

    /**
     *
     * @param filePath
     * @throws InvalidOptimizationProblemException
     * @throws EvaluationException
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public static void dumpZDT1ParetoFront(String filePath) throws InvalidOptimizationProblemException, FileNotFoundException, XMLStreamException {
        // Get ZDT1 Pareto Front Points
        Individual[] paretoPoints = PerformanceMetrics.getZDT1ParetoFront(new ZDT3ModifiedEvaluator(), 500);
        dumpPopulationObejctiveSpace(paretoPoints, filePath);
    }

    public static void dumpZDT2ParetoFront(String filePath) throws InvalidOptimizationProblemException, FileNotFoundException, XMLStreamException {
        // Get ZDT2 Pareto Front Points
        Individual[] paretoPoints = PerformanceMetrics.getZDT2ParetoFront(new ZDT2Evaluator(), 500);
        dumpPopulationObejctiveSpace(paretoPoints, filePath);
    }

    public static void dumpZDT3ParetoFront(String filePath) throws InvalidOptimizationProblemException, FileNotFoundException, XMLStreamException {
        // Get ZDT3 Pareto Front Points
        Individual[] paretoPoints = PerformanceMetrics.getZDT3ParetoFront(new ZDT3Evaluator(), 500);
        dumpPopulationObejctiveSpace(paretoPoints, filePath);
    }

    public static void dumpZDT4ParetoFront(String filePath) throws InvalidOptimizationProblemException, FileNotFoundException, XMLStreamException {
        // Get ZDT4 Pareto Front Points
        Individual[] paretoPoints = PerformanceMetrics.getZDT4ParetoFront(new ZDT4Evaluator(), 500);
        dumpPopulationObejctiveSpace(paretoPoints, filePath);
    }

    public static void dumpZDT6ParetoFront(String filePath) throws InvalidOptimizationProblemException, XMLStreamException, FileNotFoundException {
        // Get ZDT6 Pareto Front Points
        Individual[] paretoPoints = PerformanceMetrics.getZDT6ParetoFront(new ZDT6Evaluator(), 500);
        dumpPopulationObejctiveSpace(paretoPoints, filePath);
    }

    public static void dumpPopulationObejctiveSpace(Individual[] individuals, String filePath) throws FileNotFoundException {
        DecimalFormat decFormat = new DecimalFormat("######.######");
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(filePath);
            // Create a data file containing the coordinates of these points
            for (int i = 0; i < individuals.length; i++) {
                printer.println(String.format(
                        "%s %s",
                        decFormat.format(individuals[i].getObjective(0)),
                        decFormat.format(individuals[i].getObjective(1))));
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }

    public static void dumpLinePlot(String filePath, String dataFilePath) throws FileNotFoundException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(filePath);
            printer.println("set style line 1 lt 1 lw 2 lc -1 pt 1 ps 0.01");
            printer.println("set size ratio 0.75 .5");
            printer.println("plot '" + dataFilePath + "' with lines ls 1");
            printer.println("reset");
            //printer.println("reset");
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }
}
