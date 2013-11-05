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

public class PreProcessor {
	public void cutDataset(String fileName, String trainFileName,
			String testFileName) {
		Instances ins = this.gainInstances(fileName);
		int numAttributes = ins.numAttributes() - 1;
		double percent = 0.6;

		String[] classValues = new String[2];
		classValues[0] = ins.attribute("classes").value(0);
		classValues[1] = ins.attribute("classes").value(1);

		ArrayList classZero = new ArrayList();
		ArrayList classOne = new ArrayList();

		for (int i = 0; i < ins.numInstances(); i++) {
			Instance in = ins.instance(i);
			if ((int) in.classValue() == 1) {
				classOne.add(in);
			} else {
				classZero.add(in);
			}
		}

		int zeroTrainTotal = (int) (classZero.size() * percent);
		int oneTrainTotal = (int) (classOne.size() * percent);

		// System.out.println("classZero" + classZero.size());
		// System.out.println("classOne" + classOne.size());
		// System.out.println("zeroTrainTotal:" + zeroTrainTotal);

		ArrayList zeroTrain = new ArrayList();
		ArrayList zeroTest = new ArrayList();
		ArrayList oneTrain = new ArrayList();
		ArrayList oneTest = new ArrayList();

		// 填入zeroTrain
		for (int i = 0; i < zeroTrainTotal; i++) {
			int randomNumber = (int) Math.round(Math.random()
					* ((classZero.size() - 1) - 0) + 0);
			zeroTrain.add(classZero.get(randomNumber));
			classZero.remove(randomNumber);
		}

		// 填入zeroTest
		zeroTest.addAll(classZero);

		// 填入oneTrain
		for (int i = 0; i < oneTrainTotal; i++) {
			int randomNumber = (int) Math.round(Math.random()
					* ((classOne.size() - 1) - 0) + 0);
			oneTrain.add(classOne.get(randomNumber));
			classOne.remove(randomNumber);
		}
		// 填入oneTest
		oneTest.addAll(classOne);

		// System.out.println("zeroTrain:" + zeroTrain.size());
		// System.out.println("oneTrain:" + oneTrain.size());
		// System.out.println("zeroTest:" + zeroTest.size());
		// System.out.println("oneTest" + oneTest.size());

		ArrayList trainData = new ArrayList();
		ArrayList testData = new ArrayList();

		// //随机合并zeroTrain和oneTrain
		// while(zeroTrain.size() != 0 || oneTrain.size() != 0) {
		// int rd = Math.random() > 0.5 ? 1 : 0;
		// //System.out.println("rd:" + rd);
		// if(rd == 1) {
		// if(oneTrain.size() > 0) {
		// int randomNumber = (int) Math.round(Math.random()*((oneTrain.size() -
		// 1) - 0) + 0);
		// trainData.add(oneTrain.get(randomNumber));
		// oneTrain.remove(randomNumber);
		// } else {
		// continue;
		// }
		// } else {
		// if(zeroTrain.size() > 0) {
		// int randomNumber = (int) Math.round(Math.random()*((zeroTrain.size()
		// - 1) - 0) + 0);
		// trainData.add(zeroTrain.get(randomNumber));
		// zeroTrain.remove(randomNumber);
		// } else {
		// continue;
		// }
		// }
		//
		// }

		// 随机合并zeroTrain和oneTrain
		int all = zeroTrain.size() + oneTrain.size();
		for (int i = 0; i < all; i++) {
			int rd = Math.random() > 0.5 ? 1 : 0;
//			System.out.println("rd:" + rd);
			if (rd == 1) {
				if (oneTrain.size() > 0) {
					int randomNumber = (int) Math.round(Math.random()
							* ((oneTrain.size() - 1) - 0) + 0);
					trainData.add(oneTrain.get(randomNumber));
					oneTrain.remove(randomNumber);
				} else {
					trainData.addAll(zeroTrain);
					break;
				}
			} else {
				if (zeroTrain.size() > 0) {
					int randomNumber = (int) Math.round(Math.random()
							* ((zeroTrain.size() - 1) - 0) + 0);
					trainData.add(zeroTrain.get(randomNumber));
					zeroTrain.remove(randomNumber);
				} else {
					trainData.addAll(oneTrain);
					break;
				}
			}
		}
		// 随机合并zeroTest和oneTest
		all = zeroTest.size() + oneTest.size();
		for (int i = 0; i < all; i++) {
			int rd = Math.random() > 0.5 ? 1 : 0;
			if (rd == 1) {
				if (oneTest.size() > 0) {
					int randomNumber = (int) Math.round(Math.random()
							* ((oneTest.size() - 1) - 0) + 0);
					testData.add(oneTest.get(randomNumber));
					oneTest.remove(randomNumber);
				} else {
					testData.addAll(zeroTest);
					break;
				}
			} else {
				if (zeroTest.size() > 0) {
					int randomNumber = (int) Math.round(Math.random()
							* ((zeroTest.size() - 1) - 0) + 0);
					testData.add(zeroTest.get(randomNumber));
					zeroTest.remove(randomNumber);
				} else {
					testData.addAll(oneTest);
					break;
				}
			}
		}

		System.out.println("trainData:" + trainData.size());
		System.out.println("testData:" + testData.size());

		BufferedWriter writer = null;
		File trainDataFile = new File(trainFileName);
		File testDataFile = new File(testFileName);

		try {
			writer = new BufferedWriter(new FileWriter(trainDataFile));
			writeHeader(writer, trainData, classValues, numAttributes);

			for (int i = 0; i < trainData.size(); i++) {
				Instance in = (Instance) trainData.get(i);
				for (int j = 0; j < (in.numAttributes() - 1); j++) {
					writer.write(in.value(j) + ",");
				}
				writer.write(classValues[(int) in.classValue()]);
				writer.newLine();
			}
			writer.flush();

			writer = new BufferedWriter(new FileWriter(testDataFile));
			writeHeader(writer, testData, classValues, numAttributes);

			for (int i = 0; i < testData.size(); i++) {
				Instance in = (Instance) testData.get(i);
				for (int j = 0; j < (in.numAttributes() - 1); j++) {
					writer.write(in.value(j) + ",");
				}
				writer.write(classValues[(int) in.classValue()]);
				writer.newLine();
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

	public void writeHeader(BufferedWriter writer, ArrayList data,
			String[] classValues, int numAttributes) {
		try {
			writer.write("@relation filelist.weka.allclass.csv");
			writer.newLine();
			for (int i = 0; i < numAttributes; i++) {
				writer.write("@attribute " + i + " numeric");
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
