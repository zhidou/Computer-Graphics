import java.awt.Color;

//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//	 Dec 8, 2015 modified by Zhi Dou for PA4

public class Objects3D
{
	protected Vector3D center;
	protected float r;
	protected int m,n;
	public ColorType ks;
	public ColorType kd;
	public Mesh3D mesh;
	public int ID;
	
	public Objects3D()
	{
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_m(int _m)
	{
		m = _m;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_n(int _n)
	{
		n = _n;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_ks(ColorType c)
	{
		ks.r = c.r;
		ks.g = c.g;
		ks.b = c.b;
	}
	public void set_kd(ColorType c)
	{
		kd.r = c.r;
		kd.g = c.g;
		kd.b = c.b;
	}
	
	public int get_n()
	{
		return n;
	}
	
	public int get_m()
	{
		return m;
	}

	protected void initMesh()
	{
		mesh = new Mesh3D(m,n);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	protected void fillMesh()
	{
	}
}