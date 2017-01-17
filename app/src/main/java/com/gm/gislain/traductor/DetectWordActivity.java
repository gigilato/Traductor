package com.gm.gislain.traductor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gm.gislain.traductor.lipreading.FaceDetection;
import com.gm.gislain.traductor.lipreading.Word;

import java.io.File;
import java.util.ArrayList;

public class DetectWordActivity extends Activity {
    
    ArrayList<ArrayList<String>> soundsList;
    FaceDetection faceDetectionCompare[];
    Button menuButton;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_word);

        Intent intent = getIntent();

        menuButton = (Button) findViewById(R.id.menuButton);
        textView = (TextView) findViewById(R.id.detectResult);

        soundsList = new ArrayList<>();

        setUpFaceDetectionCompare();
        processDetectVideo(intent.getData());
    }

    protected void setUpFaceDetectionCompare(){
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File folder = new File(movieFile,"Traductor Assets");

        faceDetectionCompare = new FaceDetection[Word.SOUNDS.length];

        for(int i=0;i<folder.listFiles().length;i++){
            String path = folder.listFiles()[i].getPath();
            faceDetectionCompare[i] = new FaceDetection(this,
                    BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888, true)
            );
        }
    }
    
    protected void processDetectVideo(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);

        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.d("duration",""+duration);

        ArrayList<FaceDetection> listFaceDetectionVideo = new ArrayList<>();

        for(int i=100;i<duration;i+=500) {
            FaceDetection faceDetection = new FaceDetection(this,retriever.getFrameAtTime(i * 1000));
            if(faceDetection.isDetectionSuccessful())
                listFaceDetectionVideo.add(faceDetection);
        }

        Log.d("list Size ",""+listFaceDetectionVideo.size());
        if(listFaceDetectionVideo.size() == 0){
            Toast.makeText(this,"Unable to detect any frame in the video",Toast.LENGTH_LONG).show();
            return;
        }

        processCompare(listFaceDetectionVideo);
        processBuildWords();
    }

    protected void processCompare(ArrayList<FaceDetection> arrayList){
        for(int i=0;i<arrayList.size();i++){
            ArrayList<String> possibleSounds = new ArrayList<>();
            for(int j=0;j<faceDetectionCompare.length;j++){
                if(arrayList.get(i).compare(faceDetectionCompare[j].getMouth())){
                    possibleSounds.add(Word.SOUNDS[j]);
                }
            }
            if(possibleSounds.size() != 0){
                soundsList.add(possibleSounds);
            }
        }

        for(int i=0;i<soundsList.size();i++){
            for(int j=0;j<soundsList.get(i).size();j++)
                Log.d("array "+i,soundsList.get(i).get(j));
        }
    }

    protected void processBuildWords(){
        //Dictionary dic = new Dictionary(soundsList.get(i).toString(),this.getApplicationContext());
        ArrayList<ArrayList<String>> wordsList = new ArrayList<>();
        for(int i=0; i<soundsList.size(); i++) {
            for (int j = 0; j<soundsList.get(i).size(); j++) {
                if(i == 0){
                    ArrayList<String> word = new ArrayList<>();
                    word.add(soundsList.get(i).get(j));
                    wordsList.add(word);
                }else{
                    ArrayList<ArrayList<String>> newWordsList = new ArrayList<>();
                    for(int k=0;k<wordsList.size();k++){
                        ArrayList<String> newWord = processBuilder(wordsList.get(k),soundsList.get(i).get(j));
                        if(newWord != null) newWordsList.add(newWord);
                    }

                    for(int k=0;k<newWordsList.size();k++){
                        ArrayList<String> newWords = new ArrayList<>();
                        for(int l=0;l<newWordsList.get(k).size();l++){
                            newWords.add(newWordsList.get(k).get(l));
                        }
                        wordsList.add(newWords);
                    }
                }
            }
        }

        processDetectWord(suppressDuplicate(arrayToString(wordsList)));
    }

    private ArrayList<String> arrayToString(ArrayList<ArrayList<String>> list){
        ArrayList<String> result = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(list.get(i).size() > soundsList.size()) continue;
            String string = "";
            for(int j=0;j<list.get(i).size();j++){
                string += list.get(i).get(j);
            }
            result.add(string);
        }
        return result;
    }

    private ArrayList<String> processBuilder(ArrayList<String> list,String s){
        if(list.get(list.size()-1).equals(s)) return null;
        ArrayList<String> res = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            res.add(list.get(i));
        }
        res.add(s);
        return res;
    }

    private void processDetectWord(ArrayList<String> list){
        for(int i=0;i<list.size();i++){
            Log.d("word "+i,list.get(i));

            if(list.get(i).equals("IBOU")) textView.append("Hibou\n");
            if(list.get(i).equals("BOULO")) textView.append("Boulo\n");
            if(list.get(i).equals("BOUA")) textView.append("Bois\n");
            if(list.get(i).equals("ILO")) textView.append("Ilot\n");
        }
    }

    public ArrayList<String> suppressDuplicate(ArrayList<String> list){
        ArrayList<String> res = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            boolean exist = false;
            for(int j=0;j<res.size();j++){
                if(list.get(i).equals(res.get(j))){
                    exist = true;
                    break;
                }
            }
            if(!exist) res.add(list.get(i));
        }
        return res;
    }

    public void backToMenu(View view){
        Intent intent = new Intent(this,ChooseModuleLipActivity.class);
        startActivity(intent);
    }
}
