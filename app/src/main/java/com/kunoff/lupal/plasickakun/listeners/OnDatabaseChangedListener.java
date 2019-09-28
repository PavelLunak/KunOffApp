package com.kunoff.lupal.plasickakun.listeners;

import com.kunoff.lupal.plasickakun.objects.ItemMedia;

public interface OnDatabaseChangedListener {
    public void onItemAdded(long id);
    public void onItemRemoved(ItemMedia itemMedia);
}
