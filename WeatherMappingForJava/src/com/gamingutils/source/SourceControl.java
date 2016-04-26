package com.gamingutils.source;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gamingutils.source.utils.Cloud;
import com.gamingutils.source.utils.WeatherUtils;

/*
 * The Plan.
 * The whole system is governed by two 'map' types: The Weather Map and the Wind map
 * The Wind map is a 'Map' that adjusts the temperature and controls the direction of travel that a cloud object moves.
 * RGB Properties: R = wind direction X, B = Wind Direction Y, G = Temperature change.
 * The Weather map is a 'Map' that holds cloud shapes, current effective temperatures at points and where rain is falling.
 * RGB Properties: R = Land temperature level, B = Rain intensity, G = Cloud particles and appearance.
 * A cloud is a object that is to be generated via a noise algorithm that results in an island shape.
 * A cloud has the following properties:
 * 		Moisture (Determines how much it's pixels should rain or if it should dissipate),
 * 		TemperatureCurrent (The current temperature of the cloud),
 * 		Tolerance (this is the threshold of heat that the cloud cannot rain at),
 * 		FrostPoint (The point at which snow appears),
 * 		LifeTime (How many map update frames the cloud should remain alive),
 * 		Dissipating~Boolean (If the cloud is fading out).
 * A WindShape is a shape that determines the way the wind moves on the wind map.
 * A WindShape has the following properties:
 * 		DEFINE PROPERTIES LATER WHEN THEY ARE BEING DEVELOPED.
 * Every Weather update tick:
 * 		All clouds move based on their current wind map point, change temperature based off the current temperature of the wind and , update their lifetime, change the weather map temperature and rain level
 * 		All WindShapes (FINALISE WHEN WIND SHAPES ARE BEING MADE)
 */

public class SourceControl {
	
	public static List<Cloud> clouds;
	private static final boolean outputCloudsToImages = true;
	private static final int cloudsToGenerate = 5;
	
	public static void main(String[] args) throws IOException {
		//TEMP temp = new TEMP();
		//temp.generateNoise();
		//new WeatherUtils(1);
		launch();
	}
	
	public static void launch() throws IOException {
		clouds = new ArrayList<Cloud>();
		int cloudSize = 1024;
		for (int i = 0; i < cloudsToGenerate; i++) {
			Cloud c = new Cloud(cloudSize, 128);
			clouds.add(c);
			if (outputCloudsToImages) {
				BufferedImage im = new BufferedImage(cloudSize, cloudSize, BufferedImage.TYPE_INT_RGB);
				int[] raster = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
				for (int x = 0; x < cloudSize; x++) {
					for (int y = 0; y < cloudSize; y++) {
						raster[x*cloudSize + y] = getIntFromColor(c.getCloudshapef2()[x][y],c.getCloudshapef2()[x][y],c.getCloudshapef2()[x][y]);
						//System.out.print(c.getCloudshape()[x][y] + ", ");
					}
					//System.out.println();
				}
				WeatherUtils.writeOutImage(im, "Cloud_" + i);
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
