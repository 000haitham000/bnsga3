/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reference_directions;

import emo.Individual;

/**
 *
 * @author toshiba
 */
public class InvalidReferenceDirectionValue extends RuntimeException {

    private Individual individual;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }
    
    public InvalidReferenceDirectionValue(Individual aThis) {
    }
    
    @Override
    public String toString() {
        return "The individual whose reference direction is being retreived is "
                + "either not associated to any direction yet, or his "
                + "association is outdated";
    }
}
