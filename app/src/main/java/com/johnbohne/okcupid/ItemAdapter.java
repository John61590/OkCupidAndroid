package com.johnbohne.okcupid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by john on 1/15/16.
 *
 * This adapter is used to process the views for the grid view items.
 */
public class ItemAdapter extends ArrayAdapter<Person> {
    private Context mContext;
    private ViewHolder mViewHolder;
    public ItemAdapter(Context context, int resource, List<Person> people) {
        super(context, resource, people);
        this.mContext = context;
    }
    static class ViewHolder {
        ImageView image;
        TextView userName;
        TextView matchPercentage;
        TextView match;
        TextView combinedLocation;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        * convertView will have a non-null value when ListView is asking you recycle the row layout.
        * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
        */
        if (convertView == null)
        {
            // inflate the layout
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.card_view, parent, false);

            // we'll set up the ViewHolder
            mViewHolder = new ViewHolder();
            mViewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
            mViewHolder.image = (ImageView) convertView.findViewById(R.id.picture);
            mViewHolder.combinedLocation = (TextView) convertView.findViewById(R.id.age_location);
            mViewHolder.match = (TextView) convertView.findViewById(R.id.match);
            mViewHolder.matchPercentage = (TextView) convertView.findViewById(R.id.percentage);
            // store the holder with the view.
            convertView.setTag(mViewHolder);
            convertView.setFocusable(false);
            convertView.setEnabled(false);
        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        Person person = getItem(position);

        String url = person.getImageURL();
        if (url != null) {
            ImageView imageView = mViewHolder.image;
            Picasso.with(mContext).load(url).into(imageView);
        }
        TextView userName = mViewHolder.userName;
        userName.setText(person.getUserName());
        userName.setTextColor(Color.BLACK);

        TextView ageLocation = mViewHolder.combinedLocation;
        ageLocation.setText(mContext.getResources().getString(R.string.combined_age_location, person.getAge(), person.getCity(), person.getState()));

        TextView matchPercentage = mViewHolder.matchPercentage;
        double value = person.getPercentage() / 100.0;
        long percent = Math.round(value);
        matchPercentage.setText(String.format("%d%%", percent));
        return convertView;
    }
}
