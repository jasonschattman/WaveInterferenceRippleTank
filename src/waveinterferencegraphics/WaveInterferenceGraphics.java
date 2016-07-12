
package waveinterferencegraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.awt.*;
import javax.swing.JFrame;
import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;
import java.awt.event.*;

public class WaveInterferenceGraphics extends JFrame implements MouseListener, MouseMotionListener {
    
	
    int winSizeX = 600, winSizeY = 600;
    boolean inSteadyState = false;
    
    double deltaT = 0.05;
    double decayRate = 0.000;
    double defaultAmplitude = 42, defaultWaveNumber = 0.4;
    double waveSpeed = 7;
    
    final double maxTimeToReachFarthestBoundary = Math.max(winSizeX, winSizeY)/waveSpeed;
    final double waveLength = Math.PI*2/defaultWaveNumber;
    
    double timeUntilSteadyStateIsReached = maxTimeToReachFarthestBoundary;
    
    Color[] colorValues = new Color[256];
    ArrayList<WaveSource> sources = new ArrayList();
    
    int xMouse, yMouse;

    public void polygonLayout(int numSides, int radius) {
        
        double deltaTheta = Math.PI*2/numSides;
        double theta = 0;
        
        for (int i = 0; i < numSides; i++) {
            double x = winSizeX/2 + radius*Math.cos(theta);
            double y = winSizeY/2 - radius*Math.sin(theta);
            WaveSource w = new WaveSource( defaultAmplitude, defaultWaveNumber, waveSpeed, deltaT, decayRate, x, y, 0, 0 );
            sources.add(w);
            theta += deltaTheta;
        }
    }
    
    public void linearLayout(int numSources, int xStart, int yStart, int xGap, int yGap) {
        double x = xStart, y = yStart;
        
        for (int i = 0; i < numSources; i++) {
            WaveSource w = new WaveSource( defaultAmplitude, defaultWaveNumber, waveSpeed, deltaT, decayRate, x, y, 0, 0 );
            sources.add(w);
            x += xGap;
            y += yGap;
        }
    }
    
    
    public void mousePressed( MouseEvent e ) { //starts a new wave source wherever the user clicks
        
        xMouse = e.getX();
        yMouse = e.getY();
         
        if (xMouse > winSizeX || yMouse > winSizeY)
            sources.clear();
        
        else {
            WaveSource w = new WaveSource( defaultAmplitude, defaultWaveNumber, waveSpeed, deltaT, decayRate, xMouse, yMouse, 0, 0 );
            sources.add( w );
            
        }
        
        timeUntilSteadyStateIsReached = maxTimeToReachFarthestBoundary;
    }
    
    

    public void setColors() {
        for (int i = 0; i < 256; i++) 
            colorValues[i] = new Color(i,255-i,255-i);  
    }
    
    
    public Color getPixelColor( int d ) {
        int index;
        
        if ( sources.size() <= 3 )
            index = d + 127;
        else
            index = Math.max( Math.min( (d + 127), 255), 0);
        
        return colorValues[ index ];
    }
    
    
    public void paint(Graphics g) {
      Image img = createImage();
      g.drawImage(img, 0, 0,this);
    }   

    
    private Image createImage() {
      BufferedImage bufferedImage = new BufferedImage(winSizeX, winSizeY, BufferedImage.TYPE_INT_RGB);

      Graphics2D G = (Graphics2D) bufferedImage.getGraphics();
   
      double r, z, totalDisplacement;
      Color pixelColor;
      
      if ( inSteadyState ) { 

        for (int x = 0; x < winSizeX; x++) {

              for (int y = 0; y < winSizeY; y++) {
                 totalDisplacement = 0;

                  for (int s = 0; s < sources.size(); s++) {
                      WaveSource w = sources.get(s);
                      z = w.getDisplacementSteadyState( x, y );
                      totalDisplacement += z;                    
                  }

                  pixelColor = getPixelColor( (int)totalDisplacement );
                  G.setColor( pixelColor );
                  G.drawLine(x, y, x, y);  
            }
          }
      }
      
      else { //not yet in steady-state
         for (int x = 0; x < winSizeX; x++) {

              for (int y = 0; y < winSizeY; y++) {
                 totalDisplacement = 0;

                  for (int s = 0; s < sources.size(); s++) {
                      WaveSource w = sources.get(s);
                      z = w.getDisplacement( x, y );
                      totalDisplacement += z;                    
                  }

                  pixelColor = getPixelColor( (int)totalDisplacement );
                  G.setColor( pixelColor );
                  G.drawLine(x, y, x, y);  
            }
          }
      }
        return bufferedImage;
      }

    
    public void initWindow() {
        setBackground(Color.white);
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLayout(new GridLayout(3,1));
        
        addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
         });  

        setVisible(true);
    }
    
    
    public static void sleep( int duration ) {
        try {
              Thread.sleep( duration );
            }
        catch (Exception e) {
            }
    }
    
    
    public void updateWaveCentres() {
        for (int i = 0; i < sources.size(); i++) 
            sources.get(i).updatePosition();
    }
    
    
    public void mouseClicked( MouseEvent e ) {}
    public void mouseMoved( MouseEvent e ) {}
    public void mouseEntered( MouseEvent e) {}
    public void mouseExited( MouseEvent e) {}
    public void mouseReleased( MouseEvent e) {}
    public void mouseDragged( MouseEvent e) {}
    
    
    public static void main(String[] args) {
        
        WaveInterferenceGraphics wg = new WaveInterferenceGraphics();
        wg.addMouseListener(wg);
        wg.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
         });  

        wg.setColors();
        
        wg.polygonLayout( 5, (int) 30 );
        
        //wg.linearLayout( 10, 0, 300, 10, 0 );
        //wg.linearLayout( 10, 300, 0, 0, 10 );
        
        //wg.linearLayout(2, 300, 300, (int) (1.25*wg.waveLength), 0);
        //wg.linearLayout(2, 300, 500, (int) (2.8*wg.waveLength), 0);
        
        wg.initWindow(); 
        
        while( true ) {
            wg.updateWaveCentres();
            wg.timeUntilSteadyStateIsReached -= wg.deltaT;
            
            if ( wg.timeUntilSteadyStateIsReached <= 0 ) 
                wg.inSteadyState = true;
            
            sleep(10);
            wg.repaint();    
        }
    }   
}
