package com.felipecsl.asymmetricgridview.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.felipecsl.asymmetricgridview.app.model.AsymmetricItem;
import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.app.widget.AsymmetricGridView;
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

        final List<AsymmetricItem> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            int span = i % 10 == 0 ? 2 : 1;
            items.add(new DemoItem(span, span));
        }

        adapter = new ListAdapter(this, listView, items);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        } else if (id == R.id.four_columns) {
            listView.setRequestedColumnWidth(Utils.dpToPx(this, 70));
            listView.determineColumns();
            listView.setAdapter(adapter);
        } else if (id == R.id.five_columns) {
            listView.setRequestedColumnWidth(Utils.dpToPx(this, 60));
            listView.determineColumns();
            listView.setAdapter(adapter);
        }
        return super.onOptionsItemSelected(item);
    }
}
