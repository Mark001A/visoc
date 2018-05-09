package com.dovar.common.vsview;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2018/5/13.
 */

public class State {
    static final int STEP_START = 1;
    static final int STEP_LAYOUT = 1 << 1;
    static final int STEP_ANIMATIONS = 1 << 2;

    void assertLayoutStep(int accepted) {
        if ((accepted & mLayoutStep) == 0) {
            throw new IllegalStateException("Layout state should be one of "
                    + Integer.toBinaryString(accepted) + " but it is "
                    + Integer.toBinaryString(mLayoutStep));
        }
    }

    @IntDef(flag = true, value = {
            STEP_START, STEP_LAYOUT, STEP_ANIMATIONS
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface LayoutState {
    }

    int mTargetPosition = RecyclerView.NO_POSITION;

    @LayoutState
    int mLayoutStep = STEP_START;

    private SparseArray<Object> mData;

    /**
     * Number of items adapter has.
     */
    int mItemCount = 0;

    /**
     * Number of items adapter had in the previous layout.
     */
    int mPreviousLayoutItemCount = 0;

    /**
     * Number of items that were NOT laid out but has been deleted from the adapter after the
     * previous layout.
     */
    int mDeletedInvisibleItemCountSincePreviousLayout = 0;

    boolean mStructureChanged = false;

    boolean mInPreLayout = false;

    boolean mRunSimpleAnimations = false;

    boolean mRunPredictiveAnimations = false;

    boolean mTrackOldChangeHolders = false;

    boolean mIsMeasuring = false;

    /**
     * This data is saved before a layout calculation happens. After the layout is finished,
     * if the previously focused view has been replaced with another view for the same item, we
     * move the focus to the new item automatically.
     */
    int mFocusedItemPosition;
    long mFocusedItemId;
    // when a sub child has focus, record its id and see if we can directly request focus on
    // that one instead
    int mFocusedSubChildId;

    State reset() {
        mTargetPosition = RecyclerView.NO_POSITION;
        if (mData != null) {
            mData.clear();
        }
        mItemCount = 0;
        mStructureChanged = false;
        mIsMeasuring = false;
        return this;
    }

    /**
     * Returns true if the RecyclerView is currently measuring the layout. This value is
     * {@code true} only if the LayoutManager opted into the auto measure API and RecyclerView
     * has non-exact measurement specs.
     * <p>
     * Note that if the LayoutManager supports predictive animations and it is calculating the
     * pre-layout step, this value will be {@code false} even if the RecyclerView is in
     * {@code onMeasure} call. This is because pre-layout means the previous state of the
     * RecyclerView and measurements made for that state cannot change the RecyclerView's size.
     * LayoutManager is always guaranteed to receive another call to
     * {@link DRecyclerView.LayoutManager#onLayoutChildren(DRecyclerView.Recycler, State)} when this happens.
     *
     * @return True if the RecyclerView is currently calculating its bounds, false otherwise.
     */
    public boolean isMeasuring() {
        return mIsMeasuring;
    }

    /**
     * Returns true if
     *
     * @return
     */
    public boolean isPreLayout() {
        return mInPreLayout;
    }

    /**
     * Returns whether RecyclerView will run predictive animations in this layout pass
     * or not.
     *
     * @return true if RecyclerView is calculating predictive animations to be run at the end
     * of the layout pass.
     */
    public boolean willRunPredictiveAnimations() {
        return mRunPredictiveAnimations;
    }

    /**
     * Returns whether RecyclerView will run simple animations in this layout pass
     * or not.
     *
     * @return true if RecyclerView is calculating simple animations to be run at the end of
     * the layout pass.
     */
    public boolean willRunSimpleAnimations() {
        return mRunSimpleAnimations;
    }

    /**
     * Removes the mapping from the specified id, if there was any.
     *
     * @param resourceId Id of the resource you want to remove. It is suggested to use R.id.* to
     *                   preserve cross functionality and avoid conflicts.
     */
    public void remove(int resourceId) {
        if (mData == null) {
            return;
        }
        mData.remove(resourceId);
    }

    /**
     * Gets the Object mapped from the specified id, or <code>null</code>
     * if no such data exists.
     *
     * @param resourceId Id of the resource you want to remove. It is suggested to use R.id.*
     *                   to
     *                   preserve cross functionality and avoid conflicts.
     */
    public <T> T get(int resourceId) {
        if (mData == null) {
            return null;
        }
        return (T) mData.get(resourceId);
    }

    /**
     * Adds a mapping from the specified id to the specified value, replacing the previous
     * mapping from the specified key if there was one.
     *
     * @param resourceId Id of the resource you want to add. It is suggested to use R.id.* to
     *                   preserve cross functionality and avoid conflicts.
     * @param data       The data you want to associate with the resourceId.
     */
    public void put(int resourceId, Object data) {
        if (mData == null) {
            mData = new SparseArray<Object>();
        }
        mData.put(resourceId, data);
    }

    /**
     * If scroll is triggered to make a certain item visible, this value will return the
     * adapter index of that item.
     *
     * @return Adapter index of the target item or
     * {@link RecyclerView#NO_POSITION} if there is no target
     * position.
     */
    public int getTargetScrollPosition() {
        return mTargetPosition;
    }

    /**
     * Returns if current scroll has a target position.
     *
     * @return true if scroll is being triggered to make a certain position visible
     * @see #getTargetScrollPosition()
     */
    public boolean hasTargetScrollPosition() {
        return mTargetPosition != RecyclerView.NO_POSITION;
    }

    /**
     * @return true if the structure of the data set has changed since the last call to
     * onLayoutChildren, false otherwise
     */
    public boolean didStructureChange() {
        return mStructureChanged;
    }

    /**
     * Returns the total number of items that can be laid out. Note that this number is not
     * necessarily equal to the number of items in the adapter, so you should always use this
     * number for your position calculations and never access the adapter directly.
     * <p>
     * RecyclerView listens for Adapter's notify events and calculates the effects of adapter
     * data changes on existing Views. These calculations are used to decide which animations
     * should be run.
     * <p>
     * To support predictive animations, RecyclerView may rewrite or reorder Adapter changes to
     * present the correct state to LayoutManager in pre-layout pass.
     * <p>
     * For example, a newly added item is not included in pre-layout item count because
     * pre-layout reflects the contents of the adapter before the item is added. Behind the
     * scenes, RecyclerView offsets {@link DRecyclerView.Recycler#getViewForPosition(int)} calls such that
     * LayoutManager does not know about the new item's existence in pre-layout. The item will
     * be available in second layout pass and will be included in the item count. Similar
     * adjustments are made for moved and removed items as well.
     * <p>
     * You can get the adapter's item count via {@link DRecyclerView.LayoutManager#getItemCount()} method.
     *
     * @return The number of items currently available
     * @see DRecyclerView.LayoutManager#getItemCount()
     */
    public int getItemCount() {
        return mInPreLayout ?
                (mPreviousLayoutItemCount - mDeletedInvisibleItemCountSincePreviousLayout) :
                mItemCount;
    }
}
