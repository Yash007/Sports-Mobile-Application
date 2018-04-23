package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by yash007 on 2018-03-30.
 */

public class TeamGridViewAdapter extends ArrayAdapter<GridItem> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

    public TeamGridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridItem> mGridData )  {
        super(mContext, layoutResourceId, mGridData);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<GridItem> mGridData)  {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent, false);

            holder = new ViewHolder();

            holder.titleTextView = (TextView) row.findViewById(R.id.teamNameGrid);
            holder.imageView = (ImageView) row.findViewById(R.id.teamPicGrid);
            holder.ageGroupView = (TextView) row.findViewById(R.id.teamGroupGrid);
            holder.teamId = (TextView) row.findViewById(R.id.teamIdGrid);

            row.setTag(holder);
        }
        else    {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getTitle()));

        switch (item.getImage())    {
            case "cricket":
                holder.imageView.setImageResource(R.drawable.cricket);
                break;
            case "Cricket":
                holder.imageView.setImageResource(R.drawable.cricket);
                break;
            case "Badminton":
                holder.imageView.setImageResource(R.drawable.badminton);
                break;
            case "Baseball":
                holder.imageView.setImageResource(R.drawable.baseball);
                break;
            case "Basketball":
                holder.imageView.setImageResource(R.drawable.basketball);
                break;
            case "Chess":
                holder.imageView.setImageResource(R.drawable.chess);
                break;
            case "Football":
                holder.imageView.setImageResource(R.drawable.football);
                break;
            case "Golf":
                holder.imageView.setImageResource(R.drawable.golf);
                break;
            case "Gymnastics":
                holder.imageView.setImageResource(R.drawable.gymnastics);
                break;
            case "Hockey(Ice)":
                holder.imageView.setImageResource(R.drawable.ice_hockey);
                break;
            case "Hockey(Field)":
                holder.imageView.setImageResource(R.drawable.hockey);
                break;
            case "Polo":
                holder.imageView.setImageResource(R.drawable.polo);
                break;
            case "Water Polo":
                holder.imageView.setImageResource(R.drawable.water);
                break;
            case "Soccer":
                holder.imageView.setImageResource(R.drawable.soccer);
                break;
            case "Sailing":
                holder.imageView.setImageResource(R.drawable.sailboat);
                break;
            case "Tennis":
                holder.imageView.setImageResource(R.drawable.tennis);
                break;
            case "Rugby":
                holder.imageView.setImageResource(R.drawable.rugby);
                break;
            case "Volleyball":
                holder.imageView.setImageResource(R.drawable.volleyball);
                break;
            case "Wrestling":
                holder.imageView.setImageResource(R.drawable.wrestling);
                break;

        }
        //holder.imageView.setImageResource(R.drawable.team_grid);
        holder.ageGroupView.setText(Html.fromHtml(item.getAge()));
        holder.teamId.setText(Html.fromHtml(item.getId()));

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
        TextView ageGroupView;
        TextView teamId;
    }
}
