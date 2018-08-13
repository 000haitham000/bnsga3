/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkt.xmlbased;

import evaluators.GeneralDTLZ4Evaluator;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsing.XMLParser;

/**
 *
 * @author Haitham
 */
public class Dtlz4KKTPMCalculator_05obj extends KKTPM_CalculatorImpl {

    public Dtlz4KKTPMCalculator_05obj() {
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
                problem = XMLParser.readXML(new File("E:/KKTPM/Java KKTPM/XML/dtlz4_5obj_modified.xml"));
                problem.setConstant("pi", Math.PI);
                problem.setConstant("D", GeneralDTLZ4Evaluator.D);
                problem.setConstant("alpha", GeneralDTLZ4Evaluator.ALPHA);
            } catch (Throwable ex) {
                Logger.getLogger(Dtlz4KKTPMCalculator_05obj.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.toString());
            }
        }
    }
}
