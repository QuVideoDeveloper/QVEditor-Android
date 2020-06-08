package com.quvideo.application.gallery.controller;

import android.app.Activity;
import android.content.Context;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.utils.mvp.MvpView;
import java.util.ArrayList;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/11/2019
 */
public interface IGalleryFile extends MvpView {

  Context getContext();

  Activity getActivity();

  void onFileDone(ArrayList<MediaModel> modelArrayList);
}
