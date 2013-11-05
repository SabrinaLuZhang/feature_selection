import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class ArffProcessor {

	BufferedWriter writer = null;

	public int[] processAttributeArff(Instances ins, int attributeNo) {
		// 1.把数据集切开变为对应属性及所属类别，只有这两个特征的数据集
		File tempDataFile = new File(MainFrame.TEMPDATAFILENAME_ARFF);

		Instances tempData = null;
		ArffLoader loader = new ArffLoader();

		int[] classValues = { 0, 1 };

		try {
			writer = new BufferedWriter(new FileWriter(tempDataFile));
			writeHeader(writer, ins);

			for (int i = 0; i < ins.numInstances(); i++) {
				Instance in = ins.instance(i);

				writer.write(in.value(attributeNo) + ","
						+ classValues[(int) in.classValue()]);
				writer.newLine();
			}
			writer.flush();

			tempData = gainInstances(MainFrame.TEMPDATAFILENAME_ARFF);

			int[] r = new int[tempData.numInstances()]; // 结果向量 0：分类正确 1：分类错误

			r = this.leaveOneOut(tempData);

			return r;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e2) {
				}
			}
		}
		return null;

	}

	private int[] leaveOneOut(Instances tempData) {

		Instance[] trainIns = new Instance[tempData.numInstances() - 1];
		Instance testIn = null;

		Instances trainData = null;

		//Classifier classifier = null;

		
		int r[] = new int[tempData.numInstances()]; // 结果向量

		for (int i = 0; i < tempData.numInstances(); i++) {
			
			int trainsetCount = 0;
			// 第i个instance作为testIn
			for (int j = 0; j < tempData.numInstances(); j++) {
				Instance in = tempData.instance(j);
				if (j == i) {
					testIn = in;
					continue;
				}
				
				trainIns[trainsetCount] = in;
				trainsetCount++;
			}

			try {

				writer = new BufferedWriter(new FileWriter(MainFrame.TRAINDATAFILENAME_ARFF));
				writeHeader(writer, tempData);

				for (int k = 0; k < trainIns.length; k++) {
					writer.write(trainIns[k].toString() + "\r\n");
				}
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}

			trainData = this.gainInstances(MainFrame.TRAINDATAFILENAME_ARFF);

			try {
				
				weka.classifiers.functions.SMO classifier = new  weka.classifiers.functions.SMO();  
						  
						  
				classifier.setOptions(weka.core.Utils.splitOptions(" -K \" weka.classifiers.functions.supportVector.Puk \"")); 
				// 初始化分类器
				//classifier = (Classifier) Class.forName(
				//		"weka.classifiers.functions.supportVector.SMO")
				//		.newInstance();
				
				

				// 使用训练样本训练分类器
				classifier.buildClassifier(trainData);

				// 使用测试样本测试分类器的学习效果
				Evaluation eval = new Evaluation(trainData);
				int result;
				int inFactResult;

				result = (int) eval.evaluateModelOnce(classifier, testIn);
				inFactResult = (int) testIn.classValue();
				if (result == inFactResult) {
					r[i] = 0;
				} else {
					r[i] = 1;
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
//		for(int i = 0; i < r.length; i++) {
//			System.out.print(r[i] + "  ");
//		}
//		System.out.println();
		return r; // 0:分类正确，1:分类错误

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

	public void writeHeader(BufferedWriter writer, Instances ins) {
		try {
			// String[] classValues = new String[2];
			writer.write("@relation filelist.weka.allclass.csv");
			writer.newLine();
			writer.write("@attribute 1 numeric");
			writer.newLine();
			// classValues[0] = ins.attribute("classes").value(0);
			// classValues[1] = ins.attribute("classes").value(1);
			// writer.write("@attribute classes {" + classValues[0] + ","
			// + classValues[1] + "}");
			writer.write("@attribute classes {" + "0" + "," + "1" + "}");
			writer.newLine();
			writer.write("@data");
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
