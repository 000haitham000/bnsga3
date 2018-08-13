/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsing;

/**
 *
 * @author toshiba
 */
public class InvalidOptimizationProblemException extends Exception {

    private String message;

    public InvalidOptimizationProblemException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return "Invalid Optimization Problem: " + getMessage();
    }
}
