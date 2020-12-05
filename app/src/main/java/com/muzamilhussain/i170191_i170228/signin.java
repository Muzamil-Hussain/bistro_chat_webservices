package com.muzamilhussain.i170191_i170228;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class signin extends AppCompatActivity {

    EditText emailAddress, password;
    Button signIn, register;

    final String url = "http://192.168.43.173/bistro_chat/login.php";
    final String profileUrl = "http://192.168.43.173/bistro_chat/check_profile_exists.php";

    private  boolean profileExists = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        emailAddress = findViewById(R.id.email_et_sic);
        password = findViewById(R.id.password_et_sic);
        signIn = findViewById(R.id.sign_in_button_sic);
        register = findViewById(R.id.sign_up_button_sic);



        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailText = emailAddress.getText().toString();
                final String passwordText = password.getText().toString();



                if (emailText.length()>0 && passwordText.length()>0) {
                     StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,

                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        final JSONObject res=new JSONObject(response);

                                        if (res.getString("response").equals("200")) {
                                            SharedPreferences sp=getSharedPreferences("user", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor ed=sp.edit();
                                            ed.putString("email", res.getString("email"));
                                            ed.putInt("id", Integer.parseInt(res.getString("id")));
                                            ed.apply();
                                            Toast.makeText(signin.this, "Login Successful-> email: " + res.getString("email"),Toast.LENGTH_LONG).show();
//                                            Intent loginSuccessfulIntent = new Intent (signin.this, users_chat_list.class);
                                            Intent loginSuccessfulIntent = new Intent (signin.this, users_chat_list.class);
                                            startActivity(loginSuccessfulIntent);
                                            finish();

                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(signin.this,e.toString(),Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                    Log.d("MyStringRequest",response);
                                }
                            },
                            new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //This code is executed if there is an error.
                                    Toast.makeText(signin.this,error.toString(),Toast.LENGTH_LONG).show();
                                }
                            }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> data = new HashMap<String, String>();
                            data.put("email", emailText);
                            data.put("password", passwordText);
                            return data;
                        }
                    };
                    Volley.newRequestQueue(signin.this).add(MyStringRequest);

                }
                else {
                    if (emailText.length()<1) {
                        Toast.makeText(signin.this, "Please Enter Email Address",Toast.LENGTH_SHORT).show();
                    }

                    if (passwordText.length()<1) {
                        Toast.makeText(signin.this, "Please Enter Password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent (signin.this, signup.class);
                startActivity(registerIntent);
                finish();
            }
        });

    }
}
