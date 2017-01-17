package com.gm.gislain.traductor.lipreading;

import android.content.Context;
import android.util.Log;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import java.util.Locale;

/**
 * Created by sabrina on 26/02/2016.
 */
public class Dictionary implements SpellCheckerSession.SpellCheckerSessionListener {

    private SpellCheckerSession scs;
    private String suggestions;
    private StringBuilder sb;
    Context mContext;

    public Dictionary(String s,Context c){
        this.mContext=c;
        final TextServicesManager tsm = (TextServicesManager)mContext.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);

        //1er argument Bundle 2eme la langue, le listener, spellCheckeranguageSetting
        SpellCheckerSession scs = tsm.newSpellCheckerSession(null, Locale.FRENCH, this,false);
        scs.getSentenceSuggestions(new TextInfo[] {new TextInfo(s)}, 3);
        scs.getSuggestions(new TextInfo(s), 7);//le nombre de mots qu'on veut
    }

    @Override
    public void onGetSuggestions(final SuggestionsInfo[] arg0) {
        for (int i = 0; i < arg0.length; ++i) {
            final int len = arg0[i].getSuggestionsCount();
            sb.append('\n');
            for (int j = 0; j < len; ++j) {
                sb.append(arg0[i].getSuggestionAt(j) + ",");
            }
        }
        System.out.println("les mots"+sb.toString());
    }

    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        for (int i = 0; i < results.length; ++i) {
            for (int j = 0; j < results[i].getSuggestionsCount(); ++j) {
                SuggestionsInfo si =  results[i].getSuggestionsInfoAt(j);
                if ((si.getSuggestionsAttributes() & SuggestionsInfo.RESULT_ATTR_IN_THE_DICTIONARY) != 0)
                    Log.d("", "EXACT MATCH FOUND");
            }
        }
    }

    public StringBuilder getWords(){
        return sb;
    }
}
