package com.quvideo.application.editor.sound;

/**
 * Created by santa on 2020-04-17.
 */
public class AudioTemplate {

  private String audioPath;
  private String title;
  private int thumbnailResId;

  public AudioTemplate(String audioPath, String title, int thumbnailResId) {
    this.audioPath = audioPath;
    this.title = title;
    this.thumbnailResId = thumbnailResId;
  }

  public String getAudioPath() {
    return audioPath;
  }

  public void setAudioPath(String audioPath) {
    this.audioPath = audioPath;
  }

  public String getTitle() {
    return title;
  }

  public int getThumbnailResId() {
    return thumbnailResId;
  }
}
