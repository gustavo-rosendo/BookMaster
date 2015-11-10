package com.gobeyond.omgandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.support.v7.widget.ShareActionProvider;

import com.squareup.picasso.Picasso;

/**
 * Created by gusta_000 on 10/11/2015.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    String mImageUrl;

    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tell the activity to use the detail layout
        setContentView(R.layout.activity_detail);

        //Enable the "Up" button for more navigation options
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        //Access the ImageView from XML
        ImageView imageView = (ImageView) findViewById(R.id.img_cover);

        //Unpack the cover ID from the extras of the Intent sent by the MainActivity
        String coverID = this.getIntent().getExtras().getString("coverID");

        if(coverID.length() > 0) {
            //construct the full url to search for the large cover
            mImageUrl = IMAGE_URL_BASE + coverID + "-L.jpg";

            //Use Picasso to download the image in the background
            Picasso.with(this).load(mImageUrl).placeholder(R.drawable.img_books_loading).into(imageView);
        }
    }

    private void setShareIntent() {

        if(mShareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book recommendation");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mImageUrl);

            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        if(shareItem != null)
        {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }
        setShareIntent();
        return true;
    }
}
