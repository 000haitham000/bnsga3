/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

import asf_uf1_2017b_lib.AsfUF1Optimizer;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import emo.Individual;
import emo.OptimizationProblem;
import evaluators.UF1Evaluator;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author Haitham
 */
public class UF1_ASF_Minimizer implements ASF_Minimizer {

    // The object representing tha MATLAB script
    static AsfUF1Optimizer asfUf1Optimizer = null;

    public UF1_ASF_Minimizer() throws MWException {
        // This class follows a Singleton design pattern to speed up the
        // calculations. Ther is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive and different calls)
        if (asfUf1Optimizer == null) {
            asfUf1Optimizer = new AsfUF1Optimizer();
        }
    }

    @Override
    public LocalSearchOutput minimizeASF(
            double[] x0,
            double[] weightVector,
            int maxFunEvals,
            double[] idealPoint,
            double[] intercepts,
            double utopian_epsilon) throws MWException {
        // Preparing parameters
        MWNumericArray startingPoint = new MWNumericArray(x0, MWClassID.DOUBLE);
        MWNumericArray wVector = new MWNumericArray(weightVector, MWClassID.DOUBLE);
        MWNumericArray ideal = new MWNumericArray(idealPoint, MWClassID.DOUBLE);
        MWNumericArray intrcpts = new MWNumericArray(intercepts, MWClassID.DOUBLE);
        // Create the utopian displacement vector
        double[] utopian_displacement_vector = {utopian_epsilon, utopian_epsilon};
        // Call the script
        Object[] result = asfUf1Optimizer.fmincon_uf1_asf_script(3, startingPoint, wVector, maxFunEvals, ideal, intrcpts, utopian_displacement_vector);
        // Collect outputs
        double[] xSpace = ((double[][]) ((MWNumericArray) result[0]).toDoubleArray())[0];
        double[] fSpace = ((double[][]) ((MWNumericArray) result[1]).toDoubleArray())[0];
        int evalCount = Integer.parseInt(result[2].toString());
        // Create a LocalSearchOutput object to hold all the returned results
        LocalSearchOutput localSearchOutput = new LocalSearchOutput(xSpace, fSpace, evalCount);
//        System.out.format("Individual %s is minimized to %s.%n", Arrays.toString(x0), localSearchOutput.toString());
        return localSearchOutput;
    }

    public static void main(String[] args) throws MWException, IOException, XMLStreamException, InvalidOptimizationProblemException {
        // Testing point
        double[] x1 = {0.2, -0.5, 0.0, 0.3, 0.1, 0.96, -0.5, 0.213, 0.3, 0.1, -0.96, -0.5, -0.8, 0.03, 0.1, -0.99, -1, 1, 0.0, 0.0, 0.66, -0.5, 0.8, 0.3, 0.1, -0.96, -0.5, 0.8, 0.3, 0.1};

        // Test the MATLAB code
        MWNumericArray point = new MWNumericArray(x1, MWClassID.DOUBLE);
        // Create an object of the MATLAB generated class
        AsfUF1Optimizer asfUF1Optimizer = new AsfUF1Optimizer();
        // f1
        Object[] f1Result = asfUF1Optimizer.f1(1, point);
        double f1 = ((double[][]) ((MWNumericArray) f1Result[0]).toDoubleArray())[0][0];
        // f2
        Object[] f2Result = asfUF1Optimizer.f2(1, point);
        double f2 = ((double[][]) ((MWNumericArray) f2Result[0]).toDoubleArray())[0][0];

        // Test the Java Code
        URL url = UF1_ASF_Minimizer.class.getResource("../samples/uf1-30.xml");
        OptimizationProblem problem = StaXParser.readProblem(url.openStream());
        UF1Evaluator evaluator = new UF1Evaluator();
        Individual individual = new Individual(problem, evaluator, x1);

        // Display Matlab results
        System.out.format("%7s: f1 = %10.7f, f2 = %10.7f%n", "MATLAB", f1, f2);
        // Display Java results
        System.out.format("%7s: f1 = %10.7f, f2 = %10.7f%n",
                "Java",
                individual.getObjective(0),
                individual.getObjective(1));
    }
}
