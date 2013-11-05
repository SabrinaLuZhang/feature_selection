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


public class TestProcessor {
	
	
	BufferedWriter writer1 = null;
	BufferedWriter writer2 = null;
	
	BufferedWriter writer = null;
	
	public double testFeatureSelection(Instances trainIns, Instances testIns, ArrayList attributeNos) {
		
		double accuracy = 0.0;
		Collections.sort(attributeNos);
		
		File tempTrainDataFile = new File(MainFrame.TEMPTRAINDATAFILENAME_TEST);
		File tempTestDataFile = new File(MainFrame.TEMPTESTDATAFILENAME_TEST);
		
		
		Instances tempData = null;
		ArffLoader loader = new ArffLoader();
		Classifier classifier = null;
		
		String classValues[] = new String[2];
		classValues[0] = trainIns.attribute("classes").value(0);
		classValues[1] = trainIns.attribute("classes").value(1);
		
		try {
			writer1 = new BufferedWriter(new FileWriter(tempTrainDataFile));
			writeHeader(writer1, trainIns, attributeNos);

			for (int i = 0; i < trainIns.numInstances(); i++) {
				Instance in = trainIns.instance(i);
				for(int j = 0; j < attributeNos.size(); j++) {
					writer1.write(in.value((Integer)attributeNos.get(j)) + ",");
				}
				writer1.write(classValues[(int) in.classValue()]);
				writer1.newLine();
			}
			writer1.flush();
			
			writer2 = new BufferedWriter(new FileWriter(tempTestDataFile));
			writeHeader(writer2, testIns, attributeNos);

			for (int i = 0; i < testIns.numInstances(); i++) {
				Instance in = testIns.instance(i);
				for(int j = 0; j < attributeNos.size(); j++) {
					writer2.write(in.value((Integer)attributeNos.get(j)) + ",");
				}
				writer2.write(classValues[(int) in.classValue()]);
				writer2.newLine();
			}
			writer2.flush();


			Instances trainData = gainInstances(MainFrame.TEMPTRAINDATAFILENAME_TEST);
			Instances testData = gainInstances(MainFrame.TEMPTESTDATAFILENAME_TEST);

			try {
				// 初始化分类器
				classifier = (Classifier) Class.forName(
						"weka.classifiers.functions.supportVector.SMO").newInstance();

				// 使用训练样本训练分类器
				classifier.buildClassifier(trainData);

				// 使用测试样本测试分类器的学习效果
				Evaluation eval = new Evaluation(trainData);
				int result;
				int inFactResult;
				int[] trueOrFalse = new int[2]; // 0:正确的数目，1：错误的数目
				for (int i = 0; i < testData.numInstances(); i++) {
					Instance in = testData.instance(i);
					result = (int) eval.evaluateModelOnce(classifier, in);
					inFactResult = (int) in.classValue();
					if (result == inFactResult) {
						trueOrFalse[0]++;
					} else {
						trueOrFalse[1]++;
					}
				}
				
				accuracy =  (double)trueOrFalse[0] / (double)(trueOrFalse[0] + trueOrFalse[1]);

				return accuracy;

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer1 != null) {
				try {
					writer1.close();
				} catch (IOException e) {
				}
			}
			if (writer2 != null) {
				try {
					writer2.close();
				} catch (IOException e) {
				}
			}
		}
		return accuracy;
	}
	
	public static Instances gainInstances(String fileName) {

		File file = new File(fileName);
		ArffLoader loader = new ArffLoader();
		Instances ins = null;
		try {
			loader.setFile(file);
			ins = loader.getDataSet();
			int numAttr = ins.numAttributes();
			ins.setClassIndex(numAttr - 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ins;
	}
	
	public void writeHeader(BufferedWriter writer, Instances ins, ArrayList attributeNos) {
		try {
			String[] classValues = new String[2];
			classValues[0] = ins.attribute("classes").value(0);
			classValues[1] = ins.attribute("classes").value(1);
			writer.write("@relation filelist.weka.allclass.csv");
			writer.newLine();
			for(int i = 0; i < attributeNos.size(); i++) {
				writer.write("@attribute " + attributeNos.get(i) + " numeric");
				writer.newLine();
			}
			writer.write("@attribute classes {" + classValues[0] + ","
					+ classValues[1] + "}");
			writer.newLine();
			writer.write("@data");
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
