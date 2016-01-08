/**
 * Head.java - a solid sphere with a ellipse and a cylinder
 */


import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * A solid sphere with a ellipse and a cylinder
 * 
 * @author Zhi Dou
 * @since Fall 2015
 */
public class Head extends Circular implements Displayable {
  /**
   * The default number of slices to use when drawing the ellipse and the
   * sphere and cylinder.
   */
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the ellipse and the
   * sphere and cylinder.
   */
  public static final int DEFAULT_STACKS = 28;

  /**
   * The OpenGL handle to the display list which contains all the components
   * which comprise this graphics.
   */
  private int callListHandle;
  /** The scaling of ellipse. */
  private final double scaling;
  private final double radius2;
  private final double neckradius;
  private final double neckhight;
  /**
   * Instantiates this object with the specified radius and scaling of the
   * ellipse, and the GLUT object to use for drawing the ellipse and the
   * sphere at the top.
   * 
   * @param radius1
   *          The radius of this ellipse.
   * @param radius2
   * 		  The radius of this sphere.
   * @param scaling
   *          The scaling of this ellipse.
   * @param glut
   *          The OpenGL utility toolkit object to use to draw the ellipse and
   *          the sphere.
   */
  public Head(final double radius1, final double radius2, final double scaling,
     final double neckradius, final double neckhight, final GLUT glut) {
    super(radius1, glut);
    this.radius2 = radius2;
    this.scaling = scaling;
    this.neckhight=neckhight;
    this.neckradius=neckradius;
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

    gl.glNewList(this.callListHandle, GL2.GL_COMPILE);
    
    this.glut().glutSolidCylinder(neckradius, neckhight, 
    		DEFAULT_SLICES, DEFAULT_STACKS);
    gl.glPushMatrix();
    gl.glTranslated(0, 0, 0.7*(neckhight+this.radius()));
    gl.glScaled(this.scaling, 1, 1);
    this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS );
    gl.glPopMatrix();
    gl.glPushMatrix();
    gl.glTranslated(0, 0, this.radius()*0.8+neckhight);
    this.glut().glutSolidSphere(this.radius2, DEFAULT_SLICES, DEFAULT_STACKS);
    gl.glPopMatrix();

    gl.glEndList();
  }
}
