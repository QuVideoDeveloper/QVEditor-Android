package com.quvideo.application.editor.edit;

import android.app.Activity;
import com.quvideo.application.template.SimpleTemplate;

public class EditFilterTemplate extends SimpleTemplate {

  public EditFilterTemplate(long templateId) {
    super(templateId);
  }

  public EditFilterTemplate(long templateId, String title, int thumbResId) {
    super(templateId, title, thumbResId);
  }

  @Override public void onClick(Activity activity) {

  }
}
