import java.util.*;
import java.awt.*;
import java.applet.*;
// 1000,730
/*
 * <applet code="SineWave" width=1000 height=650>
 * </applet>
 */
public class SineWave extends Applet implements Runnable{
    private Vector<double[]> points=new Vector<>();
    private Vector<double[]> points_print=new Vector<>();
    private Vector<Integer> lines=new Vector<>(),lines_num=new Vector<>();
    private final int x_base=105,y_base=650/2,x_base_last=960,amplitude=200,d=x_base_last-x_base+1,period=50;
    private int current_x=x_base,start_point_x,start_point_y,end_point_x,end_point_y;
    private double current_y=y_base;
    Thread t = null;
    volatile boolean stopFlag;
    public void init(){
        setBackground(Color.black);
        setForeground(Color.white);
        for(int i=x_base,j=0;i<=x_base_last;i+=period,j+=period){
            lines.add(i);
            lines_num.add(j);
        }
        start_point_x=x_base;
        start_point_y=y_base;
        end_point_x=x_base+period;
        end_point_y=y_base-amplitude;
    }

    public void start(){
        t=new Thread(this);
        stopFlag=false;
        t.start();
    }

    public void stop() {
        stopFlag = true;
        t = null;
    }

    public void run(){
        for(;;){
            try{
                Thread.sleep(150);
                repaint();
                if(stopFlag)
                    break;
            } catch(InterruptedException e) {}
        }
    }

    public void paint(Graphics g){
        super.paint(g);
        if(y_base-current_y==amplitude){
            start_point_x=end_point_x;
            start_point_y=end_point_y;
            end_point_x+=2*period;
            end_point_y+=2*amplitude;
        }
        else if(current_y-y_base==amplitude){
            start_point_x=end_point_x;
            start_point_y=end_point_y;
            end_point_x+=2*period;
            end_point_y-=2*amplitude;
        }
        //current_y=((end_point_y-start_point_y)/(end_point_x-start_point_x))*(current_x-start_point_x)+start_point_y;
        current_y=amplitude*Math.sin((Math.PI*(current_x-x_base))/(2*period));
        current_y=y_base-current_y;
        points.add(new double[]{current_x,current_y});
        if(current_x<=x_base_last){
            for(int i=0;i<points.size();i++)
                g.drawString(".",(int)points.elementAt(i)[0],(int)points.elementAt(i)[1]);
        }
        else{
            for(int i=points.size()-d,j=x_base;i<points.size();i++,j++)
                points_print.add(new double[]{j,points.elementAt(i)[1]});
            for(int i=0;i<points_print.size();i++)
                g.drawString(".",(int)points_print.elementAt(i)[0],(int)points_print.elementAt(i)[1]);
            points_print.removeAllElements();
        }
        g.drawLine(x_base-75,y_base,x_base_last+5,y_base);
        g.drawLine(x_base-65,y_base+10,x_base-75,y_base);
        g.drawLine(x_base-65,y_base-10,x_base-75,y_base);
        g.drawLine(x_base_last-5,y_base+10,x_base_last+5,y_base);
        g.drawLine(x_base_last-5,y_base-10,x_base_last+5,y_base);
        g.drawLine(x_base,y_base-290,x_base,y_base+290);
        g.drawLine(x_base-10,y_base-280,x_base,y_base-290);
        g.drawLine(x_base+10,y_base-280,x_base,y_base-290);
        g.drawLine(x_base-10,y_base+280,x_base,y_base+290);
        g.drawLine(x_base+10,y_base+280,x_base,y_base+290);
        g.drawString("X'",x_base-85,y_base+2);
        g.drawString("X",x_base_last+15,y_base+2);
        g.drawString("Y'",x_base-8,y_base+308);
        g.drawString("Y",x_base-5,y_base-298);
        for(int i=0;i<lines.size()&&current_x<=x_base_last;i++)
            g.drawLine(lines.elementAt(i),y_base+5,lines.elementAt(i),y_base-5);
        if(current_x>x_base_last){
            for(int i=0;i<lines.size();i++)
                lines.setElementAt(lines.elementAt(i)-1,i);
            if(lines.elementAt(0)<=x_base){
                lines.removeElementAt(0);
                lines_num.removeElementAt(0);
            }
            if(lines.elementAt(lines.size()-1)+period==x_base_last){
                lines.add(x_base_last);
                lines_num.add(lines_num.elementAt(lines_num.size()-1)+period);
            }
            for(int i=0;i<lines.size();i++)
                g.drawLine(lines.elementAt(i),y_base+5,lines.elementAt(i),y_base-5);
        }
        for(int i=0;i<lines_num.size();i++){
            if(lines.elementAt(i)==x_base)
                continue;
            int no_of_digits=(lines_num.elementAt(i)+"").length();
            String num_displayed=(no_of_digits<=3)?lines_num.elementAt(i)+"":((lines_num.elementAt(i)%10==0)?"..":"...");
            int digitzz=8*num_displayed.length();
            if(no_of_digits>3&&lines_num.elementAt(i)%Math.pow(10,no_of_digits-1)==0){
                num_displayed=lines_num.elementAt(i)/(int)Math.pow(10,no_of_digits-2)+"...";
                digitzz=19;
            }
            else if(no_of_digits>3){
                int copy=lines_num.elementAt(i);digitzz=8;String temp="";
                while(copy>0&&copy%10==0){
                    temp+="0";
                    copy/=10;
                    digitzz+=8;
                }
                num_displayed+=copy%10+temp;
                digitzz+=(lines_num.elementAt(i)%10==0)?2:3;
            }
            g.drawString(num_displayed,lines.elementAt(i)-digitzz,y_base+21);
        }
        int zero=lines_num.elementAt(0)-(lines.elementAt(0)-x_base);
        int no_of_digits=(zero+"").length();
        String num_displayed=(no_of_digits<=3)?zero+"":((zero%10==0)?"..":"...");
        int digitzz=8*num_displayed.length();
        if(no_of_digits>3&&zero%Math.pow(10,no_of_digits-1)==0){
            num_displayed=zero/(int)Math.pow(10,no_of_digits-2)+"...";
            digitzz=19;
        }
        else if(no_of_digits>3){
            int copy=zero;digitzz=8;String temp="";
            while(copy>0&&copy%10==0){
                temp+="0";
                copy/=10;
                digitzz+=8;
            }
            num_displayed+=copy%10+temp;
            digitzz+=(zero%10==0)?2:3;
        }
        g.drawString(num_displayed,x_base-digitzz-5,y_base+16);
        g.drawLine(x_base-5,y_base-amplitude,x_base+5,y_base-amplitude);
        g.drawLine(x_base-5,y_base+amplitude,x_base+5,y_base+amplitude);
        g.drawString("+"+amplitude,x_base-35,y_base-amplitude+5);
        g.drawString("-"+amplitude,x_base-35,y_base+amplitude+5);
        current_x++;
    }
}
