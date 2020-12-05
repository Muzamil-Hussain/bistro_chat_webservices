package com.muzamilhussain.i170191_i170228;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class create_profile extends AppCompatActivity {

    EditText firstName, lastName, phone, bio, dob;
    Button male, female, none, save;
    CircleImageView profileImage;
    RelativeLayout selectImage;
    Uri imageUri, checkUri;
    private String gender;

    private boolean profileExists = false;
    private boolean isPictureUploaded = false;
    final String url = "http://192.168.43.173/bistro_chat/get_profile.php";
    final String profileUrl = "http://192.168.43.173/bistro_chat/profile.php";

    SharedPreferences sp;

    Bitmap bitmap;

    String profileId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


        firstName = findViewById(R.id.first_name_et);
        lastName = findViewById(R.id.last_name_et);
        phone = findViewById(R.id.phone_no_et);
        bio = findViewById(R.id.bio_et);
        dob = findViewById(R.id.dob_et);
        selectImage = findViewById(R.id.select_img_acp);
        profileImage = findViewById(R.id.profile_img_acp);

        male = findViewById(R.id.male_button);
        female = findViewById(R.id.female_button);
        none = findViewById(R.id.none_button);
        save = findViewById(R.id.save_create_profile);


        sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        final int currentUser = sp.getInt("id",0);

        checkUri = Uri.parse("dummy uri");


        if (currentUser !=0) {

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res=new JSONObject(response);

                                if (res.getString("response").equals("200")) {
                                    profileExists = true;

                                    firstName.setText(res.getString("firstName"));
                                    lastName.setText(res.getString("lastName"));
                                    phone.setText(res.getString("phoneNumber"));
                                    dob.setText(res.getString("dob"));
                                    bio.setText(res.getString("bio"));

                                    bitmap = StringToImage(res.getString("profilePicture"));
                                    profileImage.setImageBitmap(bitmap);


                                    profileId = res.getString("id");

                                    if (res.getString("gender").equals("male")) {
                                        gender = "male";
                                        male.setBackgroundResource(R.drawable.gender_rectangle_button_selected);
                                        male.setTextColor(Color.parseColor("#ffffff"));
                                    }
                                    else if (res.getString("gender").equals("female")) {
                                        gender= "female";
                                        female.setBackgroundResource(R.drawable.gender_rectangle_button_selected);
                                        female.setTextColor(Color.parseColor("#ffffff"));
                                    }
                                    else {
                                        gender = "none";
                                        none.setBackgroundResource(R.drawable.gender_rectangle_button_selected);
                                        none.setTextColor(Color.parseColor("#ffffff"));
                                    }

                                }

                            } catch (JSONException e) {
                                Toast.makeText(create_profile.this,e.toString(),Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            Log.d("MyStringRequest",response);
                        }
                    },
                    new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //This code is executed if there is an error.
                            Toast.makeText(create_profile.this,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("userId", Integer.toString(currentUser));
                    return data;
                }
            };
            Volley.newRequestQueue(create_profile.this).add(MyStringRequest);



        }


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1);
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "male";
                male.setBackgroundResource(R.drawable.gender_rectangle_button_selected);
                male.setTextColor(Color.parseColor("#ffffff"));

                female.setBackgroundResource(R.drawable.gender_rectangle_button);
                female.setTextColor(Color.parseColor("#00d664"));
                none.setBackgroundResource(R.drawable.gender_rectangle_button);
                none.setTextColor(Color.parseColor("#00d664"));
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "female";
                female.setBackgroundResource(R.drawable.gender_rectangle_button_selected);
                female.setTextColor(Color.parseColor("#ffffff"));

                male.setBackgroundResource(R.drawable.gender_rectangle_button);
                male.setTextColor(Color.parseColor("#00d664"));
                none.setBackgroundResource(R.drawable.gender_rectangle_button);
                none.setTextColor(Color.parseColor("#00d664"));
            }
        });
        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "none";
                none.setBackgroundResource(R.drawable.gender_rectangle_button_selected);
                none.setTextColor(Color.parseColor("#ffffff"));

                female.setBackgroundResource(R.drawable.gender_rectangle_button);
                female.setTextColor(Color.parseColor("#00d664"));

                male.setBackgroundResource(R.drawable.gender_rectangle_button);
                male.setTextColor(Color.parseColor("#00d664"));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, profileUrl,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res=new JSONObject(response);

                                    if (res.getString("response").equals("201")) {
                                        Intent ProfileSuccessfulIntent = new Intent(create_profile.this,users_chat_list.class);
                                        startActivity(ProfileSuccessfulIntent);
                                        Toast.makeText(create_profile.this,res.getString("msg"),Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(create_profile.this,e.toString(),Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                Log.d("MyStringRequest",response);
                            }
                        },
                        new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //This code is executed if there is an error.
                                Toast.makeText(create_profile.this,error.toString(),Toast.LENGTH_LONG).show();
                            }
                        }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("firstName", firstName.getText().toString().trim());
                        data.put("lastName", lastName.getText().toString().trim());
                        data.put("dob", dob.getText().toString().trim());
                        data.put("gender", gender);
                        data.put("bio", bio.getText().toString().trim());
                        data.put("userId", Integer.toString(currentUser));
                        data.put("phoneNumber", phone.getText().toString());
                        data.put("profilePicture",imageToString(bitmap));
                        if (profileExists) {
                            data.put("id",profileId);
                        }
                        return data;
                    }
                };
                Volley.newRequestQueue(create_profile.this).add(MyStringRequest);

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {

                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    bitmap  = BitmapFactory.decodeStream(inputStream);
                    profileImage.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            else {
                Toast.makeText(create_profile.this,
                        "Please Select Profile Image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String imageToString (Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage  = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

    private Bitmap StringToImage (String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
