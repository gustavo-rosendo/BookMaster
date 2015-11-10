package com.gobeyond.omgandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gusta_000 on 7/11/2015.
 */
public class JSONAdapter extends BaseAdapter {

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONAdapter(Context context, LayoutInflater inflater) {
        this.mContext = context;
        this.mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    public void updateData(JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mJsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        //check if the View already exists,
        //if so, no need to inflate it and load all the subviews again
        if(convertView == null) {
            //inflate the custom row layout from the XML
            convertView = mInflater.inflate(R.layout.row_book, null);

            //create a new "ViewHolder" with subviews
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.authorTextView = (TextView) convertView.findViewById(R.id.text_author);

            //hang onto this holder for future use
            convertView.setTag(holder);
        }
        else {
            //skip the inflate/findViewById expensive calls
            //and just use the holder that has already been made
            //Reduce, Reuse, Recycle! :D
            holder = (ViewHolder) convertView.getTag();
        }

        //get the current book's data in JSON form
        JSONObject jsonObject = (JSONObject) getItem(position);

        //check if the object has a cover id
        if(jsonObject.has("cover_i")) {
            //if yes, grab it from the object
            String imageID = jsonObject.optString("cover_i");

            //build the image URL to download it
            String imageURL = IMAGE_URL_BASE + imageID + "-S.jpg";

            //use Picasso lib to download the image
            //temporarily have a placeholder in case the image takes a long time to load
            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.ic_books).into(holder.thumbnailImageView);
        }
        else {
            //if there is no cover available, just use the default placeholder
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books);
        }

        String bookTitle = "";
        String authorName = "";

        if(jsonObject.has("title")) {
            bookTitle = jsonObject.optString("title");
        }
        if(jsonObject.has("author_name")) {
            authorName = jsonObject.optString("author_name");
        }

        holder.titleTextView.setText(bookTitle);
        holder.authorTextView.setText(authorName);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView authorTextView;
    }
}
