package com.iam725.kunal.gogonew.AdminUtil;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iam725.kunal.gogonew.R;

import java.util.ArrayList;

public class AdminRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AdminItemData> emailList;
    private AdminClickListener adminClickListener;

    public AdminRecyclerAdapter(ArrayList<AdminItemData> emailList,
                                AdminClickListener adminClickListener) {
        this.emailList = emailList;
        this.adminClickListener = adminClickListener;
    }

    public void setEmailList(ArrayList<AdminItemData> emailList) {
        this.emailList = emailList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView checkAction;
        private ImageView cancelAction;
        private TextView emailText;

        public MyViewHolder(View view){
            super(view);
            emailText = (TextView) view.findViewById(R.id.email_text);
            cancelAction = (ImageView)view.findViewById(R.id.cancel_action);
            checkAction = (ImageView) view.findViewById(R.id.check_action);

            cancelAction.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.v("RecyclerView", "cancel Clicked");
                    adminClickListener.onClickAction(getAdapterPosition(), AdminActivity.CANCEL_ACTION);
                }
            });

            checkAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("RecyclerView", "Check Clicked");
                    adminClickListener.onClickAction(getAdapterPosition(), AdminActivity.CHECK_ACTION);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_page_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if(emailList.get(position).getEmail() == "") return;
        Log.v("RecyclerView", "onBindView");
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        ((MyViewHolder) holder).emailText.setText(
                emailList.get(position).getKey()+"\n"+emailList.get(position).getRequest());
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public interface AdminClickListener{
        void onClickAction(int pos, String action);
    }
}
