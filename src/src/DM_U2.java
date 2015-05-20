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

/**
     Opens an image window and adds a panel below the image
*/
public class DM_U2 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	conversionTest();
    	IJ.open("C:/Users/Traute/Documents/Uni/GDM/ImageJ/plugins/DM_U2/orchid.jpg");
    	//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");
		
		DM_U2 pw = new DM_U2();
		pw.imp = IJ.getImage();
		pw.run("");
	}
    

	
    private static void conversionTest() {
    	System.out.println("Color CONVERSIONTEST");
		for (int r =0; r< 256; r++) {
			for (int g = 0; g<256; g++) {
				for  (int b = 0; b<256; b++) {
					//TODO implement Methods: RGB2YCbCr and YCbCr2RGB
					//YCbCrResult = RGB2YCbCr( r, g, b);
					
					//int backConverted[] = YCbCr2RGB(YCbCrResult[0], YCbCrResult[1], YCbCrResult[2]);
					//TODO use previous line instead of the following line:
					int backConverted[] = {r,g,b};
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
		private JSlider jSlider2;
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
            jSlider2 = makeTitledSlider("Slider2-Wert", 0, 100, 50);
            panel.add(jSliderBrightness);
            panel.add(jSlider2);
            
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
			
			if (slider == jSlider2) {
				int value = slider.getValue();
				String str = "Slider2-Wert " + value; 
				setSliderTitle(jSlider2, str); 
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
					int rn = (int) (r + brightness);
					int gn = (int) (g + brightness);
					int bn = (int) (b + brightness);
					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		
    } // CustomWindow inner class
} 
