import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class CompareProcessor {

	public double testWithoutFeatureSelection(Instances trainIns,
			Instances testIns) {

		File tempTrainDataFile = new File(MainFrame.TEMPTRAINDATAFILENAME_TEST);
		File tempTestDataFile = new File(MainFrame.TEMPTESTDATAFILENAME_TEST);

		Instances tempData = null;
		ArffLoader loader = new ArffLoader();
		Classifier classifier = null;
		double accuracy = 0.0;

		try {
			// 初始化分类器
			classifier = (Classifier) Class.forName(
					"weka.classifiers.functions.supportVector.SMO")
					.newInstance();

			// 使用训练样本训练分类器
			classifier.buildClassifier(trainIns);

			// 使用测试样本测试分类器的学习效果
			Evaluation eval = new Evaluation(testIns);
			int result;
			int inFactResult;
			int[] trueOrFalse = new int[2]; // 0:正确的数目，1：错误的数目
			for (int i = 0; i < testIns.numInstances(); i++) {
				Instance in = testIns.instance(i);
				result = (int) eval.evaluateModelOnce(classifier, in);
				inFactResult = (int) in.classValue();
				if (result == inFactResult) {
					trueOrFalse[0]++;
				} else {
					trueOrFalse[1]++;
				}
			}

			accuracy = (double) trueOrFalse[0]
					/ (double) (trueOrFalse[0] + trueOrFalse[1]);

			

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accuracy;
	}

}
