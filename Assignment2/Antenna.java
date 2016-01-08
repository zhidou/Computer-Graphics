/**
 * Antenna.java - a series rounded cylinder
 */


import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * A series rounded cylinder.
 * 
 * @author Zhi Dou
 * @since Fall 2015
 */
public class Antenna extends Circular implements Displayable {
  /**
   * The default number of slices to use when drawing the cylinder and the
   * sphere.
   */
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the cylinder and the
   * sphere.
   */
  public static final int DEFAULT_STACKS = 28;

  /**
   * The OpenGL handle to the display list which contains all the components
   * which comprise these cylinders.
   */
  private int callListHandle;
  /** The height of this cylinder. */
  private final double height;
  private final int BN;
  private final double basepartangle;
  public double Z = 0;
  public double Y = 0;
  private double R = 0;

  /**
   * Instantiates this object with the specified radius and height of the
   * cylinder, and the GLUT object to use for drawing the cylinder and the
   * sphere at the top.
   * 
   * @param radius
   *          The radius of this cylinder.
   * @param height
   *          The height of this cylinder.
   * @param glut
   *          The OpenGL utility toolkit object to use to draw the cylinder and
   *          the sphere at the top.
   */
  public Antenna(final double radius, final double height, final int BN, 
		  final double basepartangle, final GLUT glut) {
    super(radius, glut);
    this.height = height;
    this.BN = BN;
    this.basepartangle=basepartangle;
    for (int i=0; i<this.BN;i++)
    {
    		Y-=Math.sin(Math.toRadians(R))*this.height;
	    Z+=Math.cos(Math.toRadians(R))*this.height;
	    R+=basepartangle;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @param gl
   *          {@inheritDoc}
   * @see edu.bu.cs.cs480.Displayable#draw(javax.media.opengl.GL)
   */
  @Override
  public void draw(final GL2 gl) {
    gl.glCallList(this.callListHandle);
  }

  /**
   * {@inheritDoc}
   * 
   * @param gl
   *          {@inheritDoc}
   * @see edu.bu.cs.cs480.Displayable#initialize(javax.media.opengl.GL)
   */
  @Override
  public void initialize(final GL2 gl) {

    this.callListHandle = gl.glGenLists(1);
    //this.set(0, 0);
    gl.glNewList(this.callListHandle, GL2.GL_COMPILE);
    double z=0;
    double y=0;
    double r=0;
    for (int i=0;i<this.BN;i++)
    {
    		gl.glPushMatrix();
    		gl.glTranslated(0, y, z);
    		gl.glRotated(r, 1, 0, 0);// This first parameter is in degrees
    		this.glut().glutSolidCylinder(this.radius(), this.height, DEFAULT_SLICES,
    		        DEFAULT_STACKS);
    		gl.glPushMatrix();
    		gl.glTranslated(0, 0, this.height);
    		this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);
    	    gl.glPopMatrix();
    	    gl.glPopMatrix();
    	    y-=Math.sin(Math.toRadians(r))*this.height;
    	    z+=Math.cos(Math.toRadians(r))*this.height;
    	    r+=basepartangle;
    }
  //  this.set(Y, Z);
    gl.glEndList();
  }
/*  private void set(double Y, double Z)
  {
	  this.Y=Y;this.Z=Z;
  }*/
/*  public double[] getposition()
  {
	  double[] p={this.Y, this.Z};
	  return p;
  }*/
}
