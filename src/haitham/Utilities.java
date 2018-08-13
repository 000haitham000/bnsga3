/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haitham;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.NonDominatedSolutionList;

/**
 *
 * @author Haitham
 */
public class Utilities {

    public static String replaceBlanksWithSingleSpace(String text) {
        StringBuilder sb = new StringBuilder();
        boolean oneSpaceConsumed = false;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                sb.append(text.charAt(i));
                oneSpaceConsumed = false;
            } else {
                if (!oneSpaceConsumed) {
                    sb.append(" ");
                    oneSpaceConsumed = true;
                }
            }
        }
        return sb.toString();
    }

    /**
     * Reads a set of non dominated solutions from a file
     *
     * @param path The path of the file containing the data
     * @return A solution set
     */
    public static SolutionSet haithamReadNonDominatedSolutionSet(String path) {
        try {
            /* Open the file */
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            SolutionSet solutionSet = new NonDominatedSolutionList();
            String aux;
            outerLoop:
            while ((aux = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(aux);
                int i = 0;
                Solution solution = new Solution(st.countTokens());
                while (st.hasMoreTokens()) {
                    String nextToken = st.nextToken();
                    if(nextToken.startsWith("#")) {
                        continue outerLoop;
                    }
                    double value = new Double(nextToken);
                    solution.setObjective(i, value);
                    i++;
                }
                solutionSet.add(solution);
            }
            br.close();
            return solutionSet;
        } catch (Exception e) {
            System.out.println("jmetal.qualityIndicator.util.readNonDominatedSolutionSet: " + path);
            e.printStackTrace();
        }
        return null;
    } // readNonDominatedSolutionSet
}
