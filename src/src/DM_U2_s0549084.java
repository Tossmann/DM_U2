package src;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.lang.Math;
/**
     Opens an image window and adds a panel below the image
 */
public class DM_U2_s0549084 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;


	public static void main(String args[]) {
		//new ImageJ();
		conversionTest();
		IJ.open("C:/Users/Traute/Documents/Uni/GDM/ImageJ/plugins/DM_U2/orchid.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		DM_U2_s0549084 pw = new DM_U2_s0549084();
		pw.imp = IJ.getImage();
		pw.run("");
	}



	private static void conversionTest() {
		System.out.println("Color CONVERSIONTEST");
		for (int r =0; r< 256; r++) {
			for (int g = 0; g<256; g++) {
				for  (int b = 0; b<256; b++) {
					//TODO implement Methods: RGB2YCbCr and YCbCr2RGB
					double []YCbCrResult = RGB2YCbCr(r, g, b);					
					int backConverted[] = YCbCr2RGB(YCbCrResult[0], YCbCrResult[1], YCbCrResult[2]);
					//TODO use previous line instead of the following line:
					if ( r != backConverted[0] || g != backConverted[1] ||
							b !=  backConverted[2])
					{
						System.out.println( "Your Conversion failed: r: "+ r + " g: " +g + " b: "+b + 
								" != " + backConverted[0] + ", " + backConverted[1]+", "+ backConverted[2]);
					}
				}
			}
		}
		System.out.println("done");
	}

	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JSlider jSliderBrightness;
		private JSlider jSliderContrast;
		private JSlider jSliderSaturation;
		private JSlider jSliderHue;
		private double brightness;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			panel.setLayout(new GridLayout(4, 1));
			jSliderBrightness = makeTitledSlider("Helligkeit", 0, 200, 100);
			jSliderContrast = makeTitledSlider("Kontrast", 0, 100, 50);
			jSliderSaturation = makeTitledSlider("S�ttigung", 0, 100, 50);
			jSliderHue = makeTitledSlider("Hue", 0, 100, 50);
			panel.add(jSliderBrightness);
			panel.add(jSliderContrast);
			panel.add(jSliderSaturation);
			panel.add(jSliderHue);

			add(panel);

			pack();
		}

		private JSlider makeTitledSlider(String string, int minVal, int maxVal, int val) {

			JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
			Dimension preferredSize = new Dimension(width, 50);
			slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);

			return slider;
		}

		private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
					str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue()-100;
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}

			if (slider == jSliderContrast) {
				int value = slider.getValue();
				String str = "Slider2-Wert " + value; 
				setSliderTitle(jSliderContrast, str); 
			}

			changePixelValues(imp.getProcessor());

			imp.updateAndDraw();
		}


		private void changePixelValues(ImageProcessor ip) {

			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;


					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren


					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (r<<16) | (g<<8) | b;
				}
			}
		}

	} // CustomWindow inner class

	public static double[] RGB2YCbCr(int r, int g , int b){
		double [] YCbCr = new double[3];
		YCbCr[0] = (r*0.299 + g*0.587 + b*0.114);
		/*if(YCbCr[0]-Math.ceil(YCbCr[0])>=0.5)
			YCbCr[0]=Math.floor(YCbCr[0]);
		else
			YCbCr[0]=Math.ceil(YCbCr[0]);*/

		YCbCr[1] = (-0.168736*r + -0.331264*g + 0.5*b);
		/*if(YCbCr[1]-Math.ceil(YCbCr[1])>=0.5)
			YCbCr[1]=Math.floor(YCbCr[1]);
		else
			YCbCr[1]=Math.ceil(YCbCr[1]);
		 */
		YCbCr[2] = (0.5*r + -0.418688*g + -0.081312*b);
		/*if(YCbCr[2]-Math.ceil(YCbCr[2])>=0.5)
			YCbCr[2]=Math.floor(YCbCr[2]);
		else
			YCbCr[2]=Math.ceil(YCbCr[2]);*/
		return YCbCr;
	}

	public static int[] YCbCr2RGB(double Y, double Cb, double Cr){
		int[] backConverted = new int[3];
		if((Y + 1.402 * Cr)-Math.floor((Y + 1.402 * Cr))>=0.5)
			backConverted[0] = (int) Math.ceil((Y + 1.402 * Cr));
		else
			backConverted[0] = (int) Math.floor((Y + 1.402 * Cr));
		if((Y - 0.3441*Cb - 0.7141*Cr)- Math.floor((Y - 0.3441*Cb - 0.7141*Cr))>=0.5)
			backConverted[1] = (int) Math.ceil((Y - 0.3441*Cb - 0.7141*Cr));
		else
			backConverted[1] = (int) Math.floor((Y - 0.3441*Cb - 0.7141*Cr));
		if((Y + 1.772*Cb)-Math.floor((Y + 1.772*Cb))>=0.5)
			backConverted[2] = (int)Math.ceil((Y + 1.772*Cb));
		else
			backConverted[2] = (int)Math.floor((Y + 1.772*Cb));
		return backConverted;
	}
} 
