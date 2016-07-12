
package waveinterferencegraphics;
import java.awt.Point;

public class WaveSource {
    double A; //amplitude
    double k; //wave number
    double c; //wave speed
    
    //Waves then have the form z = A sin(k(r - ct)), where z is the vertical displacement of the wave at time t and distance r from the centre
    //From physics, the following hold: 2pi/k = wavelength, f = c/wavelength
    
    double xCentre, yCentre;
    double xCentreSpeed, yCentreSpeed; //0 if the source itself is fixed
    double range; //At any time t, range = ct, which is the distance the wave has traveled from the centre since time 0. 
    double deltaT;
    double deltaRange; //How much range changes in deltaT seconds.
    double decayRate;
    
    public WaveSource(double amplitude, double waveNumber, double waveSpeed, double deltaT, double decayRate, double xc, double yc, double vx, double vy) {
        this.A = amplitude;
        this.k = waveNumber;
        this.xCentre = xc;
        this.yCentre = yc;
        this.xCentreSpeed = vx;
        this.yCentreSpeed = vy;
        this.c = waveSpeed;
        this.range = 0;
        this.deltaT = deltaT;
        this.deltaRange = c*deltaT;
        this.decayRate = decayRate;
    }
    
    public void updatePosition() {
        this.xCentre += xCentreSpeed; 
        this.yCentre += yCentreSpeed; 
        this.range += this.deltaRange;
    }
    
    
    //Used if the wave has not yet covered the entire drawing screen
    public double getDisplacement( int x, int y ) {
        
        double r = Math.sqrt(Math.pow(x-xCentre,2) + Math.pow(y-yCentre,2));
        
        if ( r <= this.range ) //if the leading edge of the wave has reached this radius
            return A * Math.sin(k*(r - range)) * Math.exp(-r*decayRate);
        
        else
            return 0;
    }
    
    
    //Used if the wave has already covered the entire drawing screen, so there is no need to check whether r <= range
    public double getDisplacementSteadyState( int x, int y ) {
        
        double r = Math.sqrt(Math.pow(x-xCentre,2) + Math.pow(y-yCentre,2));
        
        return A * Math.sin( k * (r - range) ) * Math.exp(-r*decayRate);           
    }
    
}
