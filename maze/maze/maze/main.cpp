#include "stock.h"

int maze[YSIZE][XSIZE]={
		0,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		0,-1,-1,-1,-1,0,0,0,0,0,
		0,0,0,0,-1,0,-1,-1,-1,0,
		-1,-1,-1,0,-1,0,-1,0,0,0,
		0,0,0,0,-1,0,-1,0,-1,-1,
		0,-1,-1,-1,-1,0,-1,0,0,-1,
		0,-1,0,0,0,0,-1,0,0,-1,
		0,-1,0,-1,-1,-1,-1,0,0,0,
		0,0,0,0,0,-1,-1,0,-1,-1,
		-1,-1,-1,-1,0,-1,-1,0,0,0};
SqStack s;
float x=0,y=0;
int main(int argc,char *argv[])
{
	Start(s,maze);
	glutInit(&argc,argv);
	glutInitDisplayMode(GLUT_DOUBLE |GLUT_DEPTH|GLUT_RGB);
	glutInitWindowPosition(100,100);
	glutInitWindowSize(900,600);
	glutCreateWindow("maze");
	init();
	glutDisplayFunc(&display);
	glutReshapeFunc(&Reshape);
	glutIdleFunc(&ViewMove);
	glutMainLoop();
	return 0;

}