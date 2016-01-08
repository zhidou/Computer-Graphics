#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <glut.h>
#include <gl.h>
#include <glu.h>
#include <GLAUX.H>
#include <math.h>


#define SIZE 100
#define XSIZE 10
#define YSIZE 10
#define ADD 100
#define OK 1
#define FALSE 0
#define MAXTEXTURE 2
#define TRUE 1
#define OVER 2
#define OVERLOW -1
#define minn 0.0009


typedef struct
{
	int x;
	int y;
}SElemType;

typedef struct
{
	SElemType *base;
	SElemType *top;
	int stacksize;
}SqStack;

int InitStack(SqStack &s);
int Push(SqStack &s,SElemType e);
int Pop(SqStack &s,SElemType &e);
int Step(SqStack &s,int maze[][XSIZE]);
int Search(SqStack &s,int maze[][XSIZE],SElemType &e);
int init();
void display(void);
void Reshape(int w,int h);
int Start(SqStack &s,int maze[][XSIZE]);
AUX_RGBImageRec *LoadBMP(char *Filename);
int LoadGLTextures();
void ViewMove(void);
