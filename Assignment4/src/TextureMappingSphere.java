import java.awt.image.BufferedImage;

import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Position.Bias;

//****************************************************************************
//      TextureMappingSphere class
//****************************************************************************
//   Dec 8, 2015 modified by Zhi Dou for PA4 based on sphere class created by Stan Sclaroff

public class TextureMappingSphere extends Objects3D
{	
	Point3D[][] p;
	int[][] bv;
	int[][] bu;
	private BufferedImage texture;
	boolean Bumpmap = false;
	
	public TextureMappingSphere(int id, float _x, float _y, float _z, float _r, int _m, int _n, BufferedImage texture, boolean Bumpmap)
	{
		center = new Vector3D(_x,_y,_z);
		r = _r;
		m = _m;
		n = _n;
		ID = id;
		this.texture = texture;
		
		if (!Bumpmap) {
			p = new Point3D[m][n];
			for (int i = 0; i < m; i++)
				for (int j = 0; j < n; j++)
					p[i][j] = new Point3D();
		}

		else 
			setBumpTable(texture);

		initMesh();
	
	}
	
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	public void fillMesh() {
		int i, j;
		float theta, phi;
		float d_theta = (float) (2.0 * Math.PI) / ((float) (m - 1));
		float d_phi = (float) Math.PI / ((float) n - 1);
		float c_theta, s_theta;
		float c_phi, s_phi;
		
		if (!Bumpmap) {
			for (i = 0, theta = 0 * (float) Math.PI; i < m; ++i, theta += d_theta) {
				c_theta = (float) Math.cos(theta);
				s_theta = (float) Math.sin(theta);

				for (j = 0, phi = (float) (0.5 * Math.PI); j < n; ++j, phi -= d_phi) {
					// vertex location
					c_phi = (float) Math.cos(phi);
					s_phi = (float) Math.sin(phi);
					mesh.v[i][j].x = center.x + r * c_phi * c_theta;
					mesh.v[i][j].z = center.z + r * c_phi * s_theta;
					mesh.v[i][j].y = center.y + r * s_phi;

					p[i][j].x = (int) (center.x + r * c_phi * c_theta);
					p[i][j].z = (int) (center.z + r * c_phi * s_theta);
					p[i][j].y = (int) (center.y + r * s_phi);
					p[i][j].u = (float) (theta / (2 * Math.PI)) * (texture.getWidth() - 1);
					p[i][j].v = (float) ((phi + 0.5 * Math.PI) / Math.PI) * (texture.getHeight() - 1);
				}
			}
		}
		
		else
		{
			for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
		    {
				c_theta=(float)Math.cos(theta);
				s_theta=(float)Math.sin(theta);

				for (j = 0, phi = (float) (-0.5 * Math.PI); j < n; ++j, phi += d_phi) {
					// vertex location
					c_phi = (float) Math.cos(phi);
					s_phi = (float) Math.sin(phi);

					mesh.v[i][j].x = center.x + r * c_phi * c_theta;
					mesh.v[i][j].y = center.y + r * c_phi * s_theta;
					mesh.v[i][j].z = center.z + r * s_phi;
					
					Vector3D du = new Vector3D();
					Vector3D dv = new Vector3D();
					Vector3D normal = new Vector3D();
					Vector3D dun = new Vector3D();
					Vector3D dvn = new Vector3D();
					
					int u,v; 
					u = (int) ((theta+Math.PI)/(2*Math.PI)*(texture.getWidth() - 1));
					v = (int) ((phi+Math.PI/2f)/Math.PI*(texture.getHeight() - 1));

					
					du.x = -r*s_theta;
					du.y = r*c_theta;
					du.z = 0;
					
					dv.x = -r * s_phi * c_theta;
					dv.y = -r * s_phi * s_theta;
					dv.z = r*c_phi;
					
					du.crossProduct(dv, normal);
					
					// unit normal to sphere at this vertex
					mesh.n[i][j].x = c_phi * c_theta;
					mesh.n[i][j].y = c_phi * s_theta;
					mesh.n[i][j].z = s_phi;
								
					du.crossProduct( mesh.n[i][j],dun);
					mesh.n[i][j].crossProduct(dv, dvn);
					
					dun = dun.scale(bv[v][u]);
					dvn = dvn.scale(bu[v][u]);
					
					mesh.n[i][j] = normal.plus(dun.plus(dvn));
					mesh.n[i][j].normalize();
	
				}
			}
		}
	}

	public void setBumpTable(BufferedImage texture){
		int width, height;
		width = texture.getWidth();
		height = texture.getHeight();
		bv = new int[height][width];
		bu = new int[height][width];
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++)
			{
				int b, u1, u2, v1, v2;
				b = texture.getRGB(j, i);
				u1 = b&0x000000ff;
				v1 = u1;
				b = texture.getRGB((j+1) % width, i);
				u2 = b&0x000000ff;
				bu[i][j] = u2 - u1;
				b = texture.getRGB(j, (height + i - 1) % height);
				v2 = b&0x000000ff;
				bv[i][j] = v2 - v1;
			}	
		}
		Bumpmap = true;
		ks = new ColorType();
		kd = new ColorType();
	}
}