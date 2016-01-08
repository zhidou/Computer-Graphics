/**
 * Body.java - a solid ellipse
 */


import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * A solid ellipse
 * 
 * @author Zhi Dou
 * @since Fall 2015
 */
public class Body extends Circular implements Displayable {
  /**
   * The default number of slices to use when drawing the ellipse. 
   */
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the ellipse.
   */
  public static final int DEFAULT_STACKS = 28;

  /**
   * The OpenGL handle to the display list which contains all the components
   * which comprise this ellipse.
   */
  private int callListHandle;
  /** The scaling of ellipse. */
  private final double scaling;
  private final double angle;
  private GLU glu= new GLU();
  private Texture skin;
  /**
   * Instantiates this object with the specified radius and scaling of the
   * ellipse, and the GLUT object to use for drawing the ellipse.
   * 
   * @param radius
   *          The radius of this ellipse.
   * @param scaling
   *          The scaling of this ellipse.
   * @param glut
   *          The OpenGL utility toolkit object to use to draw the ellipse.
   */
  public Body(final double radius, final double angle, final double scaling,
      final GLUT glut) {
    super(radius, glut);
    this.scaling = scaling;
    this.angle=angle;
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
    //	gl.glRotated(angle, 1, 0, 0);
    	gl.glScaled (1, 1, this.scaling);
    	try
    	{   			
    		skin = TextureIO.newTexture(new File("skin2.jpg"),true);

    	}
    	catch(IOException e)
    	{
    		javax.swing.JOptionPane.showMessageDialog(null, e);
    	}
    	
    	GLUquadric ecllipse = glu.gluNewQuadric();    		
    	glu.gluQuadricDrawStyle(ecllipse, glu.GLU_FILL);
    	glu.gluQuadricTexture(ecllipse, true);
    	glu.gluQuadricNormals(ecllipse, glu.GLU_SMOOTH);
    	gl.glEnable(gl.GL_TEXTURE_2D);
    	gl.glTexEnvf(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_MODULATE);
    	skin.enable(gl);
    	skin.bind(gl);
    	glu.gluSphere(ecllipse, this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);
    	skin.disable(gl);
    	gl.glDisable(gl.GL_TEXTURE_2D);
//    this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS );
    gl.glPopMatrix();
    gl.glEndList();
  }
}
