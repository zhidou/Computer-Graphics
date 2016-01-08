import com.sun.swing.internal.plaf.metal.resources.metal_zh_HK;

//****************************************************************************
//     SuperEllipsoid class
//****************************************************************************
// 2015 modified by Zhi Dou for PA4 based on sphere class created by Stan Sclaroff

public class SuperEllipsoid3D extends Objects3D
{	
	float rx;
	float ry;
	float rz;
	float e1;
	float e2;
	public SuperEllipsoid3D(int id, float _x, float _y, float _z, float _rx, float _ry, float _rz, float e1, float e2, int _m, int _n, ColorType KS, ColorType KD)
	{
		center = new Vector3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		rz = _rz;
		this.e1 = e1;
		this.e2 = e2;
		m = _m;
		n = _n;
		ks = new ColorType(KS);
		kd = new ColorType(KD);
		ID = id;
		initMesh();
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	public void fillMesh()
	{
		int i,j;		
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_phi=(float)Math.PI / ((float)(n-1));
		float c_theta,s_theta;
		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			if (Math.abs(theta - (float)Math.PI)<0.00001)
				theta = (float)Math.PI;
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
						
			for(j=0,phi=-(float)(0.5*Math.PI);j<n;++j,phi += d_phi)
			{

				c_phi = (float)Math.cos(phi);
				s_phi = (float)Math.sin(phi);
				// to fix the error when compute
				 if (c_phi < 0)
					 c_phi = -c_phi;

				mesh.v[i][j].x = (float) (center.x + rx * c_phi * Math.pow(Math.abs(c_phi), e1-1) * c_theta
						* Math.pow(Math.abs(c_theta), e2-1));
				mesh.v[i][j].y = (float) (center.y + ry * c_phi * Math.pow(Math.abs(c_phi), e1-1) * s_theta
						* Math.pow(Math.abs(s_theta), e2-1));
				mesh.v[i][j].z = (float) (center.z + rz * s_phi * Math.pow(Math.abs(s_phi), e1-1));			
				
				
				// unit normal to sphere at this vertex
				mesh.n[i][j].x = (float) (ry*rz*
						Math.pow(Math.abs(s_phi), e1-1)*c_theta*Math.pow(Math.abs(s_theta), e2-1));
				mesh.n[i][j].y = (float) (rx*rz*
						Math.pow(Math.abs(s_phi), e1-1)*s_theta*Math.pow(Math.abs(c_theta), e2-1));
				mesh.n[i][j].z = (float) (rx*ry*s_phi*Math.pow(Math.abs(c_phi), e1-2)*
						Math.pow(Math.abs(c_theta), e2-1)*Math.pow(Math.abs(s_theta), e2-1));
				
				mesh.n[i][j].normalize();
					
			}
	    }
	}
}