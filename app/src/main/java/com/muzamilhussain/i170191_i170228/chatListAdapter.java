package com.muzamilhussain.i170191_i170228;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter .MyViewHolder> {
    List<userProfile> contacts;
    Context c;
    SharedPreferences sp;
    int currentUser;

    public chatListAdapter (List<userProfile> contacts, Context c) {
        this.contacts = contacts;
        this.c = c;
        sp=c.getSharedPreferences("user", Context.MODE_PRIVATE);
        currentUser = sp.getInt("id",0);
    }

    @NonNull
    @Override
    public chatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(c).inflate(R.layout.chat_row_received,parent,false);
//        View itemView = LayoutInflater.from(c).inflate(R.layout.contactrow,parent,false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String nameText = contacts.get(position).getFirstName()
                + " " +
                contacts.get(position).getLastName();

        final String[] infoText = {""};


        final String userId = contacts.get(position).getId();


        String chatId ="";

        if (Integer.toString(currentUser).compareTo(contacts.get(position).getId()) > 0) {
            chatId= contacts.get(position).getId() + "+" + currentUser;
        } else {
            chatId= currentUser + "+" + contacts.get(position).getId();
        }


        final String finalChatId = chatId;

        String getLastMsgurl = "http://192.168.43.173/bistro_chat/get_last_msg.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, getLastMsgurl,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);

                            if (res.getString("response").equals("200")) {

                                if (res.getString("last_msg").contains("--msgimg--")) {
                                    infoText[0] = "Image, Open to see.";
                                } else {
                                    if (infoText[0].length() > 30) {
                                        infoText[0] =  res.getString("last_msg").substring(0,30);
                                    } else {
                                        infoText[0] =  res.getString("last_msg");
                                    }
                                }
                                holder.info.setText(infoText[0]);

                            }

                        } catch (JSONException e) {
                            Toast.makeText(c,e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        Log.d("MyStringRequest",response);
                    }
                },
                new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Toast.makeText(c,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("chatId", finalChatId);
                return data;
            }
        };
        Volley.newRequestQueue(c).add(MyStringRequest);


        holder.name.setText(nameText);

//        holder.photo.setImageURI(Uri.parse(contacts.get(position).ge));
        byte[] decodedString = Base64.decode(contacts.get(position).getProfilePicture(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.photo.setImageBitmap(decodedByte);

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        v.getContext(),
                        R.style.BottomSheetDialogTheme
                );

                final View bottomSheetView = LayoutInflater.from(c.getApplicationContext())
                        .inflate(
                                R.layout.show_profile,
                                null
                        );

                ImageButton hide_sp;
                final TextView title_sp, name_sp,contact_no_sp,info_sp,bio_sp;
                CircleImageView dp_sp;
                LinearLayout edit_sp;

                hide_sp = bottomSheetView.findViewById(R.id.hide_sp);
                title_sp = bottomSheetView.findViewById(R.id.title_sp);
                name_sp = bottomSheetView.findViewById(R.id.name_sp);
                contact_no_sp = bottomSheetView.findViewById(R.id.contact_no_sp);
                info_sp = bottomSheetView.findViewById(R.id.info_sp);
                bio_sp = bottomSheetView.findViewById(R.id.bio_sp);
                dp_sp = bottomSheetView.findViewById(R.id.dp_sp);
                edit_sp = bottomSheetView.findViewById(R.id.edit_sp);

                title_sp.setText("USER PROFILE");

                name_sp.setText(contacts.get(position).getFirstName() + " " + contacts.get(position).getLastName());
                contact_no_sp.setText(contacts.get(position).getPhoneNumber());

                info_sp.setText("Male 21");

                bio_sp.setText(contacts.get(position).getBio());

                //Picasso.get().load(Uri.parse(contacts.get(position).getProfilePicture())).into(dp_sp);
                Bitmap bitmap = StringToImage(contacts.get(position).getProfilePicture());

//                                Picasso.get().load(bitmap).into(dp);
//                                Picasso.get().load(fetchedUserProfile.getProfilePicture()).into(dpSearchBar);
                dp_sp.setImageBitmap(bitmap);

                hide_sp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                edit_sp.setVisibility(View.GONE);


                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });


        holder.singleChatRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),"Contact Clicked",Toast.LENGTH_SHORT).show();
                Intent chatIntent = new Intent(v.getContext(),chatActivity.class);
                chatIntent.putExtra("USERID", userId);
                c.startActivity(chatIntent);
            }
        });

//        holder.info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(v.getContext(),"Contact Clicked",Toast.LENGTH_SHORT).show();
//                Intent chatIntent = new Intent(v.getContext(),chatActivity.class);
//                chatIntent.putExtra("USERID", userId);
//                c.startActivity(chatIntent);
//            }
//        });

        holder.singleChatRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        v.getContext(),
                        R.style.BottomSheetDialogTheme
                );

                final View bottomSheetView = LayoutInflater.from(c.getApplicationContext())
                        .inflate(
                                R.layout.delete_conv_view,
                                null
                        );

                ImageButton hide_dcv;
                Button delete_btn_dcv;


                hide_dcv = bottomSheetView.findViewById(R.id.hide_dcv);
                delete_btn_dcv = bottomSheetView.findViewById(R.id.delete_btn_dcv);

                hide_dcv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                delete_btn_dcv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String url = "http://192.168.43.173/bistro_chat/delete_chat.php";

                        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,

                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject res=new JSONObject(response);

                                            if (res.getString("response").equals("200")) {
                                                bottomSheetDialog.dismiss();
                                                contacts.remove(position);
                                                notifyItemRemoved(position);
                                                Toast.makeText(v.getContext(),"Chat Deleted",Toast.LENGTH_LONG).show();
                                            }

                                        } catch (JSONException e) {
                                            Toast.makeText(v.getContext(),e.toString(),Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                        Log.d("MyStringRequest",response);
                                    }
                                },
                                new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //This code is executed if there is an error.
                                        Toast.makeText(v.getContext(),error.toString(),Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            protected Map<String, String> getParams() {
                                Map<String, String> data = new HashMap<String, String>();
                                data.put("chatId", finalChatId);
                                return data;
                            }
                        };
                        Volley.newRequestQueue(v.getContext()).add(MyStringRequest);
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                return false;
            }
        });

    }
    private Bitmap StringToImage (String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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
