/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reference_directions;

import utils.Mathematics;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author toshiba
 */
public class DasDennis {
    /*
     int nref;

     private void add_ref_points(int p, List<ReferenceDirection> directionsList, int numberOfObjectives) {
     int i, j, l, m, e, no, tp;
     double delta, limit, temp, beta;
     delta = (double) (1.0 / (double) p);
     m = Mathematics.nchoosek(numberOfObjectives + p - 1, p);

     for (i = 0; i < numberOfObjectives - 1; i++) {
     e = nref;
     while (e <= nref + m - 1) {
     if (i == 0) {
     limit = 0;
     } else {
     limit = 0.0;
     for (j = 0; j < i; j++) {
     limit = limit + directionsList.get(e).direction[j];
     }
     }
     for (j = 0; j <= (int) (((1 - limit) / delta) + 0.5); j++) {
     beta = delta * (double) j;

     tp = (int) (((1 - limit - beta) / delta) + 0.5);
     no = Mathematics.nchoosek(numberOfObjectives - i - 2 + tp, tp);
     for (l = e; l < e + no; l++) {
     directionsList.get(l).direction[i] = beta;
     }

     e = e + no;

     }
     }
     }

     for (i = nref; i < nref + m; i++) {
     temp = 0.0;
     for (j = 0; j < numberOfObjectives - 1; j++) {
     temp = temp + directionsList.get(i).direction[j];

     }
     directionsList.get(i).direction[numberOfObjectives - 1] = 1 - temp;
     }
     nref = nref + m;
     }

     public List<ReferenceDirection> getReferenceDirections(int divisions, int numberOfObjectives) {
     List<ReferenceDirection> directionsList = new ArrayList<ReferenceDirection>();
     if(numberOfObjectives == 1) {
     directionsList.add(new ReferenceDirection(new double[]{1.0}));
     return directionsList;
     }

     int e, no, tp;
     double delta, limit, temp, beta;
     float value;

     // Das and Dennis's approach to create structred reference points
     delta = (double) (1.0 / (double) divisions);
     int m = Mathematics.nchoosek(numberOfObjectives + divisions - 1, divisions);
     nref = m;

     for (int i = 0; i < 100000; i++) {
     directionsList.add(new ReferenceDirection(new double[numberOfObjectives]));
     }

     for (int i = 0; i < numberOfObjectives - 1; i++) {
     e = 0;
     while (e <= m - 1) {
     if (i == 0) {
     limit = 0;
     } else {
     limit = 0.0;
     for (int j = 0; j < i; j++) {
     limit = limit + directionsList.get(e).direction[j];
     }
     }
     for (int j = 0; j <= (int) (((1 - limit) / delta) + 0.5); j++) {
     beta = delta * (double) j;

     tp = (int) (((1 - limit - beta) / delta) + 0.5);
     no = Mathematics.nchoosek(numberOfObjectives - i - 2 + tp, tp);

     for (int l = e; l < e + no; l++) {
     ReferenceDirection dir = directionsList.get(l);
     dir.direction[i] = beta;
     }
     e = e + no;
     }
     }
     }
     for (int i = 0; i < m; i++) {
     temp = 0.0;
     for (int j = 0; j < numberOfObjectives - 1; j++) {
     temp = temp + directionsList.get(i).direction[j];
     }
     directionsList.get(i).direction[numberOfObjectives - 1] = 1 - temp;
     }

     // if nobj is high use multilayered reference points
     if (numberOfObjectives > 5) {
     for (int i = 0; i < m; i++) {
     for (int j = 0; j < numberOfObjectives; j++) {
     directionsList.get(i).direction[j] = directionsList.get(i).direction[j] * 0.5 + 0.5 / (double) numberOfObjectives;
     }
     }
     add_ref_points(divisions + 1, directionsList, numberOfObjectives);
     }
     return directionsList;
     }
     */
}
