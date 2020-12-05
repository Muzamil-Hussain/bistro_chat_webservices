package com.muzamilhussain.i170191_i170228;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class users_chat_list extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private TextView name, email;
    CircleImageView dp,dpSearchBar;
    ImageButton search;
    TextView searchBar;

    private FirebaseAuth mAuth;
    SharedPreferences sp;
    int currentUser = 0;

    private FirebaseDatabase database;
    private DatabaseReference myRef,myChatsRef,isChatRef;

    userProfile fetchedUserProfile;

    private TextView name_sp,phone_sp,info_sp,bio_sp,title_sp;
    CircleImageView bottomSheetDp;


    final String fetch_profile_url = "http://192.168.43.173/bistro_chat/get_profile.php";


    RecyclerView rv;
    List<userProfile> contacts;
    List<String> contactNumbersList;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    final String url = "http://192.168.43.173/bistro_chat/get_all_profiles.php";

    chatListAdapter adapter;
    RecyclerView.LayoutManager lm;


    public void getContactsFromContactsDirectory() {

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);

                            if (res.getString("response").equals("200")) {
                                JSONArray userProfiles = res.getJSONArray("userProfiles");
                                for (int i=0;i<userProfiles.length();i++) {
                                    JSONObject userProfileJsonObject = userProfiles.getJSONObject(i);
                                    final userProfile userProfileData = new userProfile();
                                    userProfileData.setFirstName(userProfileJsonObject.getString("firstName"));
                                    userProfileData.setLastName(userProfileJsonObject.getString("lastName"));
                                    userProfileData.setDob(userProfileJsonObject.getString("dob"));
                                    userProfileData.setIsOnline(userProfileJsonObject.getString("isOnline"));
                                    userProfileData.setGender(userProfileJsonObject.getString("gender"));
                                    userProfileData.setBio(userProfileJsonObject.getString("bio"));
                                    userProfileData.setId(userProfileJsonObject.getString("user"));
                                    userProfileData.setPhoneNumber(userProfileJsonObject.getString("phoneNumber"));
                                    userProfileData.setProfilePicture(userProfileJsonObject.getString("profilePicture"));

                                    for (int j=0 ;j<contactNumbersList.size();j++) {
                                        if (userProfileData.getPhoneNumber().equals(contactNumbersList.get(j))
                                                && (!userProfileData.getId().equals(Integer.toString(currentUser)))) {

                                            boolean isAlreadyAdded = false;
                                            for (userProfile tempProfile: contacts) {
                                                if (tempProfile.getId().equals(userProfileData.getId())) {
                                                    isAlreadyAdded = true;
                                                }
                                            }
                                            if (!isAlreadyAdded) {
                                                contacts.add(userProfileData);
                                                //adapter.notifyDataSetChanged();
                                            }
                                            //contacts.add(userProfileData);
                                        }
                                    }
                                    adapter = new chatListAdapter(contacts, users_chat_list.this);
                                    lm = new LinearLayoutManager(users_chat_list.this);
                                    rv.setLayoutManager(lm);
                                    rv.setAdapter(adapter);
                                }
                            }

                        } catch (JSONException e) {
                            Toast.makeText(users_chat_list.this,e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        Log.d("MyStringRequest",response);
                    }
                },
                new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Toast.makeText(users_chat_list.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(users_chat_list.this).add(MyStringRequest);

    }


    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContactsFromContactsDirectory();
            }
        } else {
            getContactsFromContactsDirectory();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_chat_list);

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer);
        search = findViewById(R.id.search_button_ucl);
        dpSearchBar = findViewById(R.id.img_ucl);
        searchBar = findViewById(R.id.search_ac_ucl);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //currentUser = mAuth.getCurrentUser();
        sp=getSharedPreferences("user", Context.MODE_PRIVATE);
        currentUser = sp.getInt("id",0);
        final String userEmail = sp.getString("email","");

        rv = findViewById(R.id.rv_ucl);
        contacts = new ArrayList<>();
        contactNumbersList = new ArrayList<>();


        if (currentUser==0) {
            Intent notLoggedInIntent = new Intent(users_chat_list.this,
                    MainActivity.class);
            startActivity(notLoggedInIntent);
            finish();
        }

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, fetch_profile_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);

                            if (res.getString("response").equals("200")) {
                                View hView =  navigationView.getHeaderView(0);
                                name = hView.findViewById(R.id.username_ui);
                                email = hView.findViewById(R.id.email_ui);
                                dp = hView.findViewById(R.id.dp_ui);
                                String nameText = res.getString("firstName")
                                        + " " +
                                        res.getString("lastName");
                                name.setText(nameText);
                                email.setText(userEmail);

                                Bitmap bitmap = StringToImage(res.getString("profilePicture"));

//                                Picasso.get().load(bitmap).into(dp);
//                                Picasso.get().load(fetchedUserProfile.getProfilePicture()).into(dpSearchBar);
                                dp.setImageBitmap(bitmap);
                                dpSearchBar.setImageBitmap(bitmap);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(users_chat_list.this,e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        Log.d("MyStringRequest",response);
                    }
                },
                new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Toast.makeText(users_chat_list.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("userId", Integer.toString(currentUser));
                return data;
            }
        };
        Volley.newRequestQueue(users_chat_list.this).add(MyStringRequest);


        toolbar.setTitle("BistroChat");
        toolbar.setTitleTextColor(Color.parseColor("#00d664"));
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.logout_btn_drawer:
                        //FirebaseAuth.getInstance().signOut();
                        sp.edit().clear().apply();

                        Toast.makeText(users_chat_list.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                        //myRef.child("isOnline").setValue("false");
                        Intent logoutSuccessIntent = new Intent (users_chat_list.this,MainActivity.class);
                        startActivity(logoutSuccessIntent);
                        finish();
                        break;
                }
                return true;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchContactsIntent = new Intent (users_chat_list.this,search_contacts.class);
                startActivity(searchContactsIntent);
            }
        });

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchContactsIntent = new Intent (users_chat_list.this,search_contacts.class);
                startActivity(searchContactsIntent);
            }
        });

        dpSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        users_chat_list.this,
                        R.style.BottomSheetDialogTheme
                );
                final View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.show_profile,
                                (RelativeLayout)findViewById(R.id.bottom_sheet_profile_sp)
                        );

                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, fetch_profile_url,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res=new JSONObject(response);

                                    if (res.getString("response").equals("200")) {


                                        fetchedUserProfile = new userProfile();
                                        fetchedUserProfile.setFirstName(res.getString("firstName"));
                                        fetchedUserProfile.setLastName(res.getString("lastName"));
                                        fetchedUserProfile.setDob(res.getString("dob"));
                                        fetchedUserProfile.setPhoneNumber(res.getString("phoneNumber"));
                                        fetchedUserProfile.setBio(res.getString("bio"));
                                        fetchedUserProfile.setGender(res.getString("gender"));


                                        String nameText = fetchedUserProfile.getFirstName() + " " + fetchedUserProfile.getLastName();

                                        String [] splittedDate = fetchedUserProfile.getDob().split("/");
                                        int age = 2020-Integer.parseInt(splittedDate[2]);
                                        String infoText = fetchedUserProfile.getGender().toUpperCase()
                                                + " " +
                                                age;

                                        String titleText = "MY PROFILE";

                                        Bitmap bitmap = StringToImage(res.getString("profilePicture"));

                                        title_sp = bottomSheetView.findViewById(R.id.title_sp);
                                        title_sp.setText(titleText);

                                        name_sp = bottomSheetView.findViewById(R.id.name_sp);
                                        name_sp.setText(nameText);

                                        phone_sp = bottomSheetView.findViewById(R.id.contact_no_sp);
                                        phone_sp.setText(fetchedUserProfile.getPhoneNumber());

                                        info_sp = bottomSheetView.findViewById(R.id.info_sp);
                                        info_sp.setText(infoText);

                                        bio_sp = bottomSheetView.findViewById(R.id.bio_sp);
                                        bio_sp.setText(fetchedUserProfile.getBio());


                                        bottomSheetDp = bottomSheetView.findViewById(R.id.dp_sp);
                                        bottomSheetDp.setImageBitmap(bitmap);
                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(users_chat_list.this,e.toString(),Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                Log.d("MyStringRequest",response);
                            }
                        },
                        new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //This code is executed if there is an error.
                                Toast.makeText(users_chat_list.this,error.toString(),Toast.LENGTH_LONG).show();
                            }
                        }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("userId", Integer.toString(currentUser));
                        return data;
                    }
                };
                Volley.newRequestQueue(users_chat_list.this).add(MyStringRequest);


                bottomSheetView.findViewById(R.id.hide_sp)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialog.dismiss();
                            }
                        });

                bottomSheetView.findViewById(R.id.edit_sp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editProfileIntent = new Intent(v.getContext(),create_profile.class);
                        startActivity(editProfileIntent);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });



        final Cursor contactNumbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (contactNumbers.moveToNext())
        {
            //String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = contactNumbers.getString(contactNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace("+92", "0");
            phoneNumber = phoneNumber.replaceAll(" ", "");
            contactNumbersList.add(phoneNumber);
        }
        contactNumbers.close();


        requestContactPermission();
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private Bitmap StringToImage (String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
