import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import weka.core.Instances;


public class IODealerToMultiFiles {
	ArrayList datasetNames;
	ArrayList files;

	private static final String DATA_ROUTE = "D://workspace//featureSelection//data//";
	private static final String LOG_ROUTE = "D://workspace//featureSelection//log//";
	private static final String MATRIX_ROUTE = "D://workspace//featureSelection//matrix//";
	private static final String ARFF_SUFFIX = ".arff";
	private static final String TXT_SUFFIX = ".txt";

	public IODealerToMultiFiles() {
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
		BufferedWriter matrixWriter = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy_MM_dd");
			logWriter = new BufferedWriter(new FileWriter(
					LOG_ROUTE + df2.format(new Date()) + "logWithoutMatrix" + TXT_SUFFIX));
			
			logWriter.write(df.format(new Date()));// new Date()为获取当前系统时间
			logWriter.flush();

			logWriter.write("starting...");
			logWriter.flush();

			// PreProcessor preProcessor = new PreProcessor();
			for (int j = 0; j < 5; j++) {
				for (int i = 0; i < files.size(); i++) {
					String trainFileName = DATA_ROUTE + datasetNames.get(i)
							+ "TrainData" + j + ARFF_SUFFIX;
					String testFileName = DATA_ROUTE + datasetNames.get(i)
							+ "TestData" + j + ARFF_SUFFIX;
					// preProcessor.cutDataset((String) files.get(i),
					// trainFileName,
					// testFileName);
					matrixWriter = new BufferedWriter(new FileWriter(MATRIX_ROUTE + datasetNames.get(i) + "Matrix" + j + TXT_SUFFIX));
					
					this.process(j, logWriter, matrixWriter, trainFileName, testFileName);
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
			if (matrixWriter != null) {
				try {
					matrixWriter.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void process(int no, BufferedWriter logWriter, BufferedWriter matrixWriter, String trainFile,
			String testFile) {

		Instances ins = null;

		ArffProcessor arffProcessor = new ArffProcessor();
		ins = arffProcessor.gainInstances(trainFile);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

		try {

			logWriter.newLine();
			System.out.println(trainFile);
			logWriter.write(trainFile);
			logWriter.newLine();
			logWriter.write(testFile);
			logWriter.newLine();
			logWriter.flush();

			logWriter.write("Get matrix started...");
			logWriter.newLine();

			System.out.println("numAttributes:" + (ins.numAttributes() - 1));
			logWriter.write("numAttributes:" + (ins.numAttributes() - 1));
			logWriter.newLine();
			
			//1. matrix初始化
			int[][] matrix = new int[(ins.numAttributes() - 1)][ins
					.numInstances()];
			for (int i = 0; i < (ins.numAttributes() - 1); i++) {
				for (int j = 0; j < ins.numInstances(); j++) {
					matrix[i][j] = -1;
				}
			}
			
			//2. 调用arffProcessor.processAttributeArff方法生成matrix
			for (int i = 0; i < (ins.numAttributes() - 1); i++) {
				matrix[i] = arffProcessor.processAttributeArff(ins, i);
				logWriter.write(no + "  " + df.format(new Date()) + "  "
						+ trainFile + "Get matrix " + i);
				logWriter.newLine();
				System.out.println(no
						+ "  "
						+ (df.format(new Date()) + "  " + trainFile + "  "
								+ "Get matrix " + i));
				for (int j = 0; j < matrix[i].length; j++) {
					matrixWriter.write(matrix[i][j] + "  ");
					System.out.print(matrix[i][j] + "  ");
				}
				matrixWriter.newLine();
				logWriter.flush();
				matrixWriter.flush();
				System.out.println();
			}
			logWriter.write("Get matrix finished.");
			logWriter.newLine();
			logWriter.flush();

//			//3. 搜索得到特征自己存入features集合中
//			// ArrayList features = FeatureSelector.selectFeature(matrix);
//			FeatureSelectorByFloatSearch selectEngine = new FeatureSelectorByFloatSearch(
//					matrix);
//
//			ArrayList features = selectEngine.floatSearch();
//			System.out.println("We need these features:");
//			logWriter.write("We need these features:");
//			logWriter.newLine();
//			for (int i = 0; i < features.size(); i++) {
//				System.out.println(features.get(i));
//				logWriter.write(features.get(i) + "  ");
//			}
//			logWriter.write("Feature selection successed.");
//			logWriter.newLine();
//
//			
//			//4. 用选好的特征子集形成训练集和测试集调用testProcessor.testFeatureSelection，构造分类器并得到分类准确率
//			logWriter.write("Test the efficiency of our features...");
//			logWriter.newLine();
//
//			Instances trainIns = null;
//			Instances testIns = null;
//
//			TestProcessor testProcessor = new TestProcessor();
//			trainIns = testProcessor.gainInstances(trainFile);
//			testIns = testProcessor.gainInstances(testFile);
//
//			double accuracy = testProcessor.testFeatureSelection(trainIns,
//					testIns, features);
//			System.out.println("The accuracy  of SMO with feature selection: "
//					+ accuracy);
//			logWriter.write("The accuracy  of SMO with feature selection: "
//					+ accuracy);
//			logWriter.newLine();
//
//			//5. 用同样的训练和测试集 compareProcessor.testWithoutFeatureSelection得到未作特征选择的分类算法的准确率
//			CompareProcessor compareProcessor = new CompareProcessor();
//			double comparedAccuracy = compareProcessor
//					.testWithoutFeatureSelection(trainIns, testIns);
//			System.out
//					.println("The accuracy of SMO without dealing with features: "
//							+ comparedAccuracy);
//			logWriter.write("The accuracy of SMO without dealing with features: "
//					+ comparedAccuracy);
//			logWriter.newLine();
//			logWriter.newLine();
//
//			logWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
