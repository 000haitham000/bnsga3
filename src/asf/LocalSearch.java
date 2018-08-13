/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

import utils.Mathematics;
import utils.RandomNumberGenerator;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.OptimizationUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kkt.KKT_Calculator;
import asf.ASF_Minimizer;
import asf.LocalSearchOutput;
import parsing.IndividualEvaluator;
import parsing.KKTPM;
import reference_directions.ReferenceDirection;

/**
 *
 * @author Haitham
 */
public class LocalSearch {

    public static int infeasibleSolutionsReachedCount = 0;
    public static int sameSolutionsReachedCount = 0;
    public static List<Individual> startsOfSuccessfulLocalSearches = new ArrayList<Individual>();
    public static List<Individual> endsOfSuccessfulLocalSearches = new ArrayList<Individual>();
    public static List<Integer> generationsOfSuccessfulLocalSearches = new ArrayList<Integer>();

    public static LocalSearchSummary asfSearch(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            List<ReferenceDirection> referenceDirectionsList,
            Individual[] feasibleCandidates,
            double[][] distanceMatrix,
            ASF_Minimizer asfMinimizer,
            int maxFunEvals,
            Individual[] parentPopulation,
            int currentGenerationIndex,
            double[] idealPoint,
            double[] intercepts,
            double utopian_epsilon) throws MWException {
        // Empty reference directions analysis
        List<ReferenceDirection> promisingDirsList = new ArrayList<ReferenceDirection>(referenceDirectionsList.size());

        int promisingEmptyDirIndex = getClosestEmptyRefDir(referenceDirectionsList, distanceMatrix, promisingDirsList);
        //int promisingEmptyDirIndex = getClosestSurroundingEmptyRefDir(referenceDirectionsList, promisingDirsList);
        int localSearchStartingPointIndex = 0;
        double minDist = distanceMatrix[localSearchStartingPointIndex][promisingEmptyDirIndex];
        for (int indCounter = 1; indCounter < feasibleCandidates.length; indCounter++) {
            if (distanceMatrix[indCounter][promisingEmptyDirIndex] < minDist) {
                localSearchStartingPointIndex = indCounter;
                minDist = distanceMatrix[indCounter][promisingEmptyDirIndex];
            }
        }
        // Create and prepare the object to be returned
        LocalSearchSummary localSearchSummary = new LocalSearchSummary();
        // Append some details to the summary object to be returned
        localSearchSummary.setPromisingDirectionsList(promisingDirsList);
        localSearchSummary.setStartingIndividual(feasibleCandidates[localSearchStartingPointIndex]);
        localSearchSummary.setSearchDirection(referenceDirectionsList.get(promisingEmptyDirIndex));
//        for (int indCounter = 0; indCounter < feasibleCandidates.length; indCounter++) {
//            System.out.format("IND(%02d) -> %s = %5.3f%n",
//                    indCounter,
//                    Arrays.toString(localSearchSummary.getSearchDirection().direction),
//                    distanceMatrix[indCounter][promisingEmptyDirIndex]);
//        }
//        // TEMP MATLAB CODE
//        // All points X values
//        System.out.print("X = [");
//        for (int i = 0; i < feasibleCandidates.length; i++) {
//            System.out.format("%5.3f ", (feasibleCandidates[i].getObjective(0) - (-300)) / 298);
//        }
//        System.out.println("];");
//        // All points Y values
//        System.out.print("Y = [");
//        for (int i = 0; i < feasibleCandidates.length; i++) {
//            System.out.format("%5.3f ", (feasibleCandidates[i].getObjective(1) - (0)) / 178);
//        }
//        System.out.println("];");
//        // Start Point
//        System.out.format("X_start = [%5.3f];%n", (localSearchSummary.getStartingIndividual().getObjective(0) - (-300)) / 298);
//        System.out.format("Y_start = [%5.3f];%n", (localSearchSummary.getStartingIndividual().getObjective(1) - (0)) / 178);
//        // Direction
//        System.out.println("x = [0 1];");
//        System.out.format("y = %5.3f * x;%n", localSearchSummary.getSearchDirection().direction[1] / localSearchSummary.getSearchDirection().direction[0]);
//        // Plotting code
//        System.out.println(
//                "hold on\n"
//                + "scatter(X,Y)\n"
//                + "scatter(X_start,Y_start,'filled')\n"
//                + "plot(x,y)\n"
//                + "hold off\n"
//                + "xlim([0 1]);\n"
//                + "ylim([0 1]);\n");
        // Perform local search using the reference direction indexed
        // by (promisingEmptyDirIndex) in (referenceDirectionsList) and the
        // starting point indexed by (localSearchStartingPointIndex) in
        // (feasibleCandidates).

        // Calculate the unit weight vector
        double[] weightVector = Mathematics.getUnitVector(
                referenceDirectionsList.get(promisingEmptyDirIndex).direction);
        // x0 is taken to be the current individual
        double[] x0 = feasibleCandidates[localSearchStartingPointIndex].real;
        // Call the ASF minimizer of the problem in hand
        LocalSearchOutput asfOutput = asfMinimizer.minimizeASF(x0, weightVector, maxFunEvals, idealPoint, intercepts, utopian_epsilon);
        // Create a new individual form the returned ASF minimization output
        Individual localSearchResultingIndividual = new Individual(optimizationProblem, individualEvaluator, asfOutput.x);
        // Put the newly found individual in place of an already existing one
        // if possible. For detailed information abour this procedure, consult
        // the comments in following method.
        postLocalSearchReplacementProcedure(
                localSearchResultingIndividual,
                localSearchSummary,
                currentGenerationIndex,
                asfOutput,
                referenceDirectionsList.get(promisingEmptyDirIndex),
                parentPopulation);
        // Return the summary object of the local search process
        return localSearchSummary;
    }

    public static LocalSearchSummary asfSearchKKTBased(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            ASF_Minimizer asfMinimizer,
            KKT_Calculator kktCalculator,
            int localSearchMaxFunEvals,
            Individual[] parentPopulation,
            int currentGenerationIndex,
            double[] idealPoint,
            double[] _intercepts,
            double UTOPIAN_EPSILON
    ) throws MWException {
        // Only feasible solutions will be considered because infeasible
        // solutions are not attached to any direction. In other words, if
        // an infeasible solution exists, it will have the highest(worst)
        // KKT error, which means that it should be selected for a local
        // ASF search, and since it is not attached to any direction, a local
        // ASF will not be possible. This can be remedied later by attaching
        // infeasible solutions as well to reference diection in the objective
        // space.
        Individual[] nonDominated = OptimizationUtilities.getNonDominatedIndividuals(parentPopulation, 0);
        // Calculate all KKT errors
        KKTPM[] kktErrors
                = kktCalculator.calculatePopulationKKT(nonDominated);
        
        
        // UPDATE FE TO REFLECT NUMERICAL GRADIENT EVALUATIONS
        
        
        // Select the individual with the highest(worst) KKT error to be your
        // local search starting point.
        int worstKktIndividualIndex = 0;
        for (int i = 1; i < nonDominated.length; i++) {
            if (kktErrors[i].getKktpm() > kktErrors[worstKktIndividualIndex].getKktpm()) {
                worstKktIndividualIndex = i;
            }
        }
        Individual localSearchStartIndividual
                = nonDominated[worstKktIndividualIndex];
        // Create and prepare the object to be returned
        LocalSearchSummary localSearchSummary = new LocalSearchSummary();
        // Append some details to the summary object to be returned
        List<ReferenceDirection> promisingDirsList
                = new ArrayList<ReferenceDirection>();
        promisingDirsList.add(
                localSearchStartIndividual.getReferenceDirection());
        localSearchSummary.setPromisingDirectionsList(promisingDirsList);
        localSearchSummary.setStartingIndividual(localSearchStartIndividual);
        localSearchSummary.setSearchDirection(
                localSearchStartIndividual.getReferenceDirection());
        // Perfrom local search
        LocalSearchOutput asfOutput = asfMinimizer.minimizeASF(
                localSearchStartIndividual.real,
                Mathematics.getUnitVector(
                        localSearchStartIndividual.getReferenceDirection().direction),
                localSearchMaxFunEvals,
                idealPoint,
                _intercepts,
                UTOPIAN_EPSILON);
        // Local search may return a solution that is infeasible by a very
        // slight margin. The following call to fixVariableLimits(...) aims at
        // fixing design variables that are out of the pre-defined boxing
        // constraints.
        // NOTE-1: If the new solution violates a normal constraint (a non-boxing
        // constraint), the created individual will be deemed infeasible
        // through the updateIndividualObjectivesAndConstraints(...) method.
        // Update to Note-1: We had discovered this error before we noticed the
        // accuracy discrepancy between the Java code and Matlab's fmincon.
        // We did not check if this marginal infeasibility is still happenning
        // after having this discrepancy fixed (by increasing fmincon(...)
        // accuracy to at least the same level as the Java code).
        OptimizationUtilities.fixVariableLimits(optimizationProblem, asfOutput.x);
        // Create a new individual form the returned ASF minimization output
        Individual localSearchResultingIndividual = new Individual(optimizationProblem, individualEvaluator, asfOutput.x);
        // Put the newly found individual in place of an already existing one
        // if possible. For detailed information abour this procedure, consult
        // the comments in following method.
//        postLocalSearchReplacementProcedure(
//                localSearchResultingIndividual,
//                localSearchSummary,
//                currentGenerationIndex,
//                asfOutput,
//                localSearchStartIndividual.getReferenceDirection(),
//                parentPopulation);
        postLocalSearchSimpleReplacementProcedure(
                localSearchResultingIndividual,
                localSearchSummary,
                currentGenerationIndex,
                asfOutput,
                localSearchStartIndividual.getReferenceDirection(),
                parentPopulation);
        // Return the summary object of the local search process
        return localSearchSummary;
    }

//    public static LocalSearchSummary[] asfSearchAll(
//            OptimizationProblem optimizationProblem,
//            IndividualEvaluator individualEvaluator,
//            List<ReferenceDirection> referenceDirectionsList,
//            ASF_Minimizer asfMinimizer,
//            int localSearchMaxFunEvals,
//            Individual[] parentPopulation,
//            int currentGenerationIndex,
//            double[] idealPoint,
//            double[] intercepts,
//            double UTOPIAN_EPSILON) throws EvaluationException, MWException {
//        // Only feasible solutions will be considered because infeasible
//        // solutions are not attached to any direction. This can be remedied 
//        // later by attaching infeasible solutions as well to reference
//        // diections.
//        Individual[] feasiIndividuals = OptimizationUtilities.getFeasibleIndividuals(parentPopulation);
//        List<LocalSearchSummary> localSearchSummariesList
//                = new ArrayList<LocalSearchSummary>();
//        for (Individual localSearchStartIndividual : feasiIndividuals) {
//            // Create and prepare the object to be returned
//            LocalSearchSummary localSearchSummary = new LocalSearchSummary();
//            // Append some details to the summary object to be returned
//            List<ReferenceDirection> promisingDirsList
//                    = new ArrayList<ReferenceDirection>();
//            promisingDirsList.add(
//                    localSearchStartIndividual.getReferenceDirection());
//            localSearchSummary.setPromisingDirectionsList(promisingDirsList);
//            localSearchSummary.setStartingIndividual(localSearchStartIndividual);
//            localSearchSummary.setSearchDirection(
//                    localSearchStartIndividual.getReferenceDirection());
//            // Perfrom local search
//            LocalSearchOutput asfOutput = asfMinimizer.minimizeASF(
//                    localSearchStartIndividual.real,
//                    Mathematics.getUnitVector(
//                            localSearchStartIndividual.getReferenceDirection().direction),
//                    localSearchMaxFunEvals,
//                    idealPoint,
//                    intercepts,
//                    UTOPIAN_EPSILON);
//            // Local search may return a solution that is infeasible by a very
//            // slight margin. The following call to fixVariableLimits(...) aims at
//            // fixing design variables that are out of the pre-defined boxing
//            // constraints.
//            // NOTE: If the new solution violates a normal constraint (a non-boxing
//            // constraint), the created individual will be deemed infeasible
//            // through the updateIndividualObjectivesAndConstraints(...) method.
//            OptimizationUtilities.fixVariableLimits(optimizationProblem, asfOutput.x);
//            // Create a new individual form the returned ASF minimization output
//            Individual localSearchResultingIndividual = new Individual(optimizationProblem, individualEvaluator, asfOutput.x);
//            // Put the newly found individual in place of an already existing one
//            // if possible. For detailed information abour this procedure, consult
//            // the comments in following method.
//            // IMPORTNAT-NOTE: During the post-local-serach replacement
//            // procedure, the perpendicular distance of individuals from the
//            // direction ot which they are associated is used. If only one
//            // local search (i.e. one replacement is performed per generation,
//            // their will be no problem, BUT, if more than one replacement is
//            // performed, this means that while doing the 2nd replacement, the
//            // individual currently in the population as a result of the 1st
//            // replacement has a perpendicular distance of ZERO, which is not
//            // true. The actual distance from the direction is not calculated
//            // during inserting this new individual to the population.
//            // Consequently, the newly inserted individuals -before the current
//            // one- will be automatically avoided during replacement, because
//            // the replacement procedure thinks that they perfectly lie on
//            // their reference directions, which needs not to be true as
//            // explained. Although, this "ignoring" effect is desirable under
//            // the current condition and requirements, this might change later
//            // on)
//            postLocalSearchReplacementProcedure(
//                    localSearchResultingIndividual,
//                    localSearchSummary,
//                    currentGenerationIndex,
//                    asfOutput,
//                    localSearchStartIndividual.getReferenceDirection(),
//                    parentPopulation);
//
//            // Add your summary to the list of summaries
//            localSearchSummariesList.add(localSearchSummary);
//        }
//        // Return the array of summary objects
//        LocalSearchSummary[] localSearchSummariesArr
//                = new LocalSearchSummary[localSearchSummariesList.size()];
//        localSearchSummariesList.toArray(localSearchSummariesArr);
//        return localSearchSummariesArr;
//    }

    private static void postLocalSearchReplacementProcedure(
            Individual localSearchResultingIndividual,
            LocalSearchSummary localSearchSummary,
            int currentGenerationIndex,
            LocalSearchOutput asfOutput,
            ReferenceDirection referenceDirection,
            Individual[] parentPopulation) {
        if (localSearchResultingIndividual.isFeasible()) {
            // It has been noticed that in some cases when the number
            // of function evaluations of fmincon is restricted, the
            // result is an infeasible solution. Although the reasons
            // behind this behaviour needs further investigation, we
            // try to be PRECAUTIOUS by doing this feasibility check
            // (above). If the resulting solution is infeasible, it is
            // ignored. (see the else section of the feasibility check)
            // UPDATE-TO-COMMENT: After Mohamed pointed out the bug that ASF 
            // search code does NOT normalize constraints, and after this bug
            // is fixed, we need to re-check, if the solution can still be 
            // infeasible. Until this is done, we will remain PRECAUTIOUS.
            if (localSearchResultingIndividual.equals(localSearchSummary.getStartingIndividual())) {
                sameSolutionsReachedCount++;
            } else {
                startsOfSuccessfulLocalSearches.add(localSearchSummary.getStartingIndividual());
                endsOfSuccessfulLocalSearches.add(localSearchResultingIndividual);
                generationsOfSuccessfulLocalSearches.add(currentGenerationIndex);
            }
            // Append some details to the summary object to be returned
            localSearchSummary.setResultingIndividual(localSearchResultingIndividual);
            localSearchSummary.setLocalSearchOutput(asfOutput);
            // Now it should be attached to the new direction (NEEDS MORE
            // INVESTIGATION: This assumes that after performing an ASF search
            // using some direction and a point attached to this direction,
            // the resulting individual will be closer to this (same) direction
            // than any other direction. We did not test if this
            // assumption is 100% valid or not though. we can safely use it, at least
            // when performing a few search operation per generation)
            localSearchResultingIndividual.setReferenceDirection(referenceDirection);
            localSearchResultingIndividual.validReferenceDirection = true;

            // Some individual needs to be replaced by the new individual. This
            // individual should be as un-important as possible! In order to do
            // so, we start by searching for infeasible solutions in the
            // population. If found just pick the first of them and replace it.
            int indexOfIndividualToBeReplaced = -1;
            for (int i = 0; i < parentPopulation.length; i++) {
                if (!parentPopulation[i].isFeasible()) {
                    indexOfIndividualToBeReplaced = i;
                    break;
                }
            }
            // Otherwise, (no infeasible individuals to replace) search for
            // the reference direction having the highest number of attached
            // individuals. Pick the individual furthest from the reference
            // direction and replace it.
            // The following if statment takes care of this.
            if (indexOfIndividualToBeReplaced == -1) {
                // If all individuals in the current population are feasible
                // Add the individuals attached to each direction to a HashMap.
                // Remember that the distanceMatrix contains information about
                // associations of all feasibleCandidates not only of those
                // individuals in parentPopulation. That's why we cannot
                // use information provided in the distanceMatrix. We need to
                // consider only those individuals included in the
                // parentPopulation, because one of them needs to be replaced.
                HashMap<ReferenceDirection, List<Integer>> attachments
                        = new HashMap<ReferenceDirection, List<Integer>>();
                for (int i = 0; i < parentPopulation.length; i++) {
                    if (attachments.containsKey(parentPopulation[i].getReferenceDirection())) {
                        List<Integer> listOfIndividuals = attachments.get(parentPopulation[i].getReferenceDirection());
                        listOfIndividuals.add(i);
                    } else {
                        List<Integer> listOfIndividualsIndices = new ArrayList<Integer>();
                        listOfIndividualsIndices.add(i);
                        attachments.put(parentPopulation[i].getReferenceDirection(), listOfIndividualsIndices);
                    }
                }
                // Iterate over items in the HashMap and select the reference
                // direction with highest number of attachments (associations).
                // Actually we are not interested in the reference direction
                // itself, we are rather interested in the individuals attached
                // to it.
                int maxAttachedIndividualsCount = 0;
                List<Integer> maxAttachedIndividualsIndices = null;
                for (Map.Entry<ReferenceDirection, List<Integer>> entry : attachments.entrySet()) {
                    List<Integer> attachedIndividualsIndices = entry.getValue();
                    if (maxAttachedIndividualsCount < attachedIndividualsIndices.size()) {
                        maxAttachedIndividualsCount = attachedIndividualsIndices.size();
                        maxAttachedIndividualsIndices = attachedIndividualsIndices;
                    }
                }
                // Select out of the attached points, the point furthest from 
                // the reference direction
                indexOfIndividualToBeReplaced = maxAttachedIndividualsIndices.get(0);
                double maxDistance = parentPopulation[maxAttachedIndividualsIndices.get(0)].getPerpendicularDistance();
                for (int i = 1; i < maxAttachedIndividualsIndices.size(); i++) {
                    if (maxDistance < parentPopulation[maxAttachedIndividualsIndices.get(i)].getPerpendicularDistance()) {
                        indexOfIndividualToBeReplaced = maxAttachedIndividualsIndices.get(i);
                    }
                }
            }
            // After reaching this point, indexOfIndividualToBeReplaced should
            // now hold the index of the individual which will be replaced
            // in parentPopulation.
            // Append some details to the summary object to be returned
            localSearchSummary.setReplacedIndividual(
                    parentPopulation[indexOfIndividualToBeReplaced]);
            // Now replace the least important individual(determined above)
            // with the individual reached by local search.
            parentPopulation[indexOfIndividualToBeReplaced] = localSearchResultingIndividual;
        } else {
            infeasibleSolutionsReachedCount++;
        }
    }

    private static void postLocalSearchSimpleReplacementProcedure(Individual localSearchResultingIndividual, LocalSearchSummary localSearchSummary, int currentGenerationIndex, LocalSearchOutput asfOutput, ReferenceDirection referenceDirection, Individual[] parentPopulation) {
        if (localSearchResultingIndividual.isFeasible()) {
            // It has been noticed that in some cases when the number
            // of function evaluations of fmincon is restricted, the
            // result is an infeasible solution. Although the reasons
            // behind this behaviour needs further investigation, we
            // try to be PRECAUTIOUS by doing this feasibility check
            // (above). If the resulting solution is infeasible, it is
            // ignored. (see the else section of the feasibility check)
            // UPDATE-TO-COMMENT: After Mohamed pointed out the bug that ASF 
            // search code does NOT normalize constraints, and after this bug
            // is fixed, we need to re-check, if the solution can still be 
            // infeasible. Until this is done, we will remain PRECAUTIOUS.
            if (localSearchResultingIndividual.equals(localSearchSummary.getStartingIndividual())) {
                sameSolutionsReachedCount++;
            } else {
                startsOfSuccessfulLocalSearches.add(localSearchSummary.getStartingIndividual());
                endsOfSuccessfulLocalSearches.add(localSearchResultingIndividual);
                generationsOfSuccessfulLocalSearches.add(currentGenerationIndex);
            }
            // Append some details to the summary object to be returned
            localSearchSummary.setResultingIndividual(localSearchResultingIndividual);
            localSearchSummary.setLocalSearchOutput(asfOutput);
            // Now it should be attached to the new direction (NEEDS MORE
            // INVESTIGATION: This assumes that after performing an ASF search
            // using some direction and a point attached to this direction,
            // the resulting individual will be closer to this (same) direction
            // than any other direction. We did not test if this
            // assumption is 100% valid or not though. we can safely use it, at least
            // when performing a few search operation per generation)
            localSearchResultingIndividual.setReferenceDirection(referenceDirection);
            localSearchResultingIndividual.validReferenceDirection = true;
            // Replace the starting individual
            for(int i = 0; i < parentPopulation.length; i++) {
                if(parentPopulation[i].equals(localSearchSummary.startingIndividual)) {
                    parentPopulation[i] = localSearchResultingIndividual;
                }
            }
        } else {
            infeasibleSolutionsReachedCount++;
        }
    }

    public static class LocalSearchSummary {

        private Individual startingIndividual;
        private Individual resultingIndividual;
        private Individual replacedIndividual;
        private List<ReferenceDirection> promisingDirectionsList;
        private ReferenceDirection searchDirection;
        private LocalSearchOutput localSearchOutput;

        public LocalSearchSummary() {
        }

        /**
         * @return the startingIndividual
         */
        public Individual getStartingIndividual() {
            return startingIndividual;
        }

        /**
         * @param startingIndividual the startingIndividual to set
         */
        public void setStartingIndividual(Individual startingIndividual) {
            this.startingIndividual = startingIndividual;
        }

        /**
         * @return the resultingIndividual
         */
        public Individual getResultingIndividual() {
            return resultingIndividual;
        }

        /**
         * @param resultingIndividual the resultingIndividual to set
         */
        public void setResultingIndividual(Individual resultingIndividual) {
            this.resultingIndividual = resultingIndividual;
        }

        /**
         * @return the replacedIndividual
         */
        public Individual getReplacedIndividual() {
            return replacedIndividual;
        }

        /**
         * @param replacedIndividual the replacedIndividual to set
         */
        public void setReplacedIndividual(Individual replacedIndividual) {
            this.replacedIndividual = replacedIndividual;
        }

        /**
         * @return the promisingDirectionsList
         */
        public List<ReferenceDirection> getPromisingDirectionsList() {
            return promisingDirectionsList;
        }

        /**
         * @param promisingDirectionsList the promisingDirectionsList to set
         */
        public void setPromisingDirectionsList(List<ReferenceDirection> promisingDirectionsList) {
            this.promisingDirectionsList = promisingDirectionsList;
        }

        /**
         * @return the searchDirection
         */
        public ReferenceDirection getSearchDirection() {
            return searchDirection;
        }

        /**
         * @param searchDirection the searchDirection to set
         */
        public void setSearchDirection(ReferenceDirection searchDirection) {
            this.searchDirection = searchDirection;
        }

        /**
         * @return the localSearchOutput
         */
        public LocalSearchOutput getLocalSearchOutput() {
            return localSearchOutput;
        }

        /**
         * @param localSearchOutput the localSearchOutput to set
         */
        public void setLocalSearchOutput(LocalSearchOutput localSearchOutput) {
            this.localSearchOutput = localSearchOutput;
        }
    }

    private static int getClosestEmptyRefDir(List<ReferenceDirection> referenceDirectionsList, double[][] distanceMatrix, List<ReferenceDirection> promisingDirsList) {
        // Get the distance between each empty direction and the closest individual to it
        double[] minDistArr = new double[referenceDirectionsList.size()];
        for (int dirCounter = 0; dirCounter < minDistArr.length; dirCounter++) {
            minDistArr[dirCounter] = Double.MAX_VALUE;
        }
        for (int dirCounter = 0; dirCounter < referenceDirectionsList.size(); dirCounter++) {
            for (int indCounter = 0; indCounter < distanceMatrix.length; indCounter++) {
                if (referenceDirectionsList.get(dirCounter).surroundingIndividuals.isEmpty()) {
                    if (distanceMatrix[indCounter][dirCounter] < minDistArr[dirCounter]) {
                        minDistArr[dirCounter] = distanceMatrix[indCounter][dirCounter];
                    }
                }
            }
        }
        // Make a copy of the reference directions list (to keep the original list intact after sorting)
        for (ReferenceDirection refDirection : referenceDirectionsList) {
            promisingDirsList.add(new ReferenceDirection(refDirection.direction));
        }
        // Sort the temporary directions ascendingly according to distances in minDistArr
        for (int i = 0; i < minDistArr.length - 1; i++) {
            for (int j = i + 1; j < minDistArr.length; j++) {
                if (minDistArr[j] < minDistArr[i]) {
                    double temp = minDistArr[i];
                    minDistArr[i] = minDistArr[j];
                    minDistArr[j] = temp;
                    ReferenceDirection tempRefDir = promisingDirsList.get(i);
                    promisingDirsList.set(i, promisingDirsList.get(j));
                    promisingDirsList.set(j, tempRefDir);
                }
            }
        }
        // Remove non-empty dirs from the end of the tempDirsList
        while (Mathematics.compare(minDistArr[minDistArr.length - 1], Double.MAX_VALUE) == 0) {
            // Remove the last element of minDistArr
            double[] tempMinDistArr = new double[minDistArr.length - 1];
            System.arraycopy(minDistArr, 0, tempMinDistArr, 0, minDistArr.length - 1);
            minDistArr = tempMinDistArr;
            // Remove the last direction in tempDirsList
            promisingDirsList.remove(promisingDirsList.size() - 1);
            // If all reference directions are covered by individuals just stop
            if (promisingDirsList.isEmpty()) {
                break;
            }
        }
        // Now select the empty dir with the closest individual in order to
        // perform local search. If all directions are covered, select one
        // of the directions randomly.
        int promisingEmptyDirIndex;
        if (promisingDirsList.isEmpty()) {
            promisingEmptyDirIndex
                    = RandomNumberGenerator.rnd(
                            0,
                            referenceDirectionsList.size() - 1);
        } else {
            promisingEmptyDirIndex = 0;
            for (int i = 0; i < referenceDirectionsList.size(); i++) {
                if (referenceDirectionsList.get(i).equals(promisingDirsList.get(0))) {
                    promisingEmptyDirIndex = i;
                    break;
                }
            }
        }
        return promisingEmptyDirIndex;
    }

    public static int firstBoundingDirSelectionCount = 0;
    public static int secondBoundingDirSelectionCount = 0;

    private static int getClosestSurroundingEmptyRefDir(List<ReferenceDirection> referenceDirectionsList, List<ReferenceDirection> promisingDirsList) {
        int diff = 1;
        int refDirIndex;
        if (RandomNumberGenerator.randomperc() < 0.5) {
            firstBoundingDirSelectionCount++;
            refDirIndex = 0;
            for (int i = refDirIndex + diff; i < referenceDirectionsList.size(); i++) {
                if (!referenceDirectionsList.get(i).surroundingIndividuals.isEmpty()) {
                    refDirIndex = i - diff;
                    promisingDirsList.add(referenceDirectionsList.get(refDirIndex));
                    break;
                }
            }
        } else {
            secondBoundingDirSelectionCount++;
            refDirIndex = referenceDirectionsList.size() - 1;
            for (int i = referenceDirectionsList.size() - 1 - diff; i >= 0; i--) {
                if (!referenceDirectionsList.get(i).surroundingIndividuals.isEmpty()) {
                    refDirIndex = i + diff;
                    promisingDirsList.add(referenceDirectionsList.get(refDirIndex));
                    break;
                }
            }
        }
        return refDirIndex;
    }
}
