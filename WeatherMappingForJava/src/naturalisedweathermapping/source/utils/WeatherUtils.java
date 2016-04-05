package naturalisedweathermapping.source.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class WeatherUtils {
	
	private static int mapSize = 1;
	private static int noiseSize = 1;
	private static double[][] noise;
	
	public static double[][] generateNoised2(int size, int smoothness) {
		noiseSize = size;
		double[][] retNoise = new double[size][size];
		noise = new double[size][size];
		generateRandomNoise();
		for (int x = 0; x < noise.length; x++) {
			for (int y = 0; y < noise[x].length; y++) {
				retNoise[x][y] = Math.round((float) turbulence(x, y, smoothness));
			}
		}
		return retNoise;
	}
	public static float[][] generateNoisef2(int size, int smoothness) {
		float[][] temp = new float[size][size];
		double[][] tNoise = generateNoised2(size, smoothness);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				temp[x][y] = (float) tNoise[x][y];
			}
		}
		return temp;
	}
	
	private static void generateRandomNoise() {
		Random rnd = new Random();
		for (int x = 0; x < noise.length; x++) {
			for (int y = 0; y < noise[x].length; y++) {
				noise[x][y] = (double) ((rnd.nextInt(32768) % 32768) / 32768.0);
			}
		}
	}
	
	private static double smoothNoiseMap(double x, double y) {
		
		int rX = (int) x; //Math.round((float) x);
		int rY = (int) y; //Math.round((float) y);
		//int rX = Math.round((float) x);
		//int rY = Math.round((float) y);
		//if (rX < 0 || rY < 0) {
		//	System.out.println(rX + ", " + rY);
		//}
		
		double fractX = x - rX;
		double fractY = y - rY;
		
		int x1 = (rX + noiseSize - 1) % noiseSize;
		int y1 = (rY + noiseSize - 1) % noiseSize;
		int x2 = (x1 + noiseSize - 1) % noiseSize;
		int y2 = (y1 + noiseSize - 1) % noiseSize;
		
		double value = 0.0;
		value += fractX     * fractY     * noise[y1][x1];
		value += (1 - fractX) * fractY     * noise[y1][x2];
		value += fractX     * (1 - fractY) * noise[y2][x1];
		value += (1 - fractX) * (1 - fractY) * noise[y2][x2];
		//value = noise[rX][rY];
		
		return value;
	}
	
	private static double turbulence(double x, double y, double size) {
		double value = 0.0, initialSize = size;
		while (size >= 1) {
			value += smoothNoiseMap(x / size, y / size) * size;
			size /= 2.0;
		}
		return (128.0 * value / initialSize);
	}
	
	public static void expFilterMap(float[] cloudMap) {
		float cover = 20f;
		float sharpness = 0.95f;
		for (int x = 0; x < cloudMap.length*cloudMap.length; x++) {
			float c = cloudMap[x] - (255.0f - cover);
			if (c < 0) {
				c = 0;
			}
			cloudMap[x] = 255.0f - ((float) (Math.pow(sharpness, c))*255.0f);
		}
	}
	
	public static void writeOutImage(float[] array) {
		int[] raster = new int[array.length];
		for (int i = 0; i < raster.length; i++) {
			raster[i] = Math.round(array[i]);
		}
		writeOutImage(raster);
	}
	
	public static void writeOutImage(int[] array) {
		BufferedImage img = new BufferedImage(256 * mapSize, 256 * mapSize, BufferedImage.TYPE_INT_RGB);
		int[] raster = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < raster.length; i++) {
			raster[i] = array[i];
		}
		writeOutImage(img, "MAP");
	}
	
	public static void writeOutImage(BufferedImage img, String fileName) {
		File outFile = new File("./" + fileName + ".png");
		System.out.println("Writing out");
		try {
			ImageIO.write(img, "png", outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
