package com.quvideo.application.editor.effect.collage;

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
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.clip.ParamAdjust;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPParamAdjust;

public class CollageAdjustDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;

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

  public CollageAdjustDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.CollageAdjust;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_collage_adjust;
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
      ParamAdjust paramAdjust =
          ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex)).mParamAdjust;
      if (paramAdjust == null) {
        paramAdjust = new ParamAdjust();
      }
      if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_luminance) {
        int luminance = paramAdjust.luminance;
        holder.seekBarController.setSeekBarProgress(luminance);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_contrast) {
        int contrast = paramAdjust.contrast;
        holder.seekBarController.setSeekBarProgress(contrast);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_saturation) {
        int saturation = paramAdjust.saturation;
        holder.seekBarController.setSeekBarProgress(saturation);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_sharpness) {
        int sharpness = paramAdjust.sharpness;
        holder.seekBarController.setSeekBarProgress(sharpness);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_colortemp) {
        int colourTemp = paramAdjust.colourTemp;
        holder.seekBarController.setSeekBarProgress(colourTemp);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_vignette) {
        int vignette = paramAdjust.vignette;
        holder.seekBarController.setSeekBarProgress(vignette);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_shadow) {
        int shadow = paramAdjust.shadow;
        holder.seekBarController.setSeekBarProgress(shadow);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_hue) {
        int hue = paramAdjust.hue;
        holder.seekBarController.setSeekBarProgress(hue);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_highlight) {
        int highlight = paramAdjust.highlight;
        holder.seekBarController.setSeekBarProgress(highlight);
      } else if (ADJUST_ITEM_TITLE_RES[position] == R.string.mn_edit_adjust_fade) {
        int fade = paramAdjust.fade;
        holder.seekBarController.setSeekBarProgress(fade);
      }
    }

    private void onAdjustChanged(int titleRes, int value) {
      ParamAdjust paramAdjust =
          ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex)).mParamAdjust;
      if (paramAdjust == null) {
        paramAdjust = new ParamAdjust();
      }
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
      EffectOPParamAdjust effectOPParamAdjust = new EffectOPParamAdjust(groupId, effectIndex, paramAdjust);
      mWorkSpace.handleOperation(effectOPParamAdjust);
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
