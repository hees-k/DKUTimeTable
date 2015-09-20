package dk.too.timetable.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import dk.too.timetable.R;

public class AutoFitTextView extends TextView {

    private float mMinTextSize;
    private float mMaxTextSize;

    public AutoFitTextView(Context context) {
        super(context);
        init();
    }

    public AutoFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mMaxTextSize = this.getTextSize();

        int maxTextSize = getResources().getDimensionPixelSize(R.dimen.max_text_size);
        if (mMaxTextSize < maxTextSize) {
            mMaxTextSize = maxTextSize;
        }
        mMinTextSize = getResources().getDimensionPixelSize(R.dimen.min_text_size);
    }

    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            int availableWidth = textWidth - this.getPaddingLeft()
                    - this.getPaddingRight();
            float trySize = mMaxTextSize;

            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
            while ((trySize > mMinTextSize)
                    && (this.getPaint().measureText(text) > availableWidth)) {
                trySize -= 1;
                if (trySize <= mMinTextSize) {
                    trySize = mMinTextSize;
                    break;
                }
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
            }
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
        }
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start,
            final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        refitText(this.getText().toString(), parentWidth);
    }

    public float getMinTextSize() {
        return mMinTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.mMinTextSize = minTextSize;
    }

    public float getMaxTextSize() {
        return mMaxTextSize;
    }

    public void setMaxTextSize(int minTextSize) {
        this.mMaxTextSize = minTextSize;
    }
}