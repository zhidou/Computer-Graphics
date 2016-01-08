import java.util.Random;

//****************************************************************************
//       SuperTorus class
//****************************************************************************
//   2015 modified by Zhi Dou for PA4 based on turos class created by Stan Sclaroff

public class SuperTorus3D extends Objects3D
{
	private float r_axial;
	float rx;
	float ry;
	float rz;
	float e1;
	float e2;
	
	public SuperTorus3D(int id, float _x, float _y, float _z, float _rx, float _ry, float _rz, float _r_axial, float e1, float e2, int _m, int _n, ColorType KS, ColorType KD)
	{
		center = new Vector3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		rz = _rz;
		this.e1 = e1;
		this.e2 = e2;
		r_axial = _r_axial;
		m = _m;
		n = _n;
		ks = new ColorType(KS);
		kd = new ColorType(KD);
		ID = id;
		initMesh();
	}
	
	
	public void fillMesh()
	{
		int i,j;		
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)m-1);
		float d_phi=(float)(2.0*Math.PI) / ((float)n-1);
		float c_theta,s_theta;
		float c_phi, s_phi;
		Vector3D du = new Vector3D();
		Vector3D dv = new Vector3D();
		
		for(i=0,theta=(float)-Math.PI;i<m;++i,theta += d_theta)
	    {
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
			
			for(j=0,phi=(float)-Math.PI;j<n;++j,phi += d_phi)
			{
				// follow the formulation for torus given in textbook
				c_phi = (float)Math.cos(phi);
				s_phi = (float)Math.sin(phi);
				
				mesh.v[i][j].x=(float) (center.x+(r_axial+rx*c_phi*Math.pow(Math.abs(c_phi), e1-1))
						*c_theta*Math.pow(Math.abs(c_theta), e2-1));
				
				mesh.v[i][j].y=(float) (center.y+(r_axial+ry*c_phi*Math.pow(Math.abs(c_phi), e1-1))*s_theta*Math.pow(Math.abs(s_theta), e2-1));
				mesh.v[i][j].z=(float) (center.z+rz*s_phi*Math.pow(Math.abs(s_phi), e1-1));
				
				// compute partial derivatives
				// then use cross-product to get the normal
				// and normalize to produce a unit vector for the normal
				
				du.x = (float) (-e2*s_theta*Math.pow(Math.abs(c_theta), e2-1)*(r_axial+rx*c_phi*Math.pow(Math.abs(c_phi),e1-1)));
				du.y = (float) (e2*c_theta*Math.pow(Math.abs(s_theta), e2-1)*(r_axial+ry*c_phi*Math.pow(Math.abs(c_phi),e1-1)));
				du.z = 0;
				
				dv.x = (float) (-e1*rx*s_phi*Math.pow(Math.abs(c_phi), e1-1)*c_theta*Math.pow(Math.abs(c_theta), e2-1));
				dv.y = (float) (-e1*ry*s_phi*Math.pow(Math.abs(c_phi), e1-1)*s_theta*Math.pow(Math.abs(s_theta), e2-1));
				dv.z = (float) (e1*rz*c_phi*Math.pow(Math.abs(s_phi), e1-1));

				du.crossProduct(dv, mesh.n[i][j]);
				
				mesh.n[i][j].normalize();
			}
	    }
	}
}