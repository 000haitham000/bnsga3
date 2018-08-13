/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkt.xmlbased;

import emo.Individual;
import emo.OptimizationProblem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.KKTPM;
import parsing.StaXParser;
import parsing.XMLParser;
import test.MatlabIO;

/**
 *
 * @author Haitham
 */
public class Uf1KKTPMCalculator extends KKTPM_CalculatorImpl {

    public Uf1KKTPMCalculator() {
        // This class follows a Singleton design pattern to speed up the
        // calculations. Ther is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls should be the same as the
        // results of creating a new object for every call (has not been
        // verified independently yet though)
        if (problem == null) {
            try {
                problem = XMLParser.readXML(new File("E:/Dropbox/KKTPM/Java KKTPM/XML/uf1.xml"));
                problem.setConstant("pi", Math.PI);
            } catch (Throwable ex) {
                Logger.getLogger(Uf1KKTPMCalculator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.toString());
            }
        }
    }

    // Unlike other KKTPM calculator classes, the results of this calculator
    // haven't been compared to any other code, including MATLAB.
}
