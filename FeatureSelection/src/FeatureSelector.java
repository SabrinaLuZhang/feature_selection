import java.util.ArrayList;


public class FeatureSelector {
	
	public static ArrayList selectFeature(int[][] matrix) {
		//features���鱣��������ѡ�����Եļ��ϣ���0��ʼ
		ArrayList features = new ArrayList();
		int maxZeroFeature = 0;
		int maxZeroCount = FeatureSelector.countZero(matrix[0]);
		int numAttributes = matrix.length;
		int numDatasets = matrix[0].length;
		// �õ��������0�����ԣ�maxZeroFeature;
		// �������maxZeroFeatureһ����features�б���
		for (int i = 1; i < numAttributes; i++) {
			if (FeatureSelector.countZero(matrix[i]) > maxZeroCount) {
				maxZeroCount = FeatureSelector.countZero(matrix[i]);
				maxZeroFeature = i;
			}
		}
		features.add(maxZeroFeature);

		// �洢���ںϲ����Ժ��ֿܷ������
		int[] processFeature = matrix[maxZeroFeature];

		int zeroCount = FeatureSelector.countZero(processFeature);
		boolean flag = true; // true: �иı䣬false��û�иı�
		int max = FeatureSelector.countZero(processFeature);
		int maxI = maxZeroFeature;
System.out.println("ѭ��ǰmax:" + max);
System.out.println("ѭ��ǰmaxI:" + maxI);
//System.out.println("zeroCount:" + zeroCount);
//System.out.println("numDatasets:" + numDatasets);
		while (zeroCount < numDatasets) {
			flag = false;
			int combine;
			for (int i = 0; i < numAttributes; i++) {
				combine = FeatureSelector.combineFeatures(processFeature, matrix[i]);
System.out.println("combine:" + combine);
				if (combine > max) {
					maxI = i;
					max = combine;
				}
			}
System.out.println("numdatasets:" + numDatasets);
System.out.println("numattibutes:" + numAttributes);
System.out.println("max:" + max);
System.out.println("maxI:" + maxI);
			if (max > zeroCount) {
				flag = true;
				features.add(maxI);
				// ����processFeature����
				processFeature = FeatureSelector.combineFeaturesAndGainResult(
						processFeature, matrix[maxI]);
				zeroCount = FeatureSelector.countZero(processFeature);
			}

for (int i = 0; i < processFeature.length; i++) {
	System.out.println("processfeature" + processFeature[i]);
}

			if (flag == false) {
				break;
			}

		}
		return features;
	}

	public static int combineFeatures(int[] a, int[] b) {
		// ���������������������0ֵ�ĸ�����
		int zeroCount = 0;
		for (int i = 0; i < a.length; i++) {
			if ((a[i] == 1) && (b[i] == 1)) {
			} else {
				zeroCount++;
			}
		}
		return zeroCount;
	}

	public static int[] combineFeaturesAndGainResult(int[] a, int[] b) {
		int[] result = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			if ((a[i] == 1) && (b[i] == 1)) {
				result[i] = 1;
			} else {
				result[i] = 0;
			}
		}
		return result;
	}

	public static int countZero(int[] a) {
		// 0:��ȷ����Ŀ��1���������Ŀ
		int count = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < 1) {
				count++;
			}
		}
		return count;
	}
}
