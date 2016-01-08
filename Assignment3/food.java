
import javax.media.opengl.*;
import javax.swing.plaf.SliderUI;
import javax.swing.text.FlowView.FlowStrategy;

import org.omg.CORBA.SystemException;

import com.jogamp.opengl.FloatUtil;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.org.apache.bcel.internal.generic.DREM;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;

import java.awt.Color;
import java.awt.event.FocusListener;
import java.util.*;

public class food {

	protected GLUT glut = new GLUT();
	// current position of the central of the food
	protected float[] position = new float[3];
	// speed of falling
	protected float speed = 0.05f;
	// R is the radius of bounding sphere of this food
	protected float R = 0.2f;
	// how big is this food
	protected float scale = 0.2f;
	// to decide the weight of the food to fish
	protected float alpha = 2;
	// color of the food
	protected float r = 1;
	protected float g = 1;
	protected float b = 0;
	// ID, just used to debug
	protected int ID;
	/**
	 * The default number of slices to use when drawing the ellipse.
	 */
	protected static final int DEFAULT_SLICES = 36;
	/**
	 * The default number of stacks to use when drawing the ellipse.
	 */
	protected static final int DEFAULT_STACKS = 28;

	public food(float _x, float _y, float _z) {
		position[0] = _x;
		position[1] = _y;
		position[2] = _z;
	}

	public void init(GL2 gl) {
	}

	public void update(GL gl) {
		// the food fall to the ground
		if (position[1] > -5 + R)
			position[1] -= speed;
		if (position[1] < -5 + R)
			position[1] = -5 + R;
	}

	public void draw(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslated(position[0], position[1], position[2]);
		gl.glScaled(scale, scale, scale);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glColor3f(r, g, b);
		glut.glutSolidSphere(1, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	public void setcolor(float _r, float _g, float _b) {
		r = _r;
		g = _g;
		b = _b;
	}
}
