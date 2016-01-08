/**
 * 
 */


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public class TestCases extends CyclicIterator<Map<String, Angled>> {

  Map<String, Angled> stop() {
    return this.stop;
  }

  private final Map<String, Angled> stop;

  @SuppressWarnings("unchecked")
  TestCases() {
    this.stop = new HashMap<String, Angled>();
    final Map<String, Angled> walk = new HashMap<String, Angled>();
    final Map<String, Angled> yell = new HashMap<String, Angled>();
    final Map<String, Angled> welcome = new HashMap<String, Angled>();
    final Map<String, Angled> one = new HashMap<String, Angled>();
    final Map<String, Angled> superman = new HashMap<String, Angled>();

    super.add(stop, walk, yell, welcome, one, superman);

    // the situation of stop
    
    for (int i=0;i<2;i++)
    {
    		stop.put(PA2.ARM[i][2], new BaseAngled(60, 0, 0));
    		stop.put(PA2.ARM[i][3], new BaseAngled(6, 0, 0));
    }
    stop.put(PA2.ARM[0][1], new BaseAngled(62, 38, 0));
    stop.put(PA2.ARM[1][1], new BaseAngled(62, 14, 0));
    stop.put(PA2.ARM[0][0], new BaseAngled(0, 90, 0));
    stop.put(PA2.ARM[1][0], new BaseAngled(0, -90, 0));

    stop.put(PA2.LEG[0][0], new BaseAngled(89, 0, 0));
    stop.put(PA2.LEG[0][1], new BaseAngled(0, 0, 0));
    stop.put(PA2.LEG[0][2], new BaseAngled(-89, 0, 0));
    stop.put(PA2.LEG[1][0], new BaseAngled(89, 0, 0));
    stop.put(PA2.LEG[1][1], new BaseAngled(0, 0, 0));
    stop.put(PA2.LEG[1][2], new BaseAngled(-89, 0, 0));
   
    for (int i=0;i<2;i++)
    {
    		for(int j=1;j<5;j++)
    		{
    			stop.put(PA2.FINGER[i][j*3], new BaseAngled(18, 0, 0));
    			stop.put(PA2.FINGER[i][j*3+1], new BaseAngled(20, 0, 0));
    			stop.put(PA2.FINGER[i][j*3+2], new BaseAngled(25, 0, 0));
    		}
    		stop.put(PA2.FINGER[i][0], new BaseAngled(0, Math.pow(-1, i+1)*50, Math.pow(-1, i)*65));
    		stop.put(PA2.FINGER[i][1], new BaseAngled(20, 0, 0));
    		stop.put(PA2.FINGER[i][2], new BaseAngled(30, 0, 0));
    }

    // the walk sign test case
    
    walk.put(PA2.ARM[0][0], new BaseAngled(0, 90, 0));
    walk.put(PA2.ARM[0][1], new BaseAngled(62, 0, 0));
    walk.put(PA2.ARM[0][2], new BaseAngled(60, -57, 28));
    walk.put(PA2.ARM[0][3], new BaseAngled(6, 0, 0));
    
    walk.put(PA2.ARM[1][0], new BaseAngled(0, -90, 0));
    walk.put(PA2.ARM[1][1], new BaseAngled(62, -56, 0));
    walk.put(PA2.ARM[1][2], new BaseAngled(60, 20, -60));
    walk.put(PA2.ARM[1][3], new BaseAngled(6, 0, 0));
       
    for (int i=0;i<2;i++)
    {
    		for(int j=1;j<5;j++)
    		{
    			walk.put(PA2.FINGER[i][j*3], new BaseAngled(66, 0, 0));
    			walk.put(PA2.FINGER[i][j*3+1], new BaseAngled(95, 0, 0));
    			walk.put(PA2.FINGER[i][j*3+2], new BaseAngled(80, 0, 0));
    		}
    		walk.put(PA2.FINGER[i][0], new BaseAngled(50, Math.pow(-1, i+1)*50, Math.pow(-1, i)*65));
    		walk.put(PA2.FINGER[i][1], new BaseAngled(52, 0, 0));
    		walk.put(PA2.FINGER[i][2], new BaseAngled(80, 0, 0));
    }
	
	walk.put(PA2.LEG[0][0], new BaseAngled(110, 0, 0));
	walk.put(PA2.LEG[0][1], new BaseAngled(43, 0, 0));
	walk.put(PA2.LEG[0][2], new BaseAngled(-89, 0, 0));
	walk.put(PA2.LEG[1][0], new BaseAngled(50, 0, 0));
	walk.put(PA2.LEG[1][1], new BaseAngled(22, 0, 0));
	walk.put(PA2.LEG[1][2], new BaseAngled(-75, 0, 0));

    // the yell test case
	
	yell.put(PA2.BODY, new BaseAngled(-20, 14, 44));
	yell.put(PA2.HEAD, new BaseAngled(-15, 0, 5));
	yell.put(PA2.LEG[0][0], new BaseAngled(89, 26, 150));
	yell.put(PA2.LEG[1][0], new BaseAngled(89, -26, -150));
	yell.put(PA2.LEG[0][1], new BaseAngled(0, 0, 0));
	yell.put(PA2.LEG[1][1], new BaseAngled(0, 0, 0));
	yell.put(PA2.LEG[0][2], new BaseAngled(89, 0, 0));
	yell.put(PA2.LEG[1][2], new BaseAngled(89, 0, 0));
	
	
	for (int i=0;i<2;i++)
	{
		yell.put(PA2.ARM[i][0], new BaseAngled(0, Math.pow(-1, i)*90, 0));
		yell.put(PA2.ARM[i][1], new BaseAngled(64, Math.pow(-1, i+1)*14, 0));
		yell.put(PA2.ARM[i][2], new BaseAngled(64, Math.pow(-1, i+1)*69, Math.pow(-1, i)*58));
		yell.put(PA2.ARM[i][3], new BaseAngled(6, 0, 0));
		
		for (int j=1; j<5;j++)
		{
			yell.put(PA2.FINGER[i][j*3], new BaseAngled(0, 0, 0));
			yell.put(PA2.FINGER[i][j*3+1], new BaseAngled(0, 0, 0));
			yell.put(PA2.FINGER[i][j*3+2], new BaseAngled(0, 0, 0));
		}
		yell.put(PA2.FINGER[i][0], new BaseAngled(0, Math.pow(-1, i+1)*50, Math.pow(-1, i)*65));
		yell.put(PA2.FINGER[i][1], new BaseAngled(20, 0, 0));
		yell.put(PA2.FINGER[i][2], new BaseAngled(30, 0, 0));
	}
	
    // the welcome test case
   
    welcome.put(PA2.ARM[0][0], new BaseAngled(0, 90, 0));
    welcome.put(PA2.ARM[0][1], new BaseAngled(0, 0, 0));
    welcome.put(PA2.ARM[0][2], new BaseAngled(0, 0, 0));
    welcome.put(PA2.ARM[0][3], new BaseAngled(0, 0, 0));
    
    welcome.put(PA2.ARM[1][0], new BaseAngled(0, -90, 0));
    welcome.put(PA2.ARM[1][1], new BaseAngled(62, -20, 0));
    welcome.put(PA2.ARM[1][2], new BaseAngled(60, 0, -32));
    welcome.put(PA2.ARM[1][3], new BaseAngled(6, 0, 0));
    
    welcome.put(PA2.LEG[0][0], new BaseAngled(89, 0, 0));
    welcome.put(PA2.LEG[0][1], new BaseAngled(0, 0, 0));
    welcome.put(PA2.LEG[0][2], new BaseAngled(-89, 0, 0));
    welcome.put(PA2.LEG[1][0], new BaseAngled(89, 0, 0));
    welcome.put(PA2.LEG[1][1], new BaseAngled(0, 0, 0));
    welcome.put(PA2.LEG[1][2], new BaseAngled(-89, 0, 0));
    
    for (int i=0;i<2;i++)
    {
    		for (int j=1; j<5;j++)
    		{
			welcome.put(PA2.FINGER[i][j*3], new BaseAngled(-7, 4, 0));
			welcome.put(PA2.FINGER[i][j*3+1], new BaseAngled(8, 0, 0));
			welcome.put(PA2.FINGER[i][j*3+2], new BaseAngled(8, 0, 0));
		}
		welcome.put(PA2.FINGER[i][0], new BaseAngled(0, Math.pow(-1, i+1)*50, Math.pow(-1, i)*65));
		welcome.put(PA2.FINGER[i][1], new BaseAngled(8, 0, 0));
		welcome.put(PA2.FINGER[i][2], new BaseAngled(8, 0, 0));
    }
   
    // the one test case
 
    one.put(PA2.LEG[0][0], new BaseAngled(50, 18, 0));
    one.put(PA2.LEG[0][1], new BaseAngled(100, 0, 0));
    one.put(PA2.LEG[0][2], new BaseAngled(-103, 2, 0));
    one.put(PA2.LEG[1][0], new BaseAngled(110, -14, 0));
    one.put(PA2.LEG[1][1], new BaseAngled(100, 0, 0));
    one.put(PA2.LEG[1][2], new BaseAngled(-81, 6, 0));
    
    one.put(PA2.ARM[0][0], new BaseAngled(0, 90, 0));
    one.put(PA2.ARM[0][1], new BaseAngled(62, 34, 0));
    one.put(PA2.ARM[0][2], new BaseAngled(60, 0, 0));
    one.put(PA2.ARM[0][3], new BaseAngled(6, 2, 0));
    
    one.put(PA2.ARM[1][0], new BaseAngled(0, -90, 0));
    one.put(PA2.ARM[1][1], new BaseAngled(62, 24, 0));
    one.put(PA2.ARM[1][2], new BaseAngled(26, 26, 0));
    one.put(PA2.ARM[1][3], new BaseAngled(-6, 0, 0));
    
    for(int j=1;j<5;j++)
	{
		one.put(PA2.FINGER[0][j*3], new BaseAngled(18, 0, 0));
		one.put(PA2.FINGER[0][j*3+1], new BaseAngled(20, 0, 0));
		one.put(PA2.FINGER[0][j*3+2], new BaseAngled(25, 0, 0));
	}
	one.put(PA2.FINGER[0][0], new BaseAngled(0, -50, 65));
	one.put(PA2.FINGER[0][1], new BaseAngled(20, 0, 0));
	one.put(PA2.FINGER[0][2], new BaseAngled(30, 0, 0));
	
	for(int j=2;j<5;j++)
	{
		one.put(PA2.FINGER[1][j*3], new BaseAngled(66, 0, 0));
		one.put(PA2.FINGER[1][j*3+1], new BaseAngled(95, 0, 0));
		one.put(PA2.FINGER[1][j*3+2], new BaseAngled(80, 0, 0));
	}
	one.put(PA2.FINGER[1][0], new BaseAngled(0, 50, -65));
	one.put(PA2.FINGER[1][1], new BaseAngled(52, 0, 0));
	one.put(PA2.FINGER[1][2], new BaseAngled(80, 0, 0));
	
	one.put(PA2.FINGER[1][3], new BaseAngled(3, 0, -0));
	one.put(PA2.FINGER[1][4], new BaseAngled(0, 0, 0));
	one.put(PA2.FINGER[1][5], new BaseAngled(0, 0, 0));
   
	//superman testcase
	
	superman.put(PA2.LEG[0][0], new BaseAngled(110, 0, 0));
    superman.put(PA2.LEG[0][1], new BaseAngled(0, 0, 0));
    superman.put(PA2.LEG[0][2], new BaseAngled(-61, 0, 0));
    superman.put(PA2.LEG[1][0], new BaseAngled(50, 0, 0));
    superman.put(PA2.LEG[1][1], new BaseAngled(100, 0, 0));
    superman.put(PA2.LEG[1][2], new BaseAngled(-61, 6, 0));
    
    superman.put(PA2.ARM[0][0], new BaseAngled(0, 90, 0));
    superman.put(PA2.ARM[0][1], new BaseAngled(62, -24, 0));
    superman.put(PA2.ARM[0][2], new BaseAngled(0, -6, 0));
    superman.put(PA2.ARM[0][3], new BaseAngled(-6, 0, 0));
    
    superman.put(PA2.ARM[1][0], new BaseAngled(0, -90, 0));
    superman.put(PA2.ARM[1][1], new BaseAngled(62, 24, 0));
    superman.put(PA2.ARM[1][2], new BaseAngled(0, 0, -78));
    superman.put(PA2.ARM[1][3], new BaseAngled(0, 0, 0));
    
    for(int j=1;j<5;j++)
	{
		superman.put(PA2.FINGER[0][j*3], new BaseAngled(-15, 0, 0));
		superman.put(PA2.FINGER[0][j*3+1], new BaseAngled(0, 0, 0));
		superman.put(PA2.FINGER[0][j*3+2], new BaseAngled(0, 0, 0));
	}
	superman.put(PA2.FINGER[0][0], new BaseAngled(0, -50, 65));
	superman.put(PA2.FINGER[0][1], new BaseAngled(20, 0, 0));
	superman.put(PA2.FINGER[0][2], new BaseAngled(30, 0, 0));
	
	for(int j=1;j<5;j++)
	{
		superman.put(PA2.FINGER[1][j*3], new BaseAngled(-15, 0, 0));
		superman.put(PA2.FINGER[1][j*3+1], new BaseAngled(0, 0, 0));
		superman.put(PA2.FINGER[1][j*3+2], new BaseAngled(0, 0, 0));
	}
	superman.put(PA2.FINGER[1][0], new BaseAngled(0, 50, -65));
	superman.put(PA2.FINGER[1][1], new BaseAngled(0, 0, 0));
	superman.put(PA2.FINGER[1][2], new BaseAngled(0, 0, 0));
  }
}
