package br.com.instapromo.instapromo.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by montanha on 9/22/16.
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth() + (getMeasuredWidth() / 2);
        setMeasuredDimension(width, width);
    }
}
