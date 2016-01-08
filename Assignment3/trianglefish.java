
import javax.media.opengl.*;
import javax.swing.plaf.SliderUI;
import javax.swing.text.FlowView.FlowStrategy;

import com.jogamp.opengl.FloatUtil;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.org.apache.bcel.internal.generic.DREM;

import java.awt.event.FocusListener;
import java.util.*;

public class trianglefish extends fish {
	protected int body;

	// current positon of the central of the fish

	public trianglefish(float x, float y, float z, int id) {

		R = 0.4f;
		alpha = 1;
		position[0] = x;
		position[1] = y;
		position[2] = z;
		speed = 0.02f;
		Currentdirectionvector[0] = 1;
		Currentdirectionvector[1] = 0;
		Currentdirectionvector[2] = 0;
		RotationAngle[0] = 0;
		RotationAngle[1] = 180;
		RotationAngle[2] = 0;
		ID = id;
		scale = 0.35f;
		Zlimit = 75;
	}

	public void init(GL2 gl) {
		// generate list for every part
		body = gl.glGenLists(1);
		fish_object = gl.glGenLists(11);
		fishtail = gl.glGenLists(1);
		fins = gl.glGenLists(1);
		eyes = gl.glGenLists(1);

		// create the body list
		gl.glNewList(body, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glScaled(1.2, 0.8, 0.3);
			gl.glRotatef(90, 0, 1, 0);
			glut.glutSolidCone(1, 0.866, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();// end of body

		// create the fishtail list
		gl.glNewList(fishtail, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glTranslatef(1, 0, 0);
			gl.glScaled(1, 1, 0.5);
			gl.glRotated(-90, 0, 1, 0);
			glut.glutSolidCone(0.5, 1, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();// end of fishtail list

		/// create a list for fins
		gl.glNewList(fins, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glScaled(1.34, 1, 0.25);
			gl.glRotated(-90, 1, 0, 0);
			glut.glutSolidCone(0.5, 0.9, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();// end of fins list

		/// create a list for eyes
		gl.glNewList(eyes, GL2.GL_COMPILE);
		gl.glPushMatrix();

			gl.glPushAttrib(gl.GL_CURRENT_BIT);
			gl.glColor3f(1f, 1f, 1f);
			gl.glTranslated(-0.5, 0.42, 0.12);
			gl.glRotated(-5, 0, 0, 1);
			gl.glRotated(-65, 0, 1, 0);
			glut.glutSolidSphere(0.1, DEFAULT_SLICES, DEFAULT_STACKS);
			gl.glPopAttrib();
			// create the pulpil
			gl.glPushMatrix();
				gl.glPushAttrib(gl.GL_CURRENT_BIT);
				gl.glColor3f(0f, 0f, 0f);
				gl.glTranslated(0, 0, 0.055);
				glut.glutSolidSphere(0.05, DEFAULT_SLICES, DEFAULT_STACKS);
				gl.glPopAttrib();
			gl.glPopMatrix();

		gl.glPopMatrix();
		gl.glEndList();// end of eyes' list

		// create the list of whole fish 
		//and we call them from 0 to 3 and then down from 3 to 0
		// each pose we use 10 frame to display
		for (int i = 0; i < 4; i++) {
			gl.glNewList(fish_object + i, GL2.GL_COMPILE);

			// create the body
			gl.glPushMatrix();
				gl.glCallList(body);
				gl.glPushMatrix();
					gl.glScalef(-1, 1, 1);
					gl.glCallList(body);
				gl.glPopMatrix();
			gl.glPopMatrix();

			// call the tail list
			gl.glPushMatrix();
				gl.glTranslatef(0.5f, 0, 0);
				gl.glRotated((-15 + 10 * i), 0, 1, 0);
				gl.glCallList(fishtail);
			gl.glPopMatrix();

			// call the fins list
			gl.glPushMatrix();
				gl.glPushMatrix();
					gl.glTranslated(0.45, 0.35, 0);
					gl.glRotatef(-37, 0, 0, 1);
					// the fins rotate from -30 to 30. each time changes 20 degree
					gl.glRotated(-15 + 10 * i, 1, 0, 0);
					gl.glCallList(fins);
				gl.glPopMatrix();
				gl.glPushMatrix();
					gl.glTranslated(0.45, -0.35, 0);
					gl.glScaled(1, -1, 1);
					gl.glRotatef(-37, 0, 0, 1);
					gl.glRotated(-15 + 10 * i, 1, 0, 0);
					gl.glCallList(fins);
				gl.glPopMatrix();
			gl.glPopMatrix();

			// call the eyes list
			gl.glCallList(eyes);
				gl.glPushMatrix();
				gl.glScaled(1, 1, -1);
				gl.glCallList(eyes);
			gl.glPopMatrix();

			gl.glEndList();// end of fish list
		}
	}

}
