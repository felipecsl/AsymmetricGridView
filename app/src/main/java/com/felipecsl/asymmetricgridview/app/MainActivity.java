package com.felipecsl.asymmetricgridview.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.app.widget.DefaultCursorAdapter;
import com.felipecsl.asymmetricgridview.app.widget.DefaultListAdapter;
import com.felipecsl.asymmetricgridview.app.widget.DemoAdapter;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private static final String TAG = "MainActivity";
  private AsymmetricGridView listView;
  private DemoAdapter adapter;
  private int currentOffset;
  private static final boolean USE_CURSOR_ADAPTER = false;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = (AsymmetricGridView) findViewById(R.id.listView);

    if (USE_CURSOR_ADAPTER) {
      if (savedInstanceState == null) {
        adapter = new DefaultCursorAdapter(this, getMoreItems(50));
      } else {
        adapter = new DefaultCursorAdapter(this);
      }
    } else {
      if (savedInstanceState == null) {
        adapter = new DefaultListAdapter(this, getMoreItems(50));
      } else {
        adapter = new DefaultListAdapter(this);
      }
    }

    listView.setRequestedColumnCount(3);
    listView.setRequestedHorizontalSpacing(Utils.dpToPx(this, 3));
    listView.setAdapter( adapter );
    listView.setDebugging(true);
    listView.setOnItemClickListener(this);
  }


  private List<DemoItem> getMoreItems(int qty) {
    List<DemoItem> items = new ArrayList<>();

    for (int i = 0; i < qty; i++) {
      int colSpan = Math.random() < 0.2f ? 2 : 1;
      // Swap the next 2 lines to have items with variable
      // column/row span.
      // int rowSpan = Math.random() < 0.2f ? 2 : 1;
      int rowSpan = colSpan;
      DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
      items.add(item);
    }

    currentOffset += qty;

    return items;
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("currentOffset", currentOffset);
    outState.putInt("itemCount", adapter.getCount());
    for (int i = 0; i < adapter.getCount(); i++) {
      outState.putParcelable("item_" + i, (Parcelable) adapter.getItem(i));
    }
  }

  @Override protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    currentOffset = savedInstanceState.getInt("currentOffset");
    int count = savedInstanceState.getInt("itemCount");
    List<DemoItem> items = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      items.add((DemoItem) savedInstanceState.getParcelable("item_" + i));
    }
    adapter.setItems(items);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.one_column) {
      setNumColumns(1);
    } else if (id == R.id.two_columnns) {
      setNumColumns(2);
    } else if (id == R.id.three_columns) {
      setNumColumns(3);
    } else if (id == R.id.four_columns) {
      setNumColumns(4);
    } else if (id == R.id.five_columns) {
      setNumColumns(5);
    } else if (id == R.id.onetwenty_dp_columns) {
      setColumnWidth(120);
    } else if (id == R.id.twoforty_dp_columns) {
      setColumnWidth(240);
    } else if (id == R.id.append_items) {
      adapter.appendItems(getMoreItems(50));
    } else if (id == R.id.reset_items) {
      currentOffset = 0;
      adapter.setItems(getMoreItems(50));
    } else if (id == R.id.reordering) {
      listView.setAllowReordering(!listView.isAllowReordering());
      item.setTitle(listView.isAllowReordering() ? "Prevent reordering" : "Allow reordering");
    } else if (id == R.id.debugging) {
      int index = listView.getFirstVisiblePosition();
      View v = listView.getChildAt(0);
      int top = (v == null) ? 0 : v.getTop();

      listView.setDebugging(!listView.isDebugging());
      item.setTitle(listView.isDebugging() ? "Disable debugging" : "Enable debugging");
      listView.setAdapter(adapter);

      listView.setSelectionFromTop(index, top);
    }
    return super.onOptionsItemSelected(item);
  }

  private void setNumColumns(int numColumns) {
    listView.setRequestedColumnCount(numColumns);
    listView.determineColumns();
    listView.setAdapter(adapter);
  }

  private void setColumnWidth(int columnWidth) {
    listView.setRequestedColumnWidth(Utils.dpToPx(this, columnWidth));
    listView.determineColumns();
    listView.setAdapter(adapter);
  }

  @Override public void onItemClick(@NotNull AdapterView<?> parent, @NotNull View view,
      int position, long id) {
    Toast.makeText(this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();

      adapter.remove( position );
  }
}
