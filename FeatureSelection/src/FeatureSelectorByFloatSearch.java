import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FeatureSelectorByFloatSearch {
	public static int D = 2;
	public static int DELTA = 1;

	private static final String LOG_ROUTE = "D://workspace2//Copy of featureSelection//log//";
	private static final String TXT_SUFFIX = ".txt";

	int k;
	ArrayList selectedFeatures;
	int[] selectedFeaturesArray; // 存储已选入的特征的01向量状况
	ArrayList availableToSelectFeatures;
	int jValue;
	int tempValue;
	int sbsValue;
	int featureMatrix[][];

	int numFeatures;
	int numInstances;

	BufferedWriter writer;

	public FeatureSelectorByFloatSearch(int matrix[][]) {
		this.featureMatrix = matrix;
		this.k = 0;
		this.selectedFeatures = new ArrayList();
		// 给selectedFeaturesArray初始化，在selectedFeatures集合为空时，所有实例均无法被分开，因此赋值1
		this.selectedFeaturesArray = new int[featureMatrix[0].length];
		for (int i = 0; i < selectedFeaturesArray.length; i++) {
			selectedFeaturesArray[i] = 1;
		}
		this.availableToSelectFeatures = new ArrayList();
		for (int i = 0; i < matrix.length; i++) {
			this.availableToSelectFeatures.add(i);
		}
		this.jValue = 0;

		this.numFeatures = featureMatrix.length;
		this.numInstances = featureMatrix[0].length;

		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
			writer = new BufferedWriter(new FileWriter(LOG_ROUTE
					+ df.format(new Date()) + "floatsearch" + TXT_SUFFIX));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList floatSearch() {
		boolean step1 = true;
		boolean step2 = false;
		boolean step3 = false;

		// 开始：加入第一个特征到selectedFeatures集合里
		int step1FeatureNo = this.sfs();
		this.addFeature(step1FeatureNo);
		int step2FeatureNo;

		do {
			if (selectedFeatures.size() > 1) {
				// step2
				this.tempValue = this.computeJ(this.selectedFeaturesArray);
				step2FeatureNo = this.sbs();
				if (this.sbsValue >= this.tempValue) {
					this.removeFeature(step2FeatureNo);
					step2 = true;
				} else {
					step2 = false;
				}

				if (step2 == true)
					continue;

				// step3
				step3 = this.replaceFeature();
				if (step3 == true) {
					continue;
				}
			}

			step1FeatureNo = this.sfs();
			this.addFeature(step1FeatureNo);

			// 终止条件
			if (this.k == D + DELTA) {
				break;
			}

		} while (step1);

		return this.selectedFeatures;
	}

	private int sfs() {
		// 从availableToSelectFeatures的ArrayList集合里面挑进去最好的特征加入到selectedFeatures
		// 返回所选特征的编号

		int bestFeatureNo = (Integer) this.availableToSelectFeatures.get(0);
		int maxJ = 0;
		for (int i = 0; i < this.availableToSelectFeatures.size(); i++) {
			int j = this.computeJ(this.combineFeatures(
					this.selectedFeaturesArray,
					this.featureMatrix[(Integer) this.availableToSelectFeatures
							.get(i)]));
			if (j > maxJ) {
				bestFeatureNo = i;
				maxJ = j;
			}
		}
		this.tempValue = maxJ;
		return bestFeatureNo;
	}

	private int sfs(ArrayList tempFeatures) {
		// 从tempAvailFeatures的ArrayList集合里面挑进去最好的特征加入到tempFeatures
		// 返回所选特征的编号

		// 将tempFeatures映射到数组中，在tempFeatues中的赋值1，否则赋值0；将数组中赋值为0的下标赋值给tempAvailFeatues
		int tempArray[] = new int[numFeatures];
		ArrayList tempAvailFeatures = new ArrayList();
		for (int i = 0; i < tempFeatures.size(); i++) {
			int fea = (Integer) tempFeatures.get(i);
			tempArray[fea] = 1;
		}
		for (int i = 0; i < tempArray.length; i++) {
			if (tempArray[i] == 1)
				continue;
			tempAvailFeatures.add(i);
		}

		int[] tempFeatureArray = this.combineFeatures(tempFeatures);

		int bestFeatureNo = (Integer) tempAvailFeatures.get(0);
		int maxJ = 0;
		for (int i = 0; i < tempAvailFeatures.size(); i++) {
			int j = this.computeJ(this.combineFeatures(tempFeatureArray,
					this.featureMatrix[(Integer) tempAvailFeatures.get(i)]));
			if (j > maxJ) {
				bestFeatureNo = i;
				maxJ = j;
			}
		}
		this.tempValue = maxJ;
		return bestFeatureNo;
	}

	private void addFeature(int addFeatureNo) {
		this.selectedFeaturesArray = this.combineFeatures(
				this.selectedFeaturesArray, featureMatrix[addFeatureNo]);
		this.selectedFeatures.add(addFeatureNo);
		this.availableToSelectFeatures.remove((Object) addFeatureNo);
		this.jValue = this.computeJ(this.selectedFeaturesArray);
		this.k = this.k + 1;
		
		try {
			System.out.println("addFeature:" + addFeatureNo);
			writer.write("addFeature:" + addFeatureNo);
			writer.newLine();
			System.out.println("JValue:" + this.jValue);
			writer.write("JValue:" + this.jValue);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int sbs() {
		// 挑选出selectedFeatures里面减掉一个对J减少最小的属性
		int subFeatureNo = (Integer) selectedFeatures.get(0);
		int maxJ = 0;
		int sbsJ = 0;

		for (int i = 0; i < this.selectedFeatures.size(); i++) {
			ArrayList tempFeatures = new ArrayList();
			for (int p = 0; p < this.selectedFeatures.size(); p++) {
				if (p == i) {
					continue;
				}
				tempFeatures.add(selectedFeatures.get(p));
			}
			sbsJ = this.computeJ(this.combineFeatures(tempFeatures));
			if (sbsJ > maxJ) {
				maxJ = sbsJ;
				subFeatureNo = (Integer) selectedFeatures.get(i);
			}
		}

		this.sbsValue = maxJ;
		return subFeatureNo;
	}

	private void removeFeature(int subFeatureNo) {
		this.selectedFeatures.remove((Object) subFeatureNo);
		this.selectedFeaturesArray = this
				.combineFeatures(this.selectedFeatures);
		this.availableToSelectFeatures.add(subFeatureNo);
		this.jValue = this.computeJ(this.selectedFeaturesArray);
		this.k = this.k - 1;
		
		try {
			System.out.println("removeFeature:" + subFeatureNo);
			writer.write("removeFeature:" + subFeatureNo);
			writer.newLine();
			System.out.println("JValue:" + this.jValue);
			writer.write("JValue:" + this.jValue);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean replaceFeature() {
		// 去掉1个特征，用sfs加一个特征，找到最大值，再从这个k个J值中找到最大的，比较替换与未替换的J值，若替换后J值增大则返回true

		// 2个arrayList对应，index存储被替换的feature编号，kJValue存储J值，kAddFeature存储替换的feature编号
		ArrayList kJValue = new ArrayList();
		ArrayList kAddFeature = new ArrayList();

		for (int i = 0; i < this.selectedFeatures.size(); i++) {
			ArrayList tempFeatures = new ArrayList();
			for (int p = 0; p < this.selectedFeatures.size(); p++) {
				if (p == i) {
					continue;
				}
				tempFeatures.add(selectedFeatures.get(p));
			}
			int addFeature = this.sfs(tempFeatures);
			kJValue.add(this.tempValue);
			kAddFeature.add(addFeature);
		}

		// 找到k个J值的最大值
		int maxJ = 0;
		int addFeature = (Integer) kAddFeature.get(0);
		int removeFeature = 0;
		for (int i = 0; i < kJValue.size(); i++) {
			int j = (Integer) kJValue.get(i);
			if (j > maxJ) {
				maxJ = j;

				addFeature = (Integer) kAddFeature.get(i);
				removeFeature = (Integer) selectedFeatures.get(i);
			}
		}

		if (maxJ > this.jValue) {
			this.addFeature(addFeature);
			this.removeFeature(removeFeature);
			return true;
		} else {
			return false;
		}
	}

	private int computeJ(int[] a) {
		// 0:正确的数目，1：错误的数目
		int count = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < 1) {
				count++;
			}
		}
		return count;
	}

	public int[] combineFeatures(int[] a, int[] b) {
		// 返回两个数组做与操作后，0值的个数。
		int[] array = new int[a.length];
		for (int i = 0; i < array.length; i++) {
			if ((a[i] == 1) && (b[i] == 1)) {
				array[i] = 1;
			} else {
				array[i] = 0;
			}
		}
		return array;
	}

	public int[] combineFeatures(ArrayList features) {
		// 初始化array，所有实例均无法被分开，赋值1
		int[] array = new int[this.numInstances];
		for (int i = 0; i < array.length; i++) {
			array[i] = 1;
		}
		for (int i = 0; i < features.size(); i++) {
			array = this.combineFeatures(array,
					this.featureMatrix[(Integer) features.get(i)]);
		}
		return array;
	}

	// public static void main(String[] args) {
	// int[][] matrix = {{0,1,0,0,1},{0,0,1,1,1,},{1,1,0,0,0}};
	// FeatureSelectorByFloatSearch selectEngine = new
	// FeatureSelectorByFloatSearch(matrix);
	//
	// ArrayList features = selectEngine.floatSearch();
	//
	// for(int i = 0; i < features.size(); i++) {
	// System.out.println(features.get(i));
	// }
	// }

}
