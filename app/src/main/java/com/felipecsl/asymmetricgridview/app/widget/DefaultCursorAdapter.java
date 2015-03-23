package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.os.Parcel;
import android.support.v4.widget.SimpleCursorAdapter;

import com.felipecsl.asymmetricgridview.app.R;
import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

import java.util.List;

public class DefaultCursorAdapter extends SimpleCursorAdapter implements DemoAdapter {

  private final Context context;

  public DefaultCursorAdapter(Context context, List<DemoItem> items) {
    super(context, R.layout.adapter_item,
          new SampleDbAdapter(context).open().deleteAllData().seedDatabase(items).fetchAllData(),
          new String[]{SampleDbAdapter.KEY_TEXT}, new int[]{R.id.text}, 0);

    this.context = context;
  }

  @Override public Object getItem(int position) {
    return new CursorAdapterItem((SQLiteCursor) super.getItem(position));
  }

  @Override public void appendItems(List<DemoItem> newItems) {
    SampleDbAdapter sampleDbAdapter = new SampleDbAdapter(context).open();

    for (DemoItem item : newItems) {
      sampleDbAdapter.createItem(String.valueOf(item.getPosition()), item.getRowSpan(),
                                 item.getColumnSpan());
    }

    swapCursor(sampleDbAdapter.fetchAllData());
  }

  @Override public void setItems(List<DemoItem> moreItems) {
    swapCursor(new SampleDbAdapter(context).open().deleteAllData().seedDatabase(moreItems)
                   .fetchAllData());
  }

  public static class CursorAdapterItem implements AsymmetricItem {

    private final int colSpan;
    private final int rowSpan;

    public CursorAdapterItem(SQLiteCursor cursor) {
      rowSpan = cursor.getInt(2);
      colSpan = cursor.getInt(3);
    }

    @Override public int getColumnSpan() {
      return colSpan;
    }

    @Override public int getRowSpan() {
      return rowSpan;
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {

    }
  }
}
