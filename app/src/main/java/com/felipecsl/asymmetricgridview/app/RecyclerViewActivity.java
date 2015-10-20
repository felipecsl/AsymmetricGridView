package com.felipecsl.asymmetricgridview.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.AGVRecyclerViewAdapter;
import com.felipecsl.asymmetricgridview.AsymmetricItem;
import com.felipecsl.asymmetricgridview.AsymmetricRecyclerView;
import com.felipecsl.asymmetricgridview.AsymmetricRecyclerViewAdapter;
import com.felipecsl.asymmetricgridview.Utils;
import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {
  private DrawerLayout drawerLayout;
  private final DemoUtils demoUtils = new DemoUtils();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recyclerview);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    AsymmetricRecyclerView recyclerView = (AsymmetricRecyclerView) findViewById(R.id.recyclerView);

    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    actionBar.setDisplayHomeAsUpEnabled(true);

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    if (navigationView != null) {
      setupDrawerContent(navigationView);
    }

    RecyclerViewAdapter adapter = new RecyclerViewAdapter(demoUtils.moarItems(50));
    recyclerView.setRequestedColumnCount(3);
    recyclerView.setDebugging(true);
    recyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(this, 3));
    recyclerView.addItemDecoration(
        new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_padding)));
    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
              case R.id.nav_gridview:
                startActivity(new Intent(RecyclerViewActivity.this, MainActivity.class));
                finish();
                break;
              case R.id.nav_recyclerview:
                startActivity(new Intent(RecyclerViewActivity.this, RecyclerViewActivity.class));
                finish();
                break;
            }
            return true;
          }
        });
  }

  class RecyclerViewAdapter extends AGVRecyclerViewAdapter<ViewHolder> {
    private final List<DemoItem> items;

    public RecyclerViewAdapter(List<DemoItem> items) {
      this.items = items;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      Log.d("RecyclerViewActivity", "onCreateView");
      return new ViewHolder(parent, viewType);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      Log.d("RecyclerViewActivity", "onBindView position=" + position);
      holder.bind(items.get(position));
    }

    @Override public int getItemCount() {
      return items.size();
    }

    @Override public AsymmetricItem getItem(int position) {
      return items.get(position);
    }

    @Override public int getItemViewType(int position) {
      return position % 2 == 0 ? 1 : 0;
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      drawerLayout.openDrawer(GravityCompat.START);
    }
    return super.onOptionsItemSelected(item);
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;
    public ViewHolder(ViewGroup parent, int viewType) {
      super(LayoutInflater.from(parent.getContext()).inflate(
          viewType == 0 ? R.layout.adapter_item : R.layout.adapter_item_odd, parent, false));
      if (viewType == 0) {
        textView = (TextView) itemView.findViewById(R.id.textview);
      } else {
        textView = (TextView) itemView.findViewById(R.id.textview_odd);
      }
    }

    public void bind(DemoItem item) {
      textView.setText(String.valueOf(item.getPosition()));
    }
  }
}
