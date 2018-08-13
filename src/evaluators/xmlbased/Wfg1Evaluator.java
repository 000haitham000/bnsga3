/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluators.xmlbased;

import evaluators.*;
import utils.PerformanceMetrics;
import emo.Individual;
import emo.OptimizationProblem;
import exceptions.MisplacedTokensException;
import exceptions.TooManyDecimalPointsException;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.XMLParser;

/**
 *
 * @author toshiba
 */
public class Wfg1Evaluator extends IndividualEvaluator {

    parsing.OptimizationProblem parsedProblem;

    public Wfg1Evaluator() {
        String filePath = "E:\\KKTPM\\Java KKTPM\\XML\\wfg\\wfg1-modified.xml";
        try {
            // Load the problem
            this.parsedProblem = XMLParser.readXML(new File(filePath));
        } catch (TooManyDecimalPointsException ex) {
            Logger.getLogger(Wfg1Evaluator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR>> " + ex.toString());
            System.exit(-1);
        } catch (MisplacedTokensException ex) {
            Logger.getLogger(Wfg1Evaluator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR>> " + ex.toString());
            System.exit(-1);
        } catch (Throwable ex) {
            Logger.getLogger(Wfg1Evaluator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR>> " + ex.toString());
            System.exit(-1);
        }
    }

    @Override
    public void updateIndividualObjectivesAndConstraints(
            OptimizationProblem problem,
            Individual individual) {
        try {
            double[] x = individual.real;

            parsedProblem.setAllVariables(x);

            double obj0 = parsedProblem.getObjective(0);
            double obj1 = parsedProblem.getObjective(1);
            double obj2 = parsedProblem.getObjective(2);

            individual.setObjective(0, obj0);
            individual.setObjective(1, obj1);
            individual.setObjective(2, obj2);
            // Increase Evaluations Count by One (counted per individual)
            funEvalCount++;
            // Announce that objective function values are valid
            individual.validObjectiveFunctionsValues = true;
            // Update constraint violations if constraints exist
            if (problem.constraints != null) {
                // Evaluate the final expression and store the results as the individual's constraints values.
                for (int i = 0; i < problem.constraints.length; i++) {
                    individual.setConstraintViolation(i, 0.0);
                }
                // Announce that objective function values are valid
                individual.validConstraintsViolationValues = true;
            }
        } catch (exceptions.EvaluationException ex) {
            Logger.getLogger(Wfg1Evaluator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR>> " + ex.toString());
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws exceptions.EvaluationException {
        Wfg1Evaluator evaluator = new Wfg1Evaluator();
        double[] x = new double[]{0.5, 1.5, 2.5, 3.5};
        evaluator.parsedProblem.setAllVariables(x);
        double obj0 = evaluator.parsedProblem.getObjective(0);
        double obj1 = evaluator.parsedProblem.getObjective(1);
    }
}
