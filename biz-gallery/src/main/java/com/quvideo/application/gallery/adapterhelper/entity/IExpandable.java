package com.quvideo.application.gallery.adapterhelper.entity;

import java.util.List;

/**
 * implement the interface if the item is expandable
 */
public interface IExpandable<T> {
    boolean isExpanded();
    List<T> getSubItems();

    /**
     * Get the level of this item. The level start from 0.
     * If you don't care about the level, just return a negative.
     */
    int getLevel();
}
