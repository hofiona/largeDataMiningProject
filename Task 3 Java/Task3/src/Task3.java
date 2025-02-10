import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Task3 {
	
	// Helper function to perform classification using the specified classifier
	public void performClassification(Instances data, String classifierName) {
		try {
			// Set the class index of the dataset 
			data.setClassIndex(1);
			
			// Create a StringToWordVector filter 
			StringToWordVector swFilter = new StringToWordVector();
			swFilter.setAttributeIndices("first-last"); // Apply to all words
			swFilter.setIDFTransform(true);
			swFilter.setTFTransform(true);
			swFilter.setNormalizeDocLength(
		            new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
			swFilter.setOutputWordCounts(true);
			swFilter.setStemmer(new LovinsStemmer());
			swFilter.setStopwordsHandler(new Rainbow());
			swFilter.setTokenizer(new AlphabeticTokenizer());
			swFilter.setWordsToKeep(100);  
			
			// Create a FilteredClassifier object
			FilteredClassifier filter_classifier = new FilteredClassifier();
			filter_classifier.setFilter(swFilter);

			// Select the classifier based on the input parameter
			switch (classifierName) {
				case "IBk":
					filter_classifier.setClassifier(new IBk());
					break;
				case "SMO":
					filter_classifier.setClassifier(new SMO());
					break;
				case "J48":
					filter_classifier.setClassifier(new J48());
					break;
				case "HoeffdingTree":
					filter_classifier.setClassifier(new HoeffdingTree());
					break;
				default:
					System.out.println("Invalid classifier name!");
					return;
			}

			// Build the classifier
			filter_classifier.buildClassifier(data);

			// Evaluation
			Evaluation eval = new Evaluation(data);
			long startTime = System.currentTimeMillis();  // Start time measurement
			eval.crossValidateModel(filter_classifier, data, 10, new Random(1));
			long endTime = System.currentTimeMillis();  // End time measurement

			// Output the results
			System.out.println("Classifier: " + classifierName);
			System.out.println(eval.toSummaryString());
			System.out.println("Correctly classified instances: " + eval.correct());
			System.out.println("Incorrectly classified instances: " + eval.incorrect());
			System.out.println("Time taken: " + (endTime - startTime) + "ms");
			System.out.println("===================================================");
		}
		catch(Exception e) {
			System.out.println("Error occurred with classifier: " + classifierName);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		// Load dataset
		DataSource source = new DataSource("C:/Users/Fiona/IFN645/Project/News.arff");
		Instances data = source.getDataSet();
		
		// Create an instance of the classifier
		Task3 tc = new Task3();
		
		// Perform classification with 4 different classifiers
		tc.performClassification(data, "IBk");
		tc.performClassification(data, "SMO");
		tc.performClassification(data, "J48");
		tc.performClassification(data, "HoeffdingTree");
	}
}
