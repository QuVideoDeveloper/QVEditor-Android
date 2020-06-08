package com.quvideo.application.gallery.board.adapter;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/6/2019
 */
public class MediaBoardItemUpdate {
  private Boolean showOrderEntity;

  private MediaBoardItemUpdate(Builder builder) {
    showOrderEntity = builder.showOrderEntity;
  }

  public Boolean getShowOrderEntity() {
    return showOrderEntity;
  }

  public static final class Builder {
    private Boolean showOrderEntity;

    public Builder() {
    }

    public Builder showOrderEntity(Boolean val) {
      showOrderEntity = val;
      return this;
    }

    public MediaBoardItemUpdate build() {
      return new MediaBoardItemUpdate(this);
    }
  }
}
