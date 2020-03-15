package com.ap.project.webrtcmobile.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ap.project.webrtcmobile.custom_views.DrawableObjects.CDrawable;
import com.ap.project.webrtcmobile.custom_views.DrawableObjects.CPath;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by antwan on 10/3/2015.
 * A library for creating graphics on an object model on top of canvas.
 * See https://github.com/antwankakki/FabricView
 * <p>
 * Modified by Kushtrim Pacaj on 13/03/2020
 * kushtrimpacaj@gmail.com
 */
public class FabricView extends View {

    // painting objects and properties
    private ArrayList<CDrawable> mDrawableList = new ArrayList<>();

    public static final int DRAWING_COLOR = Color.RED;
    public static final Paint.Style PAINT_STYLE = Paint.Style.STROKE;
    public static final float PAINT_SIZE = 20f;
    @SuppressWarnings("FieldCanBeLocal")
    private static int TIME_IN_MILLIS_TO_KEEP_OLD_DRAWINGS = 2 * 1000;


    // Vars to decrease dirty area and increase performance
    private float lastTouchX, lastTouchY;
    private final RectF dirtyRect = new RectF();

    // keep track of path and paint being in use
    CPath currentPath;
    Paint currentPaint;


    /**
     * Constructor, sets defaut values.
     *
     * @param context the activity that containts the view
     * @param attrs   view attributes
     */
    public FabricView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);

        setFocusable(true);
        setFocusableInTouchMode(true);


    }

    public void addCPath(CPath path) {
        mDrawableList.add(path);
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 1);

        setMeasuredDimension(w, h);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Called when there is the canvas is being re-drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // check if background needs to be redrawn
        Rect totalBounds = new Rect(canvas.getWidth(), canvas.getHeight(), 0, 0);

        // go through each item in the list and draw it
        for (int i = 0; i < mDrawableList.size(); i++) {
            try {
                CDrawable d = mDrawableList.get(i);
                Rect bounds = d.computeBounds();
                totalBounds.union(bounds);
                d.draw(canvas);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    /*********************************************************************************************/
    /*******************************     Handling User Touch    **********************************/
    /*********************************************************************************************/

    /**
     * Handles user touch events.
     *
     * @param event the user's motion event
     * @return true, the event is consumed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouchDrawMode(event);
    }

    private static final float TOUCH_TOLERANCE = 4;

    /**
     * Handles the touch input if the mode is set to draw
     *
     * @param event the touch event
     * @return the result of the action
     */
    public boolean onTouchDrawMode(MotionEvent event) {
        // get location of touch
        float eventX = event.getX();
        float eventY = event.getY();
        if (eventX < 0) {
            eventX = 0;
        }
        if (eventY < 0) {
            eventY = 0;
        }
        if (eventX > getWidth()) {
            eventX = getWidth();
        }
        if (eventY > getHeight()) {
            eventY = getHeight();
        }

        // based on the users action, start drawing
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // create new path and paint
                currentPath = new CPath();
                currentPaint = new Paint();
                currentPaint.setAntiAlias(true);
                currentPaint.setColor(DRAWING_COLOR);
                currentPaint.setStyle(PAINT_STYLE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeWidth(PAINT_SIZE);
                currentPath.setPaint(currentPaint);
                currentPath.moveTo(eventX, eventY);
                // capture touched locations
                lastTouchX = eventX;
                lastTouchY = eventY;
                mDrawableList.add(currentPath);
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(eventX - lastTouchX);
                float dy = Math.abs(eventY - lastTouchY);

                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

                    currentPath.quadTo(lastTouchX, lastTouchY, (eventX + lastTouchX) / 2, (eventY + lastTouchY) / 2);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                }

                dirtyRect.left = Math.min(currentPath.getXcoords(), dirtyRect.left);
                dirtyRect.right = Math.max(currentPath.getXcoords() + currentPath.getWidth(), dirtyRect.right);
                dirtyRect.top = Math.min(currentPath.getYcoords(), dirtyRect.top);
                dirtyRect.bottom = Math.max(currentPath.getYcoords() + currentPath.getHeight(), dirtyRect.bottom);

                // After replaying history, connect the line to the touch point.
                cleanDirtyRegion(eventX, eventY);
                break;
            case MotionEvent.ACTION_UP:
                currentPath.lineTo(eventX, eventY);
                currentPath.markDone(true);
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                return false;
        }

        // Include some padding to ensure nothing is clipped
        invalidate();
        // register most recent touch locations
        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }


    /**
     * Retrieve the region needing to be redrawn
     *
     * @param eventX The current x location of the touch
     * @param eventY the current y location of the touch
     */
    private void cleanDirtyRegion(float eventX, float eventY) {
        // figure out the sides of the dirty region
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }


    public void cleanupOldDrawings() {
        boolean changedStuff = false;
        if (mDrawableList.size() > 0) {
            Iterator<CDrawable> iterator = mDrawableList.iterator();
            while (iterator.hasNext()) {
                CDrawable drawable = iterator.next();
                if (drawable instanceof CPath) {
                    CPath path = ((CPath) drawable);
                    if (path.isLineFinished() && (System.currentTimeMillis() - path.getTimestampOfFinish()) > TIME_IN_MILLIS_TO_KEEP_OLD_DRAWINGS) {
                        iterator.remove();
                        changedStuff = true;
                    }
                }
            }
        }
        if (changedStuff) invalidate();
    }


}
