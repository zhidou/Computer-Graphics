/**
 * ellipse.java - a solid ellipse
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
 * A solid ellipse.
 * 
 * @author Zhi Dou
 * @since Fall 2015
 */
public class Ellipse extends Circular implements Displayable {
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
  private final double scaling;
  private boolean DOtexture;
  private GLU glu= new GLU();
  private Texture skin;
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
  public Ellipse(final double radius, final double scaling,
      final GLUT glut, boolean DOtexture) {
    super(radius, glut);
    this.scaling = scaling;
    this.DOtexture=DOtexture;
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
    gl.glScaled(1, 1, scaling);
    if(DOtexture)
    {
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
    }
    else
    		this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);
    gl.glPopMatrix();
    gl.glEndList();
  }
}
