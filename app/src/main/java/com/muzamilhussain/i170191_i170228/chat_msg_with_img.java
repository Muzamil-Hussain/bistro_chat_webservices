package com.muzamilhussain.i170191_i170228;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class chat_msg_with_img extends AppCompatActivity {

    ImageView chat_img;
    ImageButton chat_img_btn;
    EditText chat_img_msg;

    String msgWithImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_msg_with_img);

        chat_img = findViewById(R.id.chat_img);
        chat_img_btn = findViewById(R.id.chat_img_send);
        chat_img_msg = findViewById(R.id.chat_img_msg);


        final String imageUri = getIntent().getStringExtra("IMAGEURI");


        chat_img.setImageURI(Uri.parse(imageUri));


        chat_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chat_img_msg.getText().toString().isEmpty()) {
                    msgWithImg = imageUri +"--msgimg--"+ chat_img_msg.getText().toString();
                } else {
                    msgWithImg = imageUri + "--msgimg--"+"nomsg";
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULTANTMSG",msgWithImg);
                setResult(200,resultIntent);
                finish();
            }
        });
    }
}
