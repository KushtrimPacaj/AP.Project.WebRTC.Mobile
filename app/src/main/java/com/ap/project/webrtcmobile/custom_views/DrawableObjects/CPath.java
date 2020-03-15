package com.ap.project.webrtcmobile.custom_views.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.ap.project.webrtcmobile.custom_views.FabricView;
import com.ap.project.webrtcmobile.events.PathCreatedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by antwan on 10/3/2015.
 * This is a series of continuous lines.
 * Note that the superclass' x, y, width, and height are irrelevant here so they are ignored.
 */
public class CPath extends CDrawable {
    private Path mPath;
    private SerializablePath serializablePath;
    private boolean lineFinished;
    private long timestampOfFinish;


    /**
     * Default constructor.
     */
    public CPath() {
        mPath = new Path();
        serializablePath = new SerializablePath();
    }

    public CPath(ArrayList<SerializablePath.Position> moveTos, ArrayList<SerializablePath.QuadPosition> quadTos, ArrayList<SerializablePath.Position> lineTos) {
        mPath = new Path();
        serializablePath = new SerializablePath(moveTos, quadTos, lineTos);

        for (SerializablePath.Position m : moveTos) {
            mPath.moveTo(m.getX(), m.getY());
        }

        for (SerializablePath.QuadPosition q : quadTos) {
            mPath.quadTo(q.getX1(), q.getY1(), q.getX2(), q.getY2());
        }

        for (SerializablePath.Position l : lineTos) {
            mPath.lineTo(l.getX(), l.getY());
        }

        Paint currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(FabricView.DRAWING_COLOR);
        currentPaint.setStyle(FabricView.PAINT_STYLE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeWidth(FabricView.PAINT_SIZE);
        setPaint(currentPaint);
        markDone(false);
    }


    @Override
    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        Path copy = new Path(mPath);
        copy.transform(matrix);
        canvas.drawPath(copy, getPaint());
    }

    /**
     * Draws a line from the last line ending to the specified position.
     *
     * @param x The horizontal position of the end of the line.
     * @param y The vertical position of the end of the line.
     */
    public void lineTo(float x, float y) {
        Log.d("CPATH", String.format("lineTo( %s , %s )", x, y));
        mPath.lineTo(x, y);
        serializablePath.lineTo(x, y);
        calculatePosition();
    }

    /**
     * Draws a quadratic bezier line from the last line ending to the specified position.
     *
     * @param x1 The x-coordinate of the control point on a quadratic curve
     * @param y1 The y-coordinate of the control point on a quadratic curve
     * @param x2 The x-coordinate of the end point on a quadratic curve
     * @param y2 The y-coordinate of the end point on a quadratic curve
     */
    public void quadTo(float x1, float y1, float x2, float y2) {
        Log.d("CPATH", String.format("quadTo( %s , %s, %s , %s )", x1, y1, x2, y2));
        mPath.quadTo(x1, y1, x2, y2);
        serializablePath.quadTo(x1, y1, x2, y2);
        calculatePosition();
    }

    /**
     * When drawing a line, use this method first to specify the start position.
     *
     * @param x The start position horizontally.
     * @param y The start position vertically.
     */
    public void moveTo(float x, float y) {
        Log.d("CPATH", String.format("moveTo( %s , %s )", x, y));
        mPath.moveTo(x, y);
        serializablePath.moveTo(x, y);
        calculatePosition();
    }

    public void markDone(boolean sendPathToRemote) {
        this.lineFinished = true;
        this.timestampOfFinish = System.currentTimeMillis();
        if (sendPathToRemote) {
            EventBus.getDefault().post(new PathCreatedEvent(serializablePath));
        }
    }

    /**
     * @return The current Path object.
     */
    public Path getPath() {
        return mPath;
    }

    private void calculatePosition() {
        RectF bounds = new RectF();
        mPath.computeBounds(bounds, true);
        setXcoords((int) (bounds.left));
        setYcoords((int) (bounds.top));
        setHeight((int) (bounds.bottom - bounds.top));
        setWidth((int) (bounds.right - bounds.left));

        if (getHeight() == 0) {
            setHeight(1);
        }
        if (getWidth() == 0) {
            setWidth(1);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof CPath)) {
            return false;
        }
        CPath other = (CPath) obj;
        if (other.mPath == null && this.mPath == null) {
            return true;
        }
        return other.mPath == this.mPath;
    }

    public boolean isLineFinished() {
        return lineFinished;
    }

    public long getTimestampOfFinish() {
        return timestampOfFinish;
    }
}

