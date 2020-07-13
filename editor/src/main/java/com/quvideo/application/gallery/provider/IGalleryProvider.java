package com.quvideo.application.gallery.provider;

import com.quvideo.application.gallery.model.MediaModel;
import java.util.ArrayList;

public class IGalleryProvider {

  /**
   * check file available
   *
   * @param filePath the file which will be check
   * @return file available
   */
  public boolean checkFileEditAble(String filePath) {
    return true;
  }

  /**
   * file select done from gallery
   *
   * @param mediaList selected file list
   */
  public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {

  }
}
