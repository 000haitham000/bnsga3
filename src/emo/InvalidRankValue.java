/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emo;

/**
 *
 * @author toshiba
 */
public class InvalidRankValue extends RuntimeException {

    private Individual individual;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }
    
    public InvalidRankValue(Individual aThis) {
    }
    
    @Override
    public String toString() {
        return "The individual whose rank is being retreived is either "
                + "unranked yet, or his rank is outdated";
    }
}
