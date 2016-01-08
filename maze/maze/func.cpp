#include "head.h"

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
	if(maze[s.top->y][(s.top->x)+1]==0)
	{
		e.x=(s.top->x)+1;
		e.y=s.top->y;
		return OK;
	}
	 if(maze[(s.top->y)+1][s.top->x]==0)
	{
		e.x=s.top->x;
		e.y=(s.top->y)+1;
		return OK;
	}
	 if(maze[(s.top->y)-1][s.top->x]==0)
	{
		e.x=s.top->x;
		e.y=(s.top->y)-1;
		return OK;
	}
	 if(maze[s.top->y][(s.top->x)-1]==0)
	{
		e.x=(s.top->x)-1;
		e.y=s.top->y;
		return OK;
	}
	 return FALSE;
}

int InitStack(SqStack &s)
{
	s.base=(SElemType *)malloc(SIZE*sizeof(SElemType));
	if (!s.base) exit(OVERLOW);
	s.top=s.base;
	s.stacksize=SIZE;
	return OK;
}