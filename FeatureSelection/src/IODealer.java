import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import weka.core.Instances;

public class IODealer {
	ArrayList datasetNames;
	ArrayList files;

	private static final String DATA_ROUTE = "D://workspace//featureSelection//data//";
	private static final String LOG_ROUTE = "D://workspace//featureSelection//log//";
	private static final String ARFF_SUFFIX = ".arff";
	private static final String TXT_SUFFIX = ".txt";

	public IODealer() {
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
		BufferedWriter writer = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy_MM_dd");
			writer = new BufferedWriter(new FileWriter(
					LOG_ROUTE + df2.format(new Date()) + "log" + TXT_SUFFIX));
			// 设置日期格式
			writer.write(df.format(new Date()));// new Date()为获取当前系统时间
			writer.flush();

			writer.write("starting...");
			writer.flush();

			 PreProcessor preProcessor = new PreProcessor();
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < files.size(); i++) {
					String trainFileName = DATA_ROUTE + datasetNames.get(i)
							+ "TrainData" + j + ARFF_SUFFIX;
					String testFileName = DATA_ROUTE + datasetNames.get(i)
							+ "TestData" + j + ARFF_SUFFIX;
					 preProcessor.cutDataset((String) files.get(i),
					 trainFileName,
					 testFileName);

					this.process(j, writer, trainFileName, testFileName);
				}
			}

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

	private void process(int no, BufferedWriter writer, String trainFile,
			String testFile) {

		Instances ins = null;

		ArffProcessor arffProcessor = new ArffProcessor();
		ins = arffProcessor.gainInstances(trainFile);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

		try {

			writer.newLine();
			System.out.println(trainFile);
			writer.write(trainFile);
			writer.newLine();
			writer.write(testFile);
			writer.newLine();
			writer.flush();

			writer.write("Get matrix started...");
			writer.newLine();

			System.out.println("numAttributes:" + (ins.numAttributes() - 1));
			writer.write("numAttributes:" + (ins.numAttributes() - 1));
			writer.newLine();
			
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
				writer.write(no + "  " + df.format(new Date()) + "  "
						+ trainFile + "Get matrix " + i);
				writer.newLine();
				System.out.println(no
						+ "  "
						+ (df.format(new Date()) + "  " + trainFile + "  "
								+ "Get matrix " + i));
				for (int j = 0; j < matrix[i].length; j++) {
					writer.write(matrix[i][j] + "  ");
					System.out.print(matrix[i][j] + "  ");
				}
				writer.newLine();
				writer.flush();
				System.out.println();
			}
			writer.write("Get matrix finished.");
			writer.newLine();
			writer.flush();

			//3. 搜索得到特征自己存入features集合中
			// ArrayList features = FeatureSelector.selectFeature(matrix);
			FeatureSelectorByFloatSearch selectEngine = new FeatureSelectorByFloatSearch(
					matrix);

			ArrayList features = selectEngine.floatSearch();
			System.out.println("We need these features:");
			writer.write("We need these features:");
			writer.newLine();
			for (int i = 0; i < features.size(); i++) {
				System.out.println(features.get(i));
				writer.write(features.get(i) + "  ");
			}
			writer.write("Feature selection successed.");
			writer.newLine();

			
			//4. 用选好的特征子集形成训练集和测试集调用testProcessor.testFeatureSelection，构造分类器并得到分类准确率
			writer.write("Test the efficiency of our features...");
			writer.newLine();

			Instances trainIns = null;
			Instances testIns = null;

			TestProcessor testProcessor = new TestProcessor();
			trainIns = testProcessor.gainInstances(trainFile);
			testIns = testProcessor.gainInstances(testFile);

			double accuracy = testProcessor.testFeatureSelection(trainIns,
					testIns, features);
			System.out.println("The accuracy  of SMO with feature selection: "
					+ accuracy);
			writer.write("The accuracy  of SMO with feature selection: "
					+ accuracy);
			writer.newLine();

			//5. 用同样的训练和测试集调用testProcessor.testFeatureSelection得到未作特征选择的分类算法的准确率
			CompareProcessor compareProcessor = new CompareProcessor();
			double comparedAccuracy = compareProcessor
					.testWithoutFeatureSelection(trainIns, testIns);
			System.out
					.println("The accuracy of SMO without dealing with features: "
							+ comparedAccuracy);
			writer.write("The accuracy of SMO without dealing with features: "
					+ comparedAccuracy);
			writer.newLine();
			writer.newLine();

			writer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
