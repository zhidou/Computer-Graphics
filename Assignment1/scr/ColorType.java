

public class ColorType implements Cloneable
{
	public float r, g, b;

	public ColorType( float _r, float _g, float _b)
	{
		r = _r;
		g = _g;
		b = _b;
	}
	public ColorType() {}
	public ColorType(ColorType p)
	{
		r=p.r;
		g=p.g;
		b=p.b;
	}
	public int getBRGUint8()
	{
		int _b = Math.round(b*255.0f); 
		int _g = Math.round(g*255.0f); 
		int _r = Math.round(r*255.0f);
		
		return (_r<<16) | (_g<<8) | _b;
	}
	public Object clone()  // I find this method on Internet and I learn how to deep clone, I think that is good for simplify my code
	{
		ColorType C=null;// so I use it here.
		try
		{
			C=(ColorType) super.clone();
			
		}
		catch(CloneNotSupportedException e)
		{
			System.out.println(e.toString());
		}
		return C;
	}
	
}
