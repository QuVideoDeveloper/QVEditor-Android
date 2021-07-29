package com.quvideo.application.gallery.media.adapter;

public class MediaItemUpdateBean {
  private Integer orderUpdateBean;

  private MediaItemUpdateBean(Builder builder) {
    orderUpdateBean = builder.orderUpdateBean;
  }

  public Integer getOrderUpdateBean() {
    return orderUpdateBean;
  }

  public static final class Builder {
    private Integer orderUpdateBean;

    public Builder() {
    }

    public Builder orderUpdateBean(Integer val) {
      orderUpdateBean = val;
      return this;
    }

    public MediaItemUpdateBean build() {
      return new MediaItemUpdateBean(this);
    }
  }
}
