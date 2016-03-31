package naturalisedweathermapping.source.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class WeatherUtils {
	
	static int mapSize = 1;
	static float[] noiseMap;
	static float[] cloudMap;
	static float[] mask;
	
	public WeatherUtils(int mapSize) {
		WeatherUtils.mapSize = mapSize;
		mask = new float[256 * 256 * mapSize];
		//r^2 = x^2 + y^2
		for (int x = 0; x < 256 * mapSize; x++) {
			for (int y = 0; y < 256 * mapSize; y++) {
				mask[x * 256 + y] = (float) (Math.sin(x)*Math.sin(y));
				System.out.print(mask[x * 256 + y] + ", ");
				//Math.sin((2 * Math.pi())/B)
			}
			System.out.println();
		}
		writeOutImage(mask);
	}
	public static void generateNoiseMap() {
		noiseMap = new float[32 * 32* mapSize];
		cloudMap = new float[256 * 256 * mapSize];
	}
	
	public static float createSeededNoise(int x, int y, int seed) {
		int n = x + y * 57 + seed * 131;
		
		n = (n << 13) ^ n;
		return (1.0f - ((n * (n * n * 15731 + 789221) + 1276312589)&0x7fffffff) * 0.000000000931322574615478515625f);
	}
	
	public static void setNoiseToTempMap() {
		float[][] tempMap = new float[34][34];
		Random rnd = new Random();
		int seed = rnd.nextInt();
		
		for (int y = 1; y < 33; y++) {
			for (int x = 1; x < 33; x++) {
				tempMap[x][y] = 128.0f + createSeededNoise(x, y,  seed)*128.0f;
			}
		}
		
		for (int x = 1; x < 33; x++) {
			tempMap[0][x] = tempMap[32][x];
			tempMap[33][x] = tempMap[1][x];
			tempMap[x][0] = tempMap[x][32];
			tempMap[x][33] = tempMap[x][1];
		}
		tempMap[0][0] = tempMap[32][32];
		tempMap[33][33] = tempMap[1][1];
		tempMap[0][33] = tempMap[32][1];
		tempMap[33][0] = tempMap[1][32];
		
		
		for (int y = 1; y < 33; y++) {
			for (int x = 1; x < 33; x++) {
				float center = tempMap[x][y]/4.0f;

				float sides = (tempMap[x+1][y] + tempMap[x-1][y] + tempMap[x][y+1] + tempMap[x][y-1])/8.0f;

				float corners = (tempMap[x+1][y+1] + tempMap[x+1][y-1] + tempMap[x-1][y+1] + tempMap[x-1][y-1])/16.0f;
				noiseMap[((x-1)*32) + (y-1)] = center + sides + corners;
			}
		}
	}
	
	public static float smoothMap(float x, float y, float[] map) {
		int X = (int) x;
		int Y = (int) y;
		
		float xFract = x - X;
		float yFract = y - Y;
		
		int X0 = X % 32;
		int Y0 = Y % 32;
		int X1 = (X + 1) % 32;
		int Y1 = (Y + 1) % 32;
		
		float bot = map[X0*32 + Y0] + xFract * (map[X1*32 + Y0] - map[X0*32 + Y0]);
		float top = map[X0*32 + Y1] + xFract * (map[X1*32 +  Y1] - map[X0*32 + Y1]);
		
		return (bot + yFract * (top - bot));
	}
	
	public static void OverlapOctaves(float[] mapSmall, float[] cloudMap, int maps) {
		for (int x = 0; x < cloudMap.length*cloudMap.length; x++) {
			cloudMap[x] = 0;
		}
		for (int octave = 0; octave < maps; octave++) {
			for (int x = 0; x < cloudMap.length; x++) {
				for (int y = 0; y < cloudMap.length; y++) {
					float scale = (float) (1 / Math.pow(2, 3-octave));
					float noise = smoothMap(x*scale, y*scale , mapSmall);
					cloudMap[(y*256) + x] += (float) (noise / Math.pow(2, octave));
				}
			}
		}
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
