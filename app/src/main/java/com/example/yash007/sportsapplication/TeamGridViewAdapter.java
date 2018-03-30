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

import com.squareup.picasso.Picasso;

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

        holder.imageView.setImageResource(R.drawable.team_grid);
        holder.ageGroupView.setText(Html.fromHtml(item.getAge()));

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
        TextView ageGroupView;
        TextView teamId;
    }
}
