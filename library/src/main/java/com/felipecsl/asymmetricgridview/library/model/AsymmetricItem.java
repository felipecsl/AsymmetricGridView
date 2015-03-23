package com.felipecsl.asymmetricgridview.library.model;

import android.os.Parcelable;

public interface AsymmetricItem extends Parcelable {

  int getColumnSpan();

  int getRowSpan();
}
