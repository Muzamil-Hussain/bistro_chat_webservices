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

public class signup extends AppCompatActivity {

    EditText email, password, confirmPassword;
    Button register, signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email= findViewById(R.id.email_et_sup);
        password= findViewById(R.id.password_et_sup);
        confirmPassword = findViewById(R.id.confirm_password_et_sup);

        register = findViewById(R.id.sign_up_button_sup);
        signIn = findViewById(R.id.sign_in_button_sign_up);

        final String url = "http://192.168.43.173/bistro_chat/register.php";


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(signup.this,signin.class);
                startActivity(loginActivityIntent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allCorrect=false;

                final String emailText = email.getText().toString();
                final String passwordText = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();

                if (emailText.length()>0 &&(passwordText.length()>0)
                        && (passwordText.equals(confirmPasswordText))) {
                    allCorrect = true;
                }

                if (allCorrect) {

                    StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,

                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject res=new JSONObject(response);

                                        if (res.getString("response").equals("201")) {
                                            SharedPreferences sp=getSharedPreferences("user", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor ed=sp.edit();
                                            ed.putString("email", res.getString("email"));
                                            ed.putInt("id", Integer.parseInt(res.getString("id")));
                                            ed.apply();
                                            Intent signUpSuccessfulIntent = new Intent(signup.this,create_profile.class);
                                            startActivity(signUpSuccessfulIntent);
                                            Toast.makeText(signup.this,res.getString("msg"),Toast.LENGTH_LONG).show();
                                            finish();
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(signup.this,e.toString(),Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                    Log.d("MyStringRequest",response);
                                }
                            },
                            new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                                @Override
                                public void onErrorResponse(VolleyError error) {
                            //This code is executed if there is an error.
                                    Toast.makeText(signup.this,error.toString(),Toast.LENGTH_LONG).show();
                                }
                            }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> data = new HashMap<String, String>();
                            data.put("email", emailText);
                            data.put("password", passwordText);
                            return data;
                        }
                    };
                    Volley.newRequestQueue(signup.this).add(MyStringRequest);
                }
                else {
                    if (emailText.length()<1) {
                        Toast.makeText(signup.this, "Please Enter Email Address",Toast.LENGTH_SHORT).show();
                    }

                    if (passwordText.length()<1) {
                        Toast.makeText(signup.this, "Please Enter Password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
