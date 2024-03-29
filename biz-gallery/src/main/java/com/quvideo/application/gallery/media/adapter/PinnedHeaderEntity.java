package com.quvideo.application.gallery.media.adapter;

import com.quvideo.application.gallery.adapterhelper.entity.MultiItemEntity;

/**
 * 实体类，可以将自己想要填充的数据包装进去，同时附带这个数据对应的类型
 */
public class PinnedHeaderEntity<T> implements MultiItemEntity {

    private final int itemType;

    private T data;

    private String pinnedHeaderName;

    public PinnedHeaderEntity(T data, int itemType, String pinnedHeaderName) {
        this.data = data;
        this.itemType = itemType;
        this.pinnedHeaderName = pinnedHeaderName;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setPinnedHeaderName(String pinnedHeaderName) {
        this.pinnedHeaderName = pinnedHeaderName;
    }

    public T getData() {
        return data;
    }

    public String getPinnedHeaderName() {
        return pinnedHeaderName;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
