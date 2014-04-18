package com.felipecsl.asymmetricgridview.library.model;

import android.os.Parcelable;

public interface AsymmetricItem extends Parcelable {
    public int getColumnSpan();
    public int getRowSpan();
}
