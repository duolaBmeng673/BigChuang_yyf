package com.example.sign_in_test.UI.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

import com.example.sign_in_test.Data.model.Msg;
import com.example.sign_in_test.Data.model.MsgContent;
import com.example.sign_in_test.Data.model.MsgImage;
import com.example.sign_in_test.Data.network.ImageService;
import com.example.sign_in_test.R;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Msg> list;
    public MsgAdapter(List<Msg> list){
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        TextView left_msg;
        ImageView left_image;

        LinearLayout rightLayout;
        TextView right_msg;
        ImageView right_image;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            left_msg = view.findViewById(R.id.left_msg);
            left_image = view.findViewById(R.id.left_image);

            rightLayout = view.findViewById(R.id.right_layout);
            right_msg = view.findViewById(R.id.right_msg);
            right_image = view.findViewById(R.id.right_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = list.get(position);

        if (msg instanceof MsgContent) { // 文本消息
            MsgContent textMsg = (MsgContent) msg;

            if (textMsg.getType() == Msg.TYPE_RECEIVED) {
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);

                holder.left_msg.setVisibility(View.VISIBLE);
                holder.left_msg.setText(textMsg.getContent());
                holder.left_image.setVisibility(View.GONE);
            } else {
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.GONE);

                holder.right_msg.setVisibility(View.VISIBLE);
                holder.right_msg.setText(textMsg.getContent());
                holder.right_image.setVisibility(View.GONE);
            }

        } else if (msg instanceof MsgImage) { // 图片消息
            MsgImage imageMsg = (MsgImage) msg;

            if (imageMsg.getType() == Msg.TYPE_RECEIVED) {
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);

                holder.left_msg.setVisibility(View.GONE);
                holder.left_image.setVisibility(View.VISIBLE);

                if (imageMsg.getImageUrl() != null) {
                    Glide.with(holder.left_image.getContext())
                            .load(imageMsg.getImageUrl())
                            .into(holder.left_image);
                } else if (imageMsg.getImageBase64() != null) {
                    Glide.with(holder.left_image.getContext())
                            .load(ImageService.base64ToBitmap(imageMsg.getImageBase64()))
                            .into(holder.left_image);
                }

            } else {
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.GONE);

                holder.right_msg.setVisibility(View.GONE);
                holder.right_image.setVisibility(View.VISIBLE);

                if (imageMsg.getImageUrl() != null) {
                    Glide.with(holder.right_image.getContext())
                            .load(imageMsg.getImageUrl())
                            .into(holder.right_image);
                } else if (imageMsg.getImageBase64() != null) {
                    Glide.with(holder.right_image.getContext())
                            .load(ImageService.base64ToBitmap(imageMsg.getImageBase64()))
                            .into(holder.right_image);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
