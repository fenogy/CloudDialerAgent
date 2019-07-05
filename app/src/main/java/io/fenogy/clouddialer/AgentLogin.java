package io.fenogy.clouddialer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//public class AgentLogin extends AppCompatActivity
public class AgentLogin extends AppCompatActivity {
    ImageButton registerUser;
    EditText username, password;
    ImageButton loginButton;
    String user, pass;
    boolean isRegistered = false;
    SharedPreferences.Editor mSharedPrefsEditor;
    SharedPreferences mSharedPrefs;
    public static final String SAVED_PASSWORD = "SavedPassowrdFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_login);

        registerUser = (ImageButton)findViewById(R.id.agent_register);
        username = (EditText)findViewById(R.id.agent_username);
        password = (EditText)findViewById(R.id.agent_password);
        loginButton = (ImageButton)findViewById(R.id.agent_login_button);

        mSharedPrefs = getSharedPreferences(SAVED_PASSWORD, MODE_PRIVATE);
        mSharedPrefsEditor = getSharedPreferences(SAVED_PASSWORD, MODE_PRIVATE).edit();

        username.setText(getSavedUserName());
        password.setText(getSavedPassword());



        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgentLogin.this, AgentRegister.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();


                if(user.equals("")){
                    username.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else{
                    String url = "https://cloudcall-0000.firebaseio.com/agents.json";
                    final ProgressDialog pd = new ProgressDialog(AgentLogin.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(AgentLogin.this, "user not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(AgentLogin.this, "user not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        setUsername(user);
                                        setPassword(pass);
                                        //updateStateToFirebase(AgentLogin.this);
                                        startActivity(new Intent(AgentLogin.this, AgentHome.class));
                                    }
                                    else {
                                        Toast.makeText(AgentLogin.this, "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            Toast.makeText(AgentLogin.this, "Check Internet", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(AgentLogin.this);
                    rQueue.add(request);
                }



            }
        });
    }

    public String getSavedUserName(){

        if(mSharedPrefsEditor!= null && mSharedPrefs!= null){

            return mSharedPrefs.getString("username","");
        }
        return "";

    }
    public String getSavedPassword(){

        if(mSharedPrefsEditor!= null && mSharedPrefs!= null){

            return mSharedPrefs.getString("password","");
        }
        return "";


    }

    public void setUsername(String s){

        if(mSharedPrefsEditor!= null && mSharedPrefs!= null){

            mSharedPrefsEditor.putString("username",s);
            mSharedPrefsEditor.apply();
            mSharedPrefsEditor.commit();
        }

    }

    public void setPassword(String s){

        if(mSharedPrefsEditor!= null && mSharedPrefs!= null){

            mSharedPrefsEditor.putString("password",s);
            mSharedPrefsEditor.apply();
            mSharedPrefsEditor.commit();
        }

    }


}

