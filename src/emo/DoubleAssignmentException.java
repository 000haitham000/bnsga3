/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emo;

/**
 *
 * @author toshiba
 */
public class DoubleAssignmentException extends Exception {

    private String message;
    
    public DoubleAssignmentException(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
