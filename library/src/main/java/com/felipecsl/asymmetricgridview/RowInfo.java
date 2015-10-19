package com.felipecsl.asymmetricgridview;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class RowInfo implements Parcelable {
  private final List<RowItem> items;
  private final int rowHeight;
  private final float spaceLeft;

  public RowInfo(int rowHeight, List<RowItem> items, float spaceLeft) {
    this.rowHeight = rowHeight;
    this.items = items;
    this.spaceLeft = spaceLeft;
  }

  @SuppressWarnings("unchecked")
  public RowInfo(final Parcel in) {
    rowHeight = in.readInt();
    spaceLeft = in.readFloat();
    int totalItems = in.readInt();

    items = new ArrayList<>();
    final ClassLoader classLoader = AsymmetricItem.class.getClassLoader();

    for (int i = 0; i < totalItems; i++) {
      items.add(new RowItem(in.readInt(), (AsymmetricItem) in.readParcelable(classLoader)));
    }
  }

  public List<RowItem> getItems() {
    return items;
  }

  public int getRowHeight() {
    return rowHeight;
  }

  public float getSpaceLeft() {
    return spaceLeft;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(@NonNull Parcel dest, final int flags) {
    dest.writeInt(rowHeight);
    dest.writeFloat(spaceLeft);
    dest.writeInt(items.size());

    for (RowItem rowItem : items) {
      dest.writeInt(rowItem.getIndex());
      dest.writeParcelable(rowItem.getItem(), 0);
    }
  }

  public static final Parcelable.Creator<RowInfo> CREATOR = new Parcelable.Creator<RowInfo>() {
    @Override public RowInfo createFromParcel(@NonNull Parcel in) {
      return new RowInfo(in);
    }

    @Override @NonNull public RowInfo[] newArray(final int size) {
      return new RowInfo[size];
    }
  };
}
