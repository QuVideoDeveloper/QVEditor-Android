package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ParamAdjust;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPParamAdjust;

public class EditAdjustDialog extends BaseMenuView {

  private static final int[] ADJUST_ITEM_TITLE_RES = new int[] {
      R.string.mn_edit_adjust_luminance,
      R.string.mn_edit_adjust_contrast,
      R.string.mn_edit_adjust_saturation,
      R.string.mn_edit_adjust_sharpness,
      R.string.mn_edit_adjust_colortemp,
      R.string.mn_edit_adjust_vignette,
      R.string.mn_edit_adjust_shadow,
      R.string.mn_edit_adjust_hue,
      R.string.mn_edit_adjust_highlight,
      R.string.mn_edit_adjust_fade,
  };

  private int clipIndex = 0;

  public EditAdjustDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipAdjust;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_adjust;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    RecyclerView recyclerView = view.findViewById(R.id.recycler_adjust);
    recyclerView.setAdapter(new AdjustListAdapter());
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
  }

  @Override protected void releaseAll() {
  }

  private class AdjustListAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private int titleWidth;

    @NonNull @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View rootView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_edit_seekbar, parent, false);

      ItemViewHolder holder = new ItemViewHolder(rootView);
      if (titleWidth == 0) {
        for (int titleId : ADJUST_ITEM_TITLE_RES) {
          String title = getContext().getString(titleId);
          float textWidth =
              holder.seekBarController.getTvTitle().getPaint().measureText(title) + 0.5f;
          titleWidth = Math.max((int) textWidth, titleWidth);
        }
      }
      holder.seekBarController.getTvTitle().getLayoutParams().width = titleWidth;
      return holder;
    }

    @Override public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
      String title = getContext().getString(ADJUST_ITEM_TITLE_RES[position]);
      holder.seekBarController.setTitle(title);
      holder.seekBarController.setSeekBarStartText("0");
      holder.seekBarController.setSeekBarEndText("100");

      initAdjustProgress(holder, position);
      holder.seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          if (!fromUser) {
            return;
          }
          onAdjustChanged(ADJUST_ITEM_TITLE_RES[position], seekBar.getProgress());
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override public void onStopTrackingTouch(SeekBar seekBar) {
        }
      });
    }

    @Override public int getItemCount() {
      return ADJUST_ITEM_TITLE_RES.length;
    }

    private void initAdjustProgress(ItemViewHolder holder, int position) {
      ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
      if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_luminance) {
        int luminance = clipData.getParamAdjust().luminance;
        holder.seekBarController.setSeekBarProgress(luminance);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_contrast) {
        int contrast = clipData.getParamAdjust().contrast;
        holder.seekBarController.setSeekBarProgress(contrast);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_saturation) {
        int saturation = clipData.getParamAdjust().saturation;
        holder.seekBarController.setSeekBarProgress(saturation);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_sharpness) {
        int sharpness = clipData.getParamAdjust().sharpness;
        holder.seekBarController.setSeekBarProgress(sharpness);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_colortemp) {
        int colourTemp = clipData.getParamAdjust().colourTemp;
        holder.seekBarController.setSeekBarProgress(colourTemp);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_vignette) {
        int vignette = clipData.getParamAdjust().vignette;
        holder.seekBarController.setSeekBarProgress(vignette);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_shadow) {
        int shadow = clipData.getParamAdjust().shadow;
        holder.seekBarController.setSeekBarProgress(shadow);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_hue) {
        int hue = clipData.getParamAdjust().hue;
        holder.seekBarController.setSeekBarProgress(hue);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_highlight) {
        int highlight = clipData.getParamAdjust().highlight;
        holder.seekBarController.setSeekBarProgress(highlight);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_fade) {
        int fade = clipData.getParamAdjust().fade;
        holder.seekBarController.setSeekBarProgress(fade);
      }
    }

    private void onAdjustChanged(int titleRes, int value) {
      ParamAdjust paramAdjust =
          mWorkSpace.getClipAPI().getClipList().get(clipIndex).getParamAdjust();
      if (titleRes == R.string.mn_edit_adjust_luminance) {
        paramAdjust.luminance = value;
      } else if (titleRes == R.string.mn_edit_adjust_contrast) {
        paramAdjust.contrast = value;
      } else if (titleRes == R.string.mn_edit_adjust_saturation) {
        paramAdjust.saturation = value;
      } else if (titleRes == R.string.mn_edit_adjust_sharpness) {
        paramAdjust.sharpness = value;
      } else if (titleRes == R.string.mn_edit_adjust_colortemp) {
        paramAdjust.colourTemp = value;
      } else if (titleRes == R.string.mn_edit_adjust_vignette) {
        paramAdjust.vignette = value;
      } else if (titleRes == R.string.mn_edit_adjust_shadow) {
        paramAdjust.shadow = value;
      } else if (titleRes == R.string.mn_edit_adjust_hue) {
        paramAdjust.hue = value;
      } else if (titleRes == R.string.mn_edit_adjust_highlight) {
        paramAdjust.highlight = value;
      } else if (titleRes == R.string.mn_edit_adjust_fade) {
        paramAdjust.fade = value;
      }
      ClipOPParamAdjust clipOPParamAdjust = new ClipOPParamAdjust(clipIndex, paramAdjust);
      mWorkSpace.handleOperation(clipOPParamAdjust);
    }
  }

  private class ItemViewHolder extends RecyclerView.ViewHolder {

    private EditSeekBarController seekBarController;

    ItemViewHolder(@NonNull View itemView) {
      super(itemView);

      seekBarController = new EditSeekBarController();
      seekBarController.bindView(itemView);
    }
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_adjust);
  }
}
