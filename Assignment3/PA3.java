
import javax.swing.*;

//import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

import sun.util.logging.resources.logging;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl

public class PA3 extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH = 800;
	private final int DEFAULT_WINDOW_HEIGHT = 800;
	private int refresh = 0; // to count the times of refresh

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;
	private GLU glu;
	@SuppressWarnings("unused")
	private GLUT glut;
	private Vivarium vivarium;
	// world rotation controlled by mouse actions
	private Quaternion viewing_quaternion; 			
	// State variables for the mouse actions
	int last_x, last_y;
	boolean rotate_world;
	// the distance between left camera and right camera
	private float eyeseperate = 0;
	// ratio of length and height
	private float ratio;
	// switch the mode of viewing
	private int stereo = 0;

	public PA3() {

		capabilities = new GLCapabilities(null);
		// Enable Double buffering
		capabilities.setDoubleBuffered(true); 
		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		// true by default. Just to be explicit
		canvas.setAutoSwapBufferMode(true); 							
		getContentPane().add(canvas);
		// drive the display loop @ 60 FPS
		animator = new FPSAnimator(canvas, 60); 

		glu = new GLU();
		glut = new GLUT();

		setTitle("CS680 : Fishes");
		setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		last_x = last_y = 0;
		rotate_world = false;

		// Set initialization code for user created classes that involves OpenGL
		// calls after here. After this line, the opengGl context will be
		// correctly initialized.
		vivarium = new Vivarium();
		viewing_quaternion = new Quaternion();
	}

	public void run() {
		animator.start();
	}

	public static void main(String[] args) {
		PA3 P = new PA3();
		P.run();
	}

	// ***************************************************************************
	// GLEventListener Interfaces
	// ***************************************************************************
	//
	// Place all OpenGL related initialization here. Including display list
	// initialization for user created classes
	//
	public void init(GLAutoDrawable drawable) {
		GL2 gl = (GL2) drawable.getGL();

		/* set up for shaded display of the vivarium */
		float light0_position[] = { 1, 1, 1, 0 };
		float light0_ambient_color[] = { 0.25f, 0.25f, 0.25f, 1 };
		float light0_diffuse_color[] = { 1, 1, 1, 1 };
		gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearAccum(0f, 0f, 0f, 0f);
		gl.glShadeModel(GL2.GL_SMOOTH);

		/* set up the light source */
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_position, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0_ambient_color, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse_color, 0);

		/* turn lighting and depth buffering on */
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);

		vivarium.init(gl);
	}

	// display in normal viewing
	public void NormalViewing(GL2 gl) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// Set the clipping volume
		glu.gluPerspective(45, ratio, 0.1, 100);

		// clear the display
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glLoadIdentity();

		glu.gluLookAt(0, 0, 20, 0, 0, 0, 0, 1, 0);
		// rotate the world and then call world display list object
		gl.glMultMatrixf(viewing_quaternion.to_matrix(), 0);

		vivarium.update(gl, refresh);
		vivarium.draw(gl);
	}

	// display the viewing in stereo viewing
	public void StereoViewing1(GL2 gl) {
		gl.glPushMatrix();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// set the left camera, display all things in red
		gl.glColorMask(true, false, false, false);

		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(45, ratio, 0.1, 100);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(-0.5 * eyeseperate, 0, 20, 0, 0, -15, 0, 1, 0);
		// rotate the world and then call world display list object
		gl.glMultMatrixf(viewing_quaternion.to_matrix(), 0);

		vivarium.update(gl, refresh);
		vivarium.draw(gl);

		gl.glPopMatrix();

		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		
		// create the right camera which is blue
		gl.glColorMask(false, false, true, false);
		gl.glPushMatrix();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45, ratio, 0.1, 100);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0.5 * eyeseperate, 0, 20, 0, 0, -15, 0, 1, 0);
		// rotate the world and then call world display list object
		gl.glMultMatrixf(viewing_quaternion.to_matrix(), 0);

		vivarium.draw(gl);
		gl.glPopMatrix();

		gl.glColorMask(true, true, true, false);

	}

	public void display(GLAutoDrawable drawable) {
		// this is used to record the number of frame
		refresh += 1;
		refresh = refresh % 61;

		GL2 gl = (GL2) drawable.getGL();
		if (stereo % 2 == 0)
			NormalViewing(gl);
		else if (stereo % 2 == 1) {
			vivarium.SetPurple();
			StereoViewing1(gl);
		}

	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// Change viewport dimensions
		GL2 gl = (GL2) drawable.getGL();
		// Prevent a divide by zero, when window is too short (you cant make a
		// window of zero width).
		if (height == 0) 
			height = 1;
		ratio = 1.0f * width / height;
		// Set the viewport to be the entire window
		gl.glViewport(0, 0, width, height);

	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	// ***********************************************
	// KeyListener Interfaces
	// ***********************************************
	public void keyTyped(KeyEvent key) {
		switch (key.getKeyChar()) {
		case 'Q':
		case 'q':
			new Thread() {
				public void run() {
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;

		// set the viewing quaternion to 0 rotation
		case 'R':
		case 'r':
			viewing_quaternion.reset();
			break;
		case '1':
			eyeseperate += 0.01f;
			System.out.println("eye: " + eyeseperate);
			break;
		case '2':
			eyeseperate -= 0.01f;
			System.out.println("eye: " + eyeseperate);
			break;
		case 's':
		case 'S':
			stereo += 1;
			break;
		case 'i':
		case 'I':
			vivarium.SetCreature();
		default:
			break;
		}
	}

	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			new Thread() {
				public void run() {
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;

		default:
			break;
		}
	}

	public void keyReleased(KeyEvent key) {
	}

	// **************************************************
	// MouseListener and MouseMotionListener Interfaces
	// **************************************************
	public void mouseClicked(MouseEvent mouse) {
		int button = mouse.getClickCount();
		if (button == MouseEvent.BUTTON2) {
			vivarium.Addfood();
		}
	}

	public void mousePressed(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			last_x = mouse.getX();
			last_y = mouse.getY();
			rotate_world = true;
		}
	}

	public void mouseReleased(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			rotate_world = false;
		}
	}

	public void mouseMoved(MouseEvent mouse) {
	}

	public void mouseDragged(MouseEvent mouse) {
		if (rotate_world) {
			// vector in the direction of mouse motion
			int x = mouse.getX();
			int y = mouse.getY();
			float dx = x - last_x;
			float dy = y - last_y;

			// spin around axis by small delta
			float mag = (float) Math.sqrt(dx * dx + dy * dy);
			if (mag < 0.0001)
				return;

			float[] axis = new float[3];
			axis[0] = dy / mag;
			axis[1] = dx / mag;
			axis[2] = 0.0f;

			// calculate appropriate quaternion
			float viewing_delta = 3.1415927f / 180.0f * 2; // 1 degree
			float s = (float) Math.sin(0.5f * viewing_delta);
			float c = (float) Math.cos(0.5f * viewing_delta);

			Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
			viewing_quaternion = Q.multiply(viewing_quaternion);

			// normalize to counteract acccumulating round-off error
			viewing_quaternion.normalize();

			// Save x, y as last x, y
			last_x = x;
			last_y = y;
		}
	}

	public void mouseEntered(MouseEvent mouse) {
	}

	public void mouseExited(MouseEvent mouse) {
	}

	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

}
