package com.gm.gislain.traductor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ChooseModuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_module);
    }

    public void choice(View view){
        switch (view.getId()){
            case R.id.lips:
                Intent i=new Intent(this,ChooseModuleLipActivity.class);
                startActivity(i);
                break;
            case R.id.canny:
                Intent ii = new Intent(this,Test.class);
                startActivity(ii);
                break;

            default:
                Toast.makeText(this,"Not implemented yet!",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
