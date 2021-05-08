package com.quvideo.application.frame.view;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.DPUtils;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.engine.process.param.TransParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.View.INVISIBLE;

public class FrameTransMenuPopwin extends PopupWindow {
  private Activity mActivity;

  private CustomSeekbarPop mCustomSeekbarPop;

  private RecyclerView mRecyclerView;

  private TransParam mTransParam = null;

  private OnParamSelectCallback mCallback;

  public FrameTransMenuPopwin(Activity activity, OnParamSelectCallback callback) {
    this.mActivity = activity;
    this.mCallback = callback;
    initView();
  }

  private void initView() {
    View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_frame_filter_menu_pop, null, false);

    view.findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dismiss();
      }
    });

    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    mCustomSeekbarPop.setVisibility(INVISIBLE);
    mCustomSeekbarPop.setShowPop(false);

    mRecyclerView = view.findViewById(R.id.clip_recyclerview);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
    mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
          outRect.left = DPUtils.dpToPixel(mActivity, 16);
        } else {
          outRect.left = DPUtils.dpToPixel(mActivity, 8);
        }
      }
    });

    FrameEffectAdapter adapter = new FrameEffectAdapter(mActivity, getDataList());
    mRecyclerView.setAdapter(adapter);

    adapter.setOnItemClickListener(new FrameEffectAdapter.OnItemSelectListener() {
      @Override public void onItemSelected(SimpleTemplate template) {
        if (template == null || template.getTemplateId() == 0) {
          mTransParam = null;
          mCustomSeekbarPop.setVisibility(INVISIBLE);
        } else {
          if (mTransParam == null) {
            mTransParam = new TransParam();
          }
          mTransParam.transPath = XytManager.getXytInfo(template.getTemplateId()).filePath;
          mCustomSeekbarPop.setVisibility(View.VISIBLE);
        }
        if (mCallback != null) {
          mCallback.onParamChange(mTransParam);
        }
      }
    });

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(30)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            if (mTransParam != null) {
              mTransParam.duration = progress * 100;
            }
            if (mCallback != null) {
              mCallback.onParamChange(mTransParam);
            }
          }
        }));
    initPopWindow(view);
  }

  private void initPopWindow(View view) {
    this.setContentView(view);
    this.setOutsideTouchable(true);
    this.setTouchable(true);
    this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
    this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
    this.setInputMethodMode(INPUT_METHOD_NOT_NEEDED);
    this.setFocusable(false);
    this.setBackgroundDrawable(new BitmapDrawable());
  }

  private List<SimpleTemplate> getDataList() {
    EditFilterTemplate[] templates = AssetConstants.getXytListByType(AssetConstants.XytType.Transition);
    if (templates != null) {
      return new ArrayList<>(Arrays.asList(templates));
    }
    return new ArrayList<>();
  }

  /**
   * 滤镜选择处理
   */
  public interface OnParamSelectCallback {
    void onParamChange(TransParam transParam);
  }
}
