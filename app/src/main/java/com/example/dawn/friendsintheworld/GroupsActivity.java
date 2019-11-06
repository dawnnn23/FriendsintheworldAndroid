package com.example.dawn.friendsintheworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dawn.friendsintheworld.Helper.LocaleHelper;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import io.paperdb.Paper;

public class GroupsActivity extends AppCompatActivity {

    private Button buttonGroup;
    private Button buttonUnregister;
    private Controller controller;
    private ListView listView;
    private Activity activity;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        //INIT
        activity=this;
        listView = (ListView) findViewById(R.id.displayListView);
        buttonGroup = (Button)findViewById(R.id.button_newGroup);
        buttonUnregister = (Button) findViewById(R.id.button_unregister);
        tv = (TextView)findViewById(R.id.textView3);


        //Language stuff
        Paper.init(activity);
        String language = Paper.book().read("language");
        if(language==null) {
            Paper.book().write("language", "en");
        }
        updateView((String) Paper.book().read("language"));


        buttonGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFormActivity();
                //controller.connection.send(Expression.currentGroups());
            }
        });
        buttonUnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Button unabled
                SharedPreferences sharedPreferences=activity.getSharedPreferences("groupId", Context.MODE_PRIVATE);
                String groupId=sharedPreferences.getString("groupId","");
                try {
                    controller.connection.send(Expression.unregister(groupId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        controller = Controller.getInstance(new GroupsActivityListener());
        controller.connectClicked();

        new PeriodicSending(controller,activity).start();

        try {
            controller.connection.send(Expression.currentGroups());

        } catch (Exception e) {
            e.printStackTrace();
        }


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

        buttonGroup.setText(resources.getString(R.string.createGroup));
        buttonUnregister.setText(resources.getString(R.string.unregister));
        tv.setText(resources.getString(R.string.instructions));

    }


    public void openFormActivity(){
        Intent intent = new Intent(this, NewGroupForm.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        controller.disconnectClicked();
        super.onDestroy();
    }

    public class GroupsActivityListener implements ReceiveListener {
        @Override
        public void newMessage(final String answer) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONObject jsonObj = new JSONObject(answer);
                        String type = jsonObj.getString("type");
                        if (type.equals("groups")) {
                            //Receiving the groups
                            final ArrayList<String> groupsArray = new ArrayList<>();

                            int arrayLength = jsonObj.getJSONArray("groups").length();
                            for (int i = 0; i < arrayLength; i++) {
                                String name = jsonObj.getJSONArray("groups").getJSONObject(i).getString("group");
                                groupsArray.add(jsonObj.getJSONArray("groups").getJSONObject(i).getString("group"));
                            }

                            SharedPreferences sharedPreferences=activity.getSharedPreferences("memberName", Context.MODE_PRIVATE);
                            final String memberName=sharedPreferences.getString("memberName","");

                            //Showing the groups
                            CustomAdapter ca = new CustomAdapter(activity, groupsArray);
                            listView.setAdapter(ca);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        controller.connection.send(Expression.register(groupsArray.get(position),memberName));
                                        Toast.makeText(activity,"You are now registered to group: "+groupsArray.get(position),Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        } else if (type.equals("register")) {
                            String groupName = jsonObj.getString("group");
                            String id = jsonObj.getString("id");
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
