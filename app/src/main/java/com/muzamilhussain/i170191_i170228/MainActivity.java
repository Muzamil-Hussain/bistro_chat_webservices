package com.muzamilhussain.i170191_i170228;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    Button signIn, signUp;

    SharedPreferences sp;
    final String url = "http://192.168.43.173/bistro_chat/check_profile_exists.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sp=getSharedPreferences("user", Context.MODE_PRIVATE);

        final int currentUser = sp.getInt("id",0);

        signIn= findViewById(R.id.sign_in_button);
        signUp= findViewById(R.id.sign_up_button);



        if (currentUser!=0) {

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res=new JSONObject(response);

                                if (res.getString("response").equals("200")) {
                                    Intent userLoggedInIntent = new Intent(MainActivity.this,users_chat_list.class);
                                    startActivity(userLoggedInIntent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            Log.d("MyStringRequest",response);
                        }
                    },
                    new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //This code is executed if there is an error.
                            Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("userId", Integer.toString(currentUser));
                    return data;
                }
            };
            Volley.newRequestQueue(MainActivity.this).add(MyStringRequest);


        }

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(MainActivity.this, signin.class);
                startActivity(signInIntent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(MainActivity.this, signup.class);
                startActivity(signUpIntent);
            }
        });
    }

}
