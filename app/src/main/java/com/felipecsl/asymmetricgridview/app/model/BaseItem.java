package com.felipecsl.asymmetricgridview.app.model;

public abstract class BaseItem {

    protected long id;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        final BaseItem model = (BaseItem)obj;
        return model.getId() == id;
    }

}
