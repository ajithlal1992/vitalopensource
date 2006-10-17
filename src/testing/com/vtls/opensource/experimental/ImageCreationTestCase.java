package testing.com.vtls.opensource.experimental;

import junit.framework.TestCase;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.*;

public class ImageCreationTestCase extends TestCase
{
   
	public ImageCreationTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testGeneric()
	{
	   // Set a generic width and height.
      int width = 500;
      int height = 125;
      int padding = 1;
      int bar_width = 10;

      // Create a new BufferedImage instance.
      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      // Get the drawing context.
      Graphics2D g2d = image.createGraphics();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, width, height);

      // Draw a border around the image.
      g2d.setColor(Color.GRAY);
      g2d.drawLine(1, (height - 1) - 1, (width - 1) - 2, (height - 1) - 1);
      
      // Set the drawing color for the graph.
      g2d.setColor(Color.RED);

      // Create a random number generator to provide sample data.
      Random generator = new Random();

      for(int i = padding; i < (width - 1) - (padding * 2); i += bar_width + 1)
      {
         int y_position = (int)Math.floor(generator.nextDouble() * (height - (padding * 2)));
         g2d.fillRect(i, padding + y_position + 2, bar_width, (height - 1) - (padding * 2) - y_position - 1);
      }

      // Dispose of the Graphics context.
      g2d.dispose();

      // Save the image.
      try
      {
         OutputStream out = new BufferedOutputStream(new FileOutputStream("sample.png"));
         ImageIO.write(image, "png", out);
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
      assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(ImageCreationTestCase.class);
	}
}
