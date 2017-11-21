package de.mpc.pqi.view.diagram;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
    
/**
 * Given the five-number summary of a set of data, this
 * class can draw the corresponding boxplot.
 * 
 * @author   D. Searls 
 * @version  Dec 2002
 */
public class BoxPlot extends JComponent
{
    private static final int INSET = 10;      // Vertical inset between box
                                              // and edge of canvas.

    private double minX, maxX;                // Left and right borders
    private double min, q1, median, q3, max;  // Five-number summary
    private double actual;                    // Actual population value
    private boolean dataIsValid;              // Indicates whether this BoxPlot
                                              // object has valid data
                                              
    private Color fillColor;                  // Fill color for box
    private Dimension myDimension             // Dimension of BoxPlot component
                    = new Dimension();
    
    /**
     * Default constructor for objects of class BoxPlot
     */
    public BoxPlot()
    {
        fillColor = Color.CYAN;
        minX = 0.0;
        min = 1.0;
        q1 = 2.0;
        median = 3.0;
        q3 = 4.0;
        max = 5.0;
        maxX = 6.0;
        actual = 3.0;
        dataIsValid = true;
    }
    
    /**
     * Initialization constructor for objects of class BoxPlot. The
     * minX and maxX values correspond to the left and right edge of
     * the canvas region. The remaining values form the five number
     * summary. The value of minX should be less than or equal to
     * min and the value of maxX should be greater than or equal to
     * max.
     * 
     * @param minX   the x-coordinate of the left edge of the canvas
     * @param min    the minimum value in the data set
     * @param q1     the first quartile
     * @param median the median
     * @param q3     the third quartile
     * @param min    the maximum value in the data set
     * @param maxX   the x-coordinate of the right edge of the canvas
     * @param actual the actual population value
     */
    public BoxPlot(double minX, double min, double q1,
                   double median, double q3, double max,
                   double maxX, double actual)
    {
        fillColor = Color.CYAN;
        this.minX = minX;
        this.min = min;
        this.q1 = q1;
        this.median = median;
        this.q3 = q3;
        this.max = max;
        this.maxX = maxX;
        this.actual = actual;
        dataIsValid = true;
    }
    
    /**
     * Set the values used to display the boxplot. The minX and maxX
     * values correspond to the left and right edge of the canvas
     * region. The remaining values form the five number summary.
     * The value of minX should be less than or equal to min and the
     * value of maxX should be greater than or equal to max.
     * 
     * @param minX   the x-coordinate of the left edge of the canvas
     * @param min    the minimum value in the data set
     * @param q1     the first quartile
     * @param median the median
     * @param q3     the third quartile
     * @param min    the maximum value in the data set
     * @param maxX   the x-coordinate of the right edge of the canvas
     * @param actual the actual population value
     */
    public void setSummary(double minX, double min, double q1,
                           double median, double q3, double max,
                           double maxX, double actual)
    {
        this.minX = minX;
        this.min = min;
        this.q1 = q1;
        this.median = median;
        this.q3 = q3;
        this.max = max;
        this.maxX = maxX;
        this.actual = actual;
        dataIsValid = true;
    }
    
    /**
     * Reset dataIsValid flag to false.
     */
    public void reset()
    {
        dataIsValid = false;
    }
    
    /**
     * Set the fill color to the specified color. The box will be
     * filled with this color.
     * 
     * @param newColor the specified color
     */
    public void setFillColor(Color newColor)
    {
        fillColor = newColor;
    }
    
    /**
     * Resizes this BoxPlot so that it has the specified width and height.
     * 
     * @param width  the new width in pixels
     * @param height the new height in pixles
     */
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        myDimension.setSize(width, height);
    }
    
    /**
     * Return the current size of this BoxPlot.
     *
     * @return currentSize the current size
     */
    public Dimension getMinimumSize()
    {
        return myDimension;
    }
    
    /**
     * Return the current size of this BoxPlot.
     *
     * @return currentSize the current size
     */
    public Dimension getMaximumSize()
    {
        return myDimension;
    }

    /**
     * Return the current size of this BoxPlot.
     *
     * @return currentSize the current size
     */
    public Dimension getPreferredSize()
    {
        return myDimension;
    }
    
    /**
     * Paint this boxplot component.
     * 
     * @param gr the Graphics object for this canvas
     */
    protected void paintComponent(Graphics gr)
    {
        int width, height;  // Width and height of boxplot itself
        int x0;             // Location of min
        int x1;             // Location of q1
        int x2;             // Location of q2 (median)
        int x3;             // Location of q3
        int x4;             // Location of max
        int xActual;        // Location of actual population value
        int midY;           // Vertical middle of canvas area

        gr.clearRect(0, 0, getWidth(), getHeight());
        if (dataIsValid) {
            gr.setColor(Color.BLACK);
            height = getHeight() - 2*INSET;
            x0 = scaleX(min);
            x1 = scaleX(q1);
            x2 = scaleX(median);
            x3 = scaleX(q3);
            x4 = scaleX(max);
            xActual = scaleX(actual);
            midY = INSET+height/2;
            
            //gr.drawRect(0, 0, getWidth()-1, getHeight()-1);  // Double border
            //gr.drawRect(1, 1, getWidth()-3, getHeight()-3);
            
            gr.drawLine(x0, midY, x1, midY);         // Left "Whisker"
            gr.setColor(fillColor);
            gr.fillRect(x1,INSET,x3-x1,height);      // Interquartile range
            gr.setColor(Color.BLACK);
            gr.drawRect(x1,INSET,x3-x1,height);      // Dark border around IQR
            gr.drawLine(x2,INSET,x2,height+INSET);   // Location of Median
            gr.drawLine(x3,midY,x4,midY);            // Right "Whisker"
            
            // Draw vertical line at actual population value
            gr.drawLine(xActual, 0, xActual, getHeight()-1);
            gr.setColor(Color.WHITE);
            gr.drawLine(xActual, INSET+1, xActual, height+INSET-1);
        }
    }
    
    //--------------------------------------------------------
    // Scale the specfied x value to a canvas coordinate.
    //
    // param x the specified x value
    //--------------------------------------------------------
    private int scaleX(double x)
    {
        double A = (getWidth() - 1)/(maxX - minX);
        double B = -A*minX;
        double newX = A*x + B;
        return (int)(newX + 0.5);
    }
}