
import javax.media.opengl.*;
import javax.swing.plaf.SliderUI;
import javax.swing.text.FlowView.FlowStrategy;

import org.omg.CORBA.SystemException;

import com.jogamp.opengl.FloatUtil;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.org.apache.bcel.internal.generic.DREM;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;

import java.awt.event.FocusListener;
import java.util.*;

public class fish {
	// fishID for debug use
	protected int ID;
	// the list of whole fish
	protected int fish_object;
	// the list of tail
	protected int fishtail;
	// the list of fins
	protected int fins;
	// this list of eyes
	protected int eyes;
	protected GLUT glut = new GLUT();
	/** @param position current positon of the central of the fish */
	protected float[] position = new float[3];
	// to store the last position of fish for the purpose of going back
	protected float[] lastposition = new float[3];
	/** @param Currentdirectionvector is current direction of fish looking at */
	protected float[] Currentdirectionvector = new float[3];
	/** @param Targetdirectionvector is the Target direction that current direction turns to */
 	protected float[] Targetdirectionvector = new float[3];
	/** @param RotationAngle are angles of current direction */
	protected float[] RotationAngle = new float[3];
	// the target angle, the fish wants to turn to
	protected float[] TargetAngle = new float[3];
	// the Target angle that the fish will rotate to
	protected float[] Increase = new float[3];
	// distance caused by each moving
	protected float speed;
	// the speed fish turning
	protected float turningspeed = 5;
	// to decide which list we should draw
	protected int timekeeper = 0;
	// to change the direction of the number of we list
	protected int timevariable = 1;
	/** @param R for bounding sphere R is the radius */
	protected float R;
	// the scale of a fish
	protected float scale;
	/** @param alpha
	 is used as the weight when we compute interact effect between creatures
	 and also alpha is used as a sign to tell what the creature is, prey or predator */
	protected float alpha;
	// to record the status of collision
	// because when the creature collide with something, the prime thing to it is avoid collison
	// to avoid collision, we make the creature go back to its last position where it does not 
	// collide with any one, and then we make it turn some degree. So when the status collision
	// is true, the creature do nothing about avoid collision.
	protected boolean collision = false;
	/** @param Zlimit is Z-axis limit is to make sure the fish do not rolling */
	protected float Zlimit;
	// these parameters are for color
	protected float r = 0;
	protected float g = 0;
	protected float b = 0;
	/**
	 * The default number of slices to use when drawing the ellipse.
	 */
	protected static final int DEFAULT_SLICES = 36;
	/**
	 * The default number of stacks to use when drawing the ellipse.
	 */
	protected static final int DEFAULT_STACKS = 28;

	public fish() {
	}

	public void init(GL2 gl) {
	}

	public void update(GL gl, int refresh) {
		// set the time variable. we constrain time keeper in the scope from 0 to
		// 35, so then from 0 to 35, time keeper divide by 10, we will get 0 to 3
		// and when time keeper reach 35, we change the time variable, make time
		// keeper down from 35 to 0 and we could get 3 to 0
		if (timekeeper == 0)
			timevariable = 1;
		else if (timekeeper == 35)
			timevariable = -1;
		timekeeper += timevariable;
		
		// our movement is caused by the change current direction to target direction
		CurtoTar();
		// this is to limit Rotation angle and Target angle not too larger then 360
		// we use this and some limitation in Vivarium class to let the fish always 
		// choose the small ange to turning
		if (Math.abs(RotationAngle[1]) > 360 && Math.abs(TargetAngle[1]) > 360) {
			RotationAngle[1] %= 360;
			TargetAngle[1] %= 360;
		}

	}

	public void draw(GL2 gl) {
		gl.glPushMatrix();
			gl.glTranslated(position[0], position[1], position[2]);
			gl.glRotated(RotationAngle[1], 0, 1, 0);
			gl.glRotated(RotationAngle[2], 0, 0, 1);
			gl.glScaled(scale, scale, scale);
			gl.glPushAttrib(GL2.GL_CURRENT_BIT);
			gl.glColor3f(r, g, b);
			gl.glCallList(fish_object + timekeeper / 10);
			gl.glPopAttrib();
		gl.glPopMatrix();
	}

	// direction vector rotation, this method it to rotate direction vector
	protected float[] DVrotation(float[] directionvector, float[] RotationAngle) {
		float Yaxis = (float) Math.toRadians(RotationAngle[1]);
		float Zaxis = (float) Math.toRadians(RotationAngle[2]);
		float[][] RotationMatrix = {
				{ (float) Math.cos(Yaxis), 0, (float) Math.sin(Yaxis), 0, 1, 0, -(float) Math.sin(Yaxis), 0,
						(float) Math.cos(Yaxis) },
				{ (float) Math.cos(Zaxis), (float) -Math.sin(Zaxis), 0, (float) Math.sin(Zaxis),
						(float) Math.cos(Zaxis), 0, 0, 0, 1 } };

		for (int i = 1; i >= 0; i--)
			directionvector = Matrixmultiply(directionvector, RotationMatrix[i]);
		return directionvector;
	}
	
	protected float[] Matrixmultiply(float[] directionvector, float[] matrix) {
		float[] M = new float[3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				// this is to eliminate errors
				if (Math.abs(matrix[i * 3 + j]) < Math.pow(10, -7))
					matrix[i * 3 + j] = 0;
				M[i] += matrix[i * 3 + j] * directionvector[j];
			}
		return M;
	}

	// normalize vector
	private float[] normalize(float[] v) {
		float magnitude = 0;
		for (int i = 0; i < 3; i++)
			magnitude += v[i] * v[i];
		magnitude = (float) Math.sqrt(magnitude);
		if (magnitude > Math.pow(10, -4))
			for (int i = 0; i < 3; i++)
				v[i] = v[i] / magnitude;
		return v;
	}

	// this is the method to change the current direction to target direction
	public void changedirection() {
		// compute the direction vector of this time. We compute it always from the origin direction
		// because the we draw the fish is based on the origin direction
		float[] directionvector = { -1, 0, 0 };
		directionvector = DVrotation(directionvector, RotationAngle);
		// update the current direction
		Currentdirectionvector[0] = directionvector[0];
		Currentdirectionvector[1] = directionvector[1];
		Currentdirectionvector[2] = directionvector[2];
	}

	public void CurtoTar() {
		// the degree per frame Rotation angle add
		float changedegree;
		
		// per frame we just change the rotation for a certain degree, that guarantee the fish turning 
		// more smoothly
		for (int i = 1; i < 3; i++) {
			// this is to decide per frame Rotation angle will add a positive angle or negative 
			changedegree = Math.signum(TargetAngle[i] - RotationAngle[i]) * turningspeed;
			if (Math.abs(TargetAngle[i] - RotationAngle[i]) > Math.abs(changedegree))
				RotationAngle[i] += changedegree;
			else
				RotationAngle[i] = TargetAngle[i];
		}
	
		
		
		// if Rotation angle reach the Target angle, we reset our Target direction vector to unit vector
		// this is to eliminate the effect of just one direction
		if (RotationAngle[1] == TargetAngle[1] && RotationAngle[2] == TargetAngle[2]) {
			normalize(Targetdirectionvector);
			if (collision == true)
				collision = false;
		}
		
		// when we finish set the angle, we begin to change the direction
		changedirection();
		
		// this is to ensure when the fish turning, they are not just turning. 
		// when the turning angle is bigger than same degree we could claim that the fish cannot collide 
		// with the thing it collide before. At that time, we allow it moving
		if (Math.abs(TargetAngle[1] - RotationAngle[1]) <= Increase[1] / 2
				&& Math.abs(TargetAngle[2] - RotationAngle[2]) <= Increase[2] / 2) {
			for (int i = 0; i < 3; i++) {
				lastposition[i] = position[i];
				position[i] += Currentdirectionvector[i] * speed;
			}
		}
	}

	public void setcolor(float _r, float _g, float _b) {
		r = _r;
		g = _g;
		b = _b;
	}

	// reset the parameter to original value
	public void ReSet(){
		for (int i = 0; i < 3; i++){
			TargetAngle[i] = RotationAngle[i];
			Targetdirectionvector[i] = Currentdirectionvector[i];
			speed = 0.02f;
			Increase[i] = 0;
			turningspeed = 5;
		}
	}
}
