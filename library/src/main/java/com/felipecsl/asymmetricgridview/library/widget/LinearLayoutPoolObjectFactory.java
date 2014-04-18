package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;

public class LinearLayoutPoolObjectFactory implements PoolObjectFactory<IcsLinearLayout>{

    private final Context context;

    public LinearLayoutPoolObjectFactory(final Context context) {
        this.context = context;
    }

    @Override
    public IcsLinearLayout createObject() {
        return new IcsLinearLayout(context, null);
    }
}
