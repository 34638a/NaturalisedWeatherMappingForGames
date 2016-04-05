package naturalisedweathermapping.source.utils;

public class Cloud {
	
	private float[][] cloudshape;
	private float positionx = 0;
	private float positiony = 0;
	private float speed = 0;
	
	public Cloud(int size, int smoothness) {
		cloudshape = WeatherUtils.generateNoisef2(size, smoothness);
	}

	public float[][] getCloudshapef2() {
		return cloudshape;
	}
	public double[][] getCloudshaped2() {
		double[][] temp = new double[cloudshape.length][cloudshape.length];
		for (int x = 0; x < cloudshape.length; x++) {
			for (int y = 0; y < cloudshape.length; y++) {
				temp[x][y] = (double) cloudshape[x][y];
			}
		}
		return temp;
	}
	public float[] getCloudshapef() {
		float[] temp = new float[cloudshape.length];
		for (int x = 0; x < cloudshape.length; x++) {
			for (int y = 0; y < cloudshape.length; y++) {
				temp[x * cloudshape.length + y] =  cloudshape[x][y];
			}
		}
		return temp;
	}
	public double[] getCloudshaped() {
		double[] temp = new double[cloudshape.length];
		for (int x = 0; x < cloudshape.length; x++) {
			for (int y = 0; y < cloudshape.length; y++) {
				temp[x * cloudshape.length + y] =  (double) cloudshape[x][y];
			}
		}
		return temp;
	}

	public float getPositionx() {
		return positionx;
	}

	public float getPositiony() {
		return positiony;
	}

	public void setPositionx(float positionx) {
		this.positionx = positionx;
	}

	public void setPositiony(float positiony) {
		this.positiony = positiony;
	}
}
