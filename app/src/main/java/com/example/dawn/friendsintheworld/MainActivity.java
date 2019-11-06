package com.example.dawn.friendsintheworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dawn.friendsintheworld.Helper.LocaleHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    private Button buttonSubmit;
    private EditText editName;
    private TextView textView;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize stuff
        buttonSubmit=(Button)findViewById(R.id.buttonSubmit);
        editName=(EditText)findViewById(R.id.editName);
        textView=(TextView)findViewById(R.id.textView2);
        activity=this;

        //Language stuff
        Paper.init(activity);
        String language = Paper.book().read("language");
        if(language==null) {
            Paper.book().write("language", "en");
        }
        updateView((String) Paper.book().read("language"));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String memberName=editName.getText().toString();

                SharedPreferences sharedPreferences=getSharedPreferences("memberName", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(memberName.equals("")){
                    memberName="User";
                }
                editor.putString("memberName",memberName);
                editor.apply();
                Intent intent = new Intent(activity, GroupsActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.language_en){
            Paper.book().write("language","en");
            updateView((String)Paper.book().read("language"));
        } else if(item.getItemId() == R.id.language_sv){
            Paper.book().write("language","sv");
            updateView((String)Paper.book().read("language"));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources = context.getResources();

        buttonSubmit.setText(resources.getString(R.string.submit));
        editName.setHint(resources.getString(R.string.hintName));
        textView.setText(resources.getString(R.string.welcome));

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


}
