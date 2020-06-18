package com.android.myapplication;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {
    private ArrayList<Memo> memos = new ArrayList<>();

    private OnItemClickListener listener;

    MemoAdapter(ArrayList<Memo> memos) {
        this.memos = memos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_memo, parent, false);
        MemoAdapter.ViewHolder vh = new MemoAdapter.ViewHolder(view);

        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull MemoAdapter.ViewHolder holder, int position) {
        Memo item = memos.get(position);

        holder.title_tv.setText(item.getTitle());
        holder.content_tv.setText(item.getContent());
        holder.date_tv.setText(item.getDate());

        if (item.isChecked())
            holder.imgbtn_check.setImageResource(R.drawable.checked_star);
        else
            holder.imgbtn_check.setImageResource(R.drawable.unchecked_star);

        // Picasso 라이브러리
        if (TextUtils.isEmpty(item.getImage())) {
            Picasso.get().load(R.mipmap.ic_launcher).into(holder.image_iv);
        } else {
            Picasso.get().load(item.getImage())
                    .centerCrop()
                    .fit()
                    .error(R.drawable.noavailable)
                    .into(holder.image_iv);
        }

        holder.itemView.setOnClickListener(view -> listener.onClick(memos.get(position)));
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public void setClickListener(OnItemClickListener listener) {this.listener = listener;}

    public interface OnItemClickListener{
        void onClick(Memo memo);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imgbtn_check;
        TextView title_tv;
        TextView content_tv;
        TextView date_tv;
        ImageView image_iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgbtn_check = (ImageButton) itemView.findViewById(R.id.imgbtn_check);
            title_tv = (TextView) itemView.findViewById(R.id.title_tv);
            content_tv = (TextView) itemView.findViewById(R.id.content_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            image_iv = (ImageView) itemView.findViewById(R.id.image_iv);

        }

    }
}
