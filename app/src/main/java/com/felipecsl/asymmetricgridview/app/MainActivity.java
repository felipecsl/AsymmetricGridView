package com.felipecsl.asymmetricgridview.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.felipecsl.asymmetricgridview.app.model.Item;
import com.felipecsl.asymmetricgridview.app.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.app.widget.AsymmetricGridViewAdapter;
import com.felipecsl.asymmetricgridview.app.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private AsymmetricGridView listView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (AsymmetricGridView) findViewById(R.id.listView);
        listView.setRequestedColumnWidth(Utils.dpToPx(this, 120));

        adapter = new ListAdapter(this, listView);
        final List<Item> items = new ArrayList<>();

        for (int i = 0; i < 100; i++)
            items.add(new Item());

        adapter.appendObjects(items);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.one_column) {
            listView.setRequestedColumnWidth(Utils.dpToPx(this, 240));
            listView.determineColumns();
            listView.setAdapter(adapter);
        } else if (id == R.id.two_columnns) {
            listView.setRequestedColumnWidth(Utils.dpToPx(this, 120));
            listView.determineColumns();
            listView.setAdapter(adapter);
        } else if (id == R.id.three_columns) {
            listView.setRequestedColumnWidth(Utils.dpToPx(this, 90));
            listView.determineColumns();
            listView.setAdapter(adapter);
        }
        return super.onOptionsItemSelected(item);
    }
}
