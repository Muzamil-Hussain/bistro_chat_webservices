package com.muzamilhussain.i170191_i170228;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class search_contacts extends AppCompatActivity {

    RecyclerView rv;
    List<userProfile> contacts, filteredContacts;
    List <String> contactNumbersList;
    ImageButton backButton;
    EditText search_ac_asc;

    SharedPreferences sp;
    int currentUser = 0;

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    searchContactAdapter adapter;
    RecyclerView.LayoutManager lm;

    final String url = "http://192.168.43.173/bistro_chat/get_all_profiles.php";


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
                                            }
                                            //contacts.add(userProfileData);
                                        }
                                    }
                                }
                                adapter = new searchContactAdapter(contacts, search_contacts.this);
                                lm = new LinearLayoutManager(search_contacts.this);
                                rv.setLayoutManager(lm);
                                rv.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(search_contacts.this,e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        Log.d("MyStringRequest",response);
                    }
                },
                new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Toast.makeText(search_contacts.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(search_contacts.this).add(MyStringRequest);

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


    public void filterContacts (String currentText) {

        filteredContacts.clear();
        for (userProfile singleContact: contacts) {
            String fullName = singleContact.getFirstName() + " " + singleContact.getLastName();
            if (currentText.length()>0 && fullName.contains(currentText)) {
                filteredContacts.add(singleContact);
            }
        }

        if (currentText.length()>0) {
            adapter = new searchContactAdapter(filteredContacts, search_contacts.this);
            lm = new LinearLayoutManager(search_contacts.this);
            rv.setLayoutManager(lm);
            rv.setAdapter(adapter);
        }
        else {
            adapter = new searchContactAdapter(contacts, search_contacts.this);
            lm = new LinearLayoutManager(search_contacts.this);
            rv.setLayoutManager(lm);
            rv.setAdapter(adapter);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);

        search_ac_asc = findViewById(R.id.search_ac_asc);


        sp=getSharedPreferences("user", Context.MODE_PRIVATE);
        currentUser = sp.getInt("id",0);

        if (currentUser==0) {
            Intent notLoggedInIntent = new Intent(search_contacts.this,
                    MainActivity.class);
            startActivity(notLoggedInIntent);
            finish();
        }


        backButton = findViewById(R.id.back_button_asc);
        rv = findViewById(R.id.rv_asc);
        contacts = new ArrayList<>();
        filteredContacts = new ArrayList<>();
        contactNumbersList = new ArrayList<>();



        final Cursor contactNumbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (contactNumbers.moveToNext())
        {
            String phoneNumber = contactNumbers.getString(contactNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace("+92", "0");
            phoneNumber = phoneNumber.replaceAll(" ", "");
            contactNumbersList.add(phoneNumber);
        }
        contactNumbers.close();


        requestContactPermission();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userChatListIntent = new Intent(search_contacts.this, users_chat_list.class);
                startActivity(userChatListIntent);
                finish();
            }
        });

    }
}
