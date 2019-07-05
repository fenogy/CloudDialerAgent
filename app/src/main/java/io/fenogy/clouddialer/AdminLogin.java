package io.fenogy.clouddialer;

import android.app.ProgressDialog;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
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

//public class AdminLogin extends AppCompatActivity
public class AdminLogin extends AppCompatActivity {
    ImageButton registerUser;
    EditText username, password;
    ImageButton loginButton;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        registerUser = (ImageButton)findViewById(R.id.admin_register);
        username = (EditText)findViewById(R.id.admin_username);
        password = (EditText)findViewById(R.id.admin_password);
        loginButton = (ImageButton)findViewById(R.id.admin_login_button);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLogin.this, AdminRegister.class));
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
                else if(!user.contains("admin") && !user.contains("Admin")){
                    username.setError("should start with admin");
                }
                else{
                    String url = "https://cloudcall-0000.firebaseio.com/admins.json";
                    final ProgressDialog pd = new ProgressDialog(AdminLogin.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(AdminLogin.this, "admin user not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(AdminLogin.this, "admin user not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        //startActivity(new Intent(AdminLogin.this, Users.class));
                                        startActivity(new Intent(AdminLogin.this, AdminDialer.class));
                                    }
                                    else {
                                        Toast.makeText(AdminLogin.this, "incorrect password", Toast.LENGTH_LONG).show();
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
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(AdminLogin.this);
                    rQueue.add(request);
                }

            }
        });
    }
}

