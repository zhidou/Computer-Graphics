/**
 * ellipse.java - a solid ellipse
 */


import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * A solid ellipse.
 * 
 * @author Zhi Dou
 * @since Fall 2015
 */
public class sphere extends Circular implements Displayable {
  /**
   * The default number of slices to use when drawing the ellipse.*/
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the ellipse.*/
  public static final int DEFAULT_STACKS = 28;

  /**
   * The OpenGL handle to the display list which contains all the components
   * which comprise this ellipse.
   */
  private int callListHandle;
  /** The height of this cylinder. */

  /**
   * Instantiates this object with the specified radius ellipse
   * and the GLUT object to use for drawing it
   * 
   * @param radius
   *          The radius of this sphere.
   * @param scaling
   * 		  The times of this sphere to enlarge
   * @param glut
   *          The OpenGL utility toolkit object to use to draw the ellipse.
   */
  public sphere(final double radius,
      final GLUT glut) {
    super(radius, glut);
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
    gl.glPushMatrix();
    this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);
    gl.glPopMatrix();
    gl.glEndList();
  }
}
