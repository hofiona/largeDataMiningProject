import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.tools.dataset_converter.TransactionDatabaseConverter;

public class Task1_2 {
    public static void main(String[] args) throws IOException {

        // Conversion of ARFF file to SPMF
        TransactionDatabaseConverter converter = new TransactionDatabaseConverter();

        String inputARFF_yes = "C:/Users/Fiona/IFN645/Project/bank_yes.arff";
        String convertSMPF_yes = "C:/Users/Fiona/IFN645/Project/bank_yes.spmf";

        String inputARFF_no = "C:/Users/Fiona/IFN645/Project/bank_no.arff";
        String convertSMPF_no = "C:/Users/Fiona/IFN645/Project/bank_no.spmf";

        // Convert ARFF to SPMF format
        converter.convertARFFandReturnMap(inputARFF_yes, convertSMPF_yes, Integer.MAX_VALUE);
        converter.convertARFFandReturnMap(inputARFF_no, convertSMPF_no, Integer.MAX_VALUE);

        String input_dataset_yes = convertSMPF_yes;
        String input_dataset_no = convertSMPF_no;

        // Corrected output paths
        String output_yes = "C:/Users/Fiona/IFN645/Project/output/Q2_bank_yes.txt";
        String output_no = "C:/Users/Fiona/IFN645/Project/output/Q2_bank_no.txt";

        // Set minimum support values for each dataset
        double minSupportYes = 0.45;  
        double minSupportNo = 0.48;  

        // Create separate instances for AlgoFPGrowth
        AlgoFPGrowth algoFPGrowthYes = new AlgoFPGrowth();
        AlgoFPGrowth algoFPGrowthNo = new AlgoFPGrowth();

        // Set pattern length constraints to only focus on size-3 patterns
        algoFPGrowthYes.setMaximumPatternLength(3);
        algoFPGrowthYes.setMinimumPatternLength(3);

        algoFPGrowthNo.setMaximumPatternLength(3);
        algoFPGrowthNo.setMinimumPatternLength(3);

        // Run FP-Growth on the "yes" dataset and extract top 5 size-3 patterns
        System.out.println("Frequent patterns for 'yes' dataset:");
        runFPGrowth(input_dataset_yes, output_yes, minSupportYes, algoFPGrowthYes);
        List<String> topPatternsYes = readPatternsFromFile(output_yes);
        System.out.println("Top 5 most frequent size-3 patterns for 'yes' class:");
        printTopSize3Patterns(topPatternsYes);

        // Run FP-Growth on the "no" dataset and extract top 5 size-3 patterns
        System.out.println("Frequent patterns for 'no' dataset:");
        runFPGrowth(input_dataset_no, output_no, minSupportNo, algoFPGrowthNo);
        List<String> topPatternsNo = readPatternsFromFile(output_no);
        System.out.println("Top 5 most frequent size-3 patterns for 'no' class:");
        printTopSize3Patterns(topPatternsNo);

        // Compare the two pattern sets and analyze common/differing characteristics
        analyzeAndComparePatterns(topPatternsYes, topPatternsNo);
    }

    public static void runFPGrowth(String datasetPath, String outputFilePath, double minSupport, AlgoFPGrowth algo) throws IOException {
        // Run FP-Growth algorithm
        algo.runAlgorithm(datasetPath, outputFilePath, minSupport);

        // Print statistics about the algorithm run
        algo.printStats();
    }

    public static List<String> readPatternsFromFile(String outputFile) throws IOException {
        List<String> patterns = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(outputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            patterns.add(line);
        }
        reader.close();

        // Sort patterns by support count in descending order
        patterns.sort(new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                int sup1 = Integer.parseInt(p1.substring(p1.lastIndexOf("#SUP:") + 6).trim());
                int sup2 = Integer.parseInt(p2.substring(p2.lastIndexOf("#SUP:") + 6).trim());
                return Integer.compare(sup2, sup1);  // Sort in descending order
            }
        });

        return patterns;
    }

    public static void printTopSize3Patterns(List<String> patterns) {
        // Filter and print only size-3 patterns
        List<String> size3Patterns = new ArrayList<>();
        for (String pattern : patterns) {
            String[] items = pattern.split(" ");
            // Adjust pattern length to match the actual format (items + #SUP keyword)
            if (items.length == 5) {  // Adjusted to match "3 items + support count"
                size3Patterns.add(pattern);
            }
        }

        // Print top 5 size-3 patterns based on their support count
        System.out.println("Top 5 size-3 patterns:");
        for (int i = 0; i < Math.min(5, size3Patterns.size()); i++) {
            System.out.println(size3Patterns.get(i));
        }
    }

    public static void analyzeAndComparePatterns(List<String> patternsYes, List<String> patternsNo) {
        System.out.println("\n### Analysis of Patterns between Yes and No Classes ###");

        System.out.println("Common patterns between 'Yes' and 'No' classes:");
        for (String patternYes : patternsYes) {
            if (patternsNo.contains(patternYes)) {
                System.out.println("Common Pattern: " + patternYes);
            }
        }

        System.out.println("\nUnique patterns for 'Yes' class:");
        for (String patternYes : patternsYes) {
            if (!patternsNo.contains(patternYes)) {
                System.out.println("Unique to 'Yes': " + patternYes);
            }
        }

        System.out.println("\nUnique patterns for 'No' class:");
        for (String patternNo : patternsNo) {
            if (!patternsYes.contains(patternNo)) {
                System.out.println("Unique to 'No': " + patternNo);
            }
        }
    }
}
