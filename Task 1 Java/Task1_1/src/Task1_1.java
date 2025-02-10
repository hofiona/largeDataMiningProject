import java.io.IOException;
import java.text.DecimalFormat;

import ca.pfv.spmf.algorithms.frequentpatterns.apriori.AlgoApriori;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.tools.dataset_converter.TransactionDatabaseConverter;

public class Task1_1 {
    public static void main(String[] args) {
        try {
            // Conversion of ARFF file to SMPF
            TransactionDatabaseConverter converter = new TransactionDatabaseConverter();
            String inputARFF = "C:/Users/Fiona/IFN645/Project/bank.arff";
            String inputfile = "C:/Users/Fiona/IFN645/Project/bank1_1.spmf";
            converter.convertARFFandReturnMap(inputARFF, inputfile, Integer.MAX_VALUE);

            String input_dataset = inputfile;

            // Specify minimum support
            double minsup = 0.1;

            DecimalFormat df = new DecimalFormat("#.##");

            while (minsup < 0.9) {
            
            // Use the formatted minimum support value in the print statement
            System.out.printf("The min support is " + df.format(minsup) + "\n");

            // Specify where to save the output files (use formatted minsup value dynamically)
            String output_fp_Apriori = "C:/Users/Fiona/IFN645/Project/output/Q1_FP_Apriori_" + df.format(minsup) + ".txt";
            String output_fp_Fpt = "C:/Users/Fiona/IFN645/Project/output/Q1_FP_Fpt_" + df.format(minsup) + ".txt";

            // Create objects of pattern mining algorithms
            AlgoApriori algo_Apri = new AlgoApriori();
            AlgoFPGrowth algo_FPGrowth = new AlgoFPGrowth();
            // Set a maximum size for patterns
            //algo_Apri.setMaximumPatternLength(3);
            //algo_FPGrowth.setMaximumPatternLength(3);
            // Set a minimum size for patterns
            algo_FPGrowth.setMinimumPatternLength(1);
            
            // Run the Apriori algorithm
            algo_Apri.runAlgorithm(minsup, input_dataset, output_fp_Apriori);
            algo_Apri.printStats();

            // Run the FPGrowth algorithm
            algo_FPGrowth.runAlgorithm(input_dataset, output_fp_Fpt, minsup);
            algo_FPGrowth.printStats();
            // Increment minimum support
            minsup += 0.1; // Increase by 0.1 for next iteration
        }

        } catch (IOException e) {
            // Print the stack trace to see details of the exception
            e.printStackTrace();
        }
    }
}
