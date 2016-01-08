// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//  Nov 2014 modified to include test cases for shading example for PA4
//
//  Dec 2015 modified by Zhi Dou for PA4

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import javax.imageio.ImageIO;
import javax.lang.model.type.PrimitiveType;
//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import java.awt.event.MouseWheelListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl
import com.sun.swing.internal.plaf.synth.resources.synth_zh_TW;

import apple.laf.JRSUIConstants.Direction;

public class lab11 extends JFrame
		implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH = 800;
	private final int DEFAULT_WINDOW_HEIGHT = 800;
	private final float DEFAULT_LINE_WIDTH = 1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	final private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	private ZBuffer Zbuff;
	@SuppressWarnings("unused")
	private ColorType color;

	// this is the position of all object of 3 scene
	Vector3D[][] objectsPosition= {
			{ new Vector3D(400f, 400f, 128f), new Vector3D(128f, 128f, 128f), new Vector3D(628f, 628f, 128f) },
			{ new Vector3D(300f, 200f, 128f), new Vector3D(600f, 100, 128f), new Vector3D(400f, 400f, 0f) },
			{ new Vector3D(400f, 200f, 128), new Vector3D(150f, 400f, 128f),  new Vector3D(650f, 400f, 128) } };
	// counter is use to count which object is chosen in every scene. 
	// there are just 3 object for each scene. so counter = 0, 1, 2 means the corresponding object is chosen
	// when counter = 3, we could control all objects as a whole 
	private int counter = 3;
	// this is used to choose ka kd, and ks. 0 means ka, 1 means kd, 2 means ks
	private int MaterialSwitch = 0;
	// specular exponent for materials
	private int ns = 5;
	// this is the direction of variable when we change ka, kd, ks
	private int sign = 1;
	// Insteand of really move the position of the eye, we rotate the view vector to achieve the effect...
	// this is used in moving camera
	private float fakemoveY = 0;
	private float fakemoveX = 0;
	// this strings are to print which object we are chosen
	private String[][] chosenObject ={{new String("Sphere is chosen!"),new String("Turos is chosen"),"Ellipse is chose"},
									{"Cylinder is chosen!","SuperEllipsoid is chosen!","Box is chosen"},
									{"SuperTuros is chosen","Environment ball is chosen","Oringe is chosen"}};

	private ArrayList<Point3D> lineSegs;
	private ArrayList<Point3D> triangles;
	private int Nsteps;
	private SketchBase sketchBase = new SketchBase();
	// ks of every object of each scene
	private ColorType[][] Ks = {
			{ new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.0f, 1.0f, 1.0f) },
			{ new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.0f, 1.0f, 1.0f) },
			{ new ColorType(1.0f, 1.0f, 1.0f), new ColorType(), new ColorType(1.0f, 1.0f, 1.0f) } };
	// kd of every object of each scene
	private ColorType[][] Kd = {
			{ new ColorType(0.9f, 0.3f, 0.1f), new ColorType(0.0f, 0.5f, 0.9f), new ColorType(0.3f, 0.6f, 0.3f) },
			{ new ColorType(0.6f, 1f, 1f), new ColorType(1f, 1f, 0.8f), new ColorType(0.7f, 0.5f, 0.8f) },
			{ new ColorType(0.8f, 0.8f, 0.6f), new ColorType(), new ColorType(1f, 0.8f, 0f) } };
	// radius or somethins is the length of object. We use this to scale the objects
	private float[][] radius = { { 50f, 50f, 50f }, { 50f, 70f, 50f }, { 50f, 70f, 70f } };

	/** The quaternion which controls the rotation of the world. */
	private Quaternion viewing_quaternion = new Quaternion();
	/** The quaternion which controls the rotation of the camera. */
	private Quaternion camera_quaternion = new Quaternion();
	/** The quaternion which controls the rotation of the chosen object. */
	private Quaternion[] object_quaternion = new Quaternion[5];

	/** parameter of all kinds of light */
	private Vector3D InfiniteLightDirection;
	private Vector3D SpotLightDirection;
	private ColorType AmbientLightColor;
	private ColorType InfiniteLightColor;
	private ColorType PointLightColor;
	private Vector3D[] PointLightPosition = {new Vector3D((DEFAULT_WINDOW_WIDTH / 2), (DEFAULT_WINDOW_HEIGHT / 2), 800),
											new Vector3D(0, 0, 800),
											new Vector3D(0, 800, 800)};
	private float SpotLightScope;
	private boolean AmbientLight = false;
	private boolean InfiniteLight = true;
	private boolean PointLight = false;
	private boolean SpotLight = false;
	
	// this used in rotete the world
	private Vector3D viewing_center = new Vector3D((float) (DEFAULT_WINDOW_WIDTH / 2),
			(float) (DEFAULT_WINDOW_HEIGHT / 2), (float) 0.0);
	// this used for camera rotation, all the objects and light rotation according to the position of camera
	private Vector3D camera_center = new Vector3D((float) (DEFAULT_WINDOW_WIDTH / 2),
			(float) (DEFAULT_WINDOW_HEIGHT / 2), 1000f);
	/** The last x and y coordinates of the mouse press. */
	private int last_x = 0, last_y = 0;
	/** Whether the world is being rotated. */
	private boolean rotate_world = false;
	// Whether dragging an object
	private boolean drag_object = false;
	private boolean Flat = false;
	private boolean Gouraud = true;
	private boolean Phong = false;
	private boolean cameraRotate = false;
	private boolean SpecularTerm = true;
	private boolean DiffuseTerm = true;
	private boolean AmbientTerm = true;

	private BufferedImage EnTexture, BumpTexture;

	public lab11() {
		capabilities = new GLCapabilities(null);
		capabilities.setDoubleBuffered(true); // Enable Double buffering

		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.setAutoSwapBufferMode(true); // true by default. Just to be
											// explicit
		canvas.setFocusable(true);
		getContentPane().add(canvas);

		animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60
												// FPS

		this.addMouseWheelListener(this);

		numTestCase = 3;
		testCase = 0;
		Nsteps = 24;

		setTitle("CS680 Light");
		setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);

		color = new ColorType(1.0f, 0.0f, 0.0f);
		lineSegs = new ArrayList<Point3D>();
		triangles = new ArrayList<Point3D>();

		try {
			EnTexture = ImageIO.read(new File("En5.jpg"));
		} catch (IOException e) {
			System.out.println("Error: reading texture image.");
			e.printStackTrace();
		}

		try {
			BumpTexture = ImageIO.read(new File("Orange.png"));
		} catch (IOException e) {
			System.out.println("Error: reading texture image.");
			e.printStackTrace();
		}

	}

	public void run() {
		animator.start();
	}

	public static void main(String[] args) {
		lab11 P = new lab11();
		P.run();
	}

	// ***********************************************
	// GLEventListener Interfaces
	// ***********************************************
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glLineWidth(DEFAULT_LINE_WIDTH);
		Dimension sz = this.getContentPane().getSize();
		buff = new BufferedImage(sz.width, sz.height, BufferedImage.TYPE_3BYTE_BGR);
		Zbuff = new ZBuffer();
		Zbuff.InitialZBuffer();
		clearPixelBuffer();
		Zbuff.CleanZBuffer();
		for (int i = 0; i < 5; i++)
			object_quaternion[i] = new Quaternion();
		System.out.println("Instruction of Interface");
		System.out.println("Q: exit		R: reset the Quaternin");
		System.out.println("C: Choose certain Object in the scene");
		System.out.println("T: Change testcases");
		System.out.println("F,G,P: Choose rendering method");
		System.out.println("A,D,S: Control specular term, diffuse term, ambient term");
		System.out.println("V,B,N: Choose ka, kd, ks");
		System.out.println("M: Control cameral rotation");
		System.out.println("U,J,H,K: Move camera to up, down, left, right");
		System.out.println("O,L: Move the chosen objects in z-axis" );
		System.out.println("1,2,3,4: Control ambient light, infinite light, point light, spot light");
		System.out.println("5,6: Adjust Ns");
		System.out.println("8,9,0: adjust r, g, b of ka or kd or ks");
		System.out.println("7: Change the direction of 8,9,0");	
	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		WritableRaster wr = buff.getRaster();
		DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
		byte[] data = dbb.getData();

		gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
		gl.glDrawPixels(buff.getWidth(), buff.getHeight(), GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data));
		drawTestCase();
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		// deliberately left blank
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		// deliberately left blank
	}

	void clearPixelBuffer() {
		lineSegs.clear();
		triangles.clear();
		Graphics2D g = buff.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
		g.dispose();
	}

	// drawTest
	void drawTestCase() {
		/* clear the window and vertex state */
		clearPixelBuffer();
		Zbuff.CleanZBuffer();

		// System.out.printf("Test case = %d\n",testCase);

		shadeTest();
	}

	// ***********************************************
	// KeyListener Interfaces
	// ***********************************************
	public void keyTyped(KeyEvent key) {
		// Q,q: quit
		// C,c: clear polygon (set vertex count=0)
		// R,r: randomly change the color
		// S,s: toggle the smooth shading
		// T,t: show testing examples (toggles between smooth shading and flat
		// shading test cases)
		// >: increase the step number for examples
		// <: decrease the step number for examples
		// +,-: increase or decrease spectral exponent

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
		case 'R':
		case 'r':
			viewing_quaternion.reset();
			break;
		case 'C':
		case 'c':
			counter += 1;
			if (counter == 4)
				counter = 0;
			if (counter < 3)
				System.out.println(chosenObject[testCase][counter]);
			// doSmoothShading = !doSmoothShading; // This is a placeholder
			// (implemented in 'T')
			break;
		case 'T':
		case 't':
			testCase = (testCase + 1) % numTestCase;
			viewing_quaternion.reset();
			drawTestCase();
			break;
		case 'G':
		case 'g':
			Gouraud = true;
			Phong = false;
			Flat = false;
			System.out.println("Gouraud rendering");
			drawTestCase();
			break;
		case 'P':
		case 'p':
			Phong = true;
			Flat = false;
			Gouraud = false;
			System.out.println("Phong rendering");
			drawTestCase();
			break;
		case 'F':
		case 'f':
			Flat = true;
			Gouraud = false;
			Phong = false;
			System.out.println("Flat rendering");
			drawTestCase();
			break;
		case 'm':
		case 'M':
			cameraRotate = !cameraRotate;
			System.out.println("Start rotate camera!");
			break;
		case 'A':
		case 'a':
			AmbientTerm = !AmbientTerm;
			break;
		case 'D':
		case 'd':
			DiffuseTerm = !DiffuseTerm;
			break;
		case 'S':
		case 's':
			SpecularTerm = !SpecularTerm;
			break;
		case 'v':
		case 'V':
			MaterialSwitch = 0;
			System.out.println("Adjust ka!");
			break;
		case 'b':
		case 'B':
			MaterialSwitch = 1;
			System.out.println("Adjust kb!");
			break;
		case 'n':
		case 'N':
			MaterialSwitch = 2;
			System.out.println("Adjust ks!");
			break;
		case 'l':
			if (counter < 3) {
				objectsPosition[testCase][counter].z += 3;
			}
			break;
		case 'o':
			if (counter < 3) {
				objectsPosition[testCase][counter].z -= 3;
			}
			break;
		case 'u':
		case 'U':
			cameraTranslate(1);
			// System.out.println(1);
			break;
		case 'j':
		case 'J':
			cameraTranslate(2);
			// System.out.println(1);
			break;
		case 'h':
		case 'H':
			cameraTranslate(3);
			// System.out.println(3);
			break;
		case 'k':
		case 'K':
			cameraTranslate(4);
			// System.out.println(4);
			break;

		case '1':
			AmbientLight = !AmbientLight;
			break;
		case '2':
			InfiniteLight = !InfiniteLight;
			break;
		case '3':
			PointLight = !PointLight;
			break;
		case '4':
			SpotLight = !SpotLight;
			break;
		case '5':
			ns++;
			drawTestCase();
			break;
		case '6':
			if (ns > 0)
				ns--;
			drawTestCase();
			break;
		case '7':
			sign = -sign;
			System.out.println("sign: "+sign);
			break;
		case '8':
			if (MaterialSwitch == 0) {
				if (Kd[testCase][counter].r >= 1 && sign == 1)
					Kd[testCase][counter].r = 1;
				else if (Kd[testCase][counter].r <= 0 && sign == -1)
					Kd[testCase][counter].r = 0;
				else
					Kd[testCase][counter].r += sign * 0.1f;
			}
			if (MaterialSwitch == 1) {
				if (Kd[testCase][counter].r >= 1 && sign == 1)
					Kd[testCase][counter].r = 1;
				else if (Kd[testCase][counter].r <= 0 && sign == -1)
					Kd[testCase][counter].r = 0;
				else
					Kd[testCase][counter].r += sign * 0.1f;

			}
			if (MaterialSwitch == 2) {
				if (Ks[testCase][counter].r >= 1 && sign == 1)
					Ks[testCase][counter].r = 1;
				else if (Ks[testCase][counter].r <= 0 && sign == -1)
					Ks[testCase][counter].r = 0;
				else
					Ks[testCase][counter].r += sign * 0.1f;
				System.out.println(
						"ks" + "[" + testCase + "]" + "[" + counter + "]" + ".r" + ": " + Ks[testCase][counter].r);
			}
			break;
		case '9':
			if (MaterialSwitch == 0) {
				if (Kd[testCase][counter].g >= 1 && sign == 1)
					Kd[testCase][counter].g = 1;
				else if (Kd[testCase][counter].g <= 0 && sign == -1)
					Kd[testCase][counter].g = 0;
				else
					Kd[testCase][counter].g += sign * 0.1f;

			}
			if (MaterialSwitch == 1) {
				if (Kd[testCase][counter].g >= 1 && sign == 1)
					Kd[testCase][counter].g = 1;
				else if (Kd[testCase][counter].g <= 0 && sign == -1)
					Kd[testCase][counter].g = 0;
				else
					Kd[testCase][counter].g += sign * 0.1f;
			}
			if (MaterialSwitch == 2) {
				if (Ks[testCase][counter].g >= 1 && sign == 1)
					Ks[testCase][counter].g = 1;
				else if (Ks[testCase][counter].g <= 0 && sign == -1)
					Ks[testCase][counter].g = 0;
				else
					Ks[testCase][counter].g += sign * 0.1f;
				System.out.println(
						"ks" + "[" + testCase + "]" + "[" + counter + "]" + ".g" + ": " + Ks[testCase][counter].g);
			}
			break;
		case '0':
			if (MaterialSwitch == 0 && sign == 1) {
				if (Kd[testCase][counter].b >= 1)
					Kd[testCase][counter].b = 1;
				else if (Kd[testCase][counter].r <= 0 && sign == -1)
					Kd[testCase][counter].b = 0;
				else
					Kd[testCase][counter].b += sign * 0.1f;
			}
			if (MaterialSwitch == 1) {
				if (Kd[testCase][counter].b >= 1 && sign == 1)
					Kd[testCase][counter].b = 1;
				else if (Kd[testCase][counter].b <= 0 && sign == -1)
					Kd[testCase][counter].b = 0;
				else
					Kd[testCase][counter].b += sign * 0.1f;
			}
			if (MaterialSwitch == 2) {
				if (Ks[testCase][counter].b >= 1 && sign == 1)
					Ks[testCase][counter].b = 1;
				else if (Ks[testCase][counter].b <= 0 && sign == -1)
					Ks[testCase][counter].b = 0;
				else
					Ks[testCase][counter].b += sign * 0.1f;
				System.out.println(
						"ks" + "[" + testCase + "]" + "[" + counter + "]" + ".b" + ": " + Ks[testCase][counter].b);
			}
			break;
		case '<':
			Nsteps = Nsteps < 4 ? Nsteps : Nsteps / 2;
			System.out.printf("Nsteps = %d \n", Nsteps);
			drawTestCase();
			break;
		case '>':
			Nsteps = Nsteps > 190 ? Nsteps : Nsteps * 2;
			System.out.printf("Nsteps = %d \n", Nsteps);
			drawTestCase();
			break;
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
		// deliberately left blank
	}

	// **************************************************
	// MouseListener and MouseMotionListener Interfaces
	// **************************************************
	public void mouseClicked(MouseEvent mouse) {
		// deliberately left blank
	}

	public void mousePressed(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			last_x = mouse.getX();
			last_y = mouse.getY();
			rotate_world = true;
		}

		if (button == MouseEvent.BUTTON3) {
			last_x = mouse.getX();
			last_y = mouse.getY();
			drag_object = true;
		}

	}

	public void mouseReleased(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			rotate_world = false;
		}

		if (button == MouseEvent.BUTTON3) {
			drag_object = false;
		}
	}

	// this is used for scaling objects
	public void mouseWheelMoved(MouseWheelEvent wheel) {
		int dir = wheel.getWheelRotation();
		if (counter < 3)
			radius[testCase][counter] += dir;

	}

	public void mouseMoved(MouseEvent mouse) {
		if (this.cameraRotate) {
			// get the current position of the mouse
			final int x = mouse.getX();
			final int y = mouse.getY();

			// System.out.println(x+","+y);
			// get the change in position from the previous one
			final int dx = x - this.last_x;
			final int dy = y - this.last_y;

			// create a unit vector in the direction of the vector (dy, dx, 0)
			final float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
			if (magnitude > 0.0001) {
				// define axis perpendicular to (dx,-dy,0)
				// use -y because origin is in upper lefthand corner of the
				// window
				final float[] axis = new float[] { -(float) (dy / magnitude), (float) (dx / magnitude), 0 };

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f * 0.5f;
				final float s = (float) Math.sin(0.5f * viewing_delta);
				final float c = (float) Math.cos(0.5f * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);

				if (counter >= 3) {
					this.camera_quaternion = Q.multiply(this.camera_quaternion);
					// normalize to counteract acccumulating round-off error
					this.camera_quaternion.normalize();
				}

				// save x, y as last x, y
				this.last_x = x;
				this.last_y = y;
				drawTestCase();
			}
		}
	}

	/**
	 * Updates the rotation quaternion as the mouse is dragged.
	 * 
	 * @param mouse
	 *            The mouse drag event object.
	 */
	public void mouseDragged(final MouseEvent mouse) {

		if (this.rotate_world) {
			// get the current position of the mouse
			final int x = mouse.getX();
			final int y = mouse.getY();

			// get the change in position from the previous one
			final int dx = x - this.last_x;
			final int dy = y - this.last_y;

			// create a unit vector in the direction of the vector (dy, dx, 0)
			final float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
			if (magnitude > 0.0001) {
				// define axis perpendicular to (dx,-dy,0)
				// use -y because origin is in upper lefthand corner of the
				// window
				final float[] axis = new float[] { -(float) (dy / magnitude), (float) (dx / magnitude), 0 };

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f * 2.5f;
				final float s = (float) Math.sin(0.5f * viewing_delta);
				final float c = (float) Math.cos(0.5f * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);

				// when counter >= 3 we could control all objects in a scene 
				if (counter >= 3) {
					this.viewing_quaternion = Q.multiply(this.viewing_quaternion);
					// normalize to counteract acccumulating round-off error
					this.viewing_quaternion.normalize();
				}
				// when counter < 3 we just control the object be chosen
				else {
					this.object_quaternion[counter] = Q.multiply(this.object_quaternion[counter]);
					this.object_quaternion[counter].normalize();
				}

				// save x, y as last x, y
				this.last_x = x;
				this.last_y = y;
				drawTestCase();
			}
		}

		if (this.drag_object) {
			final int x = mouse.getX();
			final int y = mouse.getY();

			// get the change in position from the previous one
			final int dx = x - this.last_x;
			final int dy = y - this.last_y;
			// when counter < 3 we move the object be chosen
			if (counter < 3) {
				objectsPosition[testCase][counter].x += dx;
				objectsPosition[testCase][counter].y += dy;
			}
			this.last_x = x;
			this.last_y = y;
		}

	}

	public void mouseEntered(MouseEvent mouse) {
		// Deliberately left blank
	}

	public void mouseExited(MouseEvent mouse) {
		// Deliberately left blank
	}

	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	// **************************************************
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	// **************************************************
	// **************************************************
	// Test Cases
	// Dec 8, 2015 Zhi Dou -- define Object, assign value to parameter of all kind of light in every scene
	// **************************************************
	
	
	void shadeTest() {
		if (testCase == 0) {
			Sphere3D sphere = new Sphere3D(0, objectsPosition[testCase][0].x, objectsPosition[testCase][0].y, objectsPosition[testCase][0].z,
					(float) 1.5 * radius[testCase][0], Nsteps, Nsteps, Ks[testCase][0], Kd[testCase][0]);
			Torus3D torus = new Torus3D(1, objectsPosition[testCase][1].x, objectsPosition[testCase][1].y, objectsPosition[testCase][1].z,
					(float) 0.8 * radius[testCase][1], (float) 1.25 * radius[testCase][1], Nsteps, Nsteps,
					Ks[testCase][1], Kd[testCase][1]);
			Ellipse3D ellipse = new Ellipse3D(2, objectsPosition[testCase][2].x, objectsPosition[testCase][2].y,
					objectsPosition[testCase][2].z, (float) 1.5 * radius[testCase][2], (float) 3 * radius[testCase][2],
					(float) 1.5 * radius[testCase][2], Nsteps, Nsteps, Ks[testCase][2], Kd[testCase][2]);

			//define the lights of the scene 
			InfiniteLightColor = new ColorType(1.0f, 1.0f, 1.0f);
			InfiniteLightDirection = new Vector3D((float) 0.0, (float) (-1.0 / Math.sqrt(2.0)),
					(float) (1.0 / Math.sqrt(2.0)));

			AmbientLightColor = new ColorType(0.04f, 0.02f, 0.03f);

			PointLightColor = new ColorType(1f, 1f, 1f);
			SpotLightDirection = new Vector3D(0, 0, 1);
			SpotLightScope = (float) Math.toRadians(30);

			DrawObject(sphere);
			DrawObject(torus);
			DrawObject(ellipse);

		}

		if (testCase == 1) {
			
			Cylinder3D cylinder = new Cylinder3D(0, objectsPosition[testCase][0].x, objectsPosition[testCase][0].y,
					objectsPosition[testCase][0].z, (float) 1.2 * radius[testCase][0], (float) 1.2 * radius[testCase][0], 200,
					Nsteps, Nsteps, Ks[testCase][0], Kd[testCase][0]);
			SuperEllipsoid3D superellipsoid = new SuperEllipsoid3D(1, objectsPosition[testCase][1].x, objectsPosition[testCase][1].y,
					objectsPosition[testCase][1].z, (float) 1.5 * radius[testCase][1], (float) 1.5 * radius[testCase][1],
					(float) 1.5 * radius[testCase][1], (float) 2.5, (float) 2.5, Nsteps, Nsteps, Ks[testCase][1],
					Kd[testCase][1]);
			Box box = new Box(2, objectsPosition[testCase][2].x, objectsPosition[testCase][2].y, objectsPosition[testCase][2].z,
					(float) 2 * radius[testCase][2], Ks[testCase][2], Kd[testCase][2]);

			AmbientLightColor = new ColorType(0.06f, 0.09f, 0.03f);

			InfiniteLightColor = new ColorType(0.8f, 0.6f, 1.0f);
			InfiniteLightDirection = new Vector3D((float) (1.0 / Math.sqrt(2.0)),(float) 0.0, 
					(float) (1.0 / Math.sqrt(2.0)));

			PointLightColor = new ColorType(1f, 1f, 1f);
			SpotLightDirection = new Vector3D(0, 0, 1);
			SpotLightScope = (float) Math.toRadians(30);

			DrawObject(cylinder);
			DrawObject(superellipsoid);
			DrawObject(box);
		}

		if (testCase == 2) {
			SuperTorus3D supertoroid = new SuperTorus3D(0, objectsPosition[testCase][0].x, objectsPosition[testCase][0].y,
					objectsPosition[testCase][0].z, (float) 0.8 * radius[testCase][0], (float) 0.8 * radius[testCase][0],
					(float) 0.8 * radius[testCase][0], (float) 1.25 * radius[testCase][0], (float) 4, (float) 4, Nsteps,
					Nsteps, Ks[testCase][0], Kd[testCase][0]);

			TextureMappingSphere enMappingSphere = new TextureMappingSphere(1, objectsPosition[testCase][1].x,
					objectsPosition[testCase][1].y, objectsPosition[testCase][1].z, (float) 1.5 * radius[testCase][1], Nsteps, Nsteps,
					EnTexture, false);

			TextureMappingSphere bumpMappingShere = new TextureMappingSphere(2, objectsPosition[testCase][2].x,
					objectsPosition[testCase][2].y, objectsPosition[testCase][2].z, (float) 1.5 * radius[testCase][2], Nsteps, Nsteps,
					BumpTexture, true);

			bumpMappingShere.set_kd(Kd[testCase][2]);
			bumpMappingShere.set_ks(Ks[testCase][2]);

			AmbientLightColor = new ColorType(0.04f, 0.02f, 0.03f);

			InfiniteLightColor = new ColorType(1.0f, 1.0f, 1.0f);
			InfiniteLightDirection = new Vector3D((float) (1.0 / Math.sqrt(2.0)),(float) 0.0, 
					(float) (1.0 / Math.sqrt(2.0)));


			PointLightColor = new ColorType(0.8f, 1f, 0.5f);
			SpotLightDirection = new Vector3D(0, 0, 1);
			SpotLightScope = (float) Math.toRadians(5);

			DrawObject(supertoroid);
			DrawObject(bumpMappingShere);
			DoTexture(enMappingSphere);
		}
	}

	private void DrawObject(Objects3D O) {
		// view vector is defined along z axis
		// this example assumes simple othorgraphic projection
		// view vector is used in
		// (a) calculating specular lighting contribution
		// (b) backface culling / backface rejection
		// we make some change to this, for certain case, view vector is used to calculating specular lighting contribution
		// and view vector2 is used to backface culling / backface rejection, because when we rotate camera
		// view vector actual do not need to rotate, but in order to make it looks good, we rotate it and use it to
		// calculating specular lighting contribution, but we still use the non-rotating view to do backface rejection
		Vector3D view_vector = new Vector3D((float) 0.0, (float) 0.0, (float) 1.0);
		Vector3D view_vector2 = new Vector3D((float) 0.0, (float) 0.0, (float) 1.0);

		// this is the simulate translation of position of camera, because when camera move, its view vector moves too
		view_vector.y += fakemoveY;
		view_vector.x += fakemoveX;
		view_vector.normalize();

		Material mats = new Material(O.kd, O.kd, O.ks, ns);
		// this is to control specular term, diffuse term, ambient term
		mats.ambient = AmbientTerm;
		mats.diffuse = DiffuseTerm;
		mats.specular = SpecularTerm;

		// When camera rotation, we change the view vector for looks real, but actually we rotate all the 
		// objects and light in the scene to achieve this aim
		if (cameraRotate)
			rotateView(camera_quaternion, camera_center, view_vector);

		// initialize the SceneLight
		Light SceneLight = new Light(AmbientLight, AmbientLightColor, InfiniteLight, InfiniteLightDirection,
				InfiniteLightColor, PointLight, SpotLight, SpotLightDirection, PointLightColor, PointLightPosition[testCase],
				SpotLightScope);
		// Instead of rotate camera, we rotate all objects and light in a scene to have equally effect
		if (cameraRotate) {
			SceneLight.rotateLight(camera_quaternion, camera_center);
		}

		// normal to the plane of a triangle to be used in backface culling / backface rejection
		Vector3D triangle_normal = new Vector3D();

		// a triangle mesh
		Mesh3D mesh;
		// for the rendering method. 1 means gouraud rendering, 2 means flat, 3 means phong, 4 for texture
		int mode = 0;
		int i, j, n, m;

		// temporary variables for triangle 3D vertices and 3D normals
		Vector3D v0, v1, v2, n0, n1, n2;
		// projected triangle, with vertex colors
		Point3D[] tri = { new Point3D(), new Point3D(), new Point3D() };

		mesh = O.mesh;
		n = O.get_n();
		m = O.get_m();
		// rotate the surface's 3D mesh for corresponding cases
		mesh.rotateMesh(object_quaternion[O.ID], objectsPosition[testCase][O.ID]);
		mesh.rotateMesh(viewing_quaternion, viewing_center);
		mesh.rotateMesh(camera_quaternion, camera_center);

		// this n==4 case is just for draw box....
		if (n == 4) {
			for (i = 0; i <= m - 1; i++) {
				if (view_vector2.dotProduct(mesh.n[i][0]) <= 0)
					continue;
				// draw the first triangle of a plane
				tri[0].x = (int) mesh.v[i][0].x;
				tri[0].y = (int) mesh.v[i][0].y;
				tri[0].z = (int) mesh.v[i][0].z;
				tri[1].x = (int) mesh.v[i][1].x;
				tri[1].y = (int) mesh.v[i][1].y;
				tri[1].z = (int) mesh.v[i][1].z;
				tri[2].x = (int) mesh.v[i][2].x;
				tri[2].y = (int) mesh.v[i][2].y;
				tri[2].z = (int) mesh.v[i][2].z;

				if (Phong) {
					sketchBase.setPhong(SceneLight, mats, view_vector, mesh.n[i][0], mesh.n[i][1], mesh.n[i][2]);

					tri[0].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][0], tri[0]);
					tri[1].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][1], tri[1]);
					tri[2].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][2], tri[2]);
					mode = 3;
				}

				else if (Gouraud) {
					// vertex colors for Gouraud shading
					n0 = mesh.n[i][0];
					tri[0].c = SceneLight.applyLight(mats, view_vector, n0, tri[0]);
					tri[1].c = SceneLight.applyLight(mats, view_vector, n0, tri[1]);
					tri[2].c = SceneLight.applyLight(mats, view_vector, n0, tri[2]);
					mode = 1;
				}

				else if (Flat) {
					Point3D everage1 = new Point3D();
					// flat shading: use the normal to the triangle itself
					n0 = mesh.n[i][0];
					everage1.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
					everage1.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
					everage1.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
					tri[2].c = tri[1].c = tri[0].c = SceneLight.applyLight(mats, view_vector, n0, everage1);
					mode = 2;
				}

				sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
				// draw the second part of the plane
				tri[0].x = (int) mesh.v[i][0].x;
				tri[0].y = (int) mesh.v[i][0].y;
				tri[0].z = (int) mesh.v[i][0].z;
				tri[1].x = (int) mesh.v[i][2].x;
				tri[1].y = (int) mesh.v[i][2].y;
				tri[1].z = (int) mesh.v[i][2].z;
				tri[2].x = (int) mesh.v[i][3].x;
				tri[2].y = (int) mesh.v[i][3].y;
				tri[2].z = (int) mesh.v[i][3].z;

				if (Phong) {
					sketchBase.setPhong(SceneLight, mats, view_vector, mesh.n[i][0], mesh.n[i][2], mesh.n[i][3]);

					tri[0].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][0], tri[0]);
					tri[1].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][2], tri[1]);
					tri[2].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][3], tri[2]);
					mode = 3;
				}

				else if (Gouraud) {
					// vertex colors for Gouraud shading
					n0 = mesh.n[i][0];
					tri[0].c = SceneLight.applyLight(mats, view_vector, n0, tri[0]);
					tri[1].c = SceneLight.applyLight(mats, view_vector, n0, tri[1]);
					tri[2].c = SceneLight.applyLight(mats, view_vector, n0, tri[2]);
					mode = 1;
				}

				else if (Flat) {
					Point3D everage1 = new Point3D();
					// flat shading: use the normal to the triangle itself
					n0 = mesh.n[i][0];
					everage1.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
					everage1.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
					everage1.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
					tri[2].c = tri[1].c = tri[0].c = SceneLight.applyLight(mats, view_vector, n0, everage1);
					mode = 2;
				}

				sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
			}
		}

		else {
			// draw triangles for the current surface, using vertex colors
			// this works for Gouraud and flat shading only (not Phong)
			for (i = 0; i < m - 1; ++i) {
				for (j = 0; j < n - 1; ++j) {
					v0 = mesh.v[i][j];
					v1 = mesh.v[i][j + 1];
					v2 = mesh.v[i + 1][j + 1];
					triangle_normal = computeTriangleNormal(v0, v1, v2);

					if (view_vector2.dotProduct(triangle_normal) > 0.0)
					// front-facing triangle?
					{
						tri[0].x = (int) v0.x;
						tri[0].y = (int) v0.y;
						tri[0].z = (int) v0.z;
						tri[1].x = (int) v1.x;
						tri[1].y = (int) v1.y;
						tri[1].z = (int) v1.z;
						tri[2].x = (int) v2.x;
						tri[2].y = (int) v2.y;
						tri[2].z = (int) v2.z;

						if (Phong) {
							sketchBase.setPhong(SceneLight, mats, view_vector, mesh.n[i][j], mesh.n[i][j + 1],
									mesh.n[i + 1][j + 1]);

							tri[0].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][j], tri[0]);
							tri[1].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][j + 1], tri[1]);
							tri[2].c = SceneLight.applyLight(mats, view_vector, mesh.n[i + 1][j + 1], tri[2]);
							mode = 3;
						}

						else if (Gouraud) {
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i][j + 1];
							n2 = mesh.n[i + 1][j + 1];
							tri[0].c = SceneLight.applyLight(mats, view_vector, n0, tri[0]);
							tri[1].c = SceneLight.applyLight(mats, view_vector, n1, tri[1]);
							tri[2].c = SceneLight.applyLight(mats, view_vector, n2, tri[2]);
							mode = 1;
						}

						else if (Flat) {
							Point3D everage1 = new Point3D();
							// flat shading: use the normal to the triangle
							// itself
							n2 = n1 = n0 = triangle_normal;
							everage1.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
							everage1.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
							everage1.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
							tri[2].c = tri[1].c = tri[0].c = SceneLight.applyLight(mats, view_vector, triangle_normal,
									everage1);
							mode = 2;
						}

						sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
					}

					v0 = mesh.v[i][j];
					v1 = mesh.v[i + 1][j + 1];
					v2 = mesh.v[i + 1][j];
					triangle_normal = computeTriangleNormal(v0, v1, v2);

					if (view_vector2.dotProduct(triangle_normal) > 0.0)
					// front-facing triangle?
					{
						tri[0].x = (int) v0.x;
						tri[0].y = (int) v0.y;
						tri[0].z = (int) v0.z;
						tri[1].x = (int) v1.x;
						tri[1].y = (int) v1.y;
						tri[1].z = (int) v1.z;
						tri[2].x = (int) v2.x;
						tri[2].y = (int) v2.y;
						tri[2].z = (int) v2.z;

						if (Phong) {
							sketchBase.setPhong(SceneLight, mats, view_vector, mesh.n[i][j], mesh.n[i + 1][j + 1],
									mesh.n[i + 1][j]);
							tri[0].c = SceneLight.applyLight(mats, view_vector, mesh.n[i][j], tri[0]);
							tri[1].c = SceneLight.applyLight(mats, view_vector, mesh.n[i + 1][j + 1], tri[1]);
							tri[2].c = SceneLight.applyLight(mats, view_vector, mesh.n[i + 1][j], tri[2]);
							mode = 3;
						}

						else if (Gouraud) {
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i + 1][j + 1];
							n2 = mesh.n[i + 1][j];
							tri[0].c = SceneLight.applyLight(mats, view_vector, n0, tri[0]);
							tri[1].c = SceneLight.applyLight(mats, view_vector, n1, tri[1]);
							tri[2].c = SceneLight.applyLight(mats, view_vector, n2, tri[2]);
							mode = 1;
						} else if (Flat) {
							Point3D everage = new Point3D();
							// flat shading: use the normal to the triangle
							// itself
							n2 = n1 = n0 = triangle_normal;
							everage.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
							everage.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
							everage.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
							tri[2].c = tri[1].c = tri[0].c = SceneLight.applyLight(mats, view_vector, triangle_normal,
									everage);
							mode = 2;
						}

						sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
					}
				}
			}
		}

	}

	// this is used for draw object with environment texture
	private void DoTexture(TextureMappingSphere O) {
		// normal to the plane of a triangle
		// to be used in backface culling / backface rejection
		Vector3D triangle_normal = new Vector3D();
		Vector3D v0, v1, v2;
		// a triangle mesh
		Mesh3D mesh;
		Vector3D view_vector = new Vector3D((float) 0.0, (float) 0.0, (float) 1.0);
		Point3D[] tri = { new Point3D(), new Point3D(), new Point3D() };
		// set texture to sketch base
		sketchBase.setTexture(EnTexture);

		mesh = O.mesh;
		int n = O.get_n();
		int m = O.get_m();
		// rotate the surface's 3D mesh using quaternion
		mesh.rotateMesh(object_quaternion[O.ID], objectsPosition[testCase][O.ID]);
		mesh.rotateMesh(viewing_quaternion, viewing_center);

		for (int i = 0; i < m - 1; ++i) {
			for (int j = 0; j < n - 1; ++j) {
				if (i == 9 && j == 6) {
					v0 = mesh.v[i][j];
				}
				v0 = mesh.v[i][j];
				v1 = mesh.v[i][j + 1];
				v2 = mesh.v[i + 1][j + 1];
				triangle_normal = computeTriangleNormal(v0, v1, v2);

				if (view_vector.dotProduct(triangle_normal) > 0.0) {
	
					tri[0].x = (int) v0.x;
					tri[0].y = (int) v0.y;
					tri[0].z = (int) v0.z;
					tri[1].x = (int) v1.x;
					tri[1].y = (int) v1.y;
					tri[1].z = (int) v1.z;
					tri[2].x = (int) v2.x;
					tri[2].y = (int) v2.y;
					tri[2].z = (int) v2.z;

					tri[0].u = O.p[i][j].u;
					tri[0].v = O.p[i][j].v;

					tri[1].u = O.p[i][j + 1].u;
					tri[1].v = O.p[i][j + 1].v;

					tri[2].u = O.p[i + 1][j + 1].u;
					tri[2].v = O.p[i + 1][j + 1].v;

					sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], 4);
				}

				v0 = mesh.v[i][j];
				v1 = mesh.v[i + 1][j + 1];
				v2 = mesh.v[i + 1][j];
				triangle_normal = computeTriangleNormal(v0, v1, v2);

				if (view_vector.dotProduct(triangle_normal) > 0.0) {
	
					tri[0].x = (int) v0.x;
					tri[0].y = (int) v0.y;
					tri[0].z = (int) v0.z;
					tri[1].x = (int) v1.x;
					tri[1].y = (int) v1.y;
					tri[1].z = (int) v1.z;
					tri[2].x = (int) v2.x;
					tri[2].y = (int) v2.y;
					tri[2].z = (int) v2.z;

					tri[0].u = O.p[i][j].u;
					tri[0].v = O.p[i][j].v;

					tri[1].u = O.p[i + 1][j + 1].u;
					tri[1].v = O.p[i + 1][j + 1].v;

					tri[2].u = O.p[i + 1][j].u;
					tri[2].v = O.p[i + 1][j].v;

					sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], 4);
				}
			}
		}
	}

	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	private Vector3D computeTriangleNormal(Vector3D v0, Vector3D v1, Vector3D v2) {
		Vector3D e0 = v1.minus(v2);
		Vector3D e1 = v0.minus(v2);
		Vector3D norm = e0.crossProduct(e1);

		if (norm.magnitude() > 0.000001)
			norm.normalize();
		else // detect degenerate triangle and set its normal to zero
			norm.set((float) 0.0, (float) 0.0, (float) 0.0);

		return norm;
	}

	// this is used to rotate view vector when camera rotation
	public void rotateView(Quaternion q, Vector3D center, Vector3D view_vector) {
		Quaternion q_inv = q.conjugate();

		Quaternion p;
		p = new Quaternion((float) 0.0, view_vector);
		p = q_inv.multiply(p);
		p = p.multiply(q);
		view_vector.x = p.get_v().x;
		view_vector.y = p.get_v().y;
		view_vector.z = p.get_v().z;
	}
	

	public void cameraTranslate(int direction) {
		if (direction == 1) {
			for (int i = 0; i < 3; i++)
				objectsPosition[testCase][i].y += 5;
			PointLightPosition[testCase].y += 5;
			fakemoveY -= 0.05;
		}

		if (direction == 2) {
			for (int i = 0; i < 3; i++)
				objectsPosition[testCase][i].y -= 5;
			PointLightPosition[testCase].y -= 5;
			fakemoveY += 0.05;
		}

		if (direction == 3) {
			for (int i = 0; i < 3; i++)
				objectsPosition[testCase][i].x -= 5;
			PointLightPosition[testCase].x -= 5;
			fakemoveX += 0.05;
		}

		if (direction == 4) {
			for (int i = 0; i < 3; i++)
				objectsPosition[testCase][i].x += 5;
			PointLightPosition[testCase].x += 5;
			fakemoveX -= 0.05;
		}
	}
}