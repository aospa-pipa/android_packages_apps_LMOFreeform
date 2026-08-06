package com.libremobileos.freeform.server.ui;

import static com.libremobileos.freeform.server.util.Debug.dlog;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class FreeformWindowManager {
    private static final HashMap<String, FreeformWindow> freeformWindows = new HashMap<>(1);
    private static final ArrayList<FreeformWindow> attachedWindows = new ArrayList<>();
    private static final String TAG = "FreeformWindowManager";
    private static final int HOST_FLAGS =
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

    private static WindowManager windowManager;
    private static FreeformWindowHost hostView;
    private static WindowManager.LayoutParams hostParams;

    public static void addWindow(
            Handler handler, Context context,
            String packageName, String activityName, int userId, int taskId,
            PendingIntent pendingIntent, int width, int height, int densityDpi) {
        AppConfig appConfig = new AppConfig(packageName, activityName, pendingIntent, userId, taskId);
        FreeformConfig freeformConfig = new FreeformConfig(width, height, densityDpi);
        FreeformWindow window = new FreeformWindow(handler, context, appConfig, freeformConfig);
        dlog(TAG, "addWindow: " + packageName + "/" + activityName + ", freeformId=" + window.getFreeformId()
                + ", existing freeformWindows=" + freeformWindows);
        FreeformWindow oldWindow = freeformWindows.get(window.getFreeformId());
        if (oldWindow != null) {
            oldWindow.close();
            oldWindow.destroy("addWindow", false);
        }
        freeformWindows.put(window.getFreeformId(), window);
    }

    /**
     * @param freeformId packageName,activityName,userId
     */
    public static void removeWindow(String freeformId, Boolean close) {
        FreeformWindow removedWindow = freeformWindows.remove(freeformId);
        if (close && removedWindow != null)
            removedWindow.close();
    }

    public static void removeWindow(String freeformId) {
        removeWindow(freeformId, false /*close*/);
    }

    public static synchronized boolean attachWindowView(FreeformWindow window) {
        try {
            ensureHost(window);
            View windowView = window.getFreeformLayout();
            if (windowView.getParent() != null) {
                ((ViewGroup) windowView.getParent()).removeView(windowView);
            }
            hostView.addView(windowView, createChildLayoutParams(window));
            attachedWindows.add(window);
            hostView.bringChildToFront(windowView);
            updateHostSecureFlag();
            hostView.requestLayout();
            hostView.invalidate();
            return true;
        } catch (RuntimeException exception) {
            dlog(TAG, "attachWindowView failed: " + exception);
            return false;
        }
    }

    public static synchronized void updateWindowLayout(FreeformWindow window) {
        if (hostView == null || window.getFreeformLayout().getParent() != hostView) {
            return;
        }
        window.getFreeformLayout().setLayoutParams(createChildLayoutParams(window));
        hostView.requestLayout();
        hostView.invalidate();
    }

    public static synchronized void bringToFront(FreeformWindow window) {
        if (hostView == null || window.getFreeformLayout().getParent() != hostView) {
            return;
        }
        hostView.bringChildToFront(window.getFreeformLayout());
        hostView.invalidate();
    }

    public static synchronized void updateWindowSecurity(FreeformWindow window) {
        if (hostView == null || window.getFreeformLayout().getParent() != hostView) {
            return;
        }
        updateHostSecureFlag();
    }

    public static synchronized void detachWindowView(FreeformWindow window) {
        if (hostView == null) {
            return;
        }
        hostView.removeView(window.getFreeformLayout());
        attachedWindows.remove(window);
        if (attachedWindows.isEmpty()) {
            windowManager.removeViewImmediate(hostView);
            hostView = null;
            hostParams = null;
            windowManager = null;
            return;
        }
        updateHostSecureFlag();
        hostView.requestLayout();
        hostView.invalidate();
    }

    private static void ensureHost(FreeformWindow window) {
        if (hostView != null) {
            return;
        }
        windowManager = window.getWindowManager();
        hostView = new FreeformWindowHost(window.getContext());
        hostView.setClipChildren(false);
        hostView.setClipToPadding(false);
        hostParams = new WindowManager.LayoutParams();
        hostParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        hostParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        hostParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        hostParams.flags = HOST_FLAGS;
        hostParams.format = PixelFormat.RGBA_8888;
        windowManager.addView(hostView, hostParams);
    }

    private static FrameLayout.LayoutParams createChildLayoutParams(FreeformWindow window) {
        WindowManager.LayoutParams params = window.getWindowParams();
        FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(
                params.width, params.height, Gravity.CENTER);
        childParams.leftMargin = params.x;
        childParams.topMargin = params.y;
        return childParams;
    }

    private static void updateHostSecureFlag() {
        int flags = HOST_FLAGS;
        for (FreeformWindow attachedWindow : attachedWindows) {
            if ((attachedWindow.getWindowParams().flags & WindowManager.LayoutParams.FLAG_SECURE) != 0) {
                flags |= WindowManager.LayoutParams.FLAG_SECURE;
                break;
            }
        }
        if (hostParams.flags != flags) {
            hostParams.flags = flags;
            windowManager.updateViewLayout(hostView, hostParams);
        }
    }

    private static final class FreeformWindowHost extends FrameLayout
            implements ViewTreeObserver.OnComputeInternalInsetsListener {
        FreeformWindowHost(Context context) {
            super(context);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            getViewTreeObserver().addOnComputeInternalInsetsListener(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
            super.onDetachedFromWindow();
        }

        @Override
        public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo inoutInfo) {
            inoutInfo.setTouchableInsets(ViewTreeObserver.InternalInsetsInfo.TOUCHABLE_INSETS_REGION);
            inoutInfo.touchableRegion.setEmpty();
            for (int index = 0; index < getChildCount(); index++) {
                View child = getChildAt(index);
                if (child.getVisibility() == View.VISIBLE) {
                    inoutInfo.touchableRegion.op(
                            child.getLeft(),
                            child.getTop(),
                            child.getRight(),
                            child.getBottom(),
                            android.graphics.Region.Op.UNION);
                }
            }
        }
    }
}
