#include "stock.h"

GLuint texture[MAXTEXTURE];
extern float x,y;
float u,v;
float I=0.001;
float ex=0,ez=0,cx,cz;
extern SqStack s;
extern int maze[YSIZE][XSIZE];

int InitStack(SqStack &s)
{
	s.base=(SElemType *)malloc(SIZE*sizeof(SElemType));
	if (!s.base) exit(OVERLOW);
	s.top=s.base;
	s.stacksize=SIZE;
	return OK;
}

int Push(SqStack &s,SElemType e)
{
	if (s.top-s.base+1>=s.stacksize)
	{
		s.base=(SElemType *)realloc(s.base,(s.stacksize+ADD)*sizeof(SElemType));
		if (!s.base) exit(OVERLOW);
		s.top=s.base+s.stacksize-1;
		s.stacksize+=ADD;
	}
	s.top+=1;
	*s.top=e;
	return OK;
}

int Pop(SqStack &s,SElemType &e)
{
	if (s.base==s.top)return FALSE;
	e=*s.top;
	s.top-=1;
	return OK;
}

int Step(SqStack &s,int maze[][XSIZE])
{
	SElemType e;
	SElemType k;
	int a,b;
	b=1;
	e.x=0;
	e.y=0;
	Push(s,e);
	a=Search(s,maze,e);
	while(b)
	{
		while(!a)
		{
			Pop(s,k);
			if(s.base==s.top&&maze[1][0]!=0&&maze[0][1]!=0)
			{
				//printf("There is no way! The maze is wrong!");
				return FALSE;
			}
			a=Search(s,maze,e);
		}
		Push(s,e);
		if(e.x==XSIZE-1&&e.y==YSIZE-1)
		{
			//printf("This is the exit!");
			return OK;
		}
		maze[e.y][e.x]=1;
		a=Search(s,maze,e);
	}
	return OK;
}

int Search(SqStack &s,int maze[][XSIZE],SElemType &e)
{
	if(maze[s.top->y][(s.top->x)+1]==0&&(s.top->x)+1<XSIZE)
	{
		e.x=(s.top->x)+1;
		e.y=s.top->y;
		return OK;
	}
	 if(maze[(s.top->y)+1][s.top->x]==0&&(s.top->y)+1<YSIZE)
	{
		e.x=s.top->x;
		e.y=(s.top->y)+1;
		return OK;
	}
	 if(maze[(s.top->y)-1][s.top->x]==0&&(s.top->y)-1>=0)
	{
		e.x=s.top->x;
		e.y=(s.top->y)-1;
		return OK;
	}
	 if(maze[s.top->y][(s.top->x)-1]==0&&(s.top->x)-1>=0)
	{
		e.x=(s.top->x)-1;
		e.y=s.top->y;
		return OK;
	}
	 return FALSE;
}

int init()
{
	int x,y;
	int i;
	float h=1.5;

	if (!LoadGLTextures()) return FALSE;
	glEnable(GL_TEXTURE_2D);
	glShadeModel(GL_SMOOTH);
	glClearColor(0.0,0.0,0.0,0.5);
	glClearDepth(1.0);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT,GL_NICEST);

	glNewList(1,GL_COMPILE);
	glBegin(GL_QUADS);
	for (y=0;y<YSIZE;y++)
	{
		for (x=0;x<XSIZE;)
		{
			if (maze[y][x]==-1)
			{
				i=x;
				if(maze[y+1][x]!=-1&&y+1<YSIZE)
				{
					glTexCoord2f(0.0,0.0);glVertex3f(i,0.0,-y-1);
					glTexCoord2f(1.0,0.0);glVertex3f(i+1,0.0,-y-1);
					glTexCoord2f(1.0,1.0);glVertex3f(i+1,h,-y-1);
					glTexCoord2f(0.0,1.0);glVertex3f(i,h,-y-1);	
				}
				if(maze[y-1][x]!=-1&&y-1>0)
				{
					glTexCoord2f(0.0,0.0);glVertex3f(i,0.0,-y);
					glTexCoord2f(1.0,0.0);glVertex3f(i+1,0.0,-y);
					glTexCoord2f(1.0,1.0);glVertex3f(i+1,h,-y);
					glTexCoord2f(0.0,1.0);glVertex3f(i,h,-y);	
				}
				glTexCoord2f(0.0,0.0);glVertex3f(i,0.0,-y);
				glTexCoord2f(1.0,0.0);glVertex3f(i,0.0,-y-1);
				glTexCoord2f(1.0,1.0);glVertex3f(i,h,-y-1);
				glTexCoord2f(0.0,1.0);glVertex3f(i,h,-y);
				while(maze[y][i+1]==-1&&(i+1)<XSIZE)
				{
					if(maze[y+1][i+1]!=-1&&y+1<YSIZE)
					{
						glTexCoord2f(0.0,0.0);glVertex3f(i+1,0.0,-y-1);
						glTexCoord2f(1.0,0.0);glVertex3f(i+2,0.0,-y-1);
						glTexCoord2f(1.0,1.0);glVertex3f(i+2,h,-y-1);
						glTexCoord2f(0.0,1.0);glVertex3f(i+1,h,-y-1);
					}
					if(maze[y-1][i+1]!=-1&&y-1>0)
					{
						glTexCoord2f(0.0,0.0);glVertex3f(i+1,0.0,-y);
						glTexCoord2f(1.0,0.0);glVertex3f(i+2,0.0,-y);
						glTexCoord2f(1.0,1.0);glVertex3f(i+2,h,-y);
						glTexCoord2f(0.0,1.0);glVertex3f(i+1,h,-y);
					}
					i++;
				}
				glTexCoord2f(0.0,0.0);glVertex3f(i+1,0.0,-y);
				glTexCoord2f(1.0,0.0);glVertex3f(i+1,0.0,-y-1);
				glTexCoord2f(1.0,1.0);glVertex3f(i+1,h,-y-1);
				glTexCoord2f(0.0,1.0);glVertex3f(i+1,h,-y);
				x=i+1;
			}
			x++;
		}
	}
	for (i=0;i>-YSIZE;i--)
	{
		glTexCoord2f(0.0,0.0);glVertex3f(0.0,0.0,i);
		glTexCoord2f(1.0,0.0);glVertex3f(0.0,0.0,i-1);
		glTexCoord2f(1.0,1.0);glVertex3f(0.0,h,i-1);
		glTexCoord2f(0.0,1.0);glVertex3f(0.0,h,i);
	}
	for (i=0;i<XSIZE-1;i++)
	{
		glTexCoord2f(0.0,0.0);glVertex3f(i,0.0,-YSIZE);
		glTexCoord2f(1.0,0.0);glVertex3f(i+1,0.0,-YSIZE);
		glTexCoord2f(1.0,1.0);glVertex3f(i+1,h,-YSIZE);
		glTexCoord2f(0.0,1.0);glVertex3f(i,h,-YSIZE);
	}
	for (i=-YSIZE;i<0;i++)
	{
		glTexCoord2f(0.0,0.0);glVertex3f(XSIZE,0.0,i);
		glTexCoord2f(1.0,0.0);glVertex3f(XSIZE,0.0,i+1);
		glTexCoord2f(1.0,1.0);glVertex3f(XSIZE,h,i+1);
		glTexCoord2f(0.0,1.0);glVertex3f(XSIZE,h,i);
	}
	for (i=XSIZE;i>1;i--)
	{
		glTexCoord2f(0.0,0.0);glVertex3f(i,0.0,0.0);
		glTexCoord2f(1.0,0.0);glVertex3f(i-1,0.0,0.0);
		glTexCoord2f(1.0,1.0);glVertex3f(i-1,h,0.0);
		glTexCoord2f(0.0,1.0);glVertex3f(i,h,0.0);
	}
	glEnd();
	glEndList();
	glEnable(GL_DEPTH_TEST);

}

void display(void)
{
    float i,j; 
	glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
	glLoadIdentity();
	gluLookAt(ex,1.8,ez,cx,1.5,cz,0,1,0);
	


	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glScalef(2.0,2.0,2.0);

	glBindTexture(GL_TEXTURE_2D,texture[1]);
	glBegin(GL_QUADS);
	for (i=XSIZE/2;i<=XSIZE;i=i*2)
	{
			for (j=-YSIZE/2;j>-YSIZE*2;j=j*2)
		{
			glTexCoord2f(0.0,0.0);glVertex3f(i-XSIZE/2,0.0,j+YSIZE/2);
			glTexCoord2f(1.0,0.0);glVertex3f(i,0.0,j+YSIZE/2);
			glTexCoord2f(1.0,1.0);glVertex3f(i,0.0,j);
			glTexCoord2f(0.0,1.0);glVertex3f(i-XSIZE/2,0.0,j);
		}
	}
	glEnd();
	glBindTexture(GL_TEXTURE_2D,texture[0]);
	glCallList(1);

	glPopMatrix();


	glutSwapBuffers();

}

void Reshape(int w,int h)
{
	glViewport(0,0,(GLsizei)w,(GLsizei)h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(50.0,(GLsizei)w/(GLsizei)h,0.1,50.0);
	glMatrixMode(GL_MODELVIEW);
	
	//gluLookAt(0.5,1.0,-0.3,0.5,0.6,-2.5,0,1,0);

	//gluLookAt(2.5,20,-0.5,2.3,0,-3.5,0,1,0);

}

int Start(SqStack &s,int maze[][XSIZE])
{
	SqStack a;
	SElemType e;
	InitStack(a);
	InitStack(s);
	Step(a,maze);
	while (a.top>a.base)
	{
		Pop(a,e);
		Push(s,e);
	}
	return OK;
}

void ViewMove(void)
{
	int k,k1,k2;
	if(s.top!=s.base)
	{
		if (s.top->x==(s.top-1)->x)              //k,k1,k2为控制方向的变量
		{
			if (fabs(y-(s.top-1)->y)>minn)     //用一个适当的小值来代替0，因为计算机的计算为出现细小的误差，不可能实现绝对的0
			{
				k=(s.top-1)->y-s.top->y;
				y=y+I*k;
				ex=(x+0.5)*2;ez=(-y-0.5)*2;cx=(x+0.5)*2;cz=(-(y+k*1)-0.5)*2;
				glutPostRedisplay();
				//gluLookAt(x+0.5,1.0,-y-0.5,x+0.5,0.8,-((s.top-1)->y+I*k)-0.5,0,1,0);
				u=s.top->x;v=((s.top-1)->y+k*1);
			}
			if (fabs(y-(s.top-1)->y)<minn&&fabs(u-(s.top-2)->x)>minn)
			{
				y=(s.top-1)->y;                                             //准确化
				k1=(s.top-2)->x-(s.top-1)->x;k2=(s.top-1)->y-s.top->y;
				u=u+I*k1;v=v-I*k2;
				ex=(x+0.5)*2;ez=(-y-0.5)*2;cx=(u+0.5)*2;cz=(-v-0.5)*2;
				glutPostRedisplay();
				//gluLookAt(x+0.5,1.0,-y-0.5,u+0.5,0.8,-v-0.5,0,1,0);
			}
			if (fabs(y-(s.top-1)->y)<minn&&fabs(u-(s.top-2)->x)<minn)
			{
				y=(s.top-1)->y; //准确化
				s.top-=1;
			}
		}
		else
		{
			if (fabs(x-(s.top-1)->x)>minn)
			{
				k=(s.top-1)->x-s.top->x;
				x=x+I*k;
				ex=(x+0.5)*2;ez=(-y-0.5)*2;cx=(x+1*k+0.5)*2;cz=(-y-0.5)*2;
				glutPostRedisplay();
				//gluLookAt(x+0.5,1.0,-y-0.5,((s.top-1)->x+I*k)+0.5,0.8,-y-0.5,0,1,0);
				u=(s.top-1)->x+1*k;v=s.top->y;
			}
			if (fabs(x-(s.top-1)->x)<minn&&fabs(v-(s.top-2)->y)>minn)
			{
				x=(s.top-1)->x;                                          //准确化
				k1=(s.top-2)->y-(s.top-1)->y;k2=(s.top-1)->x-s.top->x;
				v=v+I*k1;u=u-I*k2;
				ex=(x+0.5)*2;ez=(-y-0.5)*2;cx=(u+0.5)*2;cz=(-v-0.5)*2;
				glutPostRedisplay();
				//gluLookAt(x+0.5,1.0,-y-0.5,u+0.5,0.8,-v-0.5,0,1,0);
			}
			if (fabs(x-(s.top-1)->x)<minn&&fabs(v-(s.top-2)->y)<minn)
			{	x=(s.top-1)->x;   //准确化
				s.top-=1;
			}
		}
	}
	else
		{
			ex=XSIZE-0.5;ez=-YSIZE+0.5;cx=XSIZE-0.5;cz=-YSIZE+1;
			//gluLookAt(XSIZE-0.5,1.0,-YSIZE+0.5,XSIZE-0.5,0.8,-YSIZE+1,0,1,0);
			glutPostRedisplay();
	}
}

AUX_RGBImageRec *LoadBMP(char *Filename)
{
	FILE *File=NULL;
	if (!Filename) return NULL;
	File=fopen(Filename,"r");
	if (File)
	{
		fclose(File);
		return auxDIBImageLoad(Filename);
	}
	return NULL;
}

int LoadGLTextures()
{
	int Status=FALSE;
	AUX_RGBImageRec *TextureImage[2];
	memset(TextureImage,0,sizeof(void *)*2);
	if (TextureImage[0]=LoadBMP("E:/wall.bmp"))
	{
		Status=TRUE;
		glGenTextures(1,&texture[0]);
		glBindTexture(GL_TEXTURE_2D,texture[0]);
		glTexImage2D(GL_TEXTURE_2D,0,3,TextureImage[0]->sizeX,TextureImage[0]->sizeY,0,GL_RGB,GL_UNSIGNED_BYTE,TextureImage[0]->data);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
	}
	if (TextureImage[1]=LoadBMP("E:/ground.bmp"))
	{
		Status=TRUE;
		glGenTextures(1,&texture[1]);
		glBindTexture(GL_TEXTURE_2D,texture[1]);
		glTexImage2D(GL_TEXTURE_2D,0,3,TextureImage[1]->sizeX,TextureImage[1]->sizeY,0,GL_RGB,GL_UNSIGNED_BYTE,TextureImage[1]->data);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
	}
	if (TextureImage[0])
	{
		if (TextureImage[0]->data)
			free(TextureImage[0]->data);
		free(TextureImage[0]);
	}
	if (TextureImage[1])
	{
		if (TextureImage[1]->data)
			free(TextureImage[1]->data);
		free(TextureImage[1]);
	}
	return Status;
}

