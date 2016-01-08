#include <stdio.h>
#include <stdlib.h>
#define SIZE 100
#define XSIZE 10
#define YSIZE 10
#define ADD 100
#define OK 1
#define FALSE 0
#define OVER 2
#define OVERLOW -1


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
int Start(SqStack &s,int maze[][XSIZE]);