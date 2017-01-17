package com.gm.gislain.traductor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.gm.gislain.traductor.lipreading.FaceDetection;
import com.gm.gislain.traductor.lipreading.Word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProcessVideoActivity extends Activity {

    private final int BITMAP_PER_WORD = 5;
    private File mAssetFolder;
    private CircleProgress circleProgress;
    private Button returnButton;
    private boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_video);
        Intent intent = getIntent();

        circleProgress = (CircleProgress) findViewById(R.id.circle_progress);
        circleProgress.setProgress(0);

        returnButton = (Button) findViewById(R.id.returnButton);
        success = true;

        createAssetFolder();

        processAddVideo(intent.getData());

    }

    protected void processAddVideo(Uri videoUri){
        final int START_TIME = 500;
        ArrayList<Bitmap> frameList = new ArrayList<>();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, videoUri);

        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.d("duration",""+duration);

        for(int i=0;i<duration;i+= Word.TIMING){
            for(int j=0;j<BITMAP_PER_WORD*100;j+=100) {
                frameList.add(retriever.getFrameAtTime((i + j + START_TIME)*1000));
            }
        }

        processLipReading(frameList);
    }

    protected void processLipReading(ArrayList<Bitmap> list){
        FaceDetection faceDetection;
        ArrayList<Bitmap> detectionSucessfullList = new ArrayList<>();
        int size = Word.SOUNDS.length;
        for(int i=0;i<size;i++){
            for(int j=0;j<BITMAP_PER_WORD;j++){

                try {
                    faceDetection = new FaceDetection(this, list.get(i * BITMAP_PER_WORD + j));
                }catch(Exception e){
                    e.printStackTrace();
                    continue;
                }

                if (faceDetection.isDetectionSuccessful()) {
                    Log.d("detected", Word.SOUNDS[i]);
                    detectionSucessfullList.add(list.get(i * BITMAP_PER_WORD + j));
                    circleProgress.setProgress((i * 100) / size);
                    break;
                }
            }
        }

        if(detectionSucessfullList.size() != size){
            Toast.makeText(this,"Only " + detectionSucessfullList.size() + " bitmaps detected",Toast.LENGTH_LONG).show();
            success = false;
            returnButton.setVisibility(View.VISIBLE);
            returnButton.setEnabled(true);
            return;
        }else{
            circleProgress.setProgress(100);
        }

        for(int i=0;i<detectionSucessfullList.size();i++){
            try{
                File file = createAssetFileName(i);
                OutputStream stream = new FileOutputStream(file);
                detectionSucessfullList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
            }catch(IOException e){
                e.printStackTrace();
                success = false;
                Toast.makeText(this,"could not make " + Word.SOUNDS[i] + "file",Toast.LENGTH_LONG).show();
            }
        }

        returnButton.setVisibility(View.VISIBLE);
        returnButton.setEnabled(true);
    }

    protected void createAssetFolder(){
        File pictureFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mAssetFolder = new File(pictureFile,"Traductor Assets");
        if(!mAssetFolder.exists()){
            if(!mAssetFolder.mkdirs())
                Toast.makeText(this,"Can't create Assets directory",Toast.LENGTH_LONG).show();
        }
    }

    protected File createAssetFileName(int i) throws IOException {
        String prepend = "traductor_" + Word.SOUNDS[i] + "_";
        return File.createTempFile(prepend,".jpg",mAssetFolder);
    }

    public void returnToMenu(View view){
        Intent intent = new Intent(this,ChooseModuleLipActivity.class);
        if(success)
            intent.putExtra("result","Database successfully created");
        else
            intent.putExtra("result","Database not created");
        startActivity(intent);
    }
}
