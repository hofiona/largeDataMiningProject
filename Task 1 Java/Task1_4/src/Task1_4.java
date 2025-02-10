import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTopKClassRules;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import ca.pfv.spmf.tools.resultConverter.ResultConverter;

public class Task1_4 {

    public static void main(String[] args) {
        // Define input dataset and output path
        String input_dataset = "C:/Users/Fiona/IFN645/Project/bank1_1.spmf";
        String output_path = "C:/Users/Fiona/IFN645/Project/output/";

        // Create output directory if it doesn't exist
        File outputDir = new File(output_path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Specify minimum confidence and top-k value
        double minConfidentYes = 0.3;  
        double minConfidentNo = 0.4; 
        
        int top_k = 10;        // Number of top rules to generate

        // Define the item(s) to be used as consequents based on the corrected IDs
        int[] itemToBeUsedAsConsequent_yes = new int[] {42};  // Correct ID for 'subscribed=yes'
        int[] itemToBeUsedAsConsequent_no = new int[] {11};   // Correct ID for 'subscribed=no'

        // Create an object of Task1_4 and call the method
        Task1_4 generateRules = new Task1_4();

        // Generate top-k rules for "subscribed=yes"
        generateRules.topK_classRules(input_dataset, output_path, minConfidentYes, top_k, itemToBeUsedAsConsequent_yes, "yes");

        // Generate top-k rules for "subscribed=no"
        generateRules.topK_classRules(input_dataset, output_path, minConfidentNo, top_k, itemToBeUsedAsConsequent_no, "no");
    }

    /**
     * Method to generate top-k rules with a specific consequent.
     *
     * @param input_dataset  Path to the input dataset
     * @param output_path    Path to the output directory
     * @param minconf        Minimum confidence value
     * @param top_k          Number of top rules to generate
     * @param itemToBeUsedAsConsequent Array specifying the items to be used as consequent
     * @param label          Label ("yes" or "no") for the specific class
     */
    public void topK_classRules(String input_dataset, String output_path, double minconf, int top_k, int[] itemToBeUsedAsConsequent, String label) {
        // Specify the output file paths
        String output = output_path + "Q4_topk_rules_" + label + ".txt";
        String output_with_names = output_path + "Q4_topk_rules_with_names_" + label + ".txt";

        try {
            // Load the transaction database using the correct `Database` class
            Database database = new Database();
            database.loadFile(input_dataset);  // Load the dataset

            // Create an instance of the AlgoTopKClassRules
            AlgoTopKClassRules algo = new AlgoTopKClassRules();

            // Run the algorithm with the specified top-k value and minimum confidence
            algo.runAlgorithm(top_k, minconf, database, itemToBeUsedAsConsequent);

            // Print statistics
            algo.printStats();

            // Save the result to the output file
            algo.writeResultTofile(output);
            System.out.println("Top-k rules saved to: " + output);

            // Convert the results to a readable format using item names
            ResultConverter resultConverter = new ResultConverter();
            resultConverter.convert(input_dataset, output, output_with_names, null);
            System.out.println("Converted output saved to: " + output_with_names);

            // Validate and sort the generated rules
            sortAndDisplayRules(output_with_names, output_path + "sorted_Q4_topk_rules_with_names_" + label + ".txt");

        } catch (IOException e) {
            System.err.println("Error processing dataset: " + input_dataset);
            e.printStackTrace();
        }
    }

    /**
     * Method to read, sort, display, and save the rules based on confidence values.
     * 
     * @param outputFile Path to the file containing the rules
     * @param sortedOutputFile Path to save the sorted rules
     */
    public void sortAndDisplayRules(String outputFile, String sortedOutputFile) {
        List<Rule> rulesList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {
            String line;
            // Read each line and extract rule information
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#SUP:|#CONF:"); // Split by support and confidence delimiters
                if (parts.length == 3) {
                    String rule = parts[0].trim();  // The rule itself
                    int support = Integer.parseInt(parts[1].trim());  // Support value
                    double confidence = Double.parseDouble(parts[2].trim());  // Confidence value

                    // Add the rule to the list
                    rulesList.add(new Rule(rule, support, confidence));
                }
            }

            // Sort the list by confidence in descending order
            rulesList.sort(Comparator.comparingDouble(Rule::getConfidence).reversed());

            // Debugging - Print sorted rules in memory
            System.out.println("Rules after sorting by confidence:");
            for (Rule r : rulesList) {
                System.out.printf("Rule: %s | Support: %d | Confidence: %.3f%n", r.rule, r.support, r.confidence);  // Rounded confidence to 3 decimal places
            }

            // Write the sorted rules to a new file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(sortedOutputFile))) {
                for (Rule r : rulesList) {
                    writer.write(String.format("%s #SUP: %d #CONF: %.3f%n", r.rule, r.support, r.confidence));  // Write with 3 decimal places
                }
                System.out.println("Sorted rules saved to: " + sortedOutputFile);
            }

        } catch (IOException e) {
            System.err.println("Error reading or processing file: " + outputFile);
            e.printStackTrace();
        }
    }

    /**
     * Rule class to store each rule's details.
     */
    class Rule {
        String rule;
        int support;
        double confidence;

        public Rule(String rule, int support, double confidence) {
            this.rule = rule;
            this.support = support;
            this.confidence = confidence;
        }

        public double getConfidence() {
            return confidence;
        }
    }
}
