package com.example.administrator.task;

//import com.example.administrator.task.MessageItem;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewCompat2 extends ListView {

    private static final String TAG = "ListViewCompat";

    private SlideView2 mFocusedItemView;
    public boolean isScroll;

    public ListViewCompat2(Context context) {
        super(context);
    }

    public ListViewCompat2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewCompat2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void shrinkListItem(int position) {
        View item = getChildAt(position);

        if (item != null) {
            try {
                ((SlideView2) item).shrink();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = pointToPosition(x, y);
                Log.e(TAG, "postion=" + position);
                if (position != INVALID_POSITION) {
                    MessageItem2 data = (MessageItem2) getItemAtPosition(position);
                    mFocusedItemView = data.slideView;
                    Log.e(TAG, "FocusedItemView=" + mFocusedItemView);
                }
            }
            default:
                break;
        }

        if (mFocusedItemView != null) {
            isScroll=mFocusedItemView.onRequireTouchEvent(event);
            System.out.println("isScroll?:"+isScroll);
        }

        return super.onTouchEvent(event);
    }

}
