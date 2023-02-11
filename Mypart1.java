
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;


public class Mypart1 {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	int size = 512;
	int r = 256;
	int centerX = 256;
	int centerY = 256;
	int radius = 256;
	int x_max = 512;
	int y_max = 512;
	int x_min = 0;
	int y_min = 0;


	// Draws a black line on the given buffered image from the pixel defined by (x1, y1) to (x2, y2)
	public void drawLine(BufferedImage image, int x1, int y1, int x2, int y2) {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawLine(x1, y1, x2, y2);
		g.drawImage(image, 0, 0, null);
	}

	public void drawRadialLines(BufferedImage image, int n) {
		double stepSize = 360 / n;
        for (int i = 0; i < n; i++) {
          double theta = i * stepSize;
          double alpha = theta - 90 * Math.round(theta / 90);
          double[] point = intersectingPoint(centerX, centerY, Math.cos(Math.toRadians(theta)), Math.sin(Math.toRadians(theta)));
          drawLine(img, 256, 256, (int) point[0], (int) point[1]); // diagonal line
        }
	}


	public BufferedImage getAntiAliasedImage(BufferedImage image) {
		BufferedImage antiAliasedImage = new BufferedImage(size, size, image.getType());
        int[] originalPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int[] antiAliasedPixels = ((DataBufferInt) antiAliasedImage.getRaster().getDataBuffer()).getData();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int sumR = 0;
				int sumG = 0;
				int sumB = 0;
				int count = 0;
				for (int i = x - 1; i <= x + 1; i++) {
					for (int j = y - 1; j <= y + 1; j++) {
						if (i >= 0 && i < size && j >= 0 && j < size) {
							Color color = new Color(image.getRGB(i, j));
							sumR += color.getRed();
							sumG += color.getGreen();
							sumB += color.getBlue();
							count++;
						}
					}
				}
				int avgR = sumR / count;
				int avgG = sumG / count;
				int avgB = sumB / count;
				Color avgColor = new Color(avgR, avgG, avgB);
				antiAliasedImage.setRGB(x, y, avgColor.getRGB());
			}
		}
		return antiAliasedImage;
	}

	public BufferedImage getScaledImage(BufferedImage image, double scaleFactor, boolean antiAlias) {
        int scaledsize = (int) (size * scaleFactor);

		if (antiAlias) {
			image = getAntiAliasedImage(image);
		}

        BufferedImage scaledImage = new BufferedImage(scaledsize, scaledsize, image.getType());
        int[] originalPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int[] resizedPixels = ((DataBufferInt) scaledImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < scaledsize; y++) {
            for (int x = 0; x < scaledsize; x++) {
				// Calculate the original pixel index
                int originalPixelIndex = (int) (y / scaleFactor) * size + (int) (x / scaleFactor);
                resizedPixels[y * scaledsize + x] = originalPixels[originalPixelIndex];
            }
        }
        return scaledImage;
    }

	private double[] intersectingPoint(int x0, int y0, double xd, double yd) {
		double[] res = new double[2];
		String xIntersect = "";
		String yIntersect = "";
		double tx = 0, ty = 0;
		if (xd > 0) {
		  xIntersect = "RIGHT";
		  tx = (x_max - x0) / xd;
		} else if (xd < 0) {
		  xIntersect = "LEFT";
		  tx = (x_min - x0) / xd;
		} else {
		  xIntersect = "NONE";
		}
	
		if (yd > 0) {
		  yIntersect = "TOP";
		  ty = (y_max - y0) / yd;
		} else if (yd < 0) {
		  yIntersect = "BOTTOM";
		  ty = (y_min - y0) / yd;
		} else {
		  yIntersect = "NONE";
		}
	
		if (xIntersect.equals("NONE")) {
		  res[0] = x0 + ty * xd;
		  res[1] = y0 + ty * yd;
		  return res;
		} else if (yIntersect.equals("NONE")) {
		  res[0] = x0 + tx * xd;
		  res[1] = y0 + ty * yd;
		} else {
		  if (tx < ty) {
			res[0] = x0 + tx * xd;
			res[1] = y0 + tx * yd;
		  } else if (ty < tx) {
			res[0] = x0 + ty * xd;
			res[1] = y0 + ty * yd;
		  } else {
			res[0] = x0 + tx * xd;
			res[1] = y0 + tx * yd;
		  }
		}
		return res;
	  }
  
  

	public void showIms(String[] args){

		// Read parameters from command line
		int n = Integer.parseInt(args[0]); // number of radial lines
		System.out.println("n: " + n);

		double scaleFactor = Double.parseDouble(args[1]);  // the scale factor
		System.out.println("scaleFactor: " + scaleFactor);

		boolean antiAlias = "1".equals(args[2]); // anti-aliasing required or not (0 or 1)
		System.out.println("antiAlias: " + antiAlias);

		// Initialize a plain white image
		img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

		for(int y = 0; y < size; y++) {

			for(int x = 0; x < size; x++) {

				// byte a = (byte) 255;
				byte r = (byte) 255;
				byte g = (byte) 255;
				byte b = (byte) 255;

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x, y, pix);
			}
		}
		
		drawLine(img, 0, 0, size-1, 0);				// top edge
		drawLine(img, 0, 0, 0, size-1);				// left edge
		drawLine(img, 0, size-1, size-1, size-1);	// bottom edge
		drawLine(img, size-1, size-1, size-1, 0); 	// right edge
		
		// Draw the radially outward lines (filling up a circle)
		drawRadialLines(img, n);
		
		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);

		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(getScaledImage(img, scaleFactor, antiAlias))); // Get the scaled image

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		Mypart1 ren = new Mypart1();
		ren.showIms(args);
	}

}