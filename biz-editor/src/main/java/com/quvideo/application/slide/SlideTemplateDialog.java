package com.quvideo.application.slide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.quvideo.application.EditorApp;
import com.quvideo.application.download.DownloadDialog;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.QEXytUtil;
import java.util.List;
import org.json.JSONObject;

public class SlideTemplateDialog extends DialogFragment implements View.OnClickListener {

  private List<SimpleTemplate> mTemplates;

  public SlideTemplateDialog() {
    super();
  }

  public void setTemplates(List<SimpleTemplate> templates) {
    mTemplates = templates;
  }

  @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Activity activity = getActivity();
    assert activity != null;
    View view = LayoutInflater.from(activity).inflate(R.layout.dialog_home_sample_template, null);
    RecyclerView recyclerView = view.findViewById(R.id.home_template_recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
    SildeTemplateAdapter adapter = new SildeTemplateAdapter((AppCompatActivity) activity, this);
    recyclerView.setAdapter(adapter);
    adapter.updateList(mTemplates);

    ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    recyclerView.setHasFixedSize(true);

    AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view);
    return builder.create();
  }

  @Override
  public void onStart() {
    super.onStart();
    //设置 dialog 的宽高
    getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    //设置 dialog 的背景为 null
    getDialog().getWindow().setBackgroundDrawable(null);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == EditorActivity.INTENT_REQUEST_QRCODE && data != null) {
      String result = data.getStringExtra(ZXingManager.ZXING_RESULT_QRMSG);
      if (!TextUtils.isEmpty(result)) {
        try {
          JSONObject jsonObject = new JSONObject(result);
          String ttid = jsonObject.optString("ttid");
          String url = jsonObject.optString("url");
          if (!TextUtils.isEmpty(ttid) && !TextUtils.isEmpty(url)) {
            if (!ttid.contains("0x01000000004")) {
              // 需要字幕，但不是字幕素材
              // 无滤镜
              ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                  R.string.mn_edit_tips_template_qrcode_error, Toast.LENGTH_LONG);
              return;
            }
            DownloadDialog downloadDialog = new DownloadDialog(new DownloadDialog.OnTemplateDownloadOver() {
              @Override public void onDownloadOver(String templateCode) {
                handleAddEffect(templateCode);
              }
            });
            downloadDialog.showDownloading(getActivity(), ttid, url);
          }
        } catch (Exception ignore) {
        }
      }
    }
  }

  private void handleAddEffect(String templateCode) {
    long templateId = QEXytUtil.ttidHexStrToLong(templateCode);
    if (templateId <= 0) {
      // 无滤镜
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    SlideTemplate slideTemplate = new SlideTemplate(templateId, "", 0, 1, 2);
    slideTemplate.onClick(getActivity());
    dismissAllowingStateLoss();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    Window window = getDialog().getWindow();
    WindowManager.LayoutParams layoutParams = window.getAttributes();
    layoutParams.gravity = Gravity.BOTTOM;
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
    window.setAttributes(layoutParams);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onClick(View v) {
    dismissAllowingStateLoss();
  }
}
