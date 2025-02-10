import java.util.Random;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Task2_645 {

    // use CostSensitiveClassifier to find COSt, accuracy and Correctly Classified Instances value
    public void doCostSensitiveClassificationWithAttributeSelection(Instances data, AbstractClassifier classifier, boolean minimizeExpectedCost) {
        try {
            // Create the AttributeSelectedClassifier
            AttributeSelectedClassifier asc = new AttributeSelectedClassifier();
            asc.setClassifier(new CostSensitiveClassifier());
            asc.setEvaluator(new InfoGainAttributeEval());
            Ranker ranker = new Ranker();
            ranker.setNumToSelect(9); // Select top 9 attributes
            asc.setSearch(ranker);
            
            // Configure the CostSensitiveClassifier within AttributeSelectedClassifier
            CostSensitiveClassifier csc = (CostSensitiveClassifier) asc.getClassifier();
            csc.setClassifier(classifier);
            csc.setMinimizeExpectedCost(minimizeExpectedCost);
            
            // make the false negative to 5, since the impact is more importance
            String matlab = "[0.0 5.0; 1.0 0.0]";
            CostMatrix costMatrix = CostMatrix.parseMatlab(matlab);
            csc.setCostMatrix(costMatrix);
            
            
            Evaluation eval = new Evaluation(data, costMatrix);
            eval.crossValidateModel(asc, data, 10, new Random(1));
            double accuracy = eval.pctCorrect();
            double correctInstances = eval.correct();
            
            // Output the results
            System.out.println("\nClassifier: " + classifier.getClass().getSimpleName() + ", MinimizeExpectedCost: " + minimizeExpectedCost);
            System.out.println("Correctly Classified Instances: " + (int) correctInstances);
            System.out.println("Accuracy: " + accuracy + "%");
            System.out.println("Total Cost: " + eval.totalCost());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Use meta AttributeSelected to count Correctly Classified Instances, accuracy and performance
    public void doFilteredClassification(Instances data, AbstractClassifier classifier, ASEvaluation evaluator, ASSearch searcher) {
        try {
            AttributeSelectedClassifier asc = new AttributeSelectedClassifier();
            asc.setClassifier(classifier);
            asc.setEvaluator(evaluator);
            asc.setSearch(searcher);
            asc.buildClassifier(data);
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(asc, data, 10, new Random(1));
            double accuracy = eval.pctCorrect();
            double correctInstances = eval.correct();
         // count different performance 
            System.out.println("\nClassifier: " + classifier.getClass().getSimpleName());
            System.out.println("Correctly Classified Instances value: " + (int) correctInstances);
            System.out.println("Accuracy: " + accuracy + "%");// accuracy with %
            System.out.println("Precision: " + eval.precision(1));
            System.out.println("Recall: " + eval.recall(1));
            System.out.println("F-Measure: " + eval.fMeasure(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String args[]) {
        try {
        	//use COVID19 data set in java
            DataSource source = new DataSource("C:/Program Files/Weka-3-8-6/data/COVID19.arff");
            Instances data = source.getDataSet();
            data.setClass(data.attribute("infection_risk"));
            // my class name is Task2_645
            Task2_645 task = new Task2_645();
           
            InfoGainAttributeEval infoGainEval = new InfoGainAttributeEval();

            // Run classification on all attributes
            System.out.println("Java Question 1:");
            System.out.println("Classification with All Attributes:");
            
            Ranker ranker9 = new Ranker();
            ranker9.setNumToSelect(9);
            J48 j48 = new J48();
            task.doFilteredClassification(data, j48, infoGainEval, ranker9);
            
            PART part = new PART();
            task.doFilteredClassification(data, part, infoGainEval, ranker9);

            Ranker ranker3 = new Ranker();
            ranker3.setNumToSelect(3);
            OneR oneR = new OneR();
            task.doFilteredClassification(data, oneR, infoGainEval, ranker3);

            NaiveBayes nb = new NaiveBayes();
            task.doFilteredClassification(data, nb, infoGainEval, ranker3);

            // Run cost-sensitive classification on selected attributes
            System.out.println("\nJava Question 2");
            System.out.println("Cost-Sensitive Classification with Selected Attributes:");
            task.doCostSensitiveClassificationWithAttributeSelection(data, new J48(), false);
            task.doCostSensitiveClassificationWithAttributeSelection(data, new J48(), true);
            task.doCostSensitiveClassificationWithAttributeSelection(data, new PART(), false);
            task.doCostSensitiveClassificationWithAttributeSelection(data, new PART(), true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}