package naturalisedweathermapping.source.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

public class Cloud {
	
	private float[][] cloudshape;
	private float positionx = 0;
	private float positiony = 0;
	private float speed = 0;
	
	public Cloud(int size, float smoothingFactor, float featureFactor, int passes) {
		
		float r = size;
        BufferedImage img = new BufferedImage((int) r, (int) r, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setBackground(Color.WHITE);
        g.setPaint(new RadialGradientPaint(
           r/2,r/2,r,
           new float[] {0f,0.5f},
           new Color[] {Color.BLACK, Color.WHITE}
        ));
        g.fill(new Ellipse2D.Float(0,0,r,r));
        g.dispose();
        int[] maskRaster = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		int[] mask = new int[maskRaster.length];
		for (int i = 0; i < mask.length; i++) {
			mask[i] = new Color(maskRaster[i]).getRed();//&0x000000FF;
		}
		
		int scaler = 128;
		new SimplexNoiseMaker(new Random().nextInt(10000));
		if (passes < 1) {
			passes = 1;
		}
		cloudshape = new float[size][size];
		float sf = CloudProperties.smoothingDefault;
		float ff = CloudProperties.featureFactorDefault;
		for (int i = 0; i < passes; i++) {
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					cloudshape[x][y] += (float) SimplexNoiseMaker.noise(x * ff, y * ff) * sf;
					cloudshape[x][y] = clamp(cloudshape[x][y], -1.0f, 1.0f);
					cloudshape[x][y] *= scaler;
					cloudshape[x][y] = Math.round(cloudshape[x][y]);
					cloudshape[x][y] += scaler;
					//cloudshape[x][y] *= (float) mask[x*size + y]/255.0f;
				}
			}
			sf *= smoothingFactor;
			ff *= featureFactor;
		}
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				//cloudshape[x][y] *= (float) mask[x*size + y]/255.0f;
			}
		}
	}

	private float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}

	public float[][] getCloudshape() {
		return cloudshape;
	}

	public float getPositionx() {
		return positionx;
	}

	public float getPositiony() {
		return positiony;
	}

	public float getSpeed() {
		return speed;
	}

	public void setPositionx(float positionx) {
		this.positionx = positionx;
	}

	public void setPositiony(float positiony) {
		this.positiony = positiony;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
}
