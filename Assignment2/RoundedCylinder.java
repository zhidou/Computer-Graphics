/**
 * RoundedCylinder.java - a solid cylinder with a rounded top
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
 * A solid cylinder with a rounded top.
 * 
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public class RoundedCylinder extends Circular implements Displayable {
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
   * which comprise this cylinder.
   */
  private int callListHandle;
  /** The height of this cylinder. */
  private final double height;
  private boolean DOtexture;
  private GLU glu= new GLU();
  private Texture skin;

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
  public RoundedCylinder(final double radius, final double height,
      final GLUT glut, boolean DOtexture) {
    super(radius, glut);
    this.height = height;
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
    		
    		GLUquadric cylinder = glu.gluNewQuadric();    		
    		glu.gluQuadricDrawStyle(cylinder, glu.GLU_FILL);
    		glu.gluQuadricTexture(cylinder, true);
    		glu.gluQuadricNormals(cylinder, glu.GLU_SMOOTH);
    		gl.glEnable(gl.GL_TEXTURE_2D);
    		gl.glTexEnvf(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_MODULATE);
    		skin.enable(gl);
    		skin.bind(gl);
    		glu.gluCylinder(cylinder, this.radius(),this.radius(),this.height, DEFAULT_SLICES, DEFAULT_STACKS);
    		skin.disable(gl);
    		gl.glDisable(gl.GL_TEXTURE_2D);
    }
    else
    		this.glut().glutSolidCylinder(this.radius(), this.height, DEFAULT_SLICES,
    				DEFAULT_STACKS);

    gl.glPushMatrix();
    gl.glTranslated(0, 0, this.height);
    if (DOtexture)
    {
    		GLUquadric sphere = glu.gluNewQuadric();    		
		glu.gluQuadricDrawStyle(sphere, glu.GLU_FILL);
		glu.gluQuadricTexture(sphere, true);
		glu.gluQuadricNormals(sphere, glu.GLU_SMOOTH);
		gl.glEnable(gl.GL_TEXTURE_2D);
		gl.glTexEnvf(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_MODULATE);
		skin.enable(gl);
		skin.bind(gl);
		glu.gluSphere(sphere, this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);
		skin.disable(gl);
		gl.glDisable(gl.GL_TEXTURE_2D);
    }
    else
    		this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);
    gl.glPopMatrix();

    gl.glEndList();
  }
}
