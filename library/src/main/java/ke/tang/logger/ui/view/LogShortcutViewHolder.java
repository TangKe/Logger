package ke.tang.logger.ui.view;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import ke.tang.logger.LoggerService;
import ke.tang.logger.R;
import ke.tang.logger.util.HandlerUtils;

/**
 * @author tangke
 */

public class LogShortcutViewHolder {
    private DragableOverlayLayout mOverlay;
    private ProgressBar mProgress;
    private ImageButton mRecord;
    private View mShortcut;
    private int mStatus;

    public LogShortcutViewHolder(Activity activity) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        mOverlay = (DragableOverlayLayout) activity.getLayoutInflater().inflate(R.layout.logger_layout_shortcut, decor, false);
        mShortcut = mOverlay.findViewById(R.id.shortcut);
        mProgress = mOverlay.findViewById(R.id.progress);
        mRecord = mOverlay.findViewById(R.id.record);

        decor.addView(mOverlay);
    }

    public void setStatus(int status) {
        mStatus = status;
        updateStatus();
    }

    public void show() {
        mOverlay.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mOverlay.setVisibility(View.INVISIBLE);
    }

    public void getShortcutCoordinator(Point outPoint) {
        final AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mShortcut.getLayoutParams();
        outPoint.set(layoutParams.x, layoutParams.y);
    }

    private void updateStatus() {
        if (mStatus == LoggerService.STATUS_TRACING) {
            mProgress.setVisibility(View.VISIBLE);
            mRecord.setVisibility(View.GONE);
        } else {
            mProgress.setVisibility(View.GONE);
            mRecord.setVisibility(View.VISIBLE);
        }
    }

    public void updateShortcutCoordinator(final Point inPoint) {
        HandlerUtils.sMainHandler.post(new Runnable() {
            @Override
            public void run() {
                final AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mShortcut.getLayoutParams();
                layoutParams.x = inPoint.x;
                layoutParams.y = inPoint.y;
                mShortcut.setLayoutParams(layoutParams);
            }
        });
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mShortcut.setOnClickListener(listener);
    }
}
