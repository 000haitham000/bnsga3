/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haitham;

import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.problems.DTLZ.DTLZ4;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;

/**
 *
 * @author Haitham
 */
public class HypervolumeOnly {

    /*
    private static String problemName = "ZDT1";
    private static String paretoFrontFilePath = "ZDT1ParetoFront.dat";
    */
    //private static String populationFilePath = "zdt1-03-30-G500-P100-run000-data.dat";
    private static final String populationFilePath = "dtlz4-10-19-G2000-P276-run000-data.dat";

    public static void main(String[] args) throws JMException {
        // Create the problem
        /*
        Object[] params = {"Real"};
        Problem problem = (new ProblemFactory()).getProblem(problemName, params);
        */
        Problem problem = new DTLZ4("Real", 19, 10);
        // Create the quality indicators object
        /*
            QualityIndicator indicators = new QualityIndicator(problem, args[1]);
        */
        // Read the true Pareto front file
        MetricsUtil utils = new MetricsUtil();
        /*
        SolutionSet paretoFront = utils.readNonDominatedSolutionSet(paretoFrontFilePath);
        */
        // Read the population file
        SolutionSet population = utils.readNonDominatedSolutionSet(populationFilePath);
        // Calculate Hypervolume
        /*
        double hypervolume = new Hypervolume().hypervolume(population.writeObjectivesToMatrix(),
                                         paretoFront.writeObjectivesToMatrix(),
                                         problem.getNumberOfObjectives());
        System.out.format("Hypervolume = %-10.5f%n", hypervolume);
        */
        /*
        double[] minValues  = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] maxValues  = {1.01, 1.01, 1.01, 1.01, 1.01, 1.01, 1.01, 1.01, 1.01, 1.01};
        double haithamHypervolume = new Hypervolume().haithamHypervolume(
                population.writeObjectivesToMatrix(),
                minValues,
                maxValues,
                problem.getNumberOfObjectives());
        System.out.format("Haitham Hypervolume = %-10.7f%n", haithamHypervolume);
        */
        System.out.println(Utilities.replaceBlanksWithSingleSpace(" disyvfo giswoiy    y \n \f g i\f\n     dv i"));
    }
}
