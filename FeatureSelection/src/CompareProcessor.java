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
			// ��ʼ��������
			classifier = (Classifier) Class.forName(
					"weka.classifiers.functions.supportVector.SMO")
					.newInstance();

			// ʹ��ѵ������ѵ��������
			classifier.buildClassifier(trainIns);

			// ʹ�ò����������Է�������ѧϰЧ��
			Evaluation eval = new Evaluation(testIns);
			int result;
			int inFactResult;
			int[] trueOrFalse = new int[2]; // 0:��ȷ����Ŀ��1���������Ŀ
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
