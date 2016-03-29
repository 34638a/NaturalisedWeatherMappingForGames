package naturalisedweathermapping.source;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import naturalisedweathermapping.source.utils.Cloud;

public class SourceControl {
	
	public static List<Cloud> clouds;
	private static final boolean outputCloudsToImages = true;
	private static final int cloudsToGenerate = 5;
	
	public static void main(String[] args) throws IOException {
		clouds = new ArrayList<Cloud>();
		int cloudSize = 1024;
		for (int i = 0; i < cloudsToGenerate; i++) {
			Cloud c = new Cloud(cloudSize, 10f, 0.1f, 1);
			clouds.add(c);
			if (outputCloudsToImages) {
				BufferedImage im = new BufferedImage(cloudSize, cloudSize, BufferedImage.TYPE_INT_RGB);
				int[] raster = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
				for (int x = 0; x < cloudSize; x++) {
					for (int y = 0; y < cloudSize; y++) {
						raster[x*cloudSize + y] = getIntFromColor(c.getCloudshape()[x][y],c.getCloudshape()[x][y],c.getCloudshape()[x][y]);
						//System.out.print(c.getCloudshape()[x][y] + ", ");
					}
					//System.out.println();
				}
				File outFile = new File("./Cloud_"+i+".png");
				System.out.println("Writing out " + i);
				ImageIO.write(im, "png", outFile);
			}
		}
		/*
		float r = cloudSize;
        BufferedImage img = new BufferedImage((int) r, (int) r, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setBackground(Color.WHITE);
        g.setPaint(new RadialGradientPaint(
           r/2,r/2,r,
           new float[] {0f,0.5f},
           new Color[] {Color.WHITE, Color.BLACK}
        ));
        g.fill(new Ellipse2D.Float(0,0,r,r));
        g.dispose();
		
		File outFile = new File("./Cloud_.png");
		System.out.println("Writing out");
		ImageIO.write(img, "png", outFile);
		//*/
	}
	
	public static int getIntFromColor(int Red, int Green, int Blue){
	    Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
	    Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
	    Blue = Blue & 0x000000FF; //Mask out anything not blue.

	    return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
	}
	public static int getIntFromColor(float Red, float Green, float Blue){
		return getIntFromColor((int) Red, (int) Green, (int) Blue);
	}
}
