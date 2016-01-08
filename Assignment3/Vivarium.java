
import javax.lang.model.type.PrimitiveType;
import javax.management.loading.PrivateClassLoader;
import javax.media.opengl.*;
import javax.swing.GroupLayout.Alignment;

import com.jogamp.opengl.util.*;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Zephyr;

import java.awt.print.Printable;
import java.util.*;

public class Vivarium {
	private Tank tank;
	private int fishnumber = 7;
	private int foodnumber = 0;
	private List<fish> ellipse = new ArrayList<fish>();
	private List<fish> fishlist = new ArrayList<fish>();
	private List<food> foodlist = new ArrayList<food>();
	private List<fish> triangle = new ArrayList<fish>();
	private shark s;
	private float Tanklength = 10;
	private float Tankwidth = 10;
	private float Tankheight = 10;
		
	public Vivarium() {
		tank = new Tank(Tanklength, Tankwidth, Tankheight);
		
		ellipse.add(new ellipsefish(-4, 2, -4, 0));
		ellipse.get(0).setcolor(1, 0, 0);
		ellipse.add(new ellipsefish(-4, 3, 2, 1));
		ellipse.get(1).setcolor(0, 1, 0);
		ellipse.add(new ellipsefish(-3, -2, 1, 2));
		ellipse.get(2).setcolor(0, 0, 1);
		
		triangle.add(new trianglefish(3, 0, -3.5f, 0));
		triangle.get(0).setcolor(1, 1, 0);
		triangle.add(new trianglefish(2, 1, -1, 1));
		triangle.get(1).setcolor(1, 0, 1);
		triangle.add(new trianglefish(4, -3, -1, 2));
		triangle.get(2).setcolor(0, 1, 1);
		
		s = new shark(0, -4, 0, 1);
		s.setcolor(0.5f, 0.5f, 0.8f);
		
		fishlist.add(ellipse.get(0));
		fishlist.add(ellipse.get(1));
		fishlist.add(ellipse.get(2));
		fishlist.add(triangle.get(0));
		fishlist.add(triangle.get(1));
		fishlist.add(triangle.get(2));
		fishlist.add(s);
	}

	public void init(GL2 gl) {	
		tank.init(gl);
		for (int i = 0; i < fishnumber; i++)
			fishlist.get(i).init(gl);		
	}

	public void update(GL2 gl, int refresh) {
		tank.update(gl);
		settargetdirection();
		
		for (int i = 0; i < foodnumber; i++)
			foodlist.get(i).update(gl);
		
		for (int i = 0; i < fishnumber; i++)
			fishlist.get(i).update(gl, refresh);
		
		collisiondetective();
	}

	private void collisiondetective() {
		// this is the force of wall when collide with wall
		float[][] walldirection = { { -1, 0, 0 }, { 0, -1, 0 }, { 0, 0, -1 } };
		// this is to check collision between fish[i] and fish[j]
		for (int i = 0; i < fishnumber; i++) {
			// if a fish already collide with something, then it should change it position
			// and direction, so it cannot cause collision again. if there is a collision
			// it must be other fish collide with it.
			if (fishlist.get(i).collision == true)
				continue;
			for (int j = 0; j < fishnumber; j++) {
				if (i != j && Distance(fishlist.get(i).position,
						fishlist.get(j).position) < (fishlist.get(i).R + fishlist.get(j).R)) {
					// if the shark collision with other fish, then that means the shark eat it
					// so we just delete the fish from the list
					if (fishlist.get(i).alpha == s.alpha || fishlist.get(j).alpha == s.alpha) {
						if (fishlist.get(i).alpha == s.alpha) {
							fishlist.remove(j);
							fishnumber -= 1;
							j -= 1;
							break;
						} 
					else {
							fishlist.remove(i);
							fishnumber -= 1;
							i -= 1;
							break;
						}
					}
					// if there is no shark in the two colliding fish, then we should adjust the 
					// the position and direction of one of the fish, say, fish[i]
					float[] force = new float[3];
					// the force to change direction is got from potential function:
					// (q-p)'(q-p)
					force[0] = fishlist.get(i).position[0] - fishlist.get(j).position[0];
					force[1] = fishlist.get(i).position[1] - fishlist.get(j).position[1];
					force[2] = fishlist.get(i).position[2] - fishlist.get(j).position[2];
					// we just need the direction, so we normalize it
					normalize(force);
					
					fishlist.get(i).collision = true;
					response(fishlist.get(i), force);
				}
			}
		}
		// this is to detect collision between fishes and wall
		for (int j = 0; j < fishnumber; j++) {
			for (int i = 0; i < 3; i++) {
				if ((float) Math.abs(fishlist.get(j).position[i]) > (Tankheight/2 - fishlist.get(j).R)) {
					float sign = Math.signum(fishlist.get(j).position[i]);
					// this is to specify the force of the wall that collide with
					walldirection[i][i] = walldirection[i][i] * sign;
					
					fishlist.get(j).collision = true;
					response(fishlist.get(j), walldirection[i]);
				}
			}
		}
		// this is to detect collision between fishes and food, if collision happen, the food will be eaten
		for (int i = 0; i < foodnumber; i++){
			for (int j = 0; j < fishnumber; j++){
				float distance = Distance(foodlist.get(i).position, fishlist.get(j).position);
				if (distance < foodlist.get(i).R + fishlist.get(j).R) {
					foodlist.remove(i);
					foodnumber -= 1;
					i -= 1;
					break;
				}
			}
		}	
	}

	// this is the method to make response to collision
	private void response(fish f, float[] force) {
	
		float[] targetV = new float[3];

		float cos;
		// because both of vector force and Current direction vector are unit vector,
		// the cross product of these two vector is cos(ß) ß is the angle between this two vector
		cos = force[0]*f.Currentdirectionvector[0]+force[1]*f.Currentdirectionvector[1]+force[2]*f.Currentdirectionvector[2];
		// we use the reflection to response vector
		// 2 is a factor to adjust the importance of collision. if the parameter there is bigger than 2, that means
		// the collision have more influence.
		for (int i = 0; i < 3; i++)
			targetV[i] = 2 * (-2 * cos * force[i] + (1) * f.Currentdirectionvector[i]);		
		// set Target direction vector
		for (int i = 0; i < 3; i++)
			f.Targetdirectionvector[i] = targetV[i];
		SetTargetAngle(f);
	}

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

	// this is the method to set target direction
	// all the movement of fishes is caused by target direction, they want to move to 
	// their target. So all the factors that effect fishes' movement are actually effect
	// the target direction
	// every time when we compute the effect caused by external, we need to decide two things
	// the first one is, how the other things effect the current fish? that means the orientation of force vector
	// the second one is, how important this effect is? that means the magnitude of force vector
	// we use potential function (p-q)'(p-q) the solve the orientation and used model of Coulomb's law to set magnitude
	private void settargetdirection(){
		// when a fish in the status of collision, the prime thing it should do is avoiding 
		// collision. So nothing of this can effect its status.
		Cohesion(ellipse);
		Cohesion(triangle);
		Interaction();
		Alignment(ellipse);
		Alignment(triangle);
		SearchFood();
		EverageSpeed(ellipse);
		EverageSpeed(triangle);
		
		for (int i = 0; i < fishnumber; i++)
			if (fishlist.get(i).collision == false)
				SetTargetAngle(fishlist.get(i));
	}
	
	private void Cohesion(List<fish> Fishes){
		// this adjust degree is used to adjust the importance of the force caused by Cohesion
		float adjustdegree = 1f;
		float[] everageposition = new float[3];
		float[] cohesionforce = new float[3];
		int n = Fishes.size();
		for (int i = 0; i < n; i++){
			if (Fishes.get(i).collision == true)
				continue;
			everageposition[0] = 0;
			everageposition[1] = 0;
			everageposition[2] = 0;
			cohesionforce[0] = 0;
			cohesionforce[1] = 0;
			cohesionforce[2] = 0;
			for (int j = 0; j < n; j++){
				// every fish want to go the middle place of the rest fish
				if ( i != j ){
					everageposition[0] += Fishes.get(j).position[0];
					everageposition[1] += Fishes.get(j).position[1];
					everageposition[2] += Fishes.get(j).position[2];
				}	
			}
			
			for (int j = 0; j < 3; j++) {
				everageposition[j] /= (n - 1);
				Fishes.get(i).Targetdirectionvector[j] += (everageposition[j] - Fishes.get(i).position[j])/adjustdegree;
				cohesionforce[j] = (everageposition[j] - Fishes.get(i).position[j])/adjustdegree;
			}
		}
	}
	
	// this method is used to realize the interaction between fishes and fish and wall. Specifically, the same kind of fish
	// will exclude with each other, when they are too close. And will small fish want to avoid Shark, if 
	// Shark in a certain scale.
	private void Interaction(){
		for (int i = 0; i < fishnumber; i++){
			// we do not check shark, because there are just one shark and shark do not have to avoid its food
			if (fishlist.get(i).collision == true || fishlist.get(i).alpha == s.alpha)
				continue;
			for (int j = 0; j < fishnumber; j++){
				float distance = Distance(fishlist.get(i).position, fishlist.get(j).position);
				// security distance is to specify the scoop with which fishes begin to interact with each other
				float securitydistance = fishlist.get(i).R + fishlist.get(j).R;
				if (fishlist.get(j).alpha == s.alpha)
					securitydistance *= 3;
				else securitydistance *= 2;
				if (i != j && distance < securitydistance){
					// if the small fish is close to Shark, its speed and turning degree will be larger
					if (fishlist.get(j).alpha == s.alpha){
						fishlist.get(i).speed = (-0.015f*distance)/securitydistance+0.065f;
						fishlist.get(i).turningspeed = (-4*distance*3)/securitydistance+17;
					}
					
					float[] effectvector = new float[3];
					// this is the orientation of the vector
					effectvector[0] = fishlist.get(i).position[0] - fishlist.get(j).position[0];
					effectvector[1] = fishlist.get(i).position[1] - fishlist.get(j).position[1];
					effectvector[2] = fishlist.get(i).position[2] - fishlist.get(j).position[2];
					normalize(effectvector);
					// fish[i].alpha - fish[2].alpha is to detect the type of interact
					// it is exclude force between the same kind of fish? or escape?
					// 1.5 is a parameter to control the importance of this force
					// and we use Coulomb's law here to set the magnitude
					for (int k = 0; k < 3; k++) {
						effectvector[k] *= (1.5 / Math.pow(distance, 2) * (fishlist.get(j).alpha - fishlist.get(i).alpha + 1));
						fishlist.get(i).Targetdirectionvector[k] += effectvector[k];
					}
				}
			}
		}
		
		// for the wall potential
		// the first vector is represent the force of left and right wall, second is the up and down wall
		// third is the front and back wall
		float[][] walldirection = { { -1, 0, 0 }, { 0, -1, 0 }, { 0, 0, -1 } };
		for (int j = 0; j < fishnumber; j++) {
			if (fishlist.get(j).collision == true || fishlist.get(j).alpha == s.alpha)
				continue;
			for (int i = 0; i < 3; i++) {
				// when the fish is in the scope of [0, tankheight/4], it could feel the force from wall
				float distance = Tankheight/2 -(float) Math.abs(fishlist.get(j).position[i]);
				if (distance <= Tankheight/4) {
					// specify which wall is active
					float sign = Math.signum(fishlist.get(j).position[i]);
					walldirection[i][i] = walldirection[i][i] * sign;
					// 0.2 is the parameter to adjust the magnitude of the force 
					fishlist.get(j).Targetdirectionvector[i] += 0.2*walldirection[i][i] * (Tankheight/2 - distance) * 4/Tankheight;
				}
			}
		}
	}
	
	// every fish want to move in the direction of most of the rest fish
	private void Alignment(List<fish> Fishes){
		float[] everagedir = new float[3];
		int n = Fishes.size();
		for (int i = 0; i < n; i++){
			if (Fishes.get(i).collision == true)
				continue;
			everagedir[0] = 0;
			everagedir[1] = 0;
			everagedir[2] = 0;
			for (int j = 0; j < n; j++){
				if ( i != j){
					everagedir[0] += Fishes.get(j).Currentdirectionvector[0];
					everagedir[1] += Fishes.get(j).Currentdirectionvector[1];
					everagedir[2] += Fishes.get(j).Currentdirectionvector[2];
				}	
			}
			normalize(everagedir);

			for (int j = 0; j < 3; j++) {
				Fishes.get(i).Targetdirectionvector[j] += everagedir[j];
			}
		}
	}
	
	private void SearchFood(){
		// all fish search the food
		for (int i = 0; i < fishnumber; i++){
			if (fishlist.get(i).collision == true)
				continue;
			for (int j = 0; j < foodnumber; j++) {
				float distance = Distance(fishlist.get(i).position, foodlist.get(j).position);
				float[] effectvector = new float[3];
				// this is the orientation of the force
				effectvector[0] = foodlist.get(j).position[0] - fishlist.get(i).position[0];
				effectvector[1] = foodlist.get(j).position[1] - fishlist.get(i).position[1];
				effectvector[2] = foodlist.get(j).position[2] - fishlist.get(i).position[2];
				normalize(effectvector);
				for (int k = 0; k < 3; k++) {
					// there we used model of Coulomb's law to set magnitude
					effectvector[k] *= (fishlist.get(i).alpha/ Math.pow(distance, 2));
					fishlist.get(i).Targetdirectionvector[k] += effectvector[k];
				}
			}
		}
		
		// for the shark, it also search small fish
		for (int i = 0; i < fishnumber; i++){
			if (s.collision == true)
				break;
			if (fishlist.get(i).alpha != s.alpha){
				float distance = Distance(fishlist.get(i).position, s.position);
				float[] effectvector = new float[3];
				effectvector[0] = fishlist.get(i).position[0] - s.position[0];
				effectvector[1] = fishlist.get(i).position[1] - s.position[1];
				effectvector[2] = fishlist.get(i).position[2] - s.position[2];
				normalize(effectvector);
				for (int k = 0; k < 3; k++) {
					effectvector[k] *= (s.alpha/ Math.pow(distance, 2));
					s.Targetdirectionvector[k] += effectvector[k];
				}
			}
		}
		
		
	}
	
	// every fish want to in the average speed of the rest of fish
	private void EverageSpeed(List<fish> Fishes){
		float everagespeed = 0;
		int n = Fishes.size();
		for (int i = 0; i < n; i++)
			everagespeed += Fishes.get(i).speed;
		everagespeed /= n;
		for ( int i = 0; i < n; i++)
			Fishes.get(i).speed = everagespeed;
	}
	
	// the change of direction, actually is the change of angle, so there we set the angle 
	// the fish should change in order to get the target direction
	private void SetTargetAngle(fish f){
		float angle1;
		float angle2;
		float[] targetvector = new float[3];
		// now this fish have its final target direction vector in this frame,
		// we put some noise to it.
		Noise(f.Targetdirectionvector);
		targetvector[0] = f.Targetdirectionvector[0];
		targetvector[1] = f.Targetdirectionvector[1];
		targetvector[2] = f.Targetdirectionvector[2];
		
		// to the final target direction we just need its orientation
		// because this is the direction the fish want to move in
		normalize(targetvector);
		
		// project targetV to plane ZOX
		float[] pjtargetV = new float[3];
		pjtargetV[0] = targetvector[0];
		pjtargetV[1] = 0;
		pjtargetV[2] = targetvector[2];
		pjtargetV = normalize(pjtargetV);
		
		// get the angle between pjtargetV and target, and this is the the angle
		// rotate on Z-axis
		float crossproduct;
		crossproduct = pjtargetV[0] * targetvector[0] + pjtargetV[1] * targetvector[1] + pjtargetV[2] * targetvector[2];

		// to eliminate error
		if (crossproduct > 1)
			crossproduct = 1;
		else if (crossproduct < -1)
			crossproduct = -1;
		// angle1 is the rotation about Z-axis
		angle1 = (float) Math.acos(crossproduct);
		angle1 = (float) Math.toDegrees(angle1);
		if (Math.abs(angle1) > f.Zlimit)
			angle1 = Math.signum(angle1) * f.Zlimit;
		// get the angle between the origin direction of the fish and pjtargetV
		// we assume that every fish, its origin orientation is (-1,0,0)
		// and this is the angle rotate on Y-axis
		angle2 = (float) Math.acos(-pjtargetV[0]);

		angle2 = (float) Math.toDegrees(angle2);
		// angle3 is the current rotation on Y-axis
		float angle3;
		angle3 = f.RotationAngle[1];
		
		// those if below is to make sure that when fish turning it always does not choose the reflex angle.
		if (targetvector[1] > 0)
			f.TargetAngle[2] = -angle1;
		else {
			f.TargetAngle[2] = angle1;
		}

		if (targetvector[2] > 0) {
			if (Math.abs(angle3 - angle2) > 180)
				angle2 += Math.signum(angle3) * 360;
			f.TargetAngle[1] = angle2;
		} else {
			angle2 = -angle2;
			if (Math.abs(angle3 - angle2) > 180)
				angle2 += Math.signum(angle3) * 360;
			f.TargetAngle[1] = angle2;
		}
		
		// set the difference between target angle and rotaton angle
		for (int j = 0; j < 3; j++)
			f.Increase[j] = Math.abs(f.TargetAngle[j] - f.RotationAngle[j]);
		
		// if collision happen, set the fish back to the place where the collision not happen
		if (f.collision) {
			f.position[0] = f.lastposition[0];
			f.position[1] = f.lastposition[1];
			f.position[2] = f.lastposition[2];
			f.CurtoTar();
		}
		
	}
	
	private float Distance(float[] f1, float[] f2){
		float distance = 0;
		for (int i = 0; i < 3; i++)
			distance += (float)Math.pow(f1[i]-f2[i], 2);
		distance = (float)Math.sqrt(distance);
		return distance;
	}
	
	public void draw(GL2 gl) {
		tank.draw(gl);
		
		for (int i = 0; i < foodnumber; i++)
			foodlist.get(i).draw(gl);
		for(int i = 0; i < fishnumber; i++)
			fishlist.get(i).draw(gl);
	}
	
	public void Addfood(){
		float _x;
		float _y;
		float _z;
		float boundary;
		boundary = Tankheight/2 - 1f;
		Random randomposition = new Random();
		
		_x = randomposition.nextFloat()*boundary-boundary;
		_y = 6;
		_z = randomposition.nextFloat()*boundary-boundary;
	
		foodlist.add(new food(_x, _y, _z));
		
		foodnumber +=1;
	}

	// this is for stereo viewing
	public void SetPurple(){
		for (int i = 0; i < fishnumber; i++)
			fishlist.get(i).setcolor(0.8f, 0, 1);
		for (int i = 0; i < foodnumber; i++)
			foodlist.get(i).setcolor(0.8f, 0, 1);
	}

	private void Noise(float[] v){
		Random noise = new Random();
		v[0] += noise.nextFloat()*0.02-0.01;
		v[1] += noise.nextFloat()*0.02-0.01;
		v[2] += noise.nextFloat()*0.02-0.01;
	}
	
	public void SetCreature(){
		if (fishnumber == 1) {
			
			fishlist.add(ellipse.get(0));
			fishlist.add(ellipse.get(1));
			fishlist.add(ellipse.get(2));
			fishlist.add(triangle.get(0));
			fishlist.add(triangle.get(1));
			fishlist.add(triangle.get(2));
						
			for (int i = 1; i < 7; i++)
				fishlist.get(i).ReSet();
			fishnumber = 7;

		}
	}

}
