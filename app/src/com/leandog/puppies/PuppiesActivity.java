package com.leandog.puppies;

import static com.leandog.puppies.view.ViewHelper.findFor;
import static com.leandog.puppies.view.ViewHelper.hide;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.leandog.puppies.R.id;
import com.leandog.puppies.R.layout;
import com.leandog.puppies.data.PuppiesLoader;
import com.leandog.puppies.data.Puppy;
import com.leandog.puppies.view.PuppyImageLoader;

public class PuppiesActivity extends Activity {

    private final PuppiesLoader puppiesLoader;
    final PuppyImageLoader puppyImageLoader;

    public PuppiesActivity() {
        this(new PuppiesLoader(), new PuppyImageLoader());
    }

    public PuppiesActivity(final PuppiesLoader puppiesLoader, final PuppyImageLoader puppyImageLoader) {
        this.puppiesLoader = puppiesLoader;
        this.puppyImageLoader = puppyImageLoader;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_puppies);

        initializeActionBar();

        final ListView thePuppies = findFor(this, id.the_puppies_list);
        thePuppies.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent theDetails = new Intent(PuppiesActivity.this, PuppyTaleActivity.class);
                theDetails.putExtra("thePuppy", new Gson().toJson(thePuppies.getItemAtPosition(position)));
                startActivity(theDetails);
            }
        });
        new AsyncPuppiesLoader(thePuppies).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_puppies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeActionBar() {
        final ActionBar theActionBar = getActionBar();
        theActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        theActionBar.setCustomView(layout.happy_banner);
    }

    private final class AsyncPuppiesLoader extends AsyncTask<Void, Void, List<Puppy>> {
        private final ListView thePuppies;

        private AsyncPuppiesLoader(ListView thePuppies) {
            this.thePuppies = thePuppies;
        }

        @Override
        protected List<Puppy> doInBackground(Void... params) {
            return puppiesLoader.load();
        }

        protected void onPostExecute(List<Puppy> puppies) {
            thePuppies.setAdapter(new PuppyAdapter(PuppiesActivity.this, PuppiesActivity.this, puppies));
            hide(PuppiesActivity.this, id.loading);
        }
    }

}
