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
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ClipParamAdjust;
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
      int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, l);
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

    @NonNull @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View rootView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_edit_seekbar, parent, false);
      return new ItemViewHolder(rootView);
    }

    @Override public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
      String title = getContext().getString(ADJUST_ITEM_TITLE_RES[position]);
      holder.seekBarController.setTitle(title);
      holder.seekBarController.setSeekBarStartText("0");
      holder.seekBarController.setSeekBarEndText("100");

      initAdjustProgress(holder, position);
      holder.seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override public void onStopTrackingTouch(SeekBar seekBar) {
          onAdjustChanged(ADJUST_ITEM_TITLE_RES[position], seekBar.getProgress());
        }
      });
    }

    @Override public int getItemCount() {
      return ADJUST_ITEM_TITLE_RES.length;
    }

    private void initAdjustProgress(ItemViewHolder holder, int position) {
      ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
      if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_luminance) {
        int luminance = clipData.getClipParamAdjust().luminance;
        holder.seekBarController.setSeekBarProgress(luminance);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_contrast) {
        int contrast = clipData.getClipParamAdjust().contrast;
        holder.seekBarController.setSeekBarProgress(contrast);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_saturation) {
        int saturation = clipData.getClipParamAdjust().saturation;
        holder.seekBarController.setSeekBarProgress(saturation);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_sharpness) {
        int sharpness = clipData.getClipParamAdjust().sharpness;
        holder.seekBarController.setSeekBarProgress(sharpness);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_colortemp) {
        int colourTemp = clipData.getClipParamAdjust().colourTemp;
        holder.seekBarController.setSeekBarProgress(colourTemp);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_vignette) {
        int vignette = clipData.getClipParamAdjust().vignette;
        holder.seekBarController.setSeekBarProgress(vignette);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_shadow) {
        int shadow = clipData.getClipParamAdjust().shadow;
        holder.seekBarController.setSeekBarProgress(shadow);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_hue) {
        int hue = clipData.getClipParamAdjust().hue;
        holder.seekBarController.setSeekBarProgress(hue);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_highlight) {
        int highlight = clipData.getClipParamAdjust().highlight;
        holder.seekBarController.setSeekBarProgress(highlight);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_fade) {
        int fade = clipData.getClipParamAdjust().fade;
        holder.seekBarController.setSeekBarProgress(fade);
      }
    }

    private void onAdjustChanged(int titleRes, int value) {
      ClipParamAdjust clipParamAdjust =
          mWorkSpace.getClipAPI().getClipList().get(clipIndex).getClipParamAdjust();
      if (titleRes == R.string.mn_edit_adjust_luminance) {
        clipParamAdjust.luminance = value;
      } else if (titleRes == R.string.mn_edit_adjust_contrast) {
        clipParamAdjust.contrast = value;
      } else if (titleRes == R.string.mn_edit_adjust_saturation) {
        clipParamAdjust.saturation = value;
      } else if (titleRes == R.string.mn_edit_adjust_sharpness) {
        clipParamAdjust.sharpness = value;
      } else if (titleRes == R.string.mn_edit_adjust_colortemp) {
        clipParamAdjust.colourTemp = value;
      } else if (titleRes == R.string.mn_edit_adjust_vignette) {
        clipParamAdjust.vignette = value;
      } else if (titleRes == R.string.mn_edit_adjust_shadow) {
        clipParamAdjust.shadow = value;
      } else if (titleRes == R.string.mn_edit_adjust_hue) {
        clipParamAdjust.hue = value;
      } else if (titleRes == R.string.mn_edit_adjust_highlight) {
        clipParamAdjust.highlight = value;
      } else if (titleRes == R.string.mn_edit_adjust_fade) {
        clipParamAdjust.fade = value;
      }
      ClipOPParamAdjust clipOPParamAdjust = new ClipOPParamAdjust(clipIndex, clipParamAdjust);
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
