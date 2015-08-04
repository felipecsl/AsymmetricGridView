package com.felipecsl.asymmetricgridview.app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricRecyclerView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricRecyclerViewAdapter;

import java.util.Collections;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {
  private DrawerLayout drawerLayout;
  private AsymmetricRecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recyclerview);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    recyclerView = (AsymmetricRecyclerView) findViewById(R.id.recyclerView);

    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    actionBar.setDisplayHomeAsUpEnabled(true);

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    if (navigationView != null) {
      setupDrawerContent(navigationView);
    }

    RecyclerViewAdapter adapter = new RecyclerViewAdapter(Collections.<DemoItem>emptyList());
    recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            return true;
          }
        });
  }

  static class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final List<DemoItem> items;

    public RecyclerViewAdapter(List<DemoItem> items) {
      this.items = items;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ViewHolder(parent, viewType);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      holder.bind(items.get(position));
    }

    @Override public int getItemCount() {
      return items.size();
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(ViewGroup parent, int viewType) {
      super(LayoutInflater.from(parent.getContext()).inflate(
          viewType == 0 ? R.layout.adapter_item : R.layout.adapter_item_odd, parent, false));
    }

    public void bind(DemoItem item) {
      TextView textView;
      if (getItemViewType() == 0) {
        textView = (TextView) itemView.findViewById(R.id.textview);
      } else {
        textView = (TextView) itemView.findViewById(R.id.textview_odd);
      }

      textView.setText(String.valueOf(item.getPosition()));
    }
  }
}
