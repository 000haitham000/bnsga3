/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import emo.Individual;
import java.util.Set;

/**
 *
 * @author seadahai
 */
public interface InternalLocalSearchInterface {

    public void populationLocalSearch(
            Set<Individual> newPopSet,
            Individual[] feasibleOnly,
            double[][] distanceMatrix);
}
