package rs.com.loctionbased.reminder.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public final class AutoFitGridLayout extends ViewGroup {

    private int verticalSpace;

    private int horizontalSpace;

    private int columnCount = 2;

    private int childWidth;

    private ArrayList<View> notGoneViewList;

    public AutoFitGridLayout(Context context) {
        super(context);
    }

    public AutoFitGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void refreshNotGoneChildList() {
        if (notGoneViewList == null) {
            notGoneViewList = new ArrayList<>();
        }
        notGoneViewList.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                notGoneViewList.add(child);
            }
        }
    }
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        refreshNotGoneChildList();
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        childWidth = (int) ((parentWidth - (columnCount - 1) * horizontalSpace * 1.0f) / columnCount + 0.5f);
        int childCount = notGoneViewList.size();
        int line = childCount % columnCount == 0 ? childCount / columnCount
                : childCount / columnCount + 1;
        int totalHeight = 0;
        int childIndex = 0;
        for (int i = 0; i < line; i++) {
            int inlineHeight = 0;
            for (int j = 0; j < columnCount; j++) {
                childIndex = i * columnCount + j;
                if (childIndex < childCount) {
                    View child = notGoneViewList.get(childIndex);
                    int childWidthWithPadding = childWidth;
                    if (j == 0) {
                        childWidthWithPadding += getPaddingRight();
                    } else if (j == columnCount -1){
                        childWidthWithPadding += getPaddingLeft();
                    }
                    measureChild(child,
                            MeasureSpec.makeMeasureSpec(childWidthWithPadding, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    int totalInlineChildHeight = child.getMeasuredHeight();
                    if (totalInlineChildHeight > inlineHeight) {
                        inlineHeight = totalInlineChildHeight;
                    }
                }
            }
            totalHeight += inlineHeight;
            totalHeight += verticalSpace;
        }
        totalHeight -= verticalSpace;
        totalHeight += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = notGoneViewList.size();
        int line = childCount % columnCount == 0 ? childCount / columnCount
                : childCount / columnCount + 1;
        int childIndex = 0;
        int lastLeft = getPaddingLeft();
        int lastTop = getPaddingTop();
        for (int i = 0; i < line; i++) {
            int inlineHeight = 0;
            for (int j = 0; j < columnCount; j++) {
                childIndex = i * columnCount + j;
                if (childIndex < childCount) {
                    View child = notGoneViewList.get(childIndex);
                    int childWidth = child.getMeasuredWidth();
                    int childHeight = child.getMeasuredHeight();

                    child.layout(lastLeft, lastTop, lastLeft + childWidth, lastTop + childHeight);
                    lastLeft += (childWidth + horizontalSpace);
                    int totalInlineChildHeight = child.getMeasuredHeight();
                    if (totalInlineChildHeight > inlineHeight) {
                        inlineHeight = totalInlineChildHeight;
                    }
                }
            }
            lastLeft = getPaddingLeft();
            lastTop += (inlineHeight + verticalSpace);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }
}