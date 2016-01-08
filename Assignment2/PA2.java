/**
 * PA2.java - driver for the cartoon ant simulation
 * author: Zhi Dou
 * Begin: 2 October 2015
 * end:
 * 
 * History:
 * 
 * 2 February 2011
 * 
 * - added documentation
 * 
 * (Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>)
 * 
 * 16 January 2008
 * 
 * - translated from C code by Stan Sclaroff
 * 
 * (Tai-Peng Tian <tiantp@gmail.com>)
 * 
 */

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.omg.CORBA.SystemException;

import com.jogamp.opengl.FloatUtil;
import com.jogamp.opengl.util.FPSAnimator;//for new version of gl
import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.swing.internal.plaf.basic.resources.basic;

import apple.laf.JRSUIConstants.SegmentTrailingSeparator;

/**
 * The main class which drives the cartoon ant simulation.
 * 
 * @author Zhi Dou
 * @since Fall 2015
 */
public class PA2 extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	/** The angle by which to rotate the joint on user request to rotate. */
	public static final double ROTATION_ANGLE = 2.0;
	/** Randomly generated serial version UID. */
	private static final long serialVersionUID = -7060944143920496524L;
	/**
	 * The animator which controls the framerate at which the canvas is
	 * animated.
	 */
	final FPSAnimator animator;
	/** The canvas on which we draw the scene. */
	private final GLCanvas canvas;
	/** The capabilities of the canvas. */
	private final GLCapabilities capabilities = new GLCapabilities(null);
	/** The OpenGL utility object. */
	private final GLU glu = new GLU();
	/** The OpenGL utility toolkit object. */
	private final GLUT glut = new GLUT();
	/** The last x and y coordinates of the mouse press. */
	private int last_x = 0, last_y = 0;
	/** Whether the world is being rotated. */
	private boolean rotate_world = false;
	/** The axis around which to rotate the selected joints. */
	private Axis selectedAxis = Axis.X;
	/** The set of components which are currently selected for rotation. */
	private final Set<Component> selectedComponents = new HashSet<Component>(30);
	/**
	 * The set of fingers which have been selected for rotation.
	 * 
	 * Selecting a joint will only affect the joints in this set of selected
	 * fingers.
	 **/
	private final Set<Limbs> selectedJoints = new HashSet<Limbs>(5);
	/** Whether the state of the model has been changed. */
	private boolean stateChanged = false;
	/**
	 * The top level component in the scene which controls the positioning and
	 * rotation of everything in the scene.
	 */
	/** The quaternion which controls the rotation of the world. */
	private Quaternion viewing_quaternion = new Quaternion();
	/** This inverse viewing quaternion is used  for make the inverse rotation of mouse
	 * this is used in the method if mousemoved*/
	private Quaternion inverse_viewing_quaternion = new Quaternion();
	/** The color for components which are selected for rotation. */
	public static final FloatColor ACTIVE_COLOR = FloatColor.RED;
	/** The color for components which are not selected for rotation. */
	public static final FloatColor INACTIVE_COLOR = FloatColor.ORANGE;
	/** The default width of the created window. */
	public static final int DEFAULT_WINDOW_HEIGHT = 512;
	/** The default height of the created window. */
	public static final int DEFAULT_WINDOW_WIDTH = 512;
	/**
	 * The mousemove parameter is to record the Quanternion caused by mouse
	 * moving
	 */
	public Quaternion mousemove = new Quaternion();
	/** The initial position of middle place of eyes */
	public float[] eyesmiddleposition = { -0.625f, 1.385f, 1.05f };
	/** The direction of eyes to look at */
	public float[] eyelookatvector = new float[3];
	/**
	 * This is used to decide whether the mouse is the first time moving in the
	 * window this paramete is used in the method of mouseentered to initialize
	 * eyelookatevector
	 */
	public boolean fistmove = true;
	/** To decide the mouse in window or not */
	public boolean insidewin = true;
	/** This is used to choose the side*/
	private int side = 0; 

	//////// The state the parameters about body and limps//////

	/**
	 * Parameters about Head Head is made up one sphere and one ellipse
	 * 
	 * @param headradius1
	 *            and headradius2 refer to radius of two sphere
	 * @param scaling
	 *            divide the scaling of ellipse
	 */
	private final Component Initial = new Component(new Point3D(0, 0, 0), "Initial");

	private final Component head;
	private final double headradius1;
	private final double headradius2;
	private final double headscaling;
	private final double neckradius;
	private final double neckhight;

	/**
	 * Antenna has two parts, the base parts which is connected to head and the
	 * top part. base part contains BN top sphere cylinder and top parts
	 * contains TN top sphere cylinder However, there are no joints between
	 * cylinders of the base part.
	 * 
	 * @param basepartangle
	 *            every part of basepart will tilt some angle
	 * @param toppartangle
	 *            every part of toppart will tilt some angle
	 */
	private int BN;
	private int TN;
	private final Component[] Antennabasepart = new Component[2];
	private final Component[][] Antennatoppart = new Component[2][10];
	private final double Antennabasepartradius;
	private final double Antennatoppartradius;
	private final double Antennabasepartangle;
	private final double Antennatoppartangle;
	private final double Antennabasehight;
	private final double Antennatophight;
	private final Antenna Ante;

	/**
	 * Parameters about Body Body is a ellipse The location of the center of
	 * body is our INITIAL position
	 */
	private final Component body;
	private final double bodyradius;
	private final double bodyangle;
	private final double bodyscaling;

	/** Parameters about lower part of body. Lower body is made up of a ellipse*/
	private final Component lowerbody;
	private final double lowerbodyradius;
	private final double lowerbdoyscaling;

	/**
	 * Parameter about Arm Arm contains four parts -- base part, middle part,
	 * selective part and last part First part is a ellipse, middle part and
	 * selective part are cylinder with sphere top The last part is palm which
	 * is a ellipse
	 */
	private final Limbs[] Arm = new Limbs[2];
	private final Component[] upperarm = new Component[2];
	private final Component[] middlearm = new Component[2];
	private final Component[] thirdarm = new Component[2];
	private final Component[] palm = new Component[2];
	private final double upperarmradius;
	private final double upperarmscaling;
	private final double middlearmradius;
	private final double thirdarmradius;
	private final double middlearmhight;
	private final double thirdarmhight;
	private final double palmradius;

	/**
	 * Suppose every finger has three part, distal part, middle part and palm
	 * part connected to palm And every part is a top sphere cylinder.
	 */

	private final Limbs[][] finger = new Limbs[2][5];
	private final Component[] distal = new Component[2];
	private final Component[] middle = new Component[2];
	private final Component[] palmpart = new Component[2];
	private final double distalhight;
	private final double middlehight;
	private final double palmparthight;
	private final double fingerradius;
	private final double[][] palmpartposition = { { -0.12, 0.2 }, 
												{ -0.17, 0.75 * 0.58 }, 
												{ -0.05, 0.95 * 0.56 },
												{ 0.07, 0.9 * 0.58 }, 
												{ 0.19, 0.7 * 0.58 } };

	/**
	 * Leg is composed of three parts -- base part, middle part, last part The
	 * first part contains a ellipse. Second part is a cylinder with
	 * top sphere. Third part is the same with second part
	 */
	private final Limbs[] Leg = new Limbs[2];
	private final Component[] upperleg = new Component[2];
	private final Component[] middleleg = new Component[2];
	private final Component[] thirdleg = new Component[2];
	private final double upperlegradius1;
	private final double upperscaling1;
	private final double middlelegradius;
	private final double middleleghight;
	private final double thirdlegradius;
	private final double thirdleghight;
	
	/** eyes is sphere and the pupil is sphere too*/
	private final Component[] eyes = new Component[2];
	private final Component[] pupil = new Component[2];
	private final double eyeredius;
	private final double cerredius;



	///////Name all part we just create ////////
	
	public static String BODY = "body";
	public static String LOWERBODY = "lower body";
	public static String HEAD = "head";
	public static String[][] FINGER = {
			{ "l thumb palm", "l thump middle", "l thump distal", "l index palm", "l index middle", "l index distal",
					"l middle palm", "l middle middle", "l middle distal", "l ring palm", "l ring middle",
					"l ring distal", "l pinky palm", "l pinky middle", "l pinky distal" },
			{ "r thumb palm", "r thump middle", "r thump distal", "r index palm", "r index middle", "r index distal",
					"r middle palm", "r middle middle", "r middle distal", "r ring palm", "r ring middle",
					"r ring distal", "r pinky palm", "r pinky middle", "r pinky distal" } };
	public static String[][] LEG = { { "l upperleg", "l middleleg", "l thirdleg" },
			{ "r upperleg", "r middleleg", "r thirdleg" } };
	public static String[][] ARM = { { "l upperarm", "l middlearm", "l thirdarm", "l palm" },
			{ "r upperarm", "r middlearm", "r thirdarm", "r palm" } };
	public static String[] ABP = { "l antennabasepart", "r antennabasepart" };


	/**
	 * Runs the hand simulation in a single JFrame.
	 * 
	 * @param args
	 *            This parameter is ignored.
	 */
	public static void main(final String[] args) {
		new PA2().animator.start();
	}

	/**
	 * Initializes the necessary OpenGL objects and adds a canvas to this
	 * JFrame.
	 */
	public PA2() {
		this.capabilities.setDoubleBuffered(true);

		this.canvas = new GLCanvas(this.capabilities);
		this.canvas.addGLEventListener(this);
		this.canvas.addMouseListener(this);
		this.canvas.addMouseMotionListener(this);
		this.canvas.addKeyListener(this);
		// this is true by default, but we just add this line to be explicit
		this.canvas.setAutoSwapBufferMode(true);
		this.getContentPane().add(this.canvas);

		// refresh the scene at 60 frames per second
		this.animator = new FPSAnimator(this.canvas, 60);

		this.setTitle("CS680 : A Cartoon Ant");
		this.setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		///////// Initialize all the parts we created before ////////
		
		/* First initialize Body*/
		
		this.bodyangle = -65;
		this.bodyradius = 0.7;
		this.bodyscaling = 1.68;
		this.body = new Component(new Point3D(0, 0, 0), new Body(bodyradius, bodyangle, bodyscaling, this.glut), BODY);
		body.setColor(new FloatColor(1f, 1f, 1f));
		
		/*Initialize eyes*/
		
		this.eyeredius = 0.13;
		this.cerredius = 0.05;
		eyes[0] = new Component(new Point3D(-0.4, 1.4, 1.15), new sphere(eyeredius, glut), "eye0");
		pupil[0] = new Component(new Point3D(0, 0, eyeredius), new sphere(cerredius, glut), "pupil0");
		eyes[1] = new Component(new Point3D(-0.85, 1.37, 1), new sphere(eyeredius, glut), "eye1");
		pupil[1] = new Component(new Point3D(0, 0, eyeredius), new sphere(cerredius, glut), "pupil1");
		eyes[0].setColor(new FloatColor(1, 1, 1));
		eyes[1].setColor(new FloatColor(1, 1, 1));
		pupil[0].setColor(new FloatColor(0, 0, 0));
		pupil[1].setColor(new FloatColor(0, 0, 0));
		
		/* Initialize lower body */
		
		this.lowerbodyradius = 0.65;
		this.lowerbdoyscaling = 1.3;
		this.lowerbody = new Component(new Point3D(0, lowerbodyradius, -bodyradius * bodyscaling),
				new Ellipse(lowerbodyradius, lowerbdoyscaling, glut, true), LOWERBODY);
		this.lowerbody.setColor(new FloatColor(1f, 1f, 1f));
		
		/*Initialize head*/
		
		this.headradius1 = 0.5;
		this.headradius2 = 0.55;
		this.headscaling = 1.4;
		this.neckhight = 0.4;
		this.neckradius = 0.4;
		this.head = new Component(new Point3D(0, 0, bodyradius * bodyscaling * 0.7),
				new Head(headradius1, headradius2, headscaling, neckradius, neckhight, glut), HEAD);

		/* Initialize Arms and Legs */
		
		upperarmradius = 0.28;
		upperarmscaling = 1.88;
		middlearmradius = 0.20;
		thirdarmradius = 0.19;
		middlearmhight = 0.51;
		thirdarmhight = 0.498;
		palmradius = 0.30;

		upperlegradius1 = 0.28;
		upperscaling1 = 2.2;
		middlelegradius = 0.2;
		middleleghight = 0.76;
		thirdlegradius = 0.21;
		thirdleghight = 0.4;

		for (int i = 0; i < 2; i++) {
			upperleg[i] = new Component(
					new Point3D(Math.pow(-1, i) * 0.6 * lowerbodyradius,
							-0.5 * (lowerbodyradius + upperlegradius1 * upperscaling1), 0),
					new Ellipse(upperlegradius1, upperscaling1, glut, false), LEG[i][0]);
			middleleg[i] = new Component(new Point3D(0, 0, (upperlegradius1 * upperscaling1) * 0.7),
					new RoundedHeadCylinder(middlelegradius, middleleghight, glut), LEG[i][1]);
			thirdleg[i] = new Component(new Point3D(0, 0, middleleghight),
					new RoundedCylinder(thirdlegradius, thirdleghight, glut, false), LEG[i][2]);
			Leg[i] = new Limbs(upperleg[i], middleleg[i], null, thirdleg[i]);

			upperarm[i] = new Component(
					new Point3D(Math.pow(-1, i) * 0.65 * bodyradius, -0.06, 0.5 * bodyradius * bodyscaling),
					new Ellipse(upperarmradius, upperarmscaling, glut, false), ARM[i][0]);
			middlearm[i] = new Component(new Point3D(0, 0, upperarmradius * upperarmscaling * 0.7),
					new RoundedHeadCylinder(middlearmradius, middlearmhight, glut), ARM[i][1]);
			thirdarm[i] = new Component(new Point3D(0, 0, middlearmhight),
					new RoundedCylinder(thirdarmradius, thirdarmhight, glut, false), ARM[i][2]);
			palm[i] = new Component(new Point3D(0, 0, thirdarmhight), new Palm(palmradius, glut), ARM[i][3]);
			Arm[i] = new Limbs(upperarm[i], middlearm[i], thirdarm[i], palm[i]);
		}
		
		/* Initialize Antenna */
		
		BN = 5;
		TN = 10;
		Antennabasepartradius = 0.055;
		Antennatoppartradius = 0.03;
		Antennabasepartangle = 5;
		Antennatoppartangle = 10;
		Antennabasehight = 0.12;
		Antennatophight = 0.15;
		Ante = new Antenna(Antennabasepartradius, Antennabasehight, BN, Antennabasepartangle, glut);

		for (int i = 0; i < 2; i++) {
			Antennabasepart[i] = new Component(
					new Point3D(Math.pow(-1, i) * 0.6 * headradius2, 0, 0.7 * (headradius1 + headradius2 + neckhight)),
					Ante, "ab1");
			Antennatoppart[i][0] = new Component(new Point3D(0, Ante.Y, Ante.Z),
					new RoundedCylinder(Antennatoppartradius, Antennatophight, glut, true), "at1");
			Antennatoppart[i][0].setColor(new FloatColor(1, 1, 1));
			for (int j = 1; j < TN; j++) {
				Antennatoppart[i][j] = new Component(new Point3D(0, 0, Antennatophight),
						new RoundedCylinder(Antennatoppartradius, Antennatophight, glut, true), "at1");
				Antennatoppart[i][j].setColor(new FloatColor(1, 1, 1));
			}
		}

		/* Initialize fingers */
		
		distalhight = 0.08;
		middlehight = 0.12;
		palmparthight = 0.2;
		fingerradius = 0.06;

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				palmpart[i] = new Component(
						new Point3D(Math.pow(-1, i) * palmpartposition[j][0], 0, palmpartposition[j][1]),
						new RoundedCylinder(fingerradius, palmparthight, glut, false), FINGER[i][j * 3]);
				middle[i] = new Component(new Point3D(0, 0, palmparthight),
						new RoundedCylinder(fingerradius, middlehight, glut, false), FINGER[i][j * 3 + 1]);
				distal[i] = new Component(new Point3D(0, 0, middlehight),
						new RoundedCylinder(fingerradius, distalhight, glut, false), FINGER[i][j * 3 + 2]);
				finger[i][j] = new Limbs(palmpart[i], middle[i], null, distal[i]);
			}
		}

		/////// connect all parts we build ////////

		Initial.addChildren(body, eyes[0], eyes[1]);
		body.addChildren(upperarm[0], upperarm[1], head, lowerbody);
		lowerbody.addChildren(upperleg[0], upperleg[1]);
		head.addChild(Antennabasepart[0]);
		head.addChild(Antennabasepart[1]);
		eyes[1].addChild(pupil[1]);
		eyes[0].addChild(pupil[0]);

		Antennabasepart[1].addChild(Antennatoppart[1][0]);
		Antennabasepart[0].addChild(Antennatoppart[0][0]);

		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < TN - 1; i++) {
				Antennatoppart[j][i].rotate(Axis.X, Antennatoppartangle);
				Antennatoppart[j][i].addChild(Antennatoppart[0][i + 1]);
				Antennatoppart[j][i + 1].rotate(Axis.X, Antennatoppartangle);
			}
		}

		for (int i = 0; i < 2; i++) {
			upperarm[i].addChild(middlearm[i]);
			middlearm[i].addChild(thirdarm[i]);
			thirdarm[i].addChild(palm[i]);
			upperleg[i].addChild(middleleg[i]);
			middleleg[i].addChild(thirdleg[i]);
		}

		palm[0].addChildren(finger[0][0].basepart(), finger[0][1].basepart(), finger[0][2].basepart(),
				finger[0][3].basepart(), finger[0][4].basepart());
		palm[1].addChildren(finger[1][0].basepart(), finger[1][1].basepart(), finger[1][2].basepart(),
				finger[1][3].basepart(), finger[1][4].basepart());

		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 5; i++) {
				finger[j][i].basepart().addChild(finger[j][i].middlepart());
				finger[j][i].middlepart().addChild(finger[j][i].lastpart());
			}
		}
		
		///////Set the initial pose of our creature///////
		
		body.rotate(Axis.X, -60);
		body.rotate(Axis.Y, -22);
		body.rotate(Axis.Z, -38);
		lowerbody.rotate(Axis.X, 60);
		head.rotate(Axis.X, -15);
		head.rotate(Axis.Z, 25);
		Antennabasepart[0].rotate(Axis.Y, 10);
		Antennabasepart[1].rotate(Axis.Y, -10);

		for (int i = 0; i < 2; i++) {
			middlearm[i].rotate(Axis.X, 62);
			thirdarm[i].rotate(Axis.X, 60);
			palm[i].rotate(Axis.X, 6);
		}

		middlearm[0].rotate(Axis.Y, 38);
		middlearm[1].rotate(Axis.Y, 14);

		upperarm[0].rotate(Axis.Y, 90);
		upperarm[1].rotate(Axis.Y, -90);

		upperleg[0].rotate(Axis.X, 89);
		upperleg[1].rotate(Axis.X, 89);
		thirdleg[0].rotate(Axis.X, -89);
		thirdleg[1].rotate(Axis.X, -89);

		finger[1][0].basepart().rotate(Axis.Y, 50);
		finger[0][0].basepart().rotate(Axis.Y, -50);
		finger[0][0].basepart().rotate(Axis.Z, 65);
		finger[1][0].basepart().rotate(Axis.Z, -65);

		for (int i = 0; i < 2; i++) {
			for (Limbs finger : Arrays.asList(finger[i][1], finger[i][2], finger[i][3], finger[i][4])) {
				finger.basepart().rotate(Axis.X, 18);
				finger.middlepart().rotate(Axis.X, 20);
				finger.lastpart().rotate(Axis.X, 25);
			}
			finger[i][0].middlepart().rotate(Axis.X, 20);
			finger[i][0].lastpart().rotate(Axis.X, 30);
		}



		/*******************************/
		/////// set rotation limits of the some parts/////
		
		for (int i = 0; i < 2; i++) {

			// set rotation limits of arm
			this.upperarm[i].setXPositiveExtent(0);
			this.upperarm[i].setXNegativeExtent(0);
			this.upperarm[i].setZNegativeExtent(0);
			this.upperarm[i].setZPositiveExtent(0);
			this.middlearm[i].setXNegativeExtent(0);
			this.middlearm[i].setXPositiveExtent(62);
			this.middlearm[i].setZNegativeExtent(0);
			this.middlearm[i].setZPositiveExtent(0);
			this.thirdarm[i].setXNegativeExtent(0);
			this.thirdarm[i].setXPositiveExtent(60);
			this.thirdarm[i].setZNegativeExtent(-90);
			this.thirdarm[i].setZPositiveExtent(90);
			this.palm[i].setXNegativeExtent(-6);
			this.palm[i].setXPositiveExtent(6);
			this.palm[i].setZNegativeExtent(0);
			this.palm[i].setZPositiveExtent(0);

			// set rotation limits of leg
			this.upperleg[i].setXNegativeExtent(50);
			this.upperleg[i].setXPositiveExtent(110);
			this.upperleg[i].setZNegativeExtent(-90);
			this.upperleg[i].setZPositiveExtent(90);
			this.middleleg[i].setXNegativeExtent(0);
			this.middleleg[i].setXPositiveExtent(100);
			this.middleleg[i].setYNegativeExtent(0);
			this.middleleg[i].setYPositiveExtent(0);
			this.middleleg[i].setZNegativeExtent(0);
			this.middleleg[i].setZPositiveExtent(0);
			this.thirdleg[i].setXNegativeExtent(-103);
			this.thirdleg[i].setXPositiveExtent(-61);
			this.thirdleg[i].setYNegativeExtent(-6);
			this.thirdleg[i].setYPositiveExtent(6);
			this.thirdleg[i].setZNegativeExtent(0);
			this.thirdleg[i].setZPositiveExtent(0);

			//set rotation limits of finger
			for (final Component basepart : Arrays.asList(finger[i][1].basepart(), finger[i][2].basepart(),
					finger[i][3].basepart(), finger[i][4].basepart())) {
				basepart.setXPositiveExtent(66);
				basepart.setXNegativeExtent(-15);
				basepart.setYPositiveExtent(4);
				basepart.setYNegativeExtent(-4);
				basepart.setZPositiveExtent(0);
				basepart.setZNegativeExtent(0);
			}

			for (final Component middlepart : Arrays.asList(finger[i][1].middlepart(), finger[i][2].middlepart(),
					finger[i][3].middlepart(), finger[i][4].middlepart())) {
				middlepart.setXPositiveExtent(95);
				middlepart.setXNegativeExtent(0);
				middlepart.setYPositiveExtent(0);
				middlepart.setYNegativeExtent(0);
				middlepart.setZPositiveExtent(0);
				middlepart.setZNegativeExtent(0);
			}

			for (final Component lastpart : Arrays.asList(finger[i][0].lastpart(), finger[i][1].lastpart(),
					finger[i][2].lastpart(), finger[i][3].lastpart(), finger[i][4].lastpart())) {
				lastpart.setXPositiveExtent(80);
				lastpart.setXNegativeExtent(0);
				lastpart.setYPositiveExtent(0);
				lastpart.setYNegativeExtent(0);
				lastpart.setZPositiveExtent(0);
				lastpart.setZNegativeExtent(0);
			}
			finger[i][0].basepart().setXNegativeExtent(0);
			finger[i][0].basepart().setXPositiveExtent(0);
			finger[i][0].basepart().setYNegativeExtent(Math.pow(-1, i + 1) * 50);
			finger[i][0].basepart().setYPositiveExtent(Math.pow(-1, i + 1) * 50);
			finger[i][0].basepart().setZNegativeExtent(Math.pow(-1, i) * 65);
			finger[i][0].basepart().setZPositiveExtent(Math.pow(-1, i) * 65);

			finger[i][0].middlepart().setXPositiveExtent(52);
			finger[i][0].middlepart().setXNegativeExtent(0);
			finger[i][0].middlepart().setYNegativeExtent(0);
			finger[i][0].middlepart().setYPositiveExtent(0);
			finger[i][0].middlepart().setZNegativeExtent(0);
			finger[i][0].middlepart().setZPositiveExtent(0);
		}

		//set the rest parts
		this.upperarm[0].setYNegativeExtent(58);
		this.upperarm[0].setYPositiveExtent(108);
		this.upperarm[1].setYNegativeExtent(-108);
		this.upperarm[1].setYPositiveExtent(-58);

		this.middlearm[0].setYNegativeExtent(-24);
		this.middlearm[0].setYPositiveExtent(56);
		this.middlearm[1].setYNegativeExtent(-56);
		this.middlearm[1].setYPositiveExtent(24);

		this.thirdarm[0].setYNegativeExtent(-69);
		this.thirdarm[0].setYPositiveExtent(0);
		this.thirdarm[1].setYNegativeExtent(0);
		this.thirdarm[1].setYPositiveExtent(69);

		this.palm[0].setYNegativeExtent(-4);
		this.palm[0].setYPositiveExtent(12);
		this.palm[1].setYNegativeExtent(-12);
		this.palm[1].setYPositiveExtent(4);

		this.upperleg[0].setYNegativeExtent(0);
		this.upperleg[0].setYPositiveExtent(26);
		this.upperleg[1].setYNegativeExtent(-26);
		this.upperleg[1].setYPositiveExtent(0);

		this.head.setXNegativeExtent(-15);
		this.head.setXPositiveExtent(-15);
		this.head.setYNegativeExtent(0);
		this.head.setYPositiveExtent(0);
		this.head.setZNegativeExtent(25);
		this.head.setZPositiveExtent(25);
		
		this.body.setXNegativeExtent(-60);
		this.body.setXPositiveExtent(-60);
		this.body.setYNegativeExtent(-22);
		this.body.setYPositiveExtent(-22);
		this.body.setZNegativeExtent(-38);
		this.body.setZPositiveExtent(-38);

	}

	/**
	 * Redisplays the scene containing the hand model.
	 * 
	 * @param drawable
	 *            The OpenGL drawable object with which to create OpenGL models.
	 */
	public void display(final GLAutoDrawable drawable) {
		final GL2 gl = (GL2) drawable.getGL();

		// clear the display
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// from here on affect the model view
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		// start with the identity matrix initially
		gl.glLoadIdentity();
		
		// rotate the world by the appropriate rotation quaternion
		gl.glMultMatrixf(this.viewing_quaternion.toMatrix(), 0);

		// update the position of the components which need to be updated
		// TODO only need to update the selected and JUST deselected components
		if (this.stateChanged) {
			this.Initial.update(gl);
			this.stateChanged = false;
		}
		// redraw the components
		this.Initial.draw(gl);
	}

	/**
	 * This method is intentionally unimplemented.
	 * 
	 * @param drawable
	 *            This parameter is ignored.
	 * @param modeChanged
	 *            This parameter is ignored.
	 * @param deviceChanged
	 *            This parameter is ignored.
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		// intentionally unimplemented
	}

	/**
	 * Initializes the scene and model.
	 * 
	 * @param drawable
	 *            {@inheritDoc}
	 */
	public void init(final GLAutoDrawable drawable) {
		final GL2 gl = (GL2) drawable.getGL();

		// perform any initialization needed by the hand model
		this.Initial.initialize(gl);
		// initially draw the scene
		this.Initial.update(gl);
		// set up for shaded display of the hand
		final float light0_position[] = { 1, 1, 1, 0 };
		final float light0_ambient_color[] = { 0.25f, 0.25f, 0.25f, 1 };
		final float light0_diffuse_color[] = { 1, 1, 1, 1 };

		gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL2.GL_SMOOTH);

		// set up the light source
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_position, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0_ambient_color, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse_color, 0);

		// turn lighting and depth buffering on
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
	}

	/**
	 * Interprets key presses according to the following scheme:
	 * 
	 * up-arrow, down-arrow: increase/decrease rotation angle
	 * 
	 * @param key
	 *            The key press event object.
	 */
	public void keyPressed(final KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_KP_UP:
		case KeyEvent.VK_UP:
			for (final Component component : this.selectedComponents) {
				component.rotate(this.selectedAxis, ROTATION_ANGLE);
			}
			this.stateChanged = true;
			break;
		case KeyEvent.VK_KP_DOWN:
		case KeyEvent.VK_DOWN:
			for (final Component component : this.selectedComponents) {
				component.rotate(this.selectedAxis, -ROTATION_ANGLE);
			}
			this.stateChanged = true;
			break;
		default:
			break;
		}
	}

	/**
	 * This method is intentionally unimplemented.
	 * 
	 * @param key
	 *            This parameter is ignored.
	 */
	public void keyReleased(final KeyEvent key) {
		// intentionally unimplemented
	}

	private final TestCases testCases = new TestCases();

	// we just sent the part we need to make some poses, like arm, leg, and finger.
	// to the parts, like head, body, we do not use them to make pose, so we do not make them in the Map
	private void setModelState(final Map<String, Angled> state) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				this.finger[i][j].basepart().setAngles(state.get(FINGER[i][j * 3]));
				this.finger[i][j].middlepart().setAngles(state.get(FINGER[i][j * 3 + 1]));
				this.finger[i][j].lastpart().setAngles(state.get(FINGER[i][j * 3 + 2]));
			}

			this.Arm[i].basepart().setAngles(state.get(ARM[i][0]));
			this.Arm[i].middlepart().setAngles(state.get(ARM[i][1]));
			this.Arm[i].selectivepart().setAngles(state.get(ARM[i][2]));
			this.Arm[i].lastpart().setAngles(state.get(ARM[i][3]));

			this.Leg[i].basepart().setAngles(state.get(LEG[i][0]));
			this.Leg[i].middlepart().setAngles(state.get(LEG[i][1]));
			this.Leg[i].lastpart().setAngles(state.get(LEG[i][2]));
		}
		this.stateChanged = true;
	}

	/**
	 * Interprets typed keys according to the following scheme:
	 * 
	 * 1 : choose the thumb finger
	 * 
	 * 2 : choose the index finger
	 * 
	 * 3 : choose the middle finger
	 * 
	 * 4 : choose the ring finger
	 * 
	 * 5 : choose the pinky finger
	 * 
	 * 9 : to change left side to right side or from right to left
	 * 
	 * X : use the X axis rotation at the active joint(s)
	 * 
	 * Y : use the Y axis rotation at the active joint(s)
	 * 
	 * Z : use the Z axis rotation at the active joint(s)
	 * 
	 * C : resets the hand to the stop sign
	 * 
	 * B : select the base joint
	 * 
	 * M : select middle joint
	 * 
	 * s : select selective joint (for the arm)
	 * 
	 * f : select the last joint
	 * 
	 * R : resets the view to the initial rotation
	 * 
	 * Q, Esc : exits the program
	 * 
	 */
	public void keyTyped(final KeyEvent key) {
		switch (key.getKeyChar()) {
		case 'Q':
		case 'q':
		case KeyEvent.VK_ESCAPE:
			new Thread() {
				@Override
				public void run() {
					PA2.this.animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		// resets to the stop sign
		case 'C':
		case 'c':
			this.setModelState(this.testCases.stop());
			break;

		// set the state of the hand to the next test case
		case 'T':
		case 't':
			this.setModelState(this.testCases.next());
			break;

		// set the viewing quaternion to 0 rotation
		case 'R':
		case 'r':
			this.viewing_quaternion.reset();
			break;

		// To choose the Left and right
		case 'l':
		case 'L':
			System.out.println("we select leg");
			toggleSelection(this.Leg[side]);
			break;
		case 'a':
		case 'A':
			System.out.println("we select arm" );
			toggleSelection(this.Arm[side]);
			break;
		case '1':
			toggleSelection(this.finger[side][0]);
			System.out.println("we select thumb finger" );
			break;
		case '2':
			toggleSelection(this.finger[side][1]);
			System.out.println("we select index finger");
			break;
		case '3':
			toggleSelection(this.finger[side][2]);
			System.out.println("we select middle finger");
			break;
		case '4':
			toggleSelection(this.finger[side][3]);
			System.out.println("we select ring finger");
			break;
		case '5':
			toggleSelection(this.finger[side][4]);
			System.out.println("we select pinky finger");
			break;
		case '9':
			side = (side + 1) % 2;
			if (side == 1)
				System.out.println("left side" );
			else
				System.out.println("right side" );
			break;
		case 'B':
		case 'b':
			setbasepart();
			break;
		case 'M':
		case 'm':
			setmiddlepart();
			break;
		case 'S':
		case 's':
			setsectivepart();
			;
			break;
		case 'F':
		case 'f':
			setlastpart();
			break;
		case 'X':
		case 'x':
			this.selectedAxis = Axis.X;
			break;
		case 'Y':
		case 'y':
			this.selectedAxis = Axis.Y;
			break;
		case 'Z':
		case 'z':
			this.selectedAxis = Axis.Z;
			break;
		default:
			break;
		}
	}

	private void setbasepart() {
		for (final Limbs Joints : this.selectedJoints)
			toggleSelection(Joints.basepart());
	}

	private void setmiddlepart() {
		for (final Limbs Joints : this.selectedJoints)
			toggleSelection(Joints.middlepart());
	}

	private void setsectivepart() {
		for (final Limbs Joints : this.selectedJoints)
			toggleSelection(Joints.selectivepart());
	}

	private void setlastpart() {
		for (final Limbs Joints : this.selectedJoints)
			toggleSelection(Joints.lastpart());
	}

	/**
	 * This method is intentionally unimplemented.
	 * 
	 * @param mouse
	 *            This parameter is ignored.
	 */
	public void mouseClicked(MouseEvent mouse) {
		// intentionally unimplemented
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
			final double magnitude = Math.sqrt(dx * dx + dy * dy);
			final float[] axis = magnitude == 0 ? new float[] { 1, 0, 0 }
					: // avoid dividing by 0
					new float[] { (float) (dy / magnitude), (float) (dx / magnitude), 0 };

			// calculate appropriate quaternion
			final float viewing_delta = 3.1415927f / 180.0f * 2.5f;
			final float s = (float) Math.sin(0.5f * viewing_delta);
			final float c = (float) Math.cos(0.5f * viewing_delta);
			final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
			this.viewing_quaternion = Q.multiply(this.viewing_quaternion);
			
			//There we have inverse quaternion for the uses in mouse moving. We translate mouse coordinate relatively
			this.inverse_viewing_quaternion = this.viewing_quaternion.inverse();
			
			// normalize to counteract acccumulating round-off error
			this.inverse_viewing_quaternion.normalize();

			// normalize to counteract acccumulating round-off error
			this.viewing_quaternion.normalize();

			// save x, y as last x, y
			this.last_x = x;
			this.last_y = y;
		}
	}

	/**
	 * This method is intentionally unimplemented.
	 * 
	 * @param mouse
	 *            This parameter is ignored.
	 */
	public void mouseEntered(MouseEvent mouse) {
		// if this is the first time moving in the window, we initialize eyelookatvector as (0,0,1)
		if (fistmove) {
			eyelookatvector[0] = 0;
			eyelookatvector[1] = 0;
			eyelookatvector[2] = 1;
			fistmove = false;
		}
		insidewin = true;
		// intentionally unimplemented
	}

	/**
	 * This method is intentionally unimplemented.
	 * 
	 * @param mouse
	 *            This parameter is ignored.
	 */
	public void mouseExited(MouseEvent mouse) {
		insidewin = false;
		// intentionally unimplemented
	}

	/**
	 * This method is intentionally unimplemented.
	 * 
	 * @param mouse
	 *            This parameter is ignored.
	 */
	public float[] nomalized(float[] vector) {
		int n;
		float magnituide = 0;
		n = vector.length;
		for (int i = 0; i < n; i++)
			magnituide += vector[i] * vector[i];
		for (int i = 0; i < n; i++)
			vector[i] = (float) (vector[i] / Math.sqrt(magnituide));
		return vector;
	}

	public float[] MatrixMultply(float[] M1, float[] A1) {
		float[] A2 = new float[3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				A2[i] += M1[i + j * 4] * A1[j];
		return A2;
	}

	// TODO
	public void mouseMoved(MouseEvent mouse) {
		float x, y;
		int Winwidth;
		int Winheight;
		float[] mousevector = new float[3];
		float[] mouse_cor = new float[3];
		float[] rotation_axis = new float[3];
		float pointproduct = 0;
		float angle;

		//get the size of window for scaling
		Winwidth = this.getWidth();
		Winheight = this.getHeight();
		
		//scaling our x, y to some extent, then that can have a angle when eye follow the mouse
		x = (float) (-1 + (float) (mouse.getX()) / (float) (Winwidth) * 2);
		y = (float) (2.2 - (float) (mouse.getY()) / (float) (Winheight) * 4.4);
		mouse_cor[0] = x;
		mouse_cor[1] = y;
		mouse_cor[2] = 2.4f;

		//if the world is rotated by dragged, then our mouse should be rotate to the inverse direction relatively
		mouse_cor = MatrixMultply(inverse_viewing_quaternion.toMatrix(), mouse_cor);

		//make the vector from eye to the position of mouse
		mousevector[0] = mouse_cor[0] - eyesmiddleposition[0];
		mousevector[1] = mouse_cor[1] - eyesmiddleposition[1];
		mousevector[2] = mouse_cor[2] - eyesmiddleposition[2];

		//Normalize to vector
		eyelookatvector = nomalized(eyelookatvector);
		mousevector = nomalized(mousevector);

		//make cross product to compute the rotation axis
		rotation_axis[0] = (float) (eyelookatvector[1] * mousevector[2] - eyelookatvector[2] * mousevector[1]);
		rotation_axis[1] = (float) (-eyelookatvector[0] * mousevector[2] + eyelookatvector[2] * mousevector[0]);
		rotation_axis[2] = (float) (eyelookatvector[0] * mousevector[1] - eyelookatvector[1] * mousevector[0]);

		rotation_axis = nomalized(rotation_axis);

		//compute point product for getting the angle between two vector
		for (int i = 0; i < 3; i++) {
			pointproduct += eyelookatvector[i] * mousevector[i];
		}
		angle = (float) Math.acos(pointproduct);

		//if the angle is too small then we do not rotate
		if (angle > 0.04) {
			//compute the quaternion for rotation
			final float s = (float) Math.sin(0.5f * angle);
			final float c = (float) Math.cos(0.5f * angle);
			mousemove = new Quaternion(c, s * rotation_axis[0], s * rotation_axis[1], s * rotation_axis[2]);
			mousemove.normalize();

			//rotate eyes by this quaternion
			eyes[0].rotate(mousemove.toMatrix());
			eyes[1].rotate(mousemove.toMatrix());

			//update eyelookatvector
			eyelookatvector[0] = mousevector[0];
			eyelookatvector[1] = mousevector[1];
			eyelookatvector[2] = mousevector[2];

			stateChanged = true;
		}
	}

	/**
	 * Starts rotating the world if the left mouse button was released.
	 * 
	 * @param mouse
	 *            The mouse press event object.
	 */
	public void mousePressed(final MouseEvent mouse) {
		if (mouse.getButton() == MouseEvent.BUTTON1) {
			this.last_x = mouse.getX();
			this.last_y = mouse.getY();
			this.rotate_world = true;
		}
	}

	/**
	 * Stops rotating the world if the left mouse button was released.
	 * 
	 * @param mouse
	 *            The mouse release event object.
	 */
	public void mouseReleased(final MouseEvent mouse) {
		if (mouse.getButton() == MouseEvent.BUTTON1) {
			this.rotate_world = false;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param drawable
	 *            {@inheritDoc}
	 * @param x
	 *            {@inheritDoc}
	 * @param y
	 *            {@inheritDoc}
	 * @param width
	 *            {@inheritDoc}
	 * @param height
	 *            {@inheritDoc}
	 */
	public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
		final GL2 gl = (GL2) drawable.getGL();

		// prevent division by zero by ensuring window has height 1 at least
		final int newHeight = Math.max(1, height);

		// compute the aspect ratio
		final float ratio = (float) width / newHeight;

		// reset the projection coordinate system before modifying it
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// set the viewport to be the entire window
		gl.glViewport(0, 0, width, newHeight);

		// set the clipping volume
		this.glu.gluPerspective(25, ratio, 0.1, 100);

		// camera positioned at (0,0,6), look at point (0,0,0), up vector
		// (0,1,0)
		this.glu.gluLookAt(0, 0, 14, 0, 0, 0, 0, 1, 0);

		// switch back to model coordinate system
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	private void toggleSelection(final Component component) {
		if (this.selectedComponents.contains(component)) {
			this.selectedComponents.remove(component);
			component.setColor(INACTIVE_COLOR);
		} else {
			this.selectedComponents.add(component);
			component.setColor(ACTIVE_COLOR);
		}
		this.stateChanged = true;
	}

	private void toggleSelection(final Limbs Joints1) {
		if (this.selectedJoints.contains(Joints1)) {
			this.selectedJoints.remove(Joints1);
			this.selectedComponents.removeAll(Joints1.joints());
			for (final Component joint : Joints1.joints())
				joint.setColor(INACTIVE_COLOR);
		} else
			this.selectedJoints.add(Joints1);
		this.stateChanged = true;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}
}
