import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import weka.core.Instances;

public class IODealerReadMatrixFromFile {
	ArrayList datasetNames;
	ArrayList files;

	private static final String DATA_ROUTE = "D://workspace2//Copy of featureSelection//data//";
	private static final String LOG_ROUTE = "D://workspace2//Copy of featureSelection//log//";
	private static final String MATRIX_ROUTE = "D://workspace2//Copy of featureSelection//matrix//";
	private static final String ARFF_SUFFIX = ".arff";
	private static final String TXT_SUFFIX = ".txt";

	public IODealerReadMatrixFromFile() {
		this.datasetNames = new ArrayList();
		datasetNames.add("CNS");
		datasetNames.add("Colon");
		datasetNames.add("DLBCL");
		datasetNames.add("GCM");
		datasetNames.add("Leukemia");
		datasetNames.add("Lung");
		datasetNames.add("Prostate1");
		datasetNames.add("Prostate2");
		datasetNames.add("Prostate3");

		this.files = new ArrayList();
		for (int i = 0; i < datasetNames.size(); i++) {
			files.add(DATA_ROUTE + datasetNames.get(i) + ARFF_SUFFIX);
		}
	}

	public void io() {
		BufferedWriter logWriter = null;
		BufferedReader matrixReader = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			logWriter = new BufferedWriter(new FileWriter(LOG_ROUTE
					+ df2.format(new Date()) + "logFeatureSelection"
					+ TXT_SUFFIX));

			logWriter.write(df.format(new Date()));// new Date()Ϊ��ȡ��ǰϵͳʱ��
			logWriter.flush();

			logWriter.write("starting...");
			logWriter.flush();

			for (int j = 0; j < 5; j++) {
				for (int i = 0; i < files.size(); i++) {

					String trainFileName = DATA_ROUTE + datasetNames.get(i)
							+ "TrainData" + j + ARFF_SUFFIX;
					String testFileName = DATA_ROUTE + datasetNames.get(i)
							+ "TestData" + j + ARFF_SUFFIX;

					matrixReader = new BufferedReader(new FileReader(
							MATRIX_ROUTE + datasetNames.get(i) + "Matrix" + j
									+ TXT_SUFFIX));

					this.process(j, logWriter, matrixReader, trainFileName,
							testFileName);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (logWriter != null) {
				try {
					logWriter.close();
				} catch (IOException e) {
				}
			}
			if (matrixReader != null) {
				try {
					matrixReader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void process(int no, BufferedWriter logWriter,
			BufferedReader matrixReader, String trainFile, String testFile) {

		Instances ins = null;

		ArffProcessor arffProcessor = new ArffProcessor();
		ins = arffProcessor.gainInstances(trainFile);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ

		try {

			logWriter.newLine();
			System.out.println(trainFile);
			logWriter.write(trainFile);
			logWriter.newLine();
			logWriter.write(testFile);
			logWriter.newLine();
			logWriter.flush();

			logWriter.write("Read matrix started...");
			logWriter.newLine();

			System.out.println("numAttributes:" + (ins.numAttributes() - 1));
			logWriter.write("numAttributes:" + (ins.numAttributes() - 1));
			logWriter.newLine();

			// 1. matrix��ʼ��
			int[][] matrix = new int[(ins.numAttributes() - 1)][ins
					.numInstances()];
			for (int i = 0; i < (ins.numAttributes() - 1); i++) {
				for (int j = 0; j < ins.numInstances(); j++) {
					matrix[i][j] = -1;
				}
			}

			// // 2. ����arffProcessor.processAttributeArff��������matrix
			// for (int i = 0; i < (ins.numAttributes() - 1); i++) {
			// matrix[i] = arffProcessor.processAttributeArff(ins, i);
			// logWriter.write(no + "  " + df.format(new Date()) + "  "
			// + trainFile + "Get matrix " + i);
			// logWriter.newLine();
			// System.out.println(no
			// + "  "
			// + (df.format(new Date()) + "  " + trainFile + "  "
			// + "Get matrix " + i));
			// for (int j = 0; j < matrix[i].length; j++) {
			// matrixWriter.write(matrix[i][j] + "  ");
			// System.out.print(matrix[i][j] + "  ");
			// }
			// matrixWriter.newLine();
			// logWriter.flush();
			// matrixWriter.flush();
			// System.out.println();
			// }
			// logWriter.write("Get matrix finished.");
			// logWriter.newLine();
			// logWriter.flush();

			// 2. ��matrix�ļ����е��ļ���ȡmatrix�����ά����matrix��
			String line = matrixReader.readLine();
			int k = 0;
			while (line != null) {
				String[] strArray = line.split("  ");
				int[] intArray = new int[strArray.length];
				for (int j = 0; j < strArray.length; j++) {
					intArray[j] = Integer.valueOf(strArray[j]).intValue();
				}
				for (int j = 0; j < intArray.length; j++) {
					matrix[k][j] = intArray[j];
				}

				logWriter.write(no + "  " + df.format(new Date()) + "  "
						+ trainFile + "Get matrix " + k);
				logWriter.newLine();
				System.out.println(no
						+ "  "
						+ (df.format(new Date()) + "  " + trainFile + "  "
								+ "Get matrix " + k));
				for (int j = 0; j < matrix[k].length; j++) {
					logWriter.write(matrix[k][j] + "  ");
					System.out.print(matrix[k][j] + "  ");
				}
				logWriter.newLine();
				logWriter.flush();
				System.out.println();

				line = matrixReader.readLine();
				k++;

			}
			logWriter.write("Get matrix finished.");
			logWriter.newLine();
			logWriter.flush();

			
			// 3. �����õ������Լ�����features������
			// ArrayList features = FeatureSelector.selectFeature(matrix);
			FeatureSelectorByFloatSearch selectEngine = new FeatureSelectorByFloatSearch(
					matrix);

			ArrayList features = selectEngine.floatSearch();
			System.out.println("We need these features:");
			logWriter.write("We need these features:");
			logWriter.newLine();
			for (int i = 0; i < features.size(); i++) {
				System.out.println(features.get(i));
				logWriter.write(features.get(i) + "  ");
			}
			logWriter.write("Feature selection successed.");
			logWriter.newLine();

			// 4.��ѡ�õ������Ӽ��γ�ѵ�����Ͳ��Լ�����testProcessor.testFeatureSelection��������������õ�����׼ȷ��
			logWriter.write("Test the efficiency of our features...");
			logWriter.newLine();

			Instances trainIns = null;
			Instances testIns = null;

			TestProcessor testProcessor = new TestProcessor();
			trainIns = testProcessor.gainInstances(trainFile);
			testIns = testProcessor.gainInstances(testFile);

			double accuracy = testProcessor.testFeatureSelection(trainIns,
					testIns, features);
			System.out.println("The accuracy  of SMO with feature selection: "
					+ accuracy);
			logWriter.write("The accuracy  of SMO with feature selection: "
					+ accuracy);
			logWriter.newLine();

			// 5.
			// ��ͬ����ѵ���Ͳ��Լ�compareProcessor.testWithoutFeatureSelection�õ�δ������ѡ��ķ����㷨��׼ȷ��
			CompareProcessor compareProcessor = new CompareProcessor();
			double comparedAccuracy = compareProcessor
					.testWithoutFeatureSelection(trainIns, testIns);
			System.out
					.println("The accuracy of SMO without dealing with features: "
							+ comparedAccuracy);
			logWriter
					.write("The accuracy of SMO without dealing with features: "
							+ comparedAccuracy);
			logWriter.newLine();
			logWriter.newLine();

			logWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
