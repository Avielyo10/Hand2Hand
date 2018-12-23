package com.avielyosef.hand2hand.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avielyosef.hand2hand.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Ad> {
    private Context mContext;
    private int mResource;

    public CustomAdapter(Context context, int resource, ArrayList<Ad> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String title = getItem(position).getTitle();
        String description = getItem(position).getDescription();
        int price = getItem(position).getPrice();
        String sPrice = String.valueOf(price)+" NIS";
        Boolean isPaid = getItem(position).isNotPaid();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvTitle =  (TextView)convertView.findViewById(R.id.customTitle);
        TextView tvDescription =  (TextView)convertView.findViewById(R.id.customDescription);
        TextView tvPrice =  (TextView)convertView.findViewById(R.id.customPrice);
        ImageView customStar = (ImageView)convertView.findViewById(R.id.customStar);
//        Button clickAdBtn = (Button)convertView.findViewById(R.id.clickAdBtn);

        tvTitle.setText(title);
        tvDescription.setText(description);
        tvPrice.setText(sPrice);

        if(isPaid){
            try{
                customStar.setVisibility(View.VISIBLE);
            }catch (Exception e){}
        }else{
            try{
                customStar.setVisibility(View.GONE);
            }catch (Exception e){}
        }
//        clickAdBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO show the Ad in a bigger screen
//            }
//        });
        return convertView;
    }
}
