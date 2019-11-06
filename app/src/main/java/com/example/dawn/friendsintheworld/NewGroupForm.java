package com.example.dawn.friendsintheworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dawn.friendsintheworld.Helper.LocaleHelper;

import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class NewGroupForm extends AppCompatActivity {

    private Button buttonSubmit;
    private TextView tv;
    private EditText gNameField;

    private NewGroupForm activity;
    private Controller controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_form);
        activity=this;
        controller = Controller.getInstance(new NewGroupFormListener());

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        tv =(TextView) findViewById(R.id.textView);
        gNameField=(EditText) findViewById(R.id.groupNameField);


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

                String groupName = gNameField.getText().toString();

                SharedPreferences sharedPreferences=activity.getSharedPreferences("memberName", Context.MODE_PRIVATE);
                String memberName=sharedPreferences.getString("memberName","");


                try {
                    controller.connection.send(Expression.register(groupName,memberName));

                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        buttonSubmit.setText(resources.getString(R.string.buttonForm));
//        tv.setHint(resources.getString(R.string.instructionsForm));
//        gNameField.setText(resources.getString(R.string.formHint));

    }


    public class NewGroupFormListener implements ReceiveListener {
        @Override
        public void newMessage(final String answer) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONObject jsonObj = new JSONObject(answer);
                        String type = jsonObj.getString("type");
                        if (type.equals("groups")) {

                        } else if (type.equals("register")) {
                            String groupName = jsonObj.getString("group");
                            String id = jsonObj.getString("id");

                            //Saving group id
                            SharedPreferences sharedPreferences=getSharedPreferences("groupId", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("groupId",id);
                            editor.apply();

                            Intent intent = new Intent(activity, GroupsActivity.class);
                            startActivity(intent);
                        } else if (type.equals("unregister")) {
                            String id = jsonObj.getString("id");
                        } else if (type.equals("members")) {
                            String groupName = jsonObj.getString("group");
                            ArrayList<String> membersArray = new ArrayList<>();
                            int i = 0;
                            while (jsonObj.getJSONArray("groups").getJSONObject(i).getString("group") != null) {
                                membersArray.add(jsonObj.getJSONArray("members").getJSONObject(i).getString("member"));
                                i++;
                            }
                        } else if (type.equals("locations")) {
                            //Check if this is correct
                            String groupName = jsonObj.getString("group");
                            ArrayList<User> usersLocationsArray = new ArrayList<>();
                            int i = 0;
                            while (jsonObj.getJSONArray("location").getJSONObject(i).getString("member") != null) {
                                String memberName = jsonObj.getJSONArray("location").getJSONObject(i).getString("member");
                                String memberLatitude = jsonObj.getJSONArray("location").getJSONObject(i).getString("latitude");
                                String memberLongtitude = jsonObj.getJSONArray("location").getJSONObject(i).getString("longtitude");
                                User user = new User(memberName, memberLongtitude, memberLatitude);
                                usersLocationsArray.add(user);
                                i++;
                            }
                        } else if (type.equals("exception")) {
                            String message = jsonObj.getString("message");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }



}
