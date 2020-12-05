package com.muzamilhussain.i170191_i170228;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class chatActivity extends AppCompatActivity {

    ImageButton back_btn_ac, select_img_ac, send_msg_ac;
    TextView name_ac, online_status_ac;
    CircleImageView dp_ac;
    LinearLayout is_online_ac;
    RecyclerView messageRecyclerView;
    EditText msg_text_ac;

    ImageView is_online_inner_ac;

    SharedPreferences sp;
    int currentUser = 0;

    String msgText;

    List<message> chatMessages;
    List<message> tempChatMessages;

    message singleMessage;


    Uri imageUri;

    String userId;

    String chatId = "";


    boolean containsMsg = false;

    private WebSocket webSocket;
    chatAdapter adapter;
    RecyclerView.LayoutManager lm;


    final String url = "http://192.168.43.173/bistro_chat/getChatMessages.php";
    final String profileUrl = "http://192.168.43.173/bistro_chat/get_profile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back_btn_ac = findViewById(R.id.back_btn_ac);
        select_img_ac = findViewById(R.id.select_image_ac);
        send_msg_ac = findViewById(R.id.send_msg_ac);
        name_ac = findViewById(R.id.name_ac);
        online_status_ac = findViewById(R.id.online_status_ac);
        dp_ac = findViewById(R.id.dp_ac);
        is_online_ac = findViewById(R.id.is_online_ac);
        messageRecyclerView = findViewById(R.id.msg_rv);
        msg_text_ac = findViewById(R.id.msg_text_ac);
        is_online_inner_ac = findViewById(R.id.is_online_inner_ac);

        chatMessages = new ArrayList<>();


        sp=getSharedPreferences("user", Context.MODE_PRIVATE);
        currentUser = sp.getInt("id",0);

        instantiateWebSocket();

        //getting userId from search contacts or users list activity upon tapping
        userId = getIntent().getStringExtra("USERID");


        //store chat Id by comparing both ids

        if (userId.compareTo(Integer.toString(currentUser)) < 0) {
            chatId = userId + "+" + currentUser;
        } else {
            chatId = currentUser + "+" + userId;
        }

        adapter = new chatAdapter(chatMessages, chatActivity.this);
        lm = new LinearLayoutManager(chatActivity.this);
        ((LinearLayoutManager) lm).setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(lm);
        messageRecyclerView.setAdapter(adapter);

        StringRequest MyStringRequest0 = new StringRequest(com.android.volley.Request.Method.POST, profileUrl,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);

                            if (res.getString("response").equals("200")) {


                                String nameText = res.getString("firstName") + " " + res.getString("lastName");
                                String isOnline = res.getString("isOnline");

                                String onlineText = "is " + isOnline;


                                Bitmap bitmap = StringToImage(res.getString("profilePicture"));
                                dp_ac.setImageBitmap(bitmap);

                                online_status_ac.setText("is online");
                                is_online_ac.setBackgroundResource(R.drawable.circle_white);
                                is_online_inner_ac.setBackgroundResource(R.drawable.circle_green);

                                name_ac.setText(nameText);


                            }

                        } catch (JSONException e) {
                            Toast.makeText(chatActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        Log.d("MyStringRequest",response);
                    }
                },
                new com.android.volley.Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Toast.makeText(chatActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("userId", userId);
                return data;
            }
        };
        Volley.newRequestQueue(chatActivity.this).add(MyStringRequest0);






        StringRequest MyStringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);

                            if (res.getString("response").equals("200")) {
                                JSONArray userMessages = res.getJSONArray("chatMessages");
                                for (int i=0;i<userMessages.length();i++) {
                                    JSONObject singleMessageJsonObject = userMessages.getJSONObject(i);
                                    final message singleMessage = new message(singleMessageJsonObject.getString("senderId"),
                                            singleMessageJsonObject.getString("receiverId"),
                                            singleMessageJsonObject.getString("message"));
                                    singleMessage.setId(singleMessageJsonObject.getString("id"));
                                    singleMessage.setIsFav(singleMessageJsonObject.getString("isFav"));
                                    singleMessage.setIsLast(singleMessageJsonObject.getString("isLast"));
                                    singleMessage.setIsSeen(singleMessageJsonObject.getString("isSeen"));

                                    chatMessages.add(singleMessage);
                                    adapter.notifyDataSetChanged();
                                }
//                                adapter = new chatAdapter(chatMessages, chatActivity.this);
//                                lm = new LinearLayoutManager(chatActivity.this);
//                                ((LinearLayoutManager) lm).setStackFromEnd(true);
//                                messageRecyclerView.setLayoutManager(lm);
//                                messageRecyclerView.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(chatActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        Log.d("MyStringRequest",response);
                    }
                },
                new com.android.volley.Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Toast.makeText(chatActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("chatId", chatId);
                return data;
            }
        };
        Volley.newRequestQueue(chatActivity.this).add(MyStringRequest);



        back_btn_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        send_msg_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgText = msg_text_ac.getText().toString();

                if (!msgText.isEmpty()) {
                    msg_text_ac.getText().clear();
                    singleMessage = new message(Integer.toString(currentUser),
                            userId,
                            msgText);

                    JSONObject singleMessageJSONObject = new JSONObject();

                    try {
                        singleMessageJSONObject.put("senderId", singleMessage.getSenderId());
                        singleMessageJSONObject.put("receiverId", singleMessage.getReceiverId());
                        singleMessageJSONObject.put("message", singleMessage.getMessage());
                        singleMessageJSONObject.put("date",singleMessage.getDate());
                        singleMessageJSONObject.put("isLast",singleMessage.getIsLast());
                        singleMessageJSONObject.put("isSeen",singleMessage.getIsSeen());
                        singleMessageJSONObject.put("isFav",singleMessage.getIsFav());
                        singleMessageJSONObject.put("chatId",chatId);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    webSocket.send(singleMessageJSONObject.toString());

                }
            }
        });


        select_img_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1);
            }
        });
    }

    private void instantiateWebSocket() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("ws://192.168.43.173:8080").build();


        SocketListener socketListener = new SocketListener(chatActivity.this);


        webSocket = client.newWebSocket(request, socketListener);
    }



    public class SocketListener extends WebSocketListener {


        public chatActivity activity;


        public SocketListener(chatActivity activity) {
            this.activity = activity;
        }



        @Override
        public void onOpen(WebSocket webSocket, Response response) {


            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Toast.makeText(activity, "Connection Established!", Toast.LENGTH_LONG).show();

                }

            });

        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject res = null;
                    try {
                        res = new JSONObject(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        singleMessage.setSenderId(res.getString("senderId"));
                        singleMessage.setReceiverId(res.getString("receiverId"));
                        singleMessage.setId(res.getString("id"));
                        singleMessage.setMessage(res.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    chatMessages.add(singleMessage);
                    adapter.notifyDataSetChanged();

                    //Toast.makeText(chatActivity.this,res.toString(),Toast.LENGTH_LONG).show();


                }
            });
        }



        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
        }



        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }



        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, @Nullable final Response response) {
            super.onFailure(webSocket, t, response);
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {
                Intent sendImageMessageIntent = new Intent(chatActivity.this, chat_msg_with_img.class);
                sendImageMessageIntent.putExtra("IMAGEURI", imageUri.toString());
                startActivityForResult(sendImageMessageIntent, 200);
            }
        }

        else {

            if (data!=null) {
                Toast.makeText(chatActivity.this, "Helloooo", Toast.LENGTH_LONG).show();
                final String imgMsg = data.getStringExtra("RESULTANTMSG");


                String uniqueID = UUID.randomUUID().toString();

                final String[] msgContent = imgMsg.split("--msgimg--");

                if (!msgContent[1].equals("nomsg")) {
                    containsMsg = true;
                }

                Bitmap bitmap;
                String encodedImage = "";
                String finalMsg = "";
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    encodedImage = imageToString(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (containsMsg) {
                    finalMsg = encodedImage + "--msgimg--" + msgContent[1];
                } else {
                    finalMsg = encodedImage + "--msgimg--" + "nomsg";
                }

                singleMessage = new message(Integer.toString(currentUser),
                        userId,
                        finalMsg);

                JSONObject singleMessageJSONObject = new JSONObject();

                try {
                    singleMessageJSONObject.put("senderId", singleMessage.getSenderId());
                    singleMessageJSONObject.put("receiverId", singleMessage.getReceiverId());
                    singleMessageJSONObject.put("message", singleMessage.getMessage());
                    singleMessageJSONObject.put("date", singleMessage.getDate());
                    singleMessageJSONObject.put("isLast", singleMessage.getIsLast());
                    singleMessageJSONObject.put("isSeen", singleMessage.getIsSeen());
                    singleMessageJSONObject.put("isFav", singleMessage.getIsFav());
                    singleMessageJSONObject.put("chatId", chatId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                webSocket.send(singleMessageJSONObject.toString());

            }
        }
    }
    private Bitmap StringToImage (String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    private String imageToString (Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage  = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }
}
