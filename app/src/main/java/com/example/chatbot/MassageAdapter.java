package com.example.chatbot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MassageAdapter extends RecyclerView.Adapter<MassageAdapter.ItemViewHolder> {

    private Context context;
    private List<ChatModel> chatModelList;

    public MassageAdapter(Context context, List<ChatModel> postlist) {
        this.context = context;
        this.chatModelList = postlist;
    }

    @NonNull
    @Override
    public MassageAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MassageAdapter.ItemViewHolder holder, int position) {
        ChatModel chatModel = chatModelList.get(position);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.Icon.getLayoutParams();
        LinearLayout.LayoutParams paramsCard = (LinearLayout.LayoutParams) holder.card.getLayoutParams();
        LinearLayout.LayoutParams paramsName = (LinearLayout.LayoutParams) holder.name.getLayoutParams();


        if (chatModel.getId() == 0) {
            Glide.with(context)
                    .load(R.drawable.chatbotn)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)) // Cache the GIF resource
                    .into(holder.Icon);
            params.gravity = Gravity.START; // Align to the end (right in LTR layouts)
            paramsName.gravity = Gravity.START;
            paramsCard.gravity = Gravity.START;
        } else if (chatModel.getId() == 1) {
            Glide.with(context)
                    .load(R.drawable.socialmedia)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)) // Cache the GIF resource
                    .into(holder.Icon);
            params.gravity = Gravity.END; // Align to the start (left in LTR layouts)
            paramsCard.gravity = Gravity.END;
            paramsName.gravity = Gravity.END;
        }
        holder.name.setText(chatModel.getUser());
        holder.message.setText(chatModel.getMassage());

    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView message,name;
        private ImageView Icon;
        private CardView card;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            name = itemView.findViewById(R.id.user2);
            Icon = itemView.findViewById(R.id.icon);
            card = itemView.findViewById(R.id.card);
        }
    }
}
