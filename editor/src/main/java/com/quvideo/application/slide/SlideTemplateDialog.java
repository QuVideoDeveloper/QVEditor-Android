package com.quvideo.application.slide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.quvideo.application.editor.R;
import com.quvideo.application.template.SimpleTemplate;
import java.util.List;

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
