import java.io.*;
import java.util.*;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPMax;
import ca.pfv.spmf.tools.resultConverter.ResultConverter;

public class Task1_3 {

    public static void main(String[] args) {
        // Specify input datasets and output path
        String input_dataset_yes = "C:/Users/Fiona/IFN645/Project/bank_yes.spmf";
        String input_dataset_no = "C:/Users/Fiona/IFN645/Project/bank_no.spmf";
        String output_path = "C:/Users/Fiona/IFN645/Project/output/";

        // Specify minimum support
        double minSupportYes = 0.5;  
        double minSupportNo = 0.53;  

        // Create an instance of this class
        Task1_3 pattern_mining = new Task1_3();

        // Create output directory if it doesn't exist
        File outputDir = new File(output_path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Generate patterns for the "yes" class
        pattern_mining.runAllAlgorithms(input_dataset_yes, output_path, minSupportYes, "yes");

        // Generate patterns for the "no" class
        pattern_mining.runAllAlgorithms(input_dataset_no, output_path, minSupportNo, "no");
    }

    // Method to run all algorithms for a specific dataset
    public void runAllAlgorithms(String input_dataset, String output_path, double minsup, String label) {
        doMaxFp(input_dataset, output_path, minsup, label);
    }

    // Method to generate maximal itemsets using FPMax algorithm
    public void doMaxFp(String input_dataset, String output_path, double minsup, String label) {
        String output = output_path + "Q3_fpMax_" + label + ".txt";
        String final_output = output_path + "Q3_final_fpMax_" + label + ".txt";

        try {
            AlgoFPMax algo_FpMax = new AlgoFPMax();
            algo_FpMax.runAlgorithm(input_dataset, output, minsup);
            algo_FpMax.printStats();

            // Convert and extract top 5 patterns
            convert_output(input_dataset, output, final_output);
            extractAndPrintTopPatterns(final_output, 5); // Print top 5 patterns
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to convert output files to include item names
    public void convert_output(String input_dataset, String output, String final_output) {
        try {
            ResultConverter output_converter = new ResultConverter();
            output_converter.convert(input_dataset, output, final_output, null);
            System.out.println("Conversion completed: " + final_output);
        } catch (IOException e) {
            System.err.println("Conversion failed for: " + output);
            e.printStackTrace();
        }
    }

    // Method to read patterns, sort by support, and print the top N patterns
    public void extractAndPrintTopPatterns(String final_output, int topN) {
        try (BufferedReader br = new BufferedReader(new FileReader(final_output))) {
            List<Pattern> patterns = new ArrayList<>();
            String line;

            // Read and parse each line to create Pattern objects
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" #SUP: ");
                if (parts.length == 2) {
                    String patternStr = parts[0].trim();
                    int support = Integer.parseInt(parts[1].trim());
                    patterns.add(new Pattern(patternStr, support));
                }
            }

            // Sort patterns by support in descending order
            patterns.sort(Comparator.comparingInt(Pattern::getSupport).reversed());

            // Print the top N patterns
            System.out.println("Top " + topN + " patterns:");
            for (int i = 0; i < Math.min(topN, patterns.size()); i++) {
                System.out.println("Pattern: " + patterns.get(i).patternStr + " | Support: " + patterns.get(i).support);
            }
        } catch (IOException e) {
            System.err.println("Error reading or processing file: " + final_output);
            e.printStackTrace();
        }
    }

    // Class to store pattern information
    class Pattern {
        String patternStr;
        int support;

        public Pattern(String patternStr, int support) {
            this.patternStr = patternStr;
            this.support = support;
        }

        public int getSupport() {
            return support;
        }
    }
}
