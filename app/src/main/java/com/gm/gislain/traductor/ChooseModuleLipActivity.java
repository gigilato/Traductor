package com.gm.gislain.traductor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gm.gislain.traductor.lipreading.Word;

public class ChooseModuleLipActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_module_lip);
        String result = getIntent().getStringExtra("result");
        if(result != null)
            Toast.makeText(this,result,Toast.LENGTH_LONG).show();
    }

    public void onclick(View view){
        Intent i;
        switch(view.getId()){
            case R.id.readButton :
                i = new Intent(this,TakeVideoActivity.class);
                i.putExtra("action", Word.READ_WORD);
                startActivity(i);
                break;
            case R.id.addButton :
                i = new Intent(this,TakeVideoActivity.class);
                i.putExtra("action", Word.ADD_WORD);
                startActivity(i);
                break;
        }
    }
}
