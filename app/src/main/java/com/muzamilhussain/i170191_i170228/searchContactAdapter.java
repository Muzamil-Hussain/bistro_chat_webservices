package com.muzamilhussain.i170191_i170228;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchContactAdapter extends RecyclerView.Adapter<searchContactAdapter.MyViewHolder> {
    List<userProfile> contacts;
    Context c;

    public searchContactAdapter(List<userProfile> contacts, Context c) {
        this.contacts = contacts;
        this.c = c;
    }

    @NonNull
    @Override
    public searchContactAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(c).inflate(R.layout.chat_row_received,parent,false);
//        View itemView = LayoutInflater.from(c).inflate(R.layout.contactrow,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String nameText = contacts.get(position).getFirstName()
                + " " +
                contacts.get(position).getLastName();

        String [] splittedDate = contacts.get(position).getDob().split("/");
        int age = 2020-Integer.parseInt(splittedDate[2]);
        String infoText = contacts.get(position).getGender().toUpperCase()
                + " " +
                age;

        final String userId = contacts.get(position).getId();

        holder.name.setText(nameText);
        holder.info.setText(infoText);
//        holder.photo.setImageURI(Uri.parse(contacts.get(position).ge));
        byte[] decodedString = Base64.decode(contacts.get(position).getProfilePicture(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.photo.setImageBitmap(decodedByte);

        holder.singleChatRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),"Contact Clicked",Toast.LENGTH_SHORT).show();
                Intent chatIntent = new Intent(v.getContext(),chatActivity.class);
                chatIntent.putExtra("USERID", userId);
                c.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, info;
        CircleImageView photo;
        LinearLayout singleChatRow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_contact_name);
            info = itemView.findViewById(R.id.chat_info);
            photo = itemView.findViewById(R.id.chat_photo);
            singleChatRow = itemView.findViewById(R.id.chat_row);
        }
    }
}
