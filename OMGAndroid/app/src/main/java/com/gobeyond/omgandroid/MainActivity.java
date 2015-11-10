package com.gobeyond.omgandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";

    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences m_sharedPreferences;

    ShareActionProvider m_shareActionProvider;

    TextView    mainTextView;
    Button      mainButton;
    EditText    mainEditText;
    ListView    mainListView;

    JSONAdapter m_jsonAdapter;
    ArrayList   m_nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Add a spinning progress bar on the top
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //setProgressBarIndeterminateVisibility(false);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainTextView = (TextView) findViewById(R.id.main_textview);

        mainButton = (Button)findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        mainEditText = (EditText)findViewById(R.id.main_edittext);

        mainListView = (ListView)findViewById(R.id.main_listview);
        m_nameList = new ArrayList();

        // Create an ArrayAdapter for the ListView
        m_jsonAdapter = new JSONAdapter(this, getLayoutInflater());

        // Set the ListView to use the JSONAdapter
        mainListView.setAdapter(m_jsonAdapter);

        // Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

        // Greet the user or ask for their name
        displayWelcome();
    }

    public void displayWelcome()
    {
        m_sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        String name = m_sharedPreferences.getString(PREF_NAME, "");

        if(name.length() > 0)
        {
            // If the name is valid, display a toast with the welcome message
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        }
        else
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            // Create edit text for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            // Make an "OK" button to save the name
            alert.setPositiveButton("OK", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Grab the EditText's input
                            String inputName = input.getText().toString();

                            SharedPreferences.Editor e = m_sharedPreferences.edit();
                            e.putString(PREF_NAME, inputName);
                            e.commit();

                            Toast.makeText(getApplicationContext(),
                                    "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                        }
                    });

            alert.setNegativeButton("Cancel", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            {}
                        }
                    });

            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        if(shareItem != null)
        {
          // m_shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
            m_shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }
        setShareIntent();
        return true;
    }

    private void setShareIntent()
    {
        if(m_shareActionProvider != null)
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

            // Make sure the provider knows
            // it should work with that Intent
            m_shareActionProvider.setShareIntent(shareIntent);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        // Test the button
//        mainTextView.setText(mainEditText.getText().toString()
//                + " is learning Android development!");
//
//        // Add the value to the array shown in the ListView
//        m_nameList.add(mainEditText.getText().toString());
//
//        m_arrayAdapter.notifyDataSetChanged();
//        autoCompleteAdapter.notifyDataSetChanged();
//
//        mainEditText.setText("");
//
//        // Update the text to be shared
//        setShareIntent();

        //Just get what is typed in the edit text and use it to search
        queryBooks(mainEditText.getText().toString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Grab the cover data for the chosen book
        JSONObject jsonObject = (JSONObject) m_jsonAdapter.getItem(position);
        String coverID = jsonObject.optString("cover_i");

        //Create the intent to open the Detail activity
        Intent detailIntent = new Intent(this, DetailActivity.class);

        //pack away the data about the cover into the newly created Intent
        detailIntent.putExtra("coverID", coverID);

        //Use the newly created Intent to start the Detail Activity
        startActivity(detailIntent);
    }

    private void queryBooks(String searchString)
    {
        //Encode the string using UTF-8 to make it works
        //in case it has some special characters
        String urlEncoded = "";
        try {
            urlEncoded = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            //In case of an exception, show a toast message to the user
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(QUERY_URL + urlEncoded,
                    new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(JSONObject jsonObject){
                            //Display a Toast message and log the result
                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();

                            //TODO: Parse the result
                            Log.d("OMG Android", "Success!\n" + jsonObject.toString());
                            m_jsonAdapter.updateData(jsonObject.optJSONArray("docs"));
                        }

                        @Override
                        public void onFailure(int statusCode, Throwable throwable, JSONObject error){
                            //Display a Toast message and log the result
                            Toast.makeText(getApplicationContext(), "Error: " + statusCode + " - "
                                    + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                            Log.e("OMG Android", "Error: " + statusCode + " - "
                                    + throwable.getMessage());
                        }
                    });
    }
}
