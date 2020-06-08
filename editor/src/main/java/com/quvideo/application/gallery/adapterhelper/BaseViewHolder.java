/**
 * Copyright 2013 Joan Zapata
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quvideo.application.gallery.adapterhelper;

import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

  /**
   * Views indexed with their IDs
   */
  private final SparseArray<View> views;

  public Set<Integer> getNestViews() {
    return nestViews;
  }

  private final HashSet<Integer> nestViews;

  private final LinkedHashSet<Integer> childClickViewIds;

  private final LinkedHashSet<Integer> itemChildLongClickViewIds;
  private BaseQuickAdapter adapter;
  /**
   * use itemView instead
   */
  @Deprecated
  public View convertView;

  /**
   * Package private field to retain the associated user object and detect a change
   */
  private Object associatedObject;

  public BaseViewHolder(final View view) {
    super(view);
    this.views = new SparseArray<>();
    this.childClickViewIds = new LinkedHashSet<>();
    this.itemChildLongClickViewIds = new LinkedHashSet<>();
    this.nestViews = new HashSet<>();
    convertView = view;
  }

  public HashSet<Integer> getItemChildLongClickViewIds() {
    return itemChildLongClickViewIds;
  }

  public HashSet<Integer> getChildClickViewIds() {
    return childClickViewIds;
  }

  /**
   * use itemView instead
   *
   * @return the ViewHolder root view
   */
  @Deprecated
  public View getConvertView() {

    return convertView;
  }

  /**
   * Will set the text of a TextView.
   *
   * @param viewId The view id.
   * @param value The text to put in the text view.
   * @return The BaseViewHolder for chaining.
   */
  public BaseViewHolder setText(@IdRes int viewId, CharSequence value) {
    TextView view = getView(viewId);
    view.setText(value);
    return this;
  }

  public BaseViewHolder setText(@IdRes int viewId, @StringRes int strId) {
    TextView view = getView(viewId);
    view.setText(strId);
    return this;
  }

  /**
   * Set a view visibility to VISIBLE (true) or GONE (false).
   *
   * @param viewId The view id.
   * @param visible True for VISIBLE, false for GONE.
   * @return The BaseViewHolder for chaining.
   */
  public BaseViewHolder setGone(@IdRes int viewId, boolean visible) {
    View view = getView(viewId);
    view.setVisibility(visible ? View.VISIBLE : View.GONE);
    return this;
  }

  /**
   * Sets the on click listener of the view.
   *
   * @param viewId The view id.
   * @param listener The on click listener;
   * @return The BaseViewHolder for chaining.
   */
  @Deprecated
  public BaseViewHolder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
    View view = getView(viewId);
    view.setOnClickListener(listener);
    return this;
  }

  /**
   * add childView id
   *
   * @param viewIds add the child views id can support childview click
   * @return if you use adapter bind listener
   * @link {(adapter.setOnItemChildClickListener(listener))}
   * <p>
   * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
   */
  @SuppressWarnings("unchecked")
  public BaseViewHolder addOnClickListener(@IdRes final int... viewIds) {
    for (int viewId : viewIds) {
      childClickViewIds.add(viewId);
      final View view = getView(viewId);
      if (view != null) {
        if (!view.isClickable()) {
          view.setClickable(true);
        }
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (adapter.getOnItemChildClickListener() != null) {
              int position = getAdapterPosition();
              if (position == RecyclerView.NO_POSITION) {
                return;
              }
              position -= adapter.getHeaderLayoutCount();
              adapter.getOnItemChildClickListener().onItemChildClick(adapter, v, position);
            }
          }
        });
      }
    }
    return this;
  }

  /**
   * set nestview id
   *
   * @param viewIds add the child views id   can support childview click
   */
  public BaseViewHolder setNestView(@IdRes int... viewIds) {
    for (int viewId : viewIds) {
      nestViews.add(viewId);
    }
    addOnClickListener(viewIds);
    addOnLongClickListener(viewIds);
    return this;
  }

  /**
   * add long click view id
   *
   * @return if you use adapter bind listener
   * @link {(adapter.setOnItemChildLongClickListener(listener))}
   * <p>
   * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
   */
  @SuppressWarnings("unchecked")
  public BaseViewHolder addOnLongClickListener(@IdRes final int... viewIds) {
    for (int viewId : viewIds) {
      itemChildLongClickViewIds.add(viewId);
      final View view = getView(viewId);
      if (view != null) {
        if (!view.isLongClickable()) {
          view.setLongClickable(true);
        }
        view.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            return false;
          }
        });
      }
    }
    return this;
  }

  /**
   * Sets the on touch listener of the view.
   *
   * @param viewId The view id.
   * @param listener The on touch listener;
   * @return The BaseViewHolder for chaining.
   */
  @Deprecated
  public BaseViewHolder setOnTouchListener(@IdRes int viewId, View.OnTouchListener listener) {
    View view = getView(viewId);
    view.setOnTouchListener(listener);
    return this;
  }

  /**
   * Sets the on long click listener of the view.
   *
   * @param viewId The view id.
   * @param listener The on long click listener;
   * @return The BaseViewHolder for chaining.
   * Please use {@link #addOnLongClickListener} (adapter.setOnItemChildLongClickListener(listener))}
   */
  @Deprecated
  public BaseViewHolder setOnLongClickListener(@IdRes int viewId, View.OnLongClickListener listener) {
    View view = getView(viewId);
    view.setOnLongClickListener(listener);
    return this;
  }

  /**
   * Sets the listview or gridview's item click listener of the view
   *
   * @param viewId The view id.
   * @param listener The item on click listener;
   * @return The BaseViewHolder for chaining.
   * Please use {@link #addOnClickListener} (int)} (adapter.setOnItemChildClickListener(listener))}
   */
  @Deprecated
  public BaseViewHolder setOnItemClickListener(@IdRes int viewId, AdapterView.OnItemClickListener listener) {
    AdapterView view = getView(viewId);
    view.setOnItemClickListener(listener);
    return this;
  }

  /**
   * Sets the adapter of a adapter view.
   *
   * @param viewId The view id.
   * @param adapter The adapter;
   * @return The BaseViewHolder for chaining.
   */
  @SuppressWarnings("unchecked")
  public BaseViewHolder setAdapter(@IdRes int viewId, Adapter adapter) {
    AdapterView view = getView(viewId);
    view.setAdapter(adapter);
    return this;
  }

  /**
   * Sets the adapter of a adapter view.
   *
   * @param adapter The adapter;
   * @return The BaseViewHolder for chaining.
   */
  protected BaseViewHolder setAdapter(BaseQuickAdapter adapter) {
    this.adapter = adapter;
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T extends View> T getView(@IdRes int viewId) {
    View view = views.get(viewId);
    if (view == null) {
      view = itemView.findViewById(viewId);
      views.put(viewId, view);
    }
    return (T) view;
  }
}
