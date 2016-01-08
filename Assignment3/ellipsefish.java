
import javax.media.opengl.*;
import javax.swing.plaf.SliderUI;
import javax.swing.text.FlowView.FlowStrategy;

import com.jogamp.opengl.FloatUtil;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.org.apache.bcel.internal.generic.DREM;

import java.awt.event.FocusListener;
import java.util.*;

public class ellipsefish extends fish {

	public ellipsefish(float x, float y, float z, int id) {

		R = 0.4f;
		alpha = 1;
		position[0] = x;
		position[1] = y;
		position[2] = z;
		speed = 0.02f;
		Currentdirectionvector[0] = -1;
		Currentdirectionvector[1] = 0;
		Currentdirectionvector[2] = 0;
		RotationAngle[0] = 0;
		RotationAngle[1] = 0;
		RotationAngle[2] = 0;
		Targetdirectionvector[0] = -1f;
		Targetdirectionvector[1] = 0;
		Targetdirectionvector[2] = 0;
		ID = id;
		scale = 0.2f;
		Zlimit = 75;

	}

	public void init(GL2 gl) {
		// generate list for every part
		fish_object = gl.glGenLists(11);
		fishtail = gl.glGenLists(1);
		fins = gl.glGenLists(1);
		eyes = gl.glGenLists(1);

		/// create the fishtail list
		gl.glNewList(fishtail, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glTranslated(1.5, 0, 0);
			gl.glScaled(1, 1, 0.5);
			float[] shear1 = { 1, 0, 0, 0, 0.5f, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
			// multiply in OpenGL is different....it is the line vector multiply
			// with matrix
			gl.glMultMatrixf(shear1, 0);
			gl.glRotated(-90, 1, 0, 0);
			glut.glutSolidCone(0.5, 1.5, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();// end of fishtail list

		/// create a list for fins
		gl.glNewList(fins, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glScaled(1, 0.5, 1);
			glut.glutSolidCone(0.5, 1, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();// end of fins list

		/// create a list for eyes
		gl.glNewList(eyes, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glPushAttrib(gl.GL_CURRENT_BIT);
			gl.glColor3f(1f, 1f, 1f);
			gl.glTranslated(-1, 0.45, 0.3);
			gl.glRotated(-5, 0, 0, 1);
			gl.glRotated(-65, 0, 1, 0);
			glut.glutSolidSphere(0.3, DEFAULT_SLICES, DEFAULT_STACKS);
			gl.glPopAttrib();
			// create pulpil
			gl.glPushMatrix();
				gl.glPushAttrib(gl.GL_CURRENT_BIT);
				gl.glColor3f(0f, 0f, 0f);
				gl.glTranslated(0, 0, 0.25);
				glut.glutSolidSphere(0.1, DEFAULT_SLICES, DEFAULT_STACKS);
				gl.glPopAttrib();
			gl.glPopMatrix();
		gl.glPopMatrix();
		gl.glEndList();// end of eyes' list

		// create the list of whole fish we have 4 poses, 
		//and we call them from 0 to 3 and then down from 3 to 0
		// each pose we use 10 frame to display
		for (int i = 0; i < 4; i++) {
			gl.glNewList(fish_object + i, GL2.GL_COMPILE);
			
			// create the body
			gl.glPushMatrix();
				gl.glScaled(1.5, 1, 0.8);
				glut.glutSolidSphere(1, DEFAULT_SLICES, DEFAULT_STACKS);
			gl.glPopMatrix();
		
			// call the tail list
			gl.glPushMatrix();
				gl.glTranslated(1.5, 0, 0);
				// the tail is from -45 to 45 each list change 30 degree
				gl.glRotated((-45 + 30 * i), 0, 1, 0);
				gl.glTranslated(-1.5, 0, 0);
				gl.glCallList(fishtail);
				gl.glPushMatrix();
					gl.glScaled(1, -1, 1);
					gl.glCallList(fishtail);
				gl.glPopMatrix();
			gl.glPopMatrix();
			
			// call the fins list
			gl.glPushMatrix();
				gl.glPushMatrix();
					gl.glTranslated(0, 0, 0.6);
			// the fins rotate from -30 to 30. each time changes 20 degree
					gl.glRotated(-30 + 20 * i, 1, 0, 0);
					gl.glCallList(fins);
				gl.glPopMatrix();
				gl.glPushMatrix();
					gl.glTranslated(0, 0, -0.6);
					gl.glScaled(1, 1, -1);
					gl.glRotated(-30 + 20 * i, 1, 0, 0);
					gl.glCallList(fins);
				gl.glPopMatrix();
			gl.glPopMatrix();
			
			// call the eys list
			gl.glCallList(eyes);
			gl.glPushMatrix();
				gl.glScaled(1, 1, -1);
				gl.glCallList(eyes);
			gl.glPopMatrix();

			gl.glEndList();// end of fish list
		}
	}

}
