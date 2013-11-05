import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class MainFrame {

	// public static final String TRAINDATAFILENAME_TOTAL =
	// "D://workspace//featureSelection//data//CNS-train.arff";
	// public static final String TESTDATAFILENAME_TOTAL =
	// "D://workspace//featureSelection//data//CNS-test.arff";
	private static final String DATAFILENAME = "D://workspace//featureSelection//data//Colon.arff";

	public static final String TEMPDATAFILENAME_ARFF = "D://workspace//featureSelection//data//SelectFeatureTempData.arff";
	public static final String TRAINDATAFILENAME_ARFF = "D://workspace//featureSelection//data//SelectFeatureTrainData.arff";

	public static final String TEMPTRAINDATAFILENAME_TEST = "D://workspace//featureSelection//data//TrainTempData.arff";
	public static final String TEMPTESTDATAFILENAME_TEST = "D://workspace//featureSelection//data//TestTempData.arff";
	public static final String TRAINDATAFILENAME_MAIN = "D://workspace//featureSelection//data//ColonTrainData.arff";
	public static final String TESTDATAFILENAME_MAIN = "D://workspace//featureSelection//data//ColonTestData.arff";


	
	public static void main(String[] args) {
		// PreProcessor preProcessor = new PreProcessor();
		// preProcessor.cutDataset(DATAFILENAME);
		// Instances trainIns = null;
		// Instances testIns = null;

		// ArrayList features = new ArrayList();
		// features.add(2000);
		// TestProcessor testProcessor = new TestProcessor();
		// trainIns = testProcessor.gainInstances(TRAINDATAFILENAME_TOTAL);
		// testIns = testProcessor.gainInstances(TESTDATAFILENAME_TOTAL);
		// double accuracy = testProcessor.testFeatureSelection(trainIns,
		// testIns, features);
		// System.out.println("The accuracy is " + accuracy);

		// CompareProcessor compareProcessor = new CompareProcessor();
		// double comparedAccuracy =
		// compareProcessor.testWithoutFeatureSelection(trainIns, testIns);
		// System.out.println("comparedAccuracy:" + comparedAccuracy);

//		MainFrame mf = new MainFrame();
//		IODealerToMultiFiles ioDealerToMultiFiles = new IODealerToMultiFiles();
//		ioDealerToMultiFiles.io();
		
//		IODealer ioDealer = new IODealer();
//		ioDealer.io();
		
		IODealerReadMatrixFromFile ioDealer = new IODealerReadMatrixFromFile();
		ioDealer.io();

	}


}
