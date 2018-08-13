/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moeaframework.moead;

import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.OptimizationUtilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kkt.KKT_Calculator;
import optimization.TestScript_EMO2017;
import optimization.TestScript_IEEE_TEVC_DC_NSGA3;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TerminationCondition;
import org.moeaframework.core.variable.RealVariable;
import parsing.IndividualEvaluator;
import reference_directions.ReferenceDirection;
import utils.InputOutput;

/**
 *
 * @author Haitham
 */
public class ReportingExecutor extends Executor {

    private OptimizationProblem problem;
    private IndividualEvaluator evaluator;
    private KKT_Calculator kktCalculator;

    public ReportingExecutor(
            OptimizationProblem problem,
            IndividualEvaluator evaluator,
            KKT_Calculator kktCalculator,
            List<ReferenceDirection> referenceDirectionsList) {
        this.problem = problem;
        this.evaluator = evaluator;
        this.kktCalculator = kktCalculator;
    }

    @Override
    protected void preRunLogic(int run) {
        super.preRunLogic(run);
        // Create run directory
        File runOutputDir = TestScript_EMO2017.getRunOutputDir(TestScript_EMO2017.getEngineOutputDir(problem, "moead"), run - 1);
        runOutputDir.mkdirs();
    }

    @Override
    protected void postGenerationLogic(int run, int gen) {
        //System.out.println("G = " + gen + ", FE = " + currentAlgorithm.getNumberOfEvaluations());
        try {
            super.postGenerationLogic(run, gen);
            // Convert solutions to individuals (MOEA Framework to EvoMO)
            Individual[] allIndividuals;
            if (currentAlgorithm instanceof MOEAD) {
                allIndividuals = new Individual[moeadPopulation.size()];
                for (int i = 0; i < moeadPopulation.size(); i++) {
                    double[] solReal = new double[moeadPopulation.get(i).getSolution().getNumberOfVariables()];
                    for (int j = 0; j < solReal.length; j++) {
                        solReal[j] = ((RealVariable) moeadPopulation.get(i).getSolution().getVariable(j)).getValue();
                    }
                    // Notice that we are creating a redundant individual here
                    // just for logging purposes. This increase the number of
                    // function evalutaions unjustifiably. If you need the
                    // actual number of function evaluations used by the
                    // algorithm use currentAlgorithm.getNumberOfEvaluations(),
                    // i.e. use the one calculated by MOEA Framework not the one
                    // calculated using our IndividualEvaluator.
                    allIndividuals[i] = new Individual(problem, evaluator, solReal);
                }
                dumpOutputFiles(allIndividuals, run, gen);
            }
//            else {
//                allIndividuals = new Individual[currentAlgorithm.getResult().size()];
//                for (int i = 0; i < currentAlgorithm.getResult().size(); i++) {
//                    double[] solReal = new double[currentAlgorithm.getResult().get(i).getNumberOfVariables()];
//                    for (int j = 0; j < solReal.length; j++) {
//                        solReal[j] = ((RealVariable) currentAlgorithm.getResult().get(i).getVariable(j)).getValue();
//                    }
//                    // Notice that we are creating a redundant individual here
//                    // just for logging purposes. This increase the number of
//                    // function evalutaions unjustifiably. If you need the
//                    // actual number of function evaluations used by the
//                    // algorithm use currentAlgorithm.getNumberOfEvaluations(),
//                    // i.e. use the one calculated by MOEA Framework not the one
//                    // calculated using our IndividualEvaluator.
//                    allIndividuals[i] = new Individual(problem, evaluator, solReal);
//                }
//                dumpOutputFiles(allIndividuals, run, gen);
//            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReportingExecutor.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
            System.exit(-1);
        } catch (IOException ex) {
            Logger.getLogger(ReportingExecutor.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
            System.exit(-1);
        } catch (MWException ex) {
            Logger.getLogger(ReportingExecutor.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
            System.exit(-1);
        }
    }

    private void dumpOutputFiles(Individual[] allIndividuals, int run, int gen) throws IOException, MWException, FileNotFoundException {
        // Extract non-dominated solutions only.
        Individual[] nonDominatedIndividuals = OptimizationUtilities.getNonDominatedIndividuals(allIndividuals, 0.0);
        // Create the hash map to be returned
        HashMap<String, StringBuilder> dumpMap = new HashMap<>();
        // Dump output files
        File runOutputDir
                = TestScript_IEEE_TEVC_DC_NSGA3.getRunOutputDir(TestScript_IEEE_TEVC_DC_NSGA3.getEngineOutputDir(
                        problem, "moead"), run - 1);
        StringBuilder varSpaceSb = InputOutput.collectRealDecisionSpace(problem, nonDominatedIndividuals);
        StringBuilder objSpaceSb = InputOutput.collectObjectiveSpace(problem, nonDominatedIndividuals);
        // Var. Space
        dumpMap.put("var.dat", varSpaceSb);
        // Obj. Space
        dumpMap.put("obj.dat", objSpaceSb);
        // Generations Count
        dumpMap.put("gen_count", new StringBuilder(String.valueOf(gen - 1)));
        // Meta Data
        //            double[] hvIdealPoint = evaluator.getIdealPoint();
        double[] hvRefPoint = evaluator.getReferencePoint();
        for (int i = 0; i < hvRefPoint.length; i++) {
            hvRefPoint[i] *= 1.01;
        }
        StringBuilder metaDataSb = InputOutput.collectMetaData(
                allIndividuals, evaluator.getIdealPoint(),
                hvRefPoint,
                currentAlgorithm.getNumberOfEvaluations(),
                0, // Zero numerical function evaluations 
                0.0);
        dumpMap.put("meta.txt", metaDataSb);
        // KKTPM metrics
        if (kktCalculator != null) {
            StringBuilder kktpmSb = InputOutput.collectKKTPM(
                    kktCalculator,
                    nonDominatedIndividuals,
                    OptimizationUtilities.pullPointBack(evaluator.getIdealPoint(), 0.01));
            dumpMap.put("kkt.dat", kktpmSb);
        }
        // Matlab Plotting Scripts (for 2 & 3 objectives)
        StringBuilder sb = null;
        if (problem.objectives.length == 2) {
            sb = InputOutput.createMatlabScript2D(OptimizationUtilities.getNonDominatedIndividuals(nonDominatedIndividuals, 0.0));
        } else if (problem.objectives.length == 3) {
            sb = InputOutput.createMatlabScript3D(OptimizationUtilities.getNonDominatedIndividuals(nonDominatedIndividuals, 0.0));
        }
        if (sb != null) {
            dumpMap.put("matlab.m", sb);
        }
        // Write all to files
        InputOutput.dumpAll(runOutputDir, dumpMap);
    }

    /**
     * @return the problem
     */
    public OptimizationProblem getProblem() {
        return problem;
    }

    /**
     * @param problem the problem to set
     */
    public void setProblem(OptimizationProblem problem) {
        this.problem = problem;
    }

    /**
     * @return the evaluator
     */
    public IndividualEvaluator getEvaluator() {
        return evaluator;
    }

    /**
     * @param evaluator the evaluator to set
     */
    public void setEvaluator(IndividualEvaluator evaluator) {
        this.evaluator = evaluator;
    }

}
