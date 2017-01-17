package com.gm.gislain.traductor.lipreading;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Gislain on 19/05/2016.
 */
public class Word {

    public final static String[] SOUNDS = {"I","LO","A","BOU"};
    public final static int TIMING = 1500;
    public final static int ADD_WORD = 0;
    public final static int READ_WORD = 1;

    private String syllable;
    private ArrayList<Word> next;

    public Word(){
        syllable = "";
        next = new ArrayList<>();
    }

    public Word(String s){
        syllable = s;
        next = new ArrayList<>();
    }

    public String getSyllable(){ return syllable; }
    public ArrayList<Word> getNext(){ return next; }

    public void addWord(Word word,int level){
        if(level == 0){
            if(!syllable.equals(word.getSyllable())){
                next.add(word);
            }
        }else{
            for(int i=0;i<next.size();i++){
                next.get(i).addWord(word,level - 1);
            }
        }
    }

    public void findWords(ArrayList<String> result,String prev){
        result.add(prev + syllable);
        for(int i=0;i<next.size();i++){
            next.get(i).findWords(result,prev + syllable);
        }
    }

}
