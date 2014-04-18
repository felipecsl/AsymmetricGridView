package com.felipecsl.asymmetricgridview.library.widget;

import android.os.Parcel;
import android.os.Parcelable;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

import java.util.ArrayList;
import java.util.List;

class RowInfo<T extends AsymmetricItem> implements Parcelable {

    private final List<T> items;
    private final int rowHeight;
    private final float spaceLeft;

    public RowInfo(final int rowHeight,
                   final List<T> items,
                   final float spaceLeft) {

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
            items.add((T) in.readParcelable(classLoader));
        }
    }

    public List<T> getItems() {
        return items;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public float getSpaceLeft() {
        return spaceLeft;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(rowHeight);
        dest.writeFloat(spaceLeft);
        dest.writeInt(items.size());

        for (int i = 0; i < items.size(); i++)
            dest.writeParcelable(items.get(i), 0);
    }

    /* Parcelable interface implementation */
    public static final Parcelable.Creator<RowInfo> CREATOR = new Parcelable.Creator<RowInfo>() {

        @Override
        public RowInfo createFromParcel(final Parcel in) {
            return new RowInfo<AsymmetricItem>(in);
        }

        @Override
        public RowInfo[] newArray(final int size) {
            return new RowInfo[size];
        }
    };
}
