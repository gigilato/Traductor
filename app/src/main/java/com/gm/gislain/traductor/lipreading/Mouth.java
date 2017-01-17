package com.gm.gislain.traductor.lipreading;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Gislain on 29/01/16.
 */
public class Mouth {

    private Point bottomMouth;
    private Point leftMouth;
    private Point rightMouth;
    private Point topMouth;
    private Point midBottomLeftMouth;
    private Point midBottomRightMouth;
    private Point midTopLeftMouth;
    private Point midTopRightMouth;

    private Point topLeftCornerMouth;

    private int width;
    private int height;
    private double faceWidth;


    public Mouth(){}

    public boolean baseLandmarksDetected(){
        return (leftMouth != null && rightMouth != null && bottomMouth != null);
    }

    public boolean additionnalLandmarksDetected(){
        return (midTopLeftMouth != null && midTopRightMouth != null && midBottomLeftMouth != null && midBottomRightMouth != null);
    }

    public void setWidth(int width){this.width = width;}
    public int getWidth(){return width;}

    public void setHeight(int height){this.height = height;}
    public int getHeight(){return height;}

    public void setTopLeftCornerMouth(int x,int y){ topLeftCornerMouth = new Point(x,y);}
    public Point getTopLeftCornerMouth(){return topLeftCornerMouth;}

    public Point getTopMouth(){ return topMouth; }
    public void setTopMouth(int x, int y){topMouth = new Point(x,y);}

    public Point getBottomMouth(){ return bottomMouth; }
    public void setBottomMouth(int x, int y){bottomMouth = new Point(x,y);}

    public Point getLeftMouth(){ return leftMouth; }
    public void setLeftMouth(int x, int y){leftMouth = new Point(x,y);}

    public Point getRightMouth(){ return rightMouth; }
    public void setRightMouth(int x, int y){rightMouth = new Point(x,y);}

    public Point getMidTopLeftMouth(){ return midTopLeftMouth; }
    public void setMidTopLeftMouth(int x, int y){midTopLeftMouth = new Point(x,y);}

    public Point getMidTopRightMouth(){ return midTopRightMouth; }
    public void setMidTopRightMouth(int x, int y){midTopRightMouth = new Point(x,y);}

    public Point getMidBottomLeftMouth(){ return midBottomLeftMouth; }
    public void setMidBottomLeftMouth(int x, int y){midBottomLeftMouth = new Point(x,y);}

    public Point getMidBottomRightMouth(){ return midBottomRightMouth; }
    public void setMidBottomRightMouth(int x, int y){midBottomRightMouth = new Point(x,y);}

    private double getDistance(Point p1, Point p2){return Math.sqrt(((p2.y-p1.y)*(p2.y-p1.y))+((p2.x-p1.x)*(p2.x-p1.x)));}

    public void setFaceWidth(double d){faceWidth = d;}
    public double getFaceWidth(){return faceWidth;}

    private double getAngle(Point p1, Point p2, Point p3){
        Point vecP2P1=new Point ((p1.x-p2.x),(p1.y-p2.y));
        Point vecP2P3=new Point ((p3.x-p2.x),(p3.y-p2.y));
        double cosP1P2P3=((vecP2P1.x*vecP2P3.x)+(vecP2P1.y*vecP2P3.y))/(getDistance(p2,p1)*getDistance(p2,p3));
        return Math.acos(cosP1P2P3)/(Math.PI/180) ;
    }

    private double getDistanceLR(){return getDistance(leftMouth, rightMouth);}
    private double getDistanceTB(){return getDistance(topMouth, bottomMouth);}
    private double getDistanceLT(){return getDistance(leftMouth, topMouth);}
    private double getDistanceLB(){return getDistance(leftMouth, bottomMouth);}
    private double getDistanceRT(){return getDistance(leftMouth, topMouth);}
    private double getDistanceRB(){return getDistance(leftMouth, bottomMouth);}
    private double getAngleTLB(){return getAngle(topMouth, leftMouth, bottomMouth);}
    private double getAngleTRB(){return getAngle(topMouth, rightMouth, bottomMouth);}
    private double getAngleLTR(){return getAngle(leftMouth, topMouth, rightMouth);}
    private double getAngleLBR(){return getAngle(leftMouth, bottomMouth, rightMouth);}

    public void compareLogcat(Mouth mouth){
        double compareFaceWidth = mouth.getFaceWidth();

        Log.d("facewidth",faceWidth + "   " + compareFaceWidth);

        Log.d("LR", getDistanceLR()* mouth.getFaceWidth() + "    " + mouth.getDistanceLR()* getFaceWidth());
        Log.d("TB", getDistanceTB()* mouth.getFaceWidth() + "    " + mouth.getDistanceTB()* getFaceWidth());
        Log.d("LT", getDistanceLT()* mouth.getFaceWidth() + "    " + mouth.getDistanceLT()* getFaceWidth());
        Log.d("LB", getDistanceLB()* mouth.getFaceWidth() + "    " + mouth.getDistanceLB()* getFaceWidth());
        Log.d("RT", getDistanceRT()* mouth.getFaceWidth() + "    " + mouth.getDistanceRT()* getFaceWidth());
        Log.d("RB", getDistanceRB()* mouth.getFaceWidth() + "    " + mouth.getDistanceRB()* getFaceWidth());

        Log.d("TLB", getAngleTLB() + "  " + mouth.getAngleTLB());
        Log.d("TRB", getAngleTRB() + "  " + mouth.getAngleTRB());
        Log.d("LTR", getAngleLTR() + "  " + mouth.getAngleLTR());
        Log.d("LBR", getAngleLBR() + "  " + mouth.getAngleLBR());
    }

    public boolean compare(Mouth mouth){
        compareLogcat(mouth);
        int testConclusive = 0;

        double[] distances = {
                getDistanceLR() * mouth.getFaceWidth(),
                getDistanceTB() * mouth.getFaceWidth(),
                getDistanceLT() * mouth.getFaceWidth(),
                getDistanceLB() * mouth.getFaceWidth(),
                getDistanceRT() * mouth.getFaceWidth(),
                getDistanceRB() * mouth.getFaceWidth()
        };

        double[] distancesCompare = {
                mouth.getDistanceLR() * getFaceWidth(),
                mouth.getDistanceTB() * getFaceWidth(),
                mouth.getDistanceLT() * getFaceWidth(),
                mouth.getDistanceLB() * getFaceWidth(),
                mouth.getDistanceRT() * getFaceWidth(),
                mouth.getDistanceRB() * getFaceWidth()
        };

        double[] angles = {
                getAngleTLB(),
                getAngleTRB(),
                getAngleLTR(),
                getAngleLBR()
        };

        double[] anglesCompare = {
                mouth.getAngleTLB(),
                mouth.getAngleTRB(),
                mouth.getAngleLTR(),
                mouth.getAngleLBR()
        };

        double scale = (getFaceWidth() + mouth.getFaceWidth())/2;

        for(int i=0;i<distances.length;i++){
            if(Math.abs(distances[i]-distancesCompare[i]) <= 10 * scale) testConclusive++;
        }

        for(int i=0;i<angles.length;i++){
            if(Math.abs(angles[i]-anglesCompare[i]) <= 15 ) testConclusive++;
        }

        return (testConclusive >= 8);
    }
}
