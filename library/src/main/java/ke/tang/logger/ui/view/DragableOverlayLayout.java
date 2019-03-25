package ke.tang.logger.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

/**
 * @author tangke
 */

public class DragableOverlayLayout extends AbsoluteLayout {

    private ViewDragHelper mDragHelper;
    private View mReleasedView;

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //支持所有View拖动
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getHeight();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.max(0, Math.min(left, getWidth() - child.getWidth()));
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return Math.max(0, Math.min(top, getHeight() - child.getHeight()));
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mReleasedView = releasedChild;
            final int left = releasedChild.getLeft();
            final int top = releasedChild.getTop();
            final int width = getWidth();
            final int height = getHeight();
            final boolean moveHorizontal = Math.min(left, width - left) < Math.min(top, height - top);
            mDragHelper.settleCapturedViewAt(moveHorizontal ? left < width / 2 ? 0 : width - releasedChild.getWidth() : left,
                    moveHorizontal ? top : top < height / 2 ? 0 : height - releasedChild.getHeight());
            ViewCompat.postInvalidateOnAnimation(DragableOverlayLayout.this);
        }
    };

    public DragableOverlayLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragableOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragableOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else if (null != mReleasedView) {
            final AbsoluteLayout.LayoutParams layoutParams = (LayoutParams) mReleasedView.getLayoutParams();
            final int top = mReleasedView.getTop();
            final int left = mReleasedView.getLeft();
            layoutParams.y = top;
            layoutParams.x = left;
            mReleasedView.setLayoutParams(layoutParams);
            mReleasedView = null;
        }
    }
}
