package com.example.bioauthentication.pattern.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.bioauthentication.R;
import com.example.bioauthentication.pattern.entity.LockPattern;

import java.util.ArrayList;
import java.util.List;


public class PatternView extends ViewGroup {
    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;

    private List<Pair<NodeView, NodeView>> lineList;
    private NodeView currentNode;
    private ArrayList<LockPattern> nodes;

    private StringBuilder pwdSb;
    private CallBack callBack;

    private Drawable nodeSrc;
    private Drawable nodeOnSrc;

    public PatternView(Context context) {
        this(context, null);
    }

    public PatternView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PatternView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(attrs, defStyleAttr);
    }

    private void initFromAttributes(AttributeSet attrs, int defStyleAttr){
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PatternView, defStyleAttr, 0);

        nodeSrc = a.getDrawable(R.styleable.PatternView_nodeSrc);
        nodeOnSrc = a.getDrawable(R.styleable.PatternView_nodeOnSrc);
        int lineColor = Color.argb(0,0,0,0);
        lineColor = a.getColor(R.styleable.PatternView_lineColor, lineColor);
        float lineWidth = 20.0f;
        lineWidth = a.getDimension(R.styleable.PatternView_lineWidth, lineWidth);

        a.recycle();

        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(lineColor);
        paint.setAntiAlias(true);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        bitmap = Bitmap.createBitmap(dm.widthPixels, dm.widthPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);

        for(int n = 0; n < 9; n++) {
            NodeView node = new NodeView(getContext(), n + 1);
            addView(node);
        }
        lineList = new ArrayList<Pair<NodeView,NodeView>>();
        pwdSb = new StringBuilder();

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        if (!changed) {
            return;
        }
        int width = right - left;
        int nodeWidth = width / 3;
        int nodePadding = nodeWidth / 6;
        for (int n = 0; n < 9; n++) {
            NodeView node = (NodeView) getChildAt(n);
            int row = n / 3;
            int col = n % 3;
            int l = col * nodeWidth + nodePadding;
            int t = row * nodeWidth + nodePadding;
            int r = col * nodeWidth + nodeWidth - nodePadding;
            int b = row * nodeWidth + nodeWidth - nodePadding;
            node.layout(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                nodes = new ArrayList<>();
            case MotionEvent.ACTION_MOVE:
                NodeView nodeAt = getNodeAt(event.getX(), event.getY());

                //get behavioral data:
                float coordinateX = event.getX(0);
                float coordinateY = event.getY(0);
                long nanoSeconds = System.currentTimeMillis();
                float pressure = event.getPressure(0);
                float size = event.getSize();
                long downTime = event.getDownTime();
                long eventTime = event.getEventTime();


                /*Log.d("X en", String.valueOf(coordinateX));
                Log.d("Y en", String.valueOf(coordinateY));
                Log.d("NanoTime", String.valueOf(nanoSeconds));
                Log.d("pressure", String.valueOf(pressure));
                Log.d("size", String.valueOf(size));
                Log.d("downTime", String.valueOf(downTime));
                Log.d("eventTime", String.valueOf(eventTime));*/
                //END get behavioral data
                LockPattern current = new LockPattern();
                current.setX(coordinateX);
                current.setY(coordinateY);
                current.setCurrentTime(nanoSeconds);
                current.setPress(pressure);
                current.setArea(size);
                current.setTimeOnSelected(eventTime);
                current.setTimeBegin(downTime);

                if (nodeAt == null && currentNode == null) {
                    nodes.add(current);
                    return true;
                } else {
                    clearScreenAndDrawList();
                    if (currentNode == null) {
                        currentNode = nodeAt;
                        assert currentNode != null;
                        currentNode.setHighLighted(true);
                        current.setDigit(String.valueOf(currentNode.getNum()));
                        pwdSb.append(currentNode.getNum());
                    }
                    else if (nodeAt == null || nodeAt.isHighLighted()) {

                        canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), event.getX(), event.getY(), paint);
                    } else {
                        canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), nodeAt.getCenterX(), nodeAt.getCenterY(), paint);
                        nodeAt.setHighLighted(true);
                        Pair<NodeView, NodeView> pair = new Pair<NodeView, NodeView>(currentNode, nodeAt);
                        lineList.add(pair);
                        currentNode = nodeAt;
                        current.setDigit(String.valueOf(currentNode.getNum()));
                        pwdSb.append(currentNode.getNum());
                    }
                    invalidate();
                }
                nodes.add(current);
                return true;

            case MotionEvent.ACTION_UP:
                if (pwdSb.length() <= 0) {
                    return super.onTouchEvent(event);
                }

                if (callBack != null) {
                    LockPattern last = nodes.get(nodes.size()-1);
                    last.setTimeEnd(event.getEventTime());
                    callBack.onFinish(pwdSb.toString(), nodes);
                    pwdSb.setLength(0);
                }

                currentNode = null;
                lineList.clear();
                clearScreenAndDrawList();

                for (int n = 0; n < getChildCount(); n++) {
                    NodeView node = (NodeView) getChildAt(n);
                    node.setHighLighted(false);
                }

                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void clearScreenAndDrawList() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Pair<NodeView, NodeView> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),  pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
    }

    private NodeView getNodeAt(float x, float y){
        for (int n = 0; n < getChildCount(); n++) {
            NodeView node = (NodeView) getChildAt(n);
            if (!(x >= node.getLeft() && x < node.getRight())) {
                continue;
            }
            if (!(y >= node.getTop() && y < node.getBottom())) {
                continue;
            }
            return node;
        }
        return null;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public class NodeView extends View {

        private int num;
        private boolean highLighted;

        private NodeView(Context context) {
            super(context);
        }

        public NodeView(Context context, int num) {
            this(context);
            this.num = num;
            highLighted = false;
            if (nodeSrc == null) {
                setBackgroundResource(0);
            } else {
                setBackground(nodeSrc);
            }
        }

        public boolean isHighLighted() {
            return highLighted;
        }

        public void setHighLighted(boolean highLighted) {
            this.highLighted = highLighted;
            if (highLighted) {
                if (nodeOnSrc == null) {
                    setBackgroundResource(0);
                } else {
                    setBackground(nodeOnSrc);
                }
            } else {
                if (nodeSrc == null) {
                    setBackgroundResource(0);
                } else {
                    setBackground(nodeSrc);
                }
            }
        }

        public int getCenterX() {
            return (getLeft() + getRight()) / 2;
        }

        public int getCenterY() {
            return (getTop() + getBottom()) / 2;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
    public interface CallBack {
        public void onFinish(String password, ArrayList<LockPattern> nodes);
    }
}
