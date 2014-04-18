package com.felipecsl.asymmetricgridview.library.widget;

import android.view.View;

public interface PoolObjectFactory<T extends View> {
    public T createObject();
}
