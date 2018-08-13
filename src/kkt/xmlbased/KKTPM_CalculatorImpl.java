/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkt.xmlbased;

import emo.Individual;
import exceptions.EvaluationException;
import exceptions.MisplacedTokensException;
import exceptions.TooManyDecimalPointsException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kkt.KKT_Calculator;
import kktpm.KKTPMCalculator;
import org.apache.commons.math3.linear.SingularMatrixException;
import parsing.KKTPM;
import parsing.OptimizationProblem;

/**
 *
 * @author seadahai
 */
public abstract class KKTPM_CalculatorImpl implements KKT_Calculator {

    // The number of NaN KKTPM values in the population due to reaching
    // a singular matrix situation.
    private static int count = 0;
    private static final double RHO = 0.01;
    
    // The object representing tha MATLAB script
    protected OptimizationProblem problem = null;



    @Override
    public KKTPM[] calculatePopulationKKT(Individual[] individuals) {
        // Initialize ideal point
        double[] idealPoint = new double[individuals[0].getObjectivesCount()];
        for (int i = 0; i < idealPoint.length; i++) {
            idealPoint[i] = Double.MAX_VALUE;
        }
        // Update ideal point
        for (Individual individual : individuals) {
            for (int j = 0; j < individual.getObjectivesCount(); j++) {
                if (individual.getObjective(j) < idealPoint[j]) {
                    idealPoint[j] = individual.getObjective(j);
                }
            }
        }
        // Calculate the KKTPM values of all individuals
        KKTPM[] kktpmValues = new KKTPM[individuals.length];
        for (int i = 0; i < individuals.length; i++) {
            problem.setAllVariables(individuals[i].real);
            try {
                kktpmValues[i] = KKTPMCalculator.getKKTPM(problem, idealPoint, RHO);
            } catch (EvaluationException | TooManyDecimalPointsException | MisplacedTokensException ex) {
                kktpmValues[i] = new KKTPM(Double.NaN, 0);
                Logger.getLogger(TnkKKTPMCalculator.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.toString());
                System.exit(-1);
            } catch (SingularMatrixException ex) {
                count++;
                System.out.println("Singular Matrix Exception #" + count);
                // For simplicity, we ignore those function evaluations consumed
                // during a failed KKTPM evaluation i.e. during an evaluation 
                // that ended up in singular values.
                kktpmValues[i] = new KKTPM(Double.NaN, 0);
            }
        }
        // Return the KKTPM values array
        return kktpmValues;
    }
    
    @Override
    public KKTPM[] calculatePopulationKKT(Individual[] individuals, double[] z) {
        // Calculate the KKTPM values of all individuals
        KKTPM[] kktpmValues = new KKTPM[individuals.length];
        for (int i = 0; i < individuals.length; i++) {
            problem.setAllVariables(individuals[i].real);
            try {
                kktpmValues[i] = KKTPMCalculator.getKKTPM(problem, z, RHO);
            } catch (EvaluationException | TooManyDecimalPointsException | MisplacedTokensException ex) {
                kktpmValues[i] = new KKTPM(Double.NaN, 0);
                Logger.getLogger(TnkKKTPMCalculator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SingularMatrixException ex) {
                count++;
                System.out.println("Singular Matrix Exception #" + count);
                kktpmValues[i] = new KKTPM(Double.NaN, 0);
            }
        }
        // Return the KKTPM values array
        return kktpmValues;
    }
}
