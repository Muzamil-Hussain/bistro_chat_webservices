package com.muzamilhussain.i170191_i170228;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<message> messages;
    Context c;



//    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    FirebaseUser currentUser;
    SharedPreferences sp;
    int currentUser;


    public chatAdapter(List<message> messages, Context c) {
        this.messages = messages;
        this.c = c;
//        currentUser = mAuth.getCurrentUser();
        sp=c.getSharedPreferences("user", Context.MODE_PRIVATE);
        currentUser = sp.getInt("id",0);
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder{
        TextView msg_text_Chat,recvd_msg_time_chat;
        RelativeLayout singleMsg;
        CircleImageView img_chat;
        LinearLayout fav_msg;
        ImageView recvd_chat_img;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            msg_text_Chat = itemView.findViewById(R.id.msg_text_chat);
            singleMsg = itemView.findViewById(R.id.single_msg_received_rl);
            recvd_msg_time_chat = itemView.findViewById(R.id.recvd_msg_time_chat);
            img_chat = itemView.findViewById(R.id.img_chat);
            fav_msg = itemView.findViewById(R.id.fav_msg);
            recvd_chat_img = itemView.findViewById(R.id.recvd_chat_img);
        }
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{
        TextView msg_sent_text_chat,sent_msg_time_chat;
        ImageView msg_status,sent_chat_img;
        RelativeLayout sent_msg_rl;
        LinearLayout sent_chat_msg_img_ll;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            msg_sent_text_chat = itemView.findViewById(R.id.msg_sent_text_chat);
            sent_msg_time_chat = itemView.findViewById(R.id.sent_msg_time_chat);
            msg_status = itemView.findViewById(R.id.msg_status);
            sent_msg_rl = itemView.findViewById(R.id.sent_msg_rl);
            sent_chat_img = itemView.findViewById(R.id.sent_chat_img);
            sent_chat_msg_img_ll = itemView.findViewById(R.id.sent_chat_msg_img_ll);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(Integer.toString(this.currentUser))) {
            return 1;
        }
        return 2;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 1:
                itemView = LayoutInflater.from(c).inflate(R.layout.single_msg_sent,parent,false);
                return new chatAdapter.SentViewHolder(itemView);
            case 2:
                itemView = LayoutInflater.from(c).inflate(R.layout.single_msg_received,parent,false);
                final RecyclerView.ViewHolder ReceivedViewHolder = new chatAdapter.ReceivedViewHolder(itemView);

//                itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        final int position = ReceivedViewHolder.getAdapterPosition();
//
////                        FirebaseDatabase database = FirebaseDatabase.getInstance();
////                        DatabaseReference myMsgRef = null;
//
//                        String senderId = messages.get(position).getSenderId();
//                        String receiverId = messages.get(position).getReceiverId();
//
//                        String userId="";
//                        if (senderId.compareTo(receiverId) > 0) {
//                            //myMsgRef = database.getReference("user_chats").child(receiverId + "+" + senderId);
//                            userId = senderId + "+" + receiverId;
//                        } else if (senderId.compareTo(receiverId) < 0) {
//                            //myMsgRef = database.getReference("user_chats").child(senderId + "+" + receiverId);
//                            userId = receiverId + "+" + senderId;
//                        }
//
//                        myMsgRef.child(messages.get(position).getId()).child("isFav").setValue("true");
//                        return true;
//                    }
//                });

//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        final int position = ReceivedViewHolder.getAdapterPosition();
//
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference myMsgRef = null;
//
//                        String senderId = messages.get(position).getSenderId();
//                        String receiverId = messages.get(position).getReceiverId();
//
//                        if (senderId.compareTo(receiverId) > 0) {
//                            myMsgRef = database.getReference("user_chats").child(receiverId + "+" + senderId);
//                        } else if (senderId.compareTo(receiverId) < 0) {
//                            myMsgRef = database.getReference("user_chats").child(senderId + "+" + receiverId);
//                        }
//
//                        myMsgRef.child(messages.get(position).getId()).child("isFav").setValue("false");
//                    }
//                });


                return ReceivedViewHolder;

                //return new chatAdapter.ReceivedViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        String messageText;
        String msgTime;
        DateFormat df = new SimpleDateFormat("HH:mm aa");
        String senderId;


//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myDBRef = database.getReference("user_profiles");
        switch (holder.getItemViewType()) {
            case 1:
                final SentViewHolder sentViewHolder = (SentViewHolder) holder;
                messageText= messages.get(position).getMessage();

                if (messageText.contains("--msgimg--")) {
                    String [] fullMessage = messageText.split("--msgimg--");

                    if (!fullMessage[1].equals("nomsg")) {
                        sentViewHolder.sent_chat_img.setVisibility(View.VISIBLE);
                        sentViewHolder.msg_sent_text_chat.setText(fullMessage[1]);
                        sentViewHolder.sent_chat_img.setImageBitmap(StringToImage(fullMessage[0]));
                        //Picasso.get().load(Uri.parse(fullMessage[0])).into(sentViewHolder.sent_chat_img);
                    }
                    else {
                        sentViewHolder.sent_chat_img.setVisibility(View.VISIBLE);
                        sentViewHolder.msg_sent_text_chat.setText("");
                        sentViewHolder.msg_sent_text_chat.setPadding(0,0,0,0);
                        //sentViewHolder.sent_chat_msg_img_ll.setBackground(null);
//                        Picasso.get().load(Uri.parse(fullMessage[0])).into(sentViewHolder.sent_chat_img);
                        sentViewHolder.sent_chat_img.setImageBitmap(StringToImage(fullMessage[0]));
                    }
                }
                else {
                    sentViewHolder.msg_sent_text_chat.setText(messageText);
                    sentViewHolder.sent_chat_img.setVisibility(View.GONE);
                }

                if (messages.get(position).getIsLast().equals("true")) {
                    sentViewHolder.sent_msg_time_chat.setVisibility(View.VISIBLE);
                    msgTime = df.format(messages.get(position).getDate().getTime());
                    sentViewHolder.sent_msg_time_chat.setText(msgTime);
                } else {
                    sentViewHolder.sent_msg_time_chat.setVisibility(View.INVISIBLE);
                }


                String receiverId = messages.get(position).getReceiverId();
                String isMsgSeen = messages.get(position).getIsSeen();

                if (isMsgSeen.equals("true")) {
                    sentViewHolder.msg_status.setBackgroundResource(R.drawable.seen_msg);
                } else {
//                    myDBRef.child(receiverId).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            userProfile userData = dataSnapshot.getValue(userProfile.class);
//                            if (userData.getIsOnline().equals("true")) {
//                                sentViewHolder.msg_status.setBackgroundResource(R.drawable.delivered_msg);
//                            }
//                            else {
//                                sentViewHolder.msg_status.setBackgroundResource(R.drawable.sent_msg);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                    sentViewHolder.msg_status.setBackgroundResource(R.drawable.delivered_msg);

                }

                break;

            case 2:
                final ReceivedViewHolder receivedViewHolder = (ReceivedViewHolder) holder;
                messageText = messages.get(position).getMessage();

                if (messageText.contains("--msgimg--")) {
                    String [] fullMessage = messageText.split("--msgimg--");

                    if (!fullMessage[1].equals("nomsg")) {
                        receivedViewHolder.recvd_chat_img.setVisibility(View.VISIBLE);
                        receivedViewHolder.msg_text_Chat.setText(fullMessage[1]);
                        //Picasso.get().load(Uri.parse(fullMessage[0])).into(receivedViewHolder.recvd_chat_img);
                        receivedViewHolder.recvd_chat_img.setImageBitmap(StringToImage(fullMessage[0]));
                    }
                    else {
                        receivedViewHolder.recvd_chat_img.setVisibility(View.VISIBLE);
                        receivedViewHolder.msg_text_Chat.setText("");
                        receivedViewHolder.msg_text_Chat.setPadding(0,0,0,0);
                        //receivedViewHolder.sent_chat_msg_img_ll.setBackground(null);
                        //Picasso.get().load(Uri.parse(fullMessage[0])).into(receivedViewHolder.recvd_chat_img);
                        receivedViewHolder.recvd_chat_img.setImageBitmap(StringToImage(fullMessage[0]));
                    }
                }
                else {
                    receivedViewHolder.msg_text_Chat.setText(messageText);
                    receivedViewHolder.recvd_chat_img.setVisibility(View.GONE);
                }




//                if (messages.get(position).getIsLast().equals("true")) {
//                    receivedViewHolder.recvd_msg_time_chat.setVisibility(View.VISIBLE);
//                    receivedViewHolder.img_chat.setVisibility(View.VISIBLE);
//                    msgTime = df.format(messages.get(position).getDate().getTime());
//                    receivedViewHolder.recvd_msg_time_chat.setText(msgTime);
//
//                    senderId = messages.get(position).getSenderId();
//                    myDBRef.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            userProfile userData = dataSnapshot.getValue(userProfile.class);
//                            Picasso.get().load(userData.getProfilePicture()).into(receivedViewHolder.img_chat);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//                else {
//                    receivedViewHolder.recvd_msg_time_chat.setVisibility(View.INVISIBLE);
//                    receivedViewHolder.img_chat.setVisibility(View.INVISIBLE);
//                }

//                if (messages.get(position).getIsFav().equals("true")) {
//                    receivedViewHolder.fav_msg.setVisibility(View.VISIBLE);
//                } else {
//                    receivedViewHolder.fav_msg.setVisibility(View.GONE);
//                }

                break;
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    private Bitmap StringToImage (String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
