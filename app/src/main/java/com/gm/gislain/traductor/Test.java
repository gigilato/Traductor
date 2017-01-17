package com.gm.gislain.traductor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.gm.gislain.traductor.lipreading.FaceDetection;
import com.gm.gislain.traductor.lipreading.Mouth;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;

public class Test extends Activity {

    private ImageView imageView;
    private Uri uri;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSIONS = 2;


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_test_canny);
        imageView = (ImageView) findViewById(R.id.imageview);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else{
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Toast.makeText(this,"Traductor needs camera permission!",Toast.LENGTH_LONG).show();
                }
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(this,"Traductor needs write permission!",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }
        else{
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myImage.jpg");
        uri = Uri.fromFile(file);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                FaceDetection faceDetection = new FaceDetection(this,imageBitmap);

                Bitmap referenceBitmapO1 = BitmapFactory.decodeResource(getResources(), R.raw.voyelle_o1);
                Bitmap referenceBitmapO2 = BitmapFactory.decodeResource(getResources(), R.raw.voyelle_o2);
                FaceDetection faceDetectionO1 = new FaceDetection(this,referenceBitmapO1);
                FaceDetection faceDetectionO2 = new FaceDetection(this,referenceBitmapO2);
                Mouth referenceMouthO1 = faceDetectionO1.getMouth();
                Mouth referenceMouthO2 = faceDetectionO2.getMouth();

                Bitmap referenceBitmapI1 = BitmapFactory.decodeResource(getResources(), R.raw.voyelle_i1);
                Bitmap referenceBitmapI2 = BitmapFactory.decodeResource(getResources(), R.raw.voyelle_i2);
                FaceDetection faceDetectionI1 = new FaceDetection(this,referenceBitmapI1);
                FaceDetection faceDetectionI2 = new FaceDetection(this,referenceBitmapI2);
                Mouth referenceMouthI1 = faceDetectionI1.getMouth();
                Mouth referenceMouthI2 = faceDetectionI2.getMouth();

                if(faceDetection.isDetectionSuccessful()){
                    Bitmap bitmap = faceDetection.getDetectedBitmap();
                    imageView.setImageBitmap(bitmap);

                    if(faceDetection.compare(referenceMouthO1)){
                        Toast.makeText(this,"voyelle O reconnue",Toast.LENGTH_LONG).show();
                    }else if(faceDetection.compare(referenceMouthO2)){
                        Toast.makeText(this,"voyelle O reconnue",Toast.LENGTH_LONG).show();
                    }else if(faceDetection.compare(referenceMouthI1)){
                        Toast.makeText(this,"voyelle I reconnue",Toast.LENGTH_LONG).show();
                    }else if(faceDetection.compare(referenceMouthI2)){
                        Toast.makeText(this,"voyelle I reconnue",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(this,"aucune syllabe reconnue",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(this, "Not enough landmarks detected!", Toast.LENGTH_LONG).show();
                    imageView.setImageBitmap(imageBitmap);
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this,"Error bitmap!",Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case REQUEST_PERMISSIONS :
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Traductor needs permissions!",Toast.LENGTH_LONG).show();
                }else{
                    dispatchTakePictureIntent();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    public void testSyllabe(int rawImage){
        //rawImage = R.raw.nomdufichier
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),rawImage);
        FaceDetection faceDetection = new FaceDetection(this,bitmap);
        if(faceDetection.isDetectionSuccessful()){
            faceDetection.drawFaceBounds();
            faceDetection.drawMouthLandmarks();
            imageView.setImageBitmap(faceDetection.getDetectedBitmap());
        }else{
            imageView.setImageBitmap(bitmap);
        }
    }

}

