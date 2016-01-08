import javax.media.opengl.*;
import javax.swing.plaf.SliderUI;
import javax.swing.text.FlowView.FlowStrategy;

import com.jogamp.opengl.FloatUtil;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.org.apache.bcel.internal.generic.DREM;

import java.awt.event.FocusListener;
import java.util.*;

public class shark extends fish {
	
	private int upperfin;

	// current positon of the central of the fish

	public shark(float x, float y, float z, int id) {


		R = 0.8f;
		alpha = 2;
		position[0] = x;
		position[1] = y;
		position[2] = z;
		speed = 0.03f;
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
		scale = 0.4f;
		Zlimit = 90;
	}

	public void init(GL2 gl) {
		// generate list for every part
		fish_object = gl.glGenLists(11);
		fishtail = gl.glGenLists(1);
		fins = gl.glGenLists(1);
		upperfin = gl.glGenLists(1);
		eyes = gl.glGenLists(1);

		/// create the fishtail list
		gl.glNewList(fishtail, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glTranslated(0.2, 0, 0);
			gl.glScaled(1.2, 1, 0.5);
			float[] shear1 = { 1, 0, 0, 0, 1f, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 }; 
		// multiply in OpenGL is different....it is the line vector multiply with matrix
			gl.glMultMatrixf(shear1, 0);
			gl.glRotated(-90, 1, 0, 0);
			glut.glutSolidCone(0.5, 1, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
			
		gl.glPushMatrix();
			gl.glTranslated(0.2, 0, 0);
			gl.glScaled(1.2, 1, 0.5);
			float[] shear2 = { 1, 0, 0, 0, -0.5f, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 }; 
			// multiply in OpenGL is different....it is the line vector multiply with matrix
			gl.glMultMatrixf(shear2, 0);
			gl.glRotated(90, 1, 0, 0);
			glut.glutSolidCone(0.5, 1, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();// end of fishtail list

		/// create a list for fins
		gl.glNewList(fins, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glTranslated(0, -0.46, 0.6);
			gl.glRotated(30, 1, 0, 0);
			float[] shear3 = { 1, 0, 0, 0, 0, 1, 0, 0, 0.5f, 0, 1, 0, 0, 0, 0, 1 }; 
			// multiply in OpenGL is different....it is the line vector multiply with matrix
			gl.glMultMatrixf(shear3, 0);
			gl.glScaled(1, 0.5, 1);
			glut.glutSolidCone(0.5, 1, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();// end of fins list
		
		/// create a list for the upper fins 
		gl.glNewList(upperfin, GL2.GL_COMPILE);
		gl.glPushMatrix();
			gl.glTranslatef(-0.2f, 0.6f, 0);
			float[] shear4 = { 1, 0, 0, 0, 1f, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 }; 
			// multiply in OpenGL is different....it is the line vector multiply with matrix
			gl.glMultMatrixf(shear4, 0);
			gl.glScaled(1.2, 1, 0.5);
			gl.glRotatef(-90, 1, 0, 0);
			glut.glutSolidCone(0.6, 1, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glEndList();
		
		/// create a list for eyes
		gl.glNewList(eyes, GL2.GL_COMPILE);
		gl.glPushMatrix();

			gl.glPushAttrib(gl.GL_CURRENT_BIT);
			gl.glColor3f(1f, 1f, 1f);
			gl.glTranslated(-1.3, 0.3, 0.4);
			gl.glRotated(-5, 0, 0, 1);
			gl.glRotated(-55, 0, 1, 0);
			glut.glutSolidSphere(0.2, DEFAULT_SLICES, DEFAULT_STACKS);
			gl.glPopAttrib();
			// create the pulpil
			gl.glPushMatrix();
				gl.glPushAttrib(gl.GL_CURRENT_BIT);
				gl.glColor3f(0f, 0f, 0f);
				gl.glTranslated(0, 0, 0.12);
				glut.glutSolidSphere(0.1, DEFAULT_SLICES, DEFAULT_STACKS);
				gl.glPopAttrib();
			gl.glPopMatrix();

		gl.glPopMatrix();
		gl.glEndList();// end of eyes' list

		// create the list of whole fish we have 4 poses, 
		// and we call them from 0 to 3 and then down from 3 to 0
		// each pose we use 10 frame to display
		for (int i = 0; i < 4; i++) {
			gl.glNewList(fish_object + i, GL2.GL_COMPILE);
			// create the body
			gl.glPushMatrix();
				gl.glScaled(2, 0.8, 0.8);
				glut.glutSolidSphere(1, DEFAULT_SLICES, DEFAULT_STACKS);
			gl.glPopMatrix();
			
			// call the tail list
			gl.glPushMatrix();
				gl.glTranslated(1.6, 0, 0);
				// the tail is from -45 to 45 each list change 30 degree
				gl.glRotated((-30 + 20 * i), 0, 1, 0);
				gl.glCallList(fishtail);
			gl.glPopMatrix();

			// call the fins list
			gl.glPushMatrix();
				gl.glPushMatrix();
				// the fins rotate from -30 to 30. each time changes 20 degree
					gl.glRotated(-15 + 10 * i, 1, 0, 0);
					gl.glCallList(fins);
				gl.glPopMatrix();
				gl.glPushMatrix();
					gl.glScaled(1, 1, -1);
					gl.glRotated(-15 + 10 * i, 1, 0, 0);
					gl.glCallList(fins);
				gl.glPopMatrix();
			gl.glPopMatrix();
			
			// call the eys list
			gl.glCallList(eyes);
			gl.glPushMatrix();
				gl.glScaled(1, 1, -1);
				gl.glCallList(eyes);
			gl.glPopMatrix();
			
			
			//call the upper fin
			gl.glCallList(upperfin);

			gl.glEndList();// end of fish list
		}
	}

}
