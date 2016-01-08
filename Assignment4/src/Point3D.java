import java.awt.Point;

//****************************************************************************
//       2D Point Class from PA1
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
// Dec 8, 2015 modified by Zhi Dou for PA4

public class Point3D
{
	public int x, y, z;
	public float u, v; // uv coordinates for texture mapping
	public ColorType c;
	public Point3D(int _x, int _y, int _z, ColorType _c)
	{
		u = 0;
		v = 0;
		x = _x;
		y = _y;
		z = _z;
		c = _c;
	}
	public Point3D(int _x, int _y, int _z, ColorType _c, float _u, float _v)
	{
		u = _u;
		v = _v;
		x = _x;
		y = _y;
		z = _z;
		c = _c;
	}
	
	public Point3D(int _x, int _y, int _z){
		x = _x;
		y = _y;
		z = _z;
		u = 0;
		v = 0;
		c = new ColorType();
	}
	
	public Point3D()
	{
		c = new ColorType(1.0f, 1.0f, 1.0f);
	}
	public Point3D( Point3D p)
	{
		u = p.u;
		v = p.v;
		x = p.x;
		y = p.y;
		z = p.z;
		c = new ColorType(p.c.r, p.c.g, p.c.b);
	}
}