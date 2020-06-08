/*
 * Copyright 2013 Joan Zapata
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quvideo.application.gallery.adapterhelper;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.quvideo.application.gallery.adapterhelper.animation.AlphaInAnimation;
import com.quvideo.application.gallery.adapterhelper.animation.BaseAnimation;
import com.quvideo.application.gallery.adapterhelper.entity.IExpandable;
import com.quvideo.application.gallery.adapterhelper.loadmore.LoadMoreView;
import com.quvideo.application.gallery.adapterhelper.loadmore.SimpleLoadMoreView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class BaseQuickAdapter<T, K extends BaseViewHolder>
    extends RecyclerView.Adapter<K> {

  //load more
  private boolean mNextLoadEnable = false;
  private boolean mLoadMoreEnable = false;
  private boolean mLoading = false;
  private LoadMoreView mLoadMoreView = new SimpleLoadMoreView();
  private RequestLoadMoreListener mRequestLoadMoreListener;
  private boolean mEnableLoadMoreEndClick = false;

  //Animation
  /**
   *
   */
  public static final int ALPHAIN = 0x00000001;
  /**
   *
   */
  public static final int SCALEIN = 0x00000002;
  /**
   *
   */
  public static final int SLIDEIN_BOTTOM = 0x00000003;
  /**
   *
   */
  public static final int SLIDEIN_LEFT = 0x00000004;
  /**
   *
   */
  public static final int SLIDEIN_RIGHT = 0x00000005;
  private OnItemChildClickListener mOnItemChildClickListener;
  private boolean mFirstOnlyEnable = true;
  private boolean mOpenAnimationEnable = false;
  private Interpolator mInterpolator = new LinearInterpolator();
  private int mDuration = 300;
  private int mLastPosition = -1;

  private BaseAnimation mSelectAnimation = new AlphaInAnimation();
  //empty
  private FrameLayout mEmptyLayout;
  private boolean mIsUseEmpty = true;

  protected static final String TAG = BaseQuickAdapter.class.getSimpleName();
  protected Context mContext;
  protected int mLayoutResId;
  protected LayoutInflater mLayoutInflater;
  protected List<T> mData;
  public static final int HEADER_VIEW = 0x00000111;
  public static final int LOADING_VIEW = 0x00000222;
  public static final int FOOTER_VIEW = 0x00000333;
  public static final int EMPTY_VIEW = 0x00000555;
  private RecyclerView mRecyclerView;
  private int mPreLoadNumber = 1;

  @IntDef({ ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT })
  @Retention(RetentionPolicy.SOURCE) public @interface AnimationType {
  }

  protected RecyclerView getRecyclerView() {
    return mRecyclerView;
  }

  private void setRecyclerView(RecyclerView recyclerView) {
    mRecyclerView = recyclerView;
  }

  private void checkNotNull() {
    if (getRecyclerView() == null) {
      throw new IllegalStateException("please bind recyclerView first!");
    }
  }

  /**
   * same as recyclerView.setAdapter(), and save the instance of recyclerView
   */
  public void bindToRecyclerView(RecyclerView recyclerView) {
    if (getRecyclerView() == recyclerView) {
      throw new IllegalStateException("Don't bind twice");
    }
    setRecyclerView(recyclerView);
    getRecyclerView().setAdapter(this);
  }

  /**
   * @deprecated This method is because it can lead to crash: always call this method while RecyclerView is computing a layout or scrolling.
   */
  @Deprecated public void setOnLoadMoreListener(
      RequestLoadMoreListener requestLoadMoreListener) {
    openLoadMore(requestLoadMoreListener);
  }

  private void openLoadMore(RequestLoadMoreListener requestLoadMoreListener) {
    this.mRequestLoadMoreListener = requestLoadMoreListener;
    mNextLoadEnable = true;
    mLoadMoreEnable = true;
    mLoading = false;
  }

  /**
   * Load more view count
   *
   * @return 0 or 1
   */
  public int getLoadMoreViewCount() {
    if (mRequestLoadMoreListener == null || !mLoadMoreEnable) {
      return 0;
    }
    if (!mNextLoadEnable && mLoadMoreView.isLoadEndMoreGone()) {
      return 0;
    }
    if (mData.size() == 0) {
      return 0;
    }
    return 1;
  }

  /**
   * Gets to load more locations
   */
  public int getLoadMoreViewPosition() {
    return getHeaderLayoutCount() + mData.size() + getFooterLayoutCount();
  }

  /**
   * Sets the duration of the animation.
   *
   * @param duration The length of the animation, in milliseconds.
   */
  public void setDuration(int duration) {
    mDuration = duration;
  }

  /**
   * Same as QuickAdapter#QuickAdapter(Context,int) but with
   * some initialization data.
   *
   * @param layoutResId The layout resource id of each item.
   * @param data A new list is created out of this one to avoid mutable list
   */
  public BaseQuickAdapter(@LayoutRes int layoutResId, @Nullable List<T> data) {
    this.mData = data == null ? new ArrayList<T>() : data;
    if (layoutResId != 0) {
      this.mLayoutResId = layoutResId;
    }
  }

  public BaseQuickAdapter(@Nullable List<T> data) {
    this(0, data);
  }

  /**
   * setting up a new instance to data;
   */
  public void setNewData(@Nullable List<T> data) {
    this.mData = data == null ? new ArrayList<T>() : data;
    if (mRequestLoadMoreListener != null) {
      mNextLoadEnable = true;
      mLoadMoreEnable = true;
      mLoading = false;
      mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
    }
    mLastPosition = -1;
    notifyDataSetChanged();
  }

  /**
   * insert  a item associated with the specified position of adapter
   *
   * @deprecated use {@link #addData(int, Object)} instead
   */
  @Deprecated public void add(@IntRange(from = 0) int position, @NonNull T item) {
    addData(position, item);
  }

  /**
   * add one new data in to certain location
   */
  public void addData(@IntRange(from = 0) int position, @NonNull T data) {
    mData.add(position, data);
    notifyItemInserted(position + getHeaderLayoutCount());
    compatibilityDataSizeChanged(1);
  }

  /**
   * remove the item associated with the specified position of adapter
   */
  public void remove(@IntRange(from = 0) int position) {
    mData.remove(position);
    int internalPosition = position + getHeaderLayoutCount();
    notifyItemRemoved(internalPosition);
    compatibilityDataSizeChanged(0);
    notifyItemRangeChanged(internalPosition, mData.size() - internalPosition);
  }

  /**
   * compatible getLoadMoreViewCount and getEmptyViewCount may change
   *
   * @param size Need compatible data size
   */
  private void compatibilityDataSizeChanged(int size) {
    final int dataSize = mData == null ? 0 : mData.size();
    if (dataSize == size) {
      notifyDataSetChanged();
    }
  }

  /**
   * Get the data of list
   *
   * @return 列表数据
   */
  @NonNull public List<T> getData() {
    return mData;
  }

  /**
   * Get the data item associated with the specified position in the data set.
   *
   * @param position Position of the item whose data we want within the adapter's
   * data set.
   * @return The data at the specified position.
   */
  @Nullable public T getItem(@IntRange(from = 0) int position) {
    if (position >= 0 && position < mData.size()) {
      return mData.get(position);
    } else {
      return null;
    }
  }

  /**
   * if setHeadView will be return 1 if not will be return 0.
   */
  @Deprecated public int getHeaderViewsCount() {
    return getHeaderLayoutCount();
  }

  /**
   * if mFooterLayout will be return 1 or not will be return 0.
   */
  @Deprecated public int getFooterViewsCount() {
    return getFooterLayoutCount();
  }

  /**
   * if addHeaderView will be return 1, if not will be return 0
   */
  public int getHeaderLayoutCount() {
    return 0;
  }

  /**
   * if addFooterView will be return 1, if not will be return 0
   */
  public int getFooterLayoutCount() {
    return 0;
  }

  /**
   * if show empty view will be return 1 or not will be return 0
   */
  public int getEmptyViewCount() {
    if (mEmptyLayout == null || mEmptyLayout.getChildCount() == 0) {
      return 0;
    }
    if (!mIsUseEmpty) {
      return 0;
    }
    if (mData.size() != 0) {
      return 0;
    }
    return 1;
  }

  @Override public int getItemCount() {
    int count;
    if (1 == getEmptyViewCount()) {
      count = 1;
    } else {
      count = getHeaderLayoutCount()
          + mData.size()
          + getFooterLayoutCount()
          + getLoadMoreViewCount();
    }
    return count;
  }

  @Override public int getItemViewType(int position) {
    if (getEmptyViewCount() == 1) {
      boolean header = false;
      switch (position) {
        case 0:
          if (header) {
            return HEADER_VIEW;
          } else {
            return EMPTY_VIEW;
          }
        case 1:
          if (header) {
            return EMPTY_VIEW;
          } else {
            return FOOTER_VIEW;
          }
        case 2:
          return FOOTER_VIEW;
        default:
          return EMPTY_VIEW;
      }
    }
    int numHeaders = getHeaderLayoutCount();
    if (position < numHeaders) {
      return HEADER_VIEW;
    } else {
      int adjPosition = position - numHeaders;
      int adapterCount = mData.size();
      if (adjPosition < adapterCount) {
        return getDefItemViewType(adjPosition);
      } else {
        adjPosition = adjPosition - adapterCount;
        int numFooters = getFooterLayoutCount();
        if (adjPosition < numFooters) {
          return FOOTER_VIEW;
        } else {
          return LOADING_VIEW;
        }
      }
    }
  }

  protected int getDefItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @NonNull @Override public K onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    K baseViewHolder = null;
    this.mContext = parent.getContext();
    this.mLayoutInflater = LayoutInflater.from(mContext);
    switch (viewType) {
      case LOADING_VIEW:
        baseViewHolder = getLoadingView(parent);
        break;
      case HEADER_VIEW:
        break;
      case EMPTY_VIEW:
        ViewParent emptyLayoutVp = mEmptyLayout.getParent();
        if (emptyLayoutVp instanceof ViewGroup) {
          ((ViewGroup) emptyLayoutVp).removeView(mEmptyLayout);
        }

        baseViewHolder = createBaseViewHolder(mEmptyLayout);
        break;
      case FOOTER_VIEW:
        break;
      default:
        baseViewHolder = onCreateDefViewHolder(parent, viewType);
    }
    baseViewHolder.setAdapter(this);
    return baseViewHolder;
  }

  private K getLoadingView(ViewGroup parent) {
    View view = getItemView(mLoadMoreView.getLayoutId(), parent);
    K holder = createBaseViewHolder(view);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mLoadMoreView.getLoadMoreStatus() == LoadMoreView.STATUS_FAIL) {
          notifyLoadMoreToLoading();
        }
        if (mEnableLoadMoreEndClick
            && mLoadMoreView.getLoadMoreStatus() == LoadMoreView.STATUS_END) {
          notifyLoadMoreToLoading();
        }
      }
    });
    return holder;
  }

  /**
   * The notification starts the callback and loads more
   */
  public void notifyLoadMoreToLoading() {
    if (mLoadMoreView.getLoadMoreStatus() == LoadMoreView.STATUS_LOADING) {
      return;
    }
    mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
    notifyItemChanged(getLoadMoreViewPosition());
  }

  /**
   * Called when a view created by this adapter has been attached to a window.
   * simple to solve item will layout using all
   * {@link #setFullSpan(RecyclerView.ViewHolder)}
   */
  @Override public void onViewAttachedToWindow(@NonNull K holder) {
    super.onViewAttachedToWindow(holder);
    int type = holder.getItemViewType();
    if (type == EMPTY_VIEW
        || type == HEADER_VIEW
        || type == FOOTER_VIEW
        || type == LOADING_VIEW) {
      setFullSpan(holder);
    } else {
      addAnimation(holder);
    }
  }

  /**
   * When set to true, the item will layout using all span area. That means, if orientation
   * is vertical, the view will have full width; if orientation is horizontal, the view will
   * have full height.
   * if the hold view use StaggeredGridLayoutManager they should using all span area
   *
   * @param holder True if this item should traverse all spans.
   */
  protected void setFullSpan(RecyclerView.ViewHolder holder) {
    if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
      StaggeredGridLayoutManager.LayoutParams params =
          (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
      params.setFullSpan(true);
    }
  }

  @Override public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
    if (manager instanceof GridLayoutManager) {
      final GridLayoutManager gridManager = ((GridLayoutManager) manager);
      final GridLayoutManager.SpanSizeLookup defSpanSizeLookup = gridManager.getSpanSizeLookup();
      gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        @Override public int getSpanSize(int position) {
          int type = getItemViewType(position);
          if (type == HEADER_VIEW && isHeaderViewAsFlow()) {
            return 1;
          }
          if (type == FOOTER_VIEW && isFooterViewAsFlow()) {
            return 1;
          }
          if (mSpanSizeLookup == null) {
            return isFixedViewType(type) ? gridManager.getSpanCount()
                : defSpanSizeLookup.getSpanSize(position);
          } else {
            return (isFixedViewType(type)) ? gridManager.getSpanCount()
                : mSpanSizeLookup.getSpanSize(gridManager, position - getHeaderLayoutCount());
          }
        }
      });
    }
  }

  protected boolean isFixedViewType(int type) {
    return type == EMPTY_VIEW
        || type == HEADER_VIEW
        || type == FOOTER_VIEW
        || type == LOADING_VIEW;
  }

  public boolean isHeaderViewAsFlow() {
    return false;
  }

  public boolean isFooterViewAsFlow() {
    return false;
  }

  private SpanSizeLookup mSpanSizeLookup;

  public interface SpanSizeLookup {
    int getSpanSize(GridLayoutManager gridLayoutManager, int position);
  }

  /**
   * To bind different types of holder and solve different the bind events
   *
   * @see #getDefItemViewType(int)
   */
  @Override public void onBindViewHolder(@NonNull K holder, int position) {
    //Add up fetch logic, almost like load more, but simpler.
    //Do not move position, need to change before LoadMoreView binding
    autoLoadMore(position);
    int viewType = holder.getItemViewType();

    switch (viewType) {
      case 0:
        convert(holder, getItem(position - getHeaderLayoutCount()));
        break;
      case LOADING_VIEW:
        mLoadMoreView.convert(holder);
        break;
      case HEADER_VIEW:
        break;
      case EMPTY_VIEW:
        break;
      case FOOTER_VIEW:
        break;
      default:
        convert(holder, getItem(position - getHeaderLayoutCount()));
        break;
    }
  }

  /**
   * To bind different types of holder and solve different the bind events
   *
   * the ViewHolder is currently bound to old data and Adapter may run an efficient partial
   * update using the payload info.  If the payload is empty,  Adapter run a full bind.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the
   * item at the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   * @param payloads A non-null list of merged payloads. Can be empty list if requires full
   * update.
   * @see #getDefItemViewType(int)
   */
  @Override public void onBindViewHolder(@NonNull K holder, int position,
      @NonNull List<Object> payloads) {
    if (payloads.isEmpty()) {
      onBindViewHolder(holder, position);
      return;
    }
    //Do not move position, need to change before LoadMoreView binding
    autoLoadMore(position);
    int viewType = holder.getItemViewType();

    switch (viewType) {
      case 0:
        convertPayloads(holder, getItem(position - getHeaderLayoutCount()), payloads);
        break;
      case LOADING_VIEW:
        mLoadMoreView.convert(holder);
        break;
      case HEADER_VIEW:
        break;
      case EMPTY_VIEW:
        break;
      case FOOTER_VIEW:
        break;
      default:
        convertPayloads(holder, getItem(position - getHeaderLayoutCount()), payloads);
        break;
    }
  }

  protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
    return createBaseViewHolder(parent, mLayoutResId);
  }

  protected K createBaseViewHolder(ViewGroup parent, int layoutResId) {
    return createBaseViewHolder(getItemView(layoutResId, parent));
  }

  /**
   * if you want to use subclass of BaseViewHolder in the adapter,
   * you must override the method to create new ViewHolder.
   *
   * @param view view
   * @return new ViewHolder
   */
  @SuppressWarnings("unchecked") protected K createBaseViewHolder(View view) {
    Class temp = getClass();
    Class z = null;
    while (z == null && null != temp) {
      z = getInstancedGenericKClass(temp);
      temp = temp.getSuperclass();
    }
    K k;
    // 泛型擦除会导致z为null
    if (z == null) {
      k = (K) new BaseViewHolder(view);
    } else {
      k = createGenericKInstance(z, view);
    }
    return k != null ? k : (K) new BaseViewHolder(view);
  }

  /**
   * try to create Generic K instance
   */
  @SuppressWarnings("unchecked") private K createGenericKInstance(Class z, View view) {
    try {
      Constructor constructor;
      // inner and unstatic class
      if (z.isMemberClass() && !Modifier.isStatic(z.getModifiers())) {
        constructor = z.getDeclaredConstructor(getClass(), View.class);
        constructor.setAccessible(true);
        return (K) constructor.newInstance(this, view);
      } else {
        constructor = z.getDeclaredConstructor(View.class);
        constructor.setAccessible(true);
        return (K) constructor.newInstance(view);
      }
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * get generic parameter K
   */
  private Class getInstancedGenericKClass(Class z) {
    Type type = z.getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      Type[] types = ((ParameterizedType) type).getActualTypeArguments();
      for (Type temp : types) {
        if (temp instanceof Class) {
          Class tempClass = (Class) temp;
          if (BaseViewHolder.class.isAssignableFrom(tempClass)) {
            return tempClass;
          }
        } else if (temp instanceof ParameterizedType) {
          Type rawType = ((ParameterizedType) temp).getRawType();
          if (rawType instanceof Class && BaseViewHolder.class.isAssignableFrom(
              (Class<?>) rawType)) {
            return (Class<?>) rawType;
          }
        }
      }
    }
    return null;
  }

  public void setEmptyView(int layoutResId, ViewGroup viewGroup) {
    View view =
        LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
    setEmptyView(view);
  }

  /**
   * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
   * Recommend you to use {@link #setEmptyView(int, ViewGroup)}
   *
   * @see #bindToRecyclerView(RecyclerView)
   */
  @Deprecated public void setEmptyView(int layoutResId) {
    checkNotNull();
    setEmptyView(layoutResId, getRecyclerView());
  }

  public void setEmptyView(View emptyView) {
    int oldItemCount = getItemCount();
    boolean insert = false;
    if (mEmptyLayout == null) {
      mEmptyLayout = new FrameLayout(emptyView.getContext());
      final FrameLayout.LayoutParams layoutParams =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.MATCH_PARENT);
      final ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
      if (lp != null) {
        layoutParams.width = lp.width;
        layoutParams.height = lp.height;
      }
      mEmptyLayout.setLayoutParams(layoutParams);
      insert = true;
    }
    mEmptyLayout.removeAllViews();
    mEmptyLayout.addView(emptyView);
    mIsUseEmpty = true;
    if (insert && getEmptyViewCount() == 1) {
      int position = 0;
      if (getItemCount() > oldItemCount) {
        notifyItemInserted(position);
      } else {
        notifyDataSetChanged();
      }
    }
  }

  @Deprecated public void setAutoLoadMoreSize(int preLoadNumber) {
    setPreLoadNumber(preLoadNumber);
  }

  public void setPreLoadNumber(int preLoadNumber) {
    if (preLoadNumber > 1) {
      mPreLoadNumber = preLoadNumber;
    }
  }

  private void autoLoadMore(int position) {
    if (getLoadMoreViewCount() == 0) {
      return;
    }
    if (position < getItemCount() - mPreLoadNumber) {
      return;
    }
    if (mLoadMoreView.getLoadMoreStatus() != LoadMoreView.STATUS_DEFAULT) {
      return;
    }
    mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_LOADING);
    if (!mLoading) {
      mLoading = true;
      if (getRecyclerView() != null) {
        getRecyclerView().post(new Runnable() {
          @Override public void run() {
            mRequestLoadMoreListener.onLoadMoreRequested();
          }
        });
      } else {
        mRequestLoadMoreListener.onLoadMoreRequested();
      }
    }
  }

  /**
   * add animation when you want to show time
   */
  private void addAnimation(RecyclerView.ViewHolder holder) {
    if (mOpenAnimationEnable) {
      if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
        BaseAnimation animation = mSelectAnimation;
        for (Animator anim : animation.getAnimators(holder.itemView)) {
          startAnim(anim, holder.getLayoutPosition());
        }
        mLastPosition = holder.getLayoutPosition();
      }
    }
  }

  /**
   * set anim to start when loading
   */
  protected void startAnim(Animator anim, int index) {
    anim.setDuration(mDuration).start();
    anim.setInterpolator(mInterpolator);
  }

  /**
   * @param layoutResId ID for an XML layout resource to load
   * @param parent Optional view to be the parent of the generated hierarchy or else simply an object that
   * provides a set of LayoutParams values for root of the returned
   * hierarchy
   * @return view will be return
   */
  protected View getItemView(@LayoutRes int layoutResId, ViewGroup parent) {
    return mLayoutInflater.inflate(layoutResId, parent, false);
  }

  public interface RequestLoadMoreListener {

    void onLoadMoreRequested();
  }

  /**
   * Implement this method and use the helper to adapt the view to the given item.
   *
   * @param helper A fully initialized helper.
   * @param item The item that needs to be displayed.
   */
  protected abstract void convert(@NonNull K helper, T item);

  /**
   * Optional implementation this method and use the helper to adapt the view to the given item.
   *
   * If {@link DiffUtil.Callback#getChangePayload(int, int)} is implemented,
   * then {@link BaseQuickAdapter#convert(BaseViewHolder, Object)} will not execute, and will
   * perform this method, Please implement this method for partial refresh.
   *
   * If use {@link RecyclerView.Adapter#notifyItemChanged(int, Object)} with payload,
   * Will execute this method.
   *
   * @param helper A fully initialized helper.
   * @param item The item that needs to be displayed.
   * @param payloads payload info.
   */
  protected void convertPayloads(@NonNull K helper, T item, @NonNull List<Object> payloads) {
  }

  /**
   * get the specific view by position,e.g. getViewByPosition(2, R.id.textView)
   * <p>
   * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
   *
   * @see #bindToRecyclerView(RecyclerView)
   */
  @Nullable public View getViewByPosition(int position, @IdRes int viewId) {
    checkNotNull();
    return getViewByPosition(getRecyclerView(), position, viewId);
  }

  @Nullable
  public View getViewByPosition(RecyclerView recyclerView, int position, @IdRes int viewId) {
    if (recyclerView == null) {
      return null;
    }
    BaseViewHolder viewHolder =
        (BaseViewHolder) recyclerView.findViewHolderForLayoutPosition(position);
    if (viewHolder == null) {
      return null;
    }
    return viewHolder.getView(viewId);
  }

  /**
   * Get the row id associated with the specified position in the list.
   *
   * @param position The position of the item within the adapter's data set whose row id we want.
   * @return The id of the item at the specified position.
   */
  @Override public long getItemId(int position) {
    return position;
  }

  private int getItemPosition(T item) {
    return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
  }

  /**
   * Get the parent item position of the IExpandable item
   *
   * @return return the closest parent item position of the IExpandable.
   * if the IExpandable item's level is 0, return itself position.
   * if the item's level is negative which mean do not implement this, return a negative
   * if the item is not exist in the data list, return a negative.
   */
  public int getParentPosition(@NonNull T item) {
    int position = getItemPosition(item);
    if (position == -1) {
      return -1;
    }

    // if the item is IExpandable, return a closest IExpandable item position whose level smaller than this.
    // if it is not, return the closest IExpandable item position whose level is not negative
    int level;
    if (item instanceof IExpandable) {
      level = ((IExpandable) item).getLevel();
    } else {
      level = Integer.MAX_VALUE;
    }
    if (level == 0) {
      return position;
    } else if (level == -1) {
      return -1;
    }

    for (int i = position; i >= 0; i--) {
      T temp = mData.get(i);
      if (temp instanceof IExpandable) {
        IExpandable expandable = (IExpandable) temp;
        if (expandable.getLevel() >= 0 && expandable.getLevel() < level) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * Interface definition for a callback to be invoked when an itemchild in this
   * view has been clicked
   */
  public interface OnItemChildClickListener {
    /**
     * callback method to be invoked when an itemchild in this view has been click
     *
     * @param view The view whihin the ItemView that was clicked
     * @param position The position of the view int the adapter
     */
    void onItemChildClick(BaseQuickAdapter adapter, View view, int position);
  }

  /**
   * Register a callback to be invoked when an itemchild in View has
   * been  clicked
   *
   * @param listener The callback that will run
   */
  public void setOnItemChildClickListener(OnItemChildClickListener listener) {
    mOnItemChildClickListener = listener;
  }

  /**
   * @return The callback to be invoked with an itemchild in this RecyclerView has
   * been clicked, or null id no callback has been set.
   */
  @Nullable public final OnItemChildClickListener getOnItemChildClickListener() {
    return mOnItemChildClickListener;
  }
}
