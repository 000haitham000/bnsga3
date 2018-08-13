/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emo;

/**
 *
 * @author toshiba
 */
public class InvalidObjectiveValue extends RuntimeException {

    public InvalidObjectiveValue() {
        super("The objective function being retreived is either not "
                + "calculated yet or outdated.");
    }

    public InvalidObjectiveValue(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}

