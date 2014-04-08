package com.felipecsl.asymmetricgridview.app.widget;

import java.util.List;

public interface SimpleDynamicAdapter<T> {

    void setObjects(final List<T> newItems);

    void appendObjects(final List<T> newItems);

    int getCount();
}
