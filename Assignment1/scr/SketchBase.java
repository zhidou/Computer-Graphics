//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import com.jogamp.opengl.util.GLReadBufferUtil;
import com.sun.corba.se.impl.oa.poa.POAImpl;
//import com.sun.glass.ui.TouchInputSupport;
//import com.sun.javafx.scene.paint.GradientUtils.Point;
//import com.sun.javafx.sg.prism.web.NGWebView;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import com.sun.swing.internal.plaf.basic.resources.basic;
//import com.sun.xml.internal.ws.client.sei.ResponseBuilder.RpcLit;

import jogamp.opengl.SystemUtil;
import sun.misc.GC;
import sun.net.www.content.text.plain;
import sun.print.resources.serviceui;
import sun.security.krb5.internal.crypto.crc32;

public class SketchBase 
{
	public static int N=5; // this is used in anti-aliasing to set how many points we should test when draw one point.
	public SketchBase()
	{
		// deliberately left blank
	}
	
	// draw a point
	public static void drawPoint(BufferedImage buff, Point2D p)
	{
		buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getBRGUint8());
	}
	
	//////////////////////////////////////////////////
	//	Implement the following two functions
	//////////////////////////////////////////////////
	
	//this method is to compute the volume, or we could say we use this method to find the weight of intensity of every point.
	public static float[] Volume(double[] D, int n)
	{
		float[] volume= new float[n];
		double rvolume[]={0.524,0.461,0.401,0.343,.289,.238,.193,.151,.115,.084,.059,.038,.022,.011,.004,0,0};
		//rvolume is the discrete volume of....I cannot explain it clearly by just writing words, but I can explain it by drawing some picture
		// and these data is from an article I find on the Internet, I can show you this article.
		double Cvolume=1.04719;  // this is volume of our cone filter
		for (int i=0; i<n; i++)
		{
			if (D[i]<0.5)
				volume[i]=(float) (Cvolume-rvolume[(int) Math.rint((D[i]+0.5)*16)]-rvolume[(int) Math.rint((0.5-D[i])*16)]);
			else if (D[i]>=0.5 && D[i]<=1.5)
				volume[i]=(float) rvolume[(int) Math.rint((D[i]-0.5)*16)];
			else volume[i]=0;
		}
		return volume;
	}
	
	// to set the intensity of the color this is for anti-aliasing.
	public static float[] Intensity(int a, int b ,double d)
	{
		float[] intensity = new float[N];
		double[] D=new double[N];				//D is the distance of the center of a pixel to the actual line
		double deno=2*Math.sqrt(a*a+b*b);
		D[0]=(3*b+d)/deno;						//we can get the formula from mid point algorithm 
		D[1]=(b+d)/deno;
		D[2]=(b-d)/deno;
		D[3]=(3*b-d)/deno;
		D[4]=(5*b-d)/deno;
		intensity=Volume(D, N);
		return intensity;
		
	}
	//the Anti-aliasing is designed by using Gupta-Sproull Algorithm and draw the line by using mid point algorithm
	public static void drawAntiLine(BufferedImage buff, Point2D p1, Point2D p2)
	{
		int x_start, y_start, x_end, y_end, x, y, a, b, steps;  //the function of the line is ax+by+c=0
		int pixel;
		double d;                                               // d is the parameter to decide the which point to choose next
		float r,g,b1;                                           //r,g,b1 is to get the color that buff already has 
		float[] intensity=new float[N];                         // intensity of the color or we could say the weight
		ColorType Color_start= new ColorType(), Color_end=new ColorType(), c=new ColorType(), c1=new ColorType();
		boolean k=false;                                        // k is to represent the the slope>1 or <1
		float rincrement, gincrement, bincrement;               // increment of color
		if (p1.x<p2.x)                                          //choose the point with low x to be our start point
		{
			x_start=p1.x;
			y_start=p1.y;
			Color_start=(ColorType) p1.c.clone();
			x_end=p2.x;
			y_end=p2.y;
			Color_end=(ColorType) p2.c.clone();
		}
		else
		{
			x_start=p2.x;
			y_start=p2.y;
			Color_start=(ColorType) p2.c.clone();
			x_end=p1.x;
			y_end=p1.y;
			Color_end=(ColorType) p1.c.clone();
		}
		x=x_start; y=y_start; 
		c.r=Color_start.r;
		c.g=Color_start.g;
		c.b=Color_start.b;
		
		if (y_start>y_end)                                     //make the line to the x-axis symmetric position in the case the slope<0
		{
			x_start=-x_end;
			x_end=-x;
			x=x_start;
			y_start=y_end;
			y_end=y;
			y=y_start;
			Color_start.r=Color_end.r;
			Color_start.g=Color_end.g;
			Color_start.b=Color_end.b;
			Color_end.r=c.r;
			Color_end.g=c.g;
			Color_end.b=c.b;
			c.r=Color_start.r;
			c.g=Color_start.g;
			c.b=Color_start.b;
		}
		
		
		if (y_end-y_start>x_end-x_start)                        //if the slope>1 we decide the steps based on y, else based on x
		{
			k=true;
			a=x_start-x_end;
			b=y_end-y_start;
			steps=Math.abs(b);
		}
		else 
		{
			a=-y_end+y_start; b=-x_start+x_end; steps=Math.abs(b);
		}
		
		d=2*a+b;												//initiate d
		
		rincrement=(Color_end.r-Color_start.r)/steps;
		gincrement=(Color_end.g-Color_start.g)/steps;
		bincrement=(Color_end.b-Color_start.b)/steps;
		
		intensity=Intensity(a, Math.abs(b), d); 				//to compute the intensity of the fist points 
		
		
		for (int i=1; i<=steps; i++)
		{
			for(int j=0;j<N;j++)
			{
				c1.r=intensity[j]*c.r;
				c1.b=intensity[j]*c.b;
				c1.g=intensity[j]*c.g;
				
				if (k) 
				{	
					pixel=buff.getRGB(Math.abs(x+2-j),buff.getHeight()-y-2);//get the color of buff
					r=(pixel>>16&255)/255.0f;								
					g=(pixel>>8&255)/255.0f;
					b1=(pixel&0x000000ff)/255.0f;
					if (r+g+b1>c1.r+c1.g+c1.b)								//if the sum of the color of buff is bigger then the color 
					{
						c1.r=r;c1.g=g;c1.b=b1;								//we want to draw, so we just choose the buff color 
					}
					drawPoint(buff, new Point2D(Math.abs(x+2-j),y+1,c1));	//because same intensity of the point is black, if we not choose the color of buff, the black will cover the color of buff
				}
				else 
				{
					pixel=buff.getRGB(Math.abs(x+1),buff.getHeight()-y-3+j);// this is the function
					r=(pixel>>16&255)/255.0f;
					g=(pixel>>8&255)/255.0f;
					b1=(pixel&0x000000ff)/255.0f;
					if (r+g+b1>c1.r+c1.g+c1.b)
					{
						c1.r=r;c1.g=g;c1.b=b1;
					}
					drawPoint(buff, new Point2D(Math.abs(x+1),y+2-j,c1));
				}
			}
			if(d>=0) d=d+2*a;
			else 
			{
				d=d+2*a+2*b;
				if (k) x++;
				else y++;
			}
			if (k) y++;
			else x++;
			intensity=Intensity(a, Math.abs(b), d);
			c.r+=rincrement;
			c.g+=gincrement;
			c.b+=bincrement;
		}
	}
	
	// the method setcolor is to set color based on Bresenham's line algorithm
	public static void setcolor(int steps, int[] rem, int[] cIncrement, int[] color_start, int[] pcc)
	{
		//the increment of color is designed based on Bresenham's line algorithm
		//color increase cIncrement per steps, but there is error, since we computer color by int
		//so in some step, color should increase more than cIncrement, and we should decide when to increase more
		//we could use the error and steps to make this decision. We can use the Bresenham's line algorithms on error and steps
		
		for (int i=0;i<3;i++)
		{	if(rem[i]>=0)
			{
				if(2*(pcc[i]+rem[i])<steps) 
				{
					pcc[i]+=rem[i];
					color_start[i]+=cIncrement[i];
				}
				else 
				{
					pcc[i]+=rem[i]-steps;
					color_start[i]+=cIncrement[i]+1;
				}
			}
			else
			{
				if(2*(pcc[i]+rem[i])>-steps) 
				{
					pcc[i]+=rem[i];
					color_start[i]+=cIncrement[i];
				}
				else 
				{
					pcc[i]+=rem[i]+steps;
					color_start[i]+=cIncrement[i]-1;
				}
			}
		}
	}
	
	//this method is to transform float color into int color for computing color just using integer
	public static void InitiateColor(int steps, int[] rem, int[] cIncrement, int[] color_start, int[] color_end, ColorType Color_start, ColorType Color_end)
	{
		for(int i=0;i<3;i++)
		{
			color_start[i]=Color_start.getBRGUint8()>>(16-8*i)&255;
			color_end[i]=Color_end.getBRGUint8()>>(16-8*i)&255;
			cIncrement[i]=(color_end[i]-color_start[i])/steps;
			rem[i]=color_end[i]-color_start[i]-cIncrement[i]*steps;
		}
	}
	
	
	// draw a line segment
	public static void drawLine(BufferedImage buff, Point2D p1, Point2D p2)
	{
		int x_start, y_start, x_end, y_end,p, y, x;
		ColorType Color_start= null, Color_end=null, c=new ColorType();
		int dx, dy;
		int steps;
		int[] color_start=new int[3], color_end=new int[3];
		int[] Rem=new int[3],pcc={0,0,0};
		int[] cIncrement= new int[3];
		
		if (p1.x==p2.x && p1.y==p2.y)   // is p1 and p2 is the same we just draw one point and return, this is for triangle
		{
			buff.setRGB(p1.x, buff.getHeight()-p1.y-1, p1.c.getBRGUint8());
			return;
		}
		
		if (p1.x<p2.x)                 //  to set which point is the start point, we always like to start from the point with smaller x
		{
			x_start=p1.x;
			y_start=p1.y;
			Color_start=(ColorType) p1.c.clone();
			x_end=p2.x;
			y_end=p2.y;
			Color_end=(ColorType) p2.c.clone();
		}
		else
		{
			x_start=p2.x;
			y_start=p2.y;
			Color_start=(ColorType)p2.c.clone();
			x_end=p1.x;
			y_end=p1.y;
			Color_end=(ColorType)p1.c.clone();
		}
		
		x=x_start; y=y_start; 
		c=(ColorType) Color_start.clone();
		dx=x_end-x_start; dy=Math.abs(y_end-y_start);
		
		if (y_start>y_end)             // if the slope is negative, we could turn it to x-axis symmetrical position
		{
			x_start=-x_end;
			x_end=-x;
			x=x_start;
			y_start=y_end;
			y_end=y;
			y=y_start;
			Color_start=(ColorType)Color_end.clone();
			Color_end=(ColorType)c.clone();
		}
		
		if(dy>dx) steps=dy;
		else steps=dx;
		
		buff.setRGB(p1.x, buff.getHeight()-p1.y-1, p1.c.getBRGUint8()); //  draw the start point and the end point
		buff.setRGB(p2.x, buff.getHeight()-p2.y-1, p2.c.getBRGUint8());
		
    	InitiateColor(steps, Rem, cIncrement, color_start, color_end, Color_start, Color_end);
		
		
		if (dy>dx)  // star draw line by Bresenham's algorithm
		{
			
			for (p=2*dx-dy; y<y_end;y++)
			{
				setcolor(steps, Rem, cIncrement, color_start, pcc);
				if (p<0) 
					{
						buff.setRGB(Math.abs(x),buff.getHeight()-y-2, (color_start[0]<<16) | (color_start[1]<<8) | color_start[2]);
						p=p+2*dx;
					}
				else 
					{
						buff.setRGB(Math.abs(x+1),buff.getHeight()-y-2, (color_start[0]<<16) | (color_start[1]<<8) | color_start[2]);
						p=p+2*dx-2*dy;
						x++;
					}
			}
		}
		else
		{	
			for (p=2*dy-dx; x<x_end;x++)
			{
				setcolor(steps, Rem, cIncrement, color_start, pcc);
				if (p<0) 
					{
						buff.setRGB(Math.abs(x+1),buff.getHeight()-y-1, (color_start[0]<<16) | (color_start[1]<<8) | color_start[2]);
						p=p+2*dy;
					}
				else 
					{
						buff.setRGB(Math.abs(x+1),buff.getHeight()-y-2, (color_start[0]<<16) | (color_start[1]<<8) | color_start[2]);
						p=p+2*dy-2*dx;
						y++;
					}
			}
		}
	}
	
	// draw a triangle
	public static void drawTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth, boolean Fill)
	{
		Point2D p= new Point2D();
		p.c=p1.c;                  	//this is to store the color of first point for the flat interpolation
		sortpoint(p1, p2, p3);		//sort point by x, let the smallest x in the first place
		p=Drawline(buff, p1, p2, p3,do_smooth,p,Fill);
		if (p.y!=p3.y) p=Drawline(buff, p3, p, p2,do_smooth,p,Fill);
	}
	
	
	public static void sortpoint(Point2D p1, Point2D p2, Point2D p3)

	{
		Point2D p=new Point2D();
		if (p1.y>p2.y)
		{
			p.x=p1.x; p.y=p1.y; p.c=p1.c; p.u=p1.u; p.v=p1.v;
			p1.x=p2.x; p1.y=p2.y; p1.c=p2.c; p1.u=p2.u; p1.v=p2.v;
			p2.x=p.x; p2.y=p.y; p2.c=p.c; p2.u=p.u; p2.v=p.v;  
		}
		if (p1.y>p3.y)
		{
			p.x=p1.x; p.y=p1.y; p.c=p1.c; p.u=p1.u; p.v=p1.v;
			p1.x=p3.x; p1.y=p3.y; p1.c=p3.c; p1.u=p3.u; p1.v=p3.v;
			p3.x=p.x; p3.y=p.y; p3.c=p.c; p3.u=p.u; p3.v=p.v;
		}
		if(p2.y>p3.y)
		{
			p.x=p2.x; p.y=p2.y; p.c=p2.c; p.u=p2.u; p.v=p2.v;
			p2.x=p3.x; p2.y=p3.y; p2.c=p3.c; p2.u=p3.u; p2.v=p3.v;
			p3.x=p.x; p3.y=p.y; p3.c=p.c; p3.u=p.u; p3.v=p.v;
		}
	}
	
	//this Drawline method is to draw the line of triangle using the method of DDA to draw line
	public static Point2D Drawline(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth, Point2D p0, boolean Fill)
	{	// 12 13 means point1 to point2 and point1 to point3
		Point2D p= new Point2D();
		int x12, x13, y12, y13;
		float X12, X13, Y12, Y13;

		int[] color_start12=new int[3], color_end12=new int[3],color_start13=new int[3], color_end13=new int[3], testcolor=new int[3];
		int[] Rem12=new int[3],pcc12={0,0,0},Rem13=new int[3],pcc13={0,0,0};
		int[] cIncrement12= new int[3],cIncrement13= new int[3];
		
		float xIncrement12,xIncrement13, yIncrement12, yIncrement13;
		int steps12, steps13;
		
		x12=x13=p1.x; y12=y13=p1.y;
		X12=(float)p1.x; Y12=(float)p1.y; X13=(float)p1.x; Y13=(float)p1.y;
		
		int dx12=p2.x-p1.x, dx13=p3.x-p1.x, dy12=p2.y-p1.y, dy13=p3.y-p1.y;
		
		if(!do_smooth)
		{
			p1.c.r=p2.c.r=p3.c.r=p0.c.r;	
			p1.c.g=p2.c.g=p3.c.g=p0.c.g;	
			p1.c.b=p2.c.b=p3.c.b=p0.c.b;	
		}
		
		if (Math.abs(dx12)>Math.abs(dy12)) steps12=Math.abs(dx12);
		else steps12=Math.abs(dy12);
		if (Math.abs(dx13)>Math.abs(dy13)) steps13=Math.abs(dx13);
		else steps13=Math.abs(dy13);
		
		InitiateColor(steps12, Rem12, cIncrement12, color_start12, color_end12, p1.c, p2.c);
		InitiateColor(steps13, Rem13, cIncrement13, color_start13, color_end13, p1.c, p3.c);

		xIncrement12=(float)dx12/(float)steps12;
		yIncrement12=(float)dy12/(float)steps12;
		xIncrement13=(float)dx13/(float)steps13;
		yIncrement13=(float)dy13/(float)steps13;
		
		while(Math.abs(y12-p1.y)<=Math.abs(dy12) && Math.abs(x12-p1.x)<=Math.abs(dx12))
		{
			if (Math.abs(y12-p1.y)>Math.abs(y13-p1.y))
			{
				buff.setRGB(Math.abs(x13),buff.getHeight()-y13-1, (color_start13[0]<<16) | (color_start13[1]<<8) | color_start13[2]);
				drawPoint(buff, new Point2D(x13,y13,color_start13[0],color_start13[1],color_start13[2]));	
				X13+=xIncrement13;
				Y13+=yIncrement13;
				setcolor(steps13, Rem13, cIncrement13, color_start13, pcc13);
				x13=Math.round(X13);
				y13=Math.round(Y13);
			}
			else if (Math.abs(y12-p1.y)<Math.abs(y13-p1.y))
			{
				buff.setRGB(Math.abs(x12),buff.getHeight()-y12-1, (color_start12[0]<<16) | (color_start12[1]<<8) | color_start12[2]);
				drawPoint(buff, new Point2D(x12,y12,color_start12[0],color_start12[1],color_start12[2]));	
				X12+=xIncrement12;
				Y12+=yIncrement12;
				setcolor(steps12, Rem12, cIncrement12, color_start12, pcc12);
				x12=Math.round(X12);
				y12=Math.round(Y12);
			}
			else  
			{
				if(Fill) drawLine(buff, new Point2D(x12, y12, color_start12[0],color_start12[1],color_start12[2]), 
						new Point2D(x13, y13, color_start13[0],color_start13[1],color_start13[2]));
				else 
				{
					buff.setRGB(Math.abs(x12),buff.getHeight()-y12-1, (color_start12[0]<<16) | (color_start12[1]<<8) | color_start12[2]);
					buff.setRGB(Math.abs(x13),buff.getHeight()-y12-1, (color_start13[0]<<16) | (color_start13[1]<<8) | color_start13[2]);
				}
				X13+=xIncrement13;
				Y13+=yIncrement13;
				x13=Math.round(X13);
				y13=Math.round(Y13);
				testcolor[0]=color_start13[0];
				testcolor[1]=color_start13[1];
				testcolor[2]=color_start13[2];
				setcolor(steps13, Rem13, cIncrement13, color_start13, pcc13);
				X12+=xIncrement12;
				Y12+=yIncrement12;
				x12=Math.round(X12);
				y12=Math.round(Y12);
				setcolor(steps12, Rem12, cIncrement12, color_start12, pcc12);
				}
			}
		
		X13-=xIncrement13;
		Y13-=yIncrement13;
		x13=Math.round(X13);
		y13=Math.round(Y13);
		p.x=x13; p.y=y13; p.c.r=(float)(testcolor[0])/(float)255;p.c.g=(float)(testcolor[1])/(float)255;
		p.c.b=(float)(testcolor[2])/(float)255;		
		return p;  // the return value is the point on the line of 13
		
	}
	
	
	//this search color method is to search color from the texture and then set color to the point we will draw
	public static void SearchColor(Point2D p, BufferedImage texture)
	{
		int u,v;
		int[] pixel= new int[4], r=new int[4], g= new int[4], b=new int[4];
		float[] rm=new float[2], bm=new float[2], gm=new float[2];
		
		u=(int)Math.floor(p.u);
		v=(int)Math.floor(p.v);
		if(u<0&&u>=-1) u=0;// because the computation is not so precise, so some point many less than 0, and we just assign 0 to them
		if(v<0&&v>=-1) v=0;// but if less than -1, that means.... there is bug...
		if(u<texture.getWidth()-1&&v<texture.getHeight()-1)
		{
			pixel[0]=texture.getRGB(u, v);
			pixel[1]=texture.getRGB(u+1, v);
			pixel[2]=texture.getRGB(u, v+1);
			pixel[3]=texture.getRGB(u+1, v+1);
			r[0]=((pixel[0]&0x00ff0000)>>16);
			r[1]=((pixel[1]&0x00ff0000)>>16);
			r[2]=((pixel[2]&0x00ff0000)>>16);
			r[3]=((pixel[3]&0x00ff0000)>>16);
			g[0]=(pixel[0]&0x0000ff00)>>8;
			g[1]=(pixel[1]&0x0000ff00)>>8;
			g[2]=(pixel[2]&0x0000ff00)>>8;
			g[3]=(pixel[3]&0x0000ff00)>>8;
		    b[0]=pixel[0]&0x000000ff;
		    b[1]=pixel[1]&0x000000ff;
		    b[2]=pixel[2]&0x000000ff;
		    b[3]=pixel[3]&0x000000ff;
		    rm[0]=(float)(r[2]-r[0])*(p.v-v)+r[0];
		    gm[0]=(float)(g[2]-g[0])*(p.v-v)+g[0];
		    bm[0]=(float)(b[2]-b[0])*(p.v-v)+b[0];
		    rm[1]=(float)(r[3]-r[1])*(p.v-v)+r[1];
		    gm[1]=(float)(g[3]-g[1])*(p.v-v)+g[1];
		    bm[1]=(float)(b[3]-b[1])*(p.v-v)+b[1];
		    p.c.r=((rm[1]-rm[0])*(p.u-u)+rm[0])/255;
		    p.c.g=((gm[1]-gm[0])*(p.u-u)+gm[0])/255;
		    p.c.b=((bm[1]-bm[0])*(p.u-u)+bm[0])/255;
		}
		else 
		{	//the some reason, some point may bigger than the upper bound, we just chose the upper bound
			pixel[0]=texture.getRGB(u, v);
			p.c.r=(float)((pixel[0]&0x00ff0000)>>16)/255;
		    p.c.g=(float)((pixel[0]&0x0000ff00)>>8)/255;
		    p.c.b=(float)(pixel[0]&0x000000ff)/255;
	    }
	}
	
	//this is the method to draw the line of triangle with texture by using DDA
	public static Point2D Drawlineoftexture(BufferedImage buff,BufferedImage texture, Point2D p1, Point2D p2, Point2D p3,boolean Fill)
	{
		Point2D p= new Point2D();
		Point2D p12= new Point2D(), p13= new Point2D();
		float X12, X13, Y12, Y13;
		float xIncrement12,xIncrement13, yIncrement12, yIncrement13,uIncrement12,vIncrement12,uIncrement13,vIncrement13;
		int steps12, steps13;
		
		p12.x=p13.x=p1.x; p12.y=p13.y=p1.y; 
		p12.u=p13.u=p1.u; p12.v=p13.v=p1.v;
		
		X12=(float)p1.x; Y12=(float)p1.y; X13=(float)p1.x; Y13=(float)p1.y;
		
		int dx12=p2.x-p1.x, dx13=p3.x-p1.x, dy12=p2.y-p1.y, dy13=p3.y-p1.y;
		
		if (Math.abs(dx12)>Math.abs(dy12)) steps12=Math.abs(dx12);
		else steps12=Math.abs(dy12);
		if (Math.abs(dx13)>Math.abs(dy13)) steps13=Math.abs(dx13);
		else steps13=Math.abs(dy13);

		xIncrement12=(float)dx12/(float)steps12;
		yIncrement12=(float)dy12/(float)steps12;
		xIncrement13=(float)dx13/(float)steps13;
		yIncrement13=(float)dy13/(float)steps13;
		uIncrement12=(p2.u-p1.u)/steps12;
		vIncrement12=(p2.v-p1.v)/steps12;
		uIncrement13=(p3.u-p1.u)/steps13;
		vIncrement13=(p3.v-p1.v)/steps13;
		
		
		while(Math.abs(p12.y-p1.y)<=Math.abs(dy12) && Math.abs(p12.x-p1.x)<=Math.abs(dx12))
		{
			if (Math.abs(p12.y-p1.y)>Math.abs(p13.y-p1.y))
			{
				SearchColor(p13, texture);
				drawPoint(buff, p13);	
				X13+=xIncrement13;
				Y13+=yIncrement13;
				p13.x=Math.round(X13);
				p13.y=Math.round(Y13);
				p13.u+=uIncrement13;
				p13.v+=vIncrement13;
			}
			else if (Math.abs(p12.y-p1.y)<Math.abs(p13.y-p1.y))
			{
				SearchColor(p12, texture);
				drawPoint(buff, p12);	
				X12+=xIncrement12;
				Y12+=yIncrement12;
				p12.x=Math.round(X12);
				p12.y=Math.round(Y12);
				p12.u+=uIncrement12;
				p12.v+=vIncrement12;
			}
			else  
			{
				if(Fill)
				{
					Point2D p01= new Point2D();
					p01.x=p12.x; p01.y=p12.y; p01.u=p12.u; p01.v=p12.v;
					int s;
					if(p12.x-p13.x==0) s=0;
					else s=-(p12.x-p13.x)/Math.abs(p12.x-p13.x);
					for (int i=Math.min(p12.x, p13.x);i<=Math.max(p12.x, p13.x);i++)
					{
						SearchColor(p01, texture);
						drawPoint(buff, p01);
						p01.x+=s;
						p01.u=(p01.x-p12.x)*(p13.u-p12.u)/(p13.x-p12.x)+p12.u;
						p01.v=(p01.x-p12.x)*(p13.v-p12.v)/(p13.x-p12.x)+p12.v;
					}
				}
				else
				{
					SearchColor(p12, texture);
					drawPoint(buff, p12);	
					SearchColor(p13, texture);
					drawPoint(buff, p13);
				}
				X13+=xIncrement13;
				Y13+=yIncrement13;
				p13.x=Math.round(X13);
				p13.y=Math.round(Y13);
				p13.u+=uIncrement13;
				p13.v+=vIncrement13;
				X12+=xIncrement12;
				Y12+=yIncrement12;
				p12.x=Math.round(X12);
				p12.y=Math.round(Y12);
				p12.u+=uIncrement12;
				p12.v+=vIncrement12; 
			}
		}
		X13-=xIncrement13;
		Y13-=yIncrement13;
		p13.x=Math.round(X13);
		p13.y=Math.round(Y13);
		p13.u-=uIncrement13;
		p13.v-=vIncrement13;
		SearchColor(p13, texture);
		p.x=p13.x; p.y=p13.y; p.c=p13.c;p.u=p13.u;p.v=p13.v;
		
		return p;
		
	}
/////////////////////////////////////////////////
// for texture mapping (Extra Credit for CS680)
/////////////////////////////////////////////////
	
	public static void triangleTextureMap(BufferedImage buff, BufferedImage texture, Point2D p1, Point2D p2, Point2D p3,boolean Fill)
	{	
		Point2D p=new Point2D();
		p3.u=texture.getWidth()-1;
		p3.v=0;
		p1.u=(float)(texture.getWidth()-1)/2;
		p1.v=texture.getHeight()-1;
		p2.u=0;
		p2.v=0;
		SearchColor(p1, texture);
		SearchColor(p2, texture);
		SearchColor(p3, texture);
		sortpoint(p1, p2, p3);
		p=Drawlineoftexture(buff, texture, p1, p2, p3,Fill);
		if (p.y!=p3.y) p=Drawlineoftexture(buff, texture, p3, p2, p,Fill);
	}
}
