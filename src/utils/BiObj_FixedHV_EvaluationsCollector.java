/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author Haitham
 */
public class BiObj_FixedHV_EvaluationsCollector {

    static String dir = "d:/results/_bi_objective/N_greater_than_H";

    private static StringBuilder nsga2Sb = new StringBuilder("nsga2 = [");
    private static StringBuilder nsga3Sb = new StringBuilder("nsga3 = [");
    private static StringBuilder uniNsga3Sb = new StringBuilder("uni_nsga3 = [");
    private static List<Integer> nsga2EvaluationsList = new ArrayList<Integer>();
    private static List<Integer> nsga3EvaluationsList = new ArrayList<Integer>();
    private static List<Integer> uniNsga3EvaluationsList = new ArrayList<Integer>();

    static JFileChooser fileChooser;

    public static void main(String[] args) throws IOException {
        fileChooser = new JFileChooser(new File(dir));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File[] dirs = fileChooser.getSelectedFiles();
            int nsga2EvaluationsCount = -1;
            int nsga3EvaluationsCount = -1;
            int uniNsga3EvaluationsCount = -1;
            for (int i = 0; i < dirs.length; i++) {
                int nsga2TempCount = getEvaluationsCount(dirs[i], "nsga2");
                nsga2EvaluationsCount = nsga2TempCount == -1 ? nsga2EvaluationsCount : nsga2TempCount;
                int nsga3TempCount = getEvaluationsCount(dirs[i], "nsga3");
                nsga3EvaluationsCount = nsga3TempCount == -1 ? nsga3EvaluationsCount : nsga3TempCount;
                int uniNsga3TempCount = getEvaluationsCount(dirs[i], "unified_nsga3");
                uniNsga3EvaluationsCount = uniNsga3TempCount == -1 ? uniNsga3EvaluationsCount : uniNsga3TempCount;
                nsga2Sb.append(String.valueOf(nsga2EvaluationsCount));
                nsga2EvaluationsList.add(nsga2EvaluationsCount);
                nsga3EvaluationsList.add(nsga3EvaluationsCount);
                uniNsga3EvaluationsList.add(uniNsga3EvaluationsCount);
                nsga3Sb.append(String.valueOf(nsga3EvaluationsCount));
                uniNsga3Sb.append(String.valueOf(uniNsga3EvaluationsCount));
                if (i != dirs.length - 1) {
                    nsga2Sb.append(", ");
                    nsga3Sb.append(", ");
                    uniNsga3Sb.append(", ");
                }
            }
        }
        nsga2Sb.append("];");
        nsga3Sb.append("];");
        uniNsga3Sb.append("];");
        System.out.println(nsga2Sb.toString());
        System.out.println(nsga3Sb.toString());
        System.out.println(uniNsga3Sb.toString());
        dumpMedianEvaluations(dir, nsga2EvaluationsList, nsga3EvaluationsList, uniNsga3EvaluationsList);
    }

    private static int getEvaluationsCount(File dir, String subDirName) throws FileNotFoundException, IOException {
        File subDir = new File(dir.getPath() + "/" + subDirName);
        File[] allFiles = subDir.listFiles();
        File evaluationsFile = null;
        if (allFiles != null) {
            for (File file : allFiles) {
                if (file.getName().contains("evaluations")) {
                    evaluationsFile = file;
                    break;
                }
            }
        }
        if (evaluationsFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(evaluationsFile));
                boolean meanTagEncountered = false;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (meanTagEncountered && line.toLowerCase().contains("evaluations")) {
                        String[] splits = line.split(" ");
                        return (int) Double.parseDouble(splits[2]);
                    }
                    if (line.toLowerCase().contains("mean")) {
                        meanTagEncountered = true;
                    }
                }
                throw new IllegalArgumentException("Invalid File Format");
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } else {
            return -1;
        }
    }

    private static void dumpMedianEvaluations(String dir, List<Integer> nsga2EvaluationsList, List<Integer> nsga3EvaluationsList, List<Integer> uniNsga3EvaluationsList) throws FileNotFoundException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(dir + "/eval_median_stats.dat");
            for (int i = 0; i < nsga2EvaluationsList.size(); i++) {
                printer.format("%06d %06d %06d%n",
                        nsga2EvaluationsList.get(i),
                        nsga3EvaluationsList.get(i),
                        uniNsga3EvaluationsList.get(i));
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }
}
