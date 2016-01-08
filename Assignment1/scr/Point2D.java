import sun.net.www.content.text.plain;

public class Point2D implements Cloneable
{
	public int x, y;
	public float u, v; // uv coordinates for texture mapping
	public ColorType c;
	public Point2D(int _x, int _y, int _r, int _g, int _b)
	{	//This construction method create a point with integer color and then it is to turn the integer color to float
		u = 0;
		v = 0;
		x = _x;
		y = _y;
		c=new ColorType();
		c.r=(float)(_r)/255.0f;
		c.g=(float)(_g)/255.0f;
		c.b=(float)(_b)/255.0f;
		
		
	}
	public Point2D(int _x, int _y, ColorType _c)
	{
		u = 0;
		v = 0;
		x = _x;
		y = _y;
		c = _c;
	}
	public Point2D(int _x, int _y, ColorType _c, float _u, float _v)
	{
		u = _u;
		v = _v;
		x = _x;
		y = _y;
		c = _c;
	}
	public Point2D()
	{
		c = new ColorType(1.0f, 1.0f, 1.0f);
	}
	public Point2D( Point2D p)
	{
		u = p.u;
		v = p.v;
		x = p.x;
		y = p.y;
		c = new ColorType(p.c.r, p.c.g, p.c.b);
	}
	public Object clone()
	{	//this clone is to deep clone
		Point2D P=null;
		try
		{
			P=(Point2D) super.clone();
			P.c=(ColorType) c.clone();
		}
		catch(CloneNotSupportedException e)
		{
			System.out.println(e.toString());
		}
		return P;
	}
}