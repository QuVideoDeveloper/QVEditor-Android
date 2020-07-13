package com.quvideo.application.gallery.controller;

import android.app.Activity;
import android.content.Context;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.utils.mvp.MvpView;
import java.util.ArrayList;

public interface IGalleryFile extends MvpView {

  Context getContext();

  Activity getActivity();

  void onFileDone(ArrayList<MediaModel> modelArrayList);
}
