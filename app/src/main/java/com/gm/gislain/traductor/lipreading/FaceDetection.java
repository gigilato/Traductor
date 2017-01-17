package com.gm.gislain.traductor.lipreading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by Gislain on 25/01/16.
 */
public class FaceDetection {

    private Bitmap original;
    private Canvas canvas;
    private Face face;
    private Mouth mouth;

    private Point boxTopLeftCorner;
    private Point boxBottomRightCorner;

    private boolean detectionSuccessfull;

    public FaceDetection(Context context,Bitmap bitmap){
        original = bitmap;
        canvas = new Canvas(original);
        mouth = new Mouth();
        detectionSuccessfull = false;

        FaceDetector detector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setTrackingEnabled(true)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                .setMode(com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE)
                .build();

        while(!detector.isOperational()){
            Toast.makeText(context,"FaceDetector dependencies are not yet available!",Toast.LENGTH_SHORT).show();
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        Frame frame = new Frame.Builder().setBitmap(original).build();
        SparseArray<Face> mFaces = detector.detect(frame);
        face = mFaces.valueAt(0);

        mouth.setFaceWidth((double)face.getWidth());

        setFaceBounds();
        setBaseMouthLandmarks();
        if(mouth.baseLandmarksDetected()){
            detectionSuccessfull = true;
            setMouthBounds();
            setAdditionnalMouthLandmarks();
        }

        detector.release();
    }

    public boolean isDetectionSuccessful(){return detectionSuccessfull;}

    public Mouth getMouth(){return mouth;}

    public Bitmap getDetectedBitmap(){
        return original;
    }

    private Bitmap getCannyBitmap(){
        Canny canny = new Canny();
        canny.setSourceImage(getMouthBitmap());
        canny.setLowThreshold(1f);
        canny.setHighThreshold(2f);
        canny.process();
        return canny.getEdgesImage();
    }

    private Bitmap getGrayscaleBitmap() {
        int width, height;
        height = original.getHeight();
        width = original.getWidth();

        Bitmap grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(original, 0, 0, paint);
        return grayscale;
    }

    private Bitmap getRotatedBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap scaledBitmap =
                Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(), true);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap.copy(Bitmap.Config.RGB_565, true);
    }


    private Bitmap getMouthBitmap(){
        return Bitmap.createBitmap(original, mouth.getTopLeftCornerMouth().x, mouth.getTopLeftCornerMouth().y,
                mouth.getWidth(), mouth.getHeight());
    }

    private void setFaceBounds(){
        boxTopLeftCorner = new Point((int)face.getPosition().x,(int)face.getPosition().y);
        boxBottomRightCorner = new Point((int)(face.getPosition().x + face.getWidth()),(int)(face.getPosition().y + face.getHeight()));
    }

    private void setMouthBounds(){
        int width = mouth.getRightMouth().x - mouth.getLeftMouth().x;
        int height = (mouth.getBottomMouth().y - ((mouth.getRightMouth().y + mouth.getLeftMouth().y) / 2))*2;
        mouth.setWidth(width);
        mouth.setHeight(height);
        Point leftTopCornerMouth = new Point(mouth.getLeftMouth().x,mouth.getBottomMouth().y - height);
        mouth.setTopLeftCornerMouth(leftTopCornerMouth.x,leftTopCornerMouth.y);
    }

    private void setBaseMouthLandmarks() {
        for(Landmark landmark : face.getLandmarks()) {
            switch (landmark.getType()){
                case Landmark.BOTTOM_MOUTH :
                    mouth.setBottomMouth((int)landmark.getPosition().x, (int)landmark.getPosition().y);
                    Log.d("Detection","Botom mouth detected");
                    break;
                case Landmark.RIGHT_MOUTH :
                    mouth.setLeftMouth((int) landmark.getPosition().x, (int) landmark.getPosition().y);
                    Log.d("Detection", "Left mouth detected");
                    break;
              case Landmark.LEFT_MOUTH :
                    mouth.setRightMouth((int) landmark.getPosition().x, (int) landmark.getPosition().y);
                    Log.d("Detection", "Right mouth detected");
                    break;
                default :
                    break;
            }
        }
    }

    private void setAdditionnalMouthLandmarks(){
        int x = mouth.getTopLeftCornerMouth().x;
        int y = mouth.getTopLeftCornerMouth().y;

        Bitmap canny = getCannyBitmap();
        int mid = (mouth.getLeftMouth().y + mouth.getRightMouth().y) / 2;
        int range = mouth.getBottomMouth().y - mid;

        int yTop = 0;
        int yMidTopLeft = 0;
        int yMidTopRight = 0;
        int yMidBottomLeft = 0;
        int yMidBottomRight = 0;

        int xMidLeft = (mouth.getLeftMouth().x + mouth.getBottomMouth().x) /2;
        int xMidRight = (mouth.getRightMouth().x + mouth.getBottomMouth().x) /2;

        for(int i = 0;i<range;i++){
            int pixelTop = canny.getPixel(mouth.getBottomMouth().x - x,mid -i - y);
            int pixelMidTopLeft = canny.getPixel(xMidLeft - x,mid -i - y);
            int pixelMidTopRight = canny.getPixel(xMidRight - x,mid -i - y);
            int pixelMidBottomLeft = canny.getPixel(xMidLeft - x,mid +i - y);
            int pixelMidBottomRight = canny.getPixel(xMidRight - x,mid +i - y);

            if(pixelTop == -1) yTop = mid - i;
            if(pixelMidTopLeft == -1) yMidTopLeft = mid - i;
            if(pixelMidTopRight == -1) yMidTopRight = mid - i;
            if(pixelMidBottomLeft == -1) yMidBottomLeft = mid + i;
            if(pixelMidBottomRight == -1) yMidBottomRight = mid + i;
        }

        if(yTop != 0) mouth.setTopMouth(mouth.getBottomMouth().x,yTop);
        else mouth.setTopMouth(mouth.getBottomMouth().x,mouth.getBottomMouth().y - mouth.getHeight());
        if(yMidTopLeft != 0) mouth.setMidTopLeftMouth(xMidLeft, yMidTopLeft);
        if(yMidTopRight != 0) mouth.setMidTopRightMouth(xMidRight, yMidTopRight);
        if(yMidBottomLeft != 0) mouth.setMidBottomLeftMouth(xMidLeft,yMidBottomLeft);
        if(yMidBottomRight != 0) mouth.setMidBottomRightMouth(xMidRight, yMidBottomRight);
    }

    public void drawMouthBounds(){
        Paint paintLandmark = new Paint();
        paintLandmark.setColor(Color.GREEN);
        paintLandmark.setStyle(Paint.Style.STROKE);
        paintLandmark.setStrokeWidth(5);

        canvas.drawRect(mouth.getTopLeftCornerMouth().x, mouth.getTopLeftCornerMouth().y,
                mouth.getTopLeftCornerMouth().x + mouth.getWidth(),
                mouth.getTopLeftCornerMouth().y + mouth.getHeight(),
                paintLandmark);
    }

    public void drawFaceBounds(){
        Paint paintBox = new Paint();
        paintBox.setColor(Color.RED);
        paintBox.setStyle(Paint.Style.STROKE);
        paintBox.setStrokeWidth(5);

        if(boxTopLeftCorner != null && boxBottomRightCorner != null)
            canvas.drawRect(boxTopLeftCorner.x, boxTopLeftCorner.y, boxBottomRightCorner.x, boxBottomRightCorner.y, paintBox);
    }

    public void drawMouthLandmarks(){
        Paint paintLandmark = new Paint();
        paintLandmark.setColor(Color.BLUE);
        paintLandmark.setStyle(Paint.Style.FILL);
        paintLandmark.setStrokeWidth(5);

        if(mouth.baseLandmarksDetected()){
            canvas.drawPoint(mouth.getBottomMouth().x, mouth.getBottomMouth().y, paintLandmark);
            canvas.drawPoint(mouth.getLeftMouth().x, mouth.getLeftMouth().y, paintLandmark);
            canvas.drawPoint(mouth.getRightMouth().x, mouth.getRightMouth().y, paintLandmark);
            canvas.drawPoint(mouth.getTopMouth().x, mouth.getTopMouth().y, paintLandmark);
        }

    }

    public void drawAdditionalLandmarks(){
        Paint paintLandmarkMidTop = new Paint();
        paintLandmarkMidTop.setColor(Color.YELLOW);
        paintLandmarkMidTop.setStyle(Paint.Style.FILL);
        paintLandmarkMidTop.setStrokeWidth(5);

        Paint paintLandmarkMidBottom = new Paint();
        paintLandmarkMidBottom.setColor(Color.GREEN);
        paintLandmarkMidBottom.setStyle(Paint.Style.FILL);
        paintLandmarkMidBottom.setStrokeWidth(5);

        if(mouth.additionnalLandmarksDetected()){
            canvas.drawPoint(mouth.getMidTopLeftMouth().x, mouth.getMidTopLeftMouth().y, paintLandmarkMidTop);
            canvas.drawPoint(mouth.getMidTopRightMouth().x, mouth.getMidTopRightMouth().y, paintLandmarkMidTop);
            canvas.drawPoint(mouth.getMidBottomLeftMouth().x, mouth.getMidBottomLeftMouth().y, paintLandmarkMidBottom);
            canvas.drawPoint(mouth.getMidBottomRightMouth().x, mouth.getMidBottomRightMouth().y, paintLandmarkMidBottom);
       }
    }

    public boolean compare(Mouth m){return mouth.compare(m);}

}
