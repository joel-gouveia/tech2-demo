package tech2.demo.com.demo.helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import androidx.test.espresso.matcher.BoundedMatcher;

import static androidx.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.is;

public class DrawableMatcher {
    public static Matcher<View> withDrawable(final int resourceId) {
        checkNotNull(resourceId);
        return new BoundedMatcher<View, ImageView>(ImageView.class) {

            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(ImageView item) {
                if (!(item instanceof ImageView)){
                    return false;
                }
                ImageView imageView = (ImageView) item;
                if (resourceId < 0){
                    return imageView.getDrawable() == null;
                }
                Resources resources = item.getContext().getResources();
                Drawable expectedDrawable = resources.getDrawable(resourceId);
                if (expectedDrawable == null) {
                    return false;
                }
                Bitmap bitmap = getBitmap (imageView.getDrawable());
                Bitmap otherBitmap = getBitmap(expectedDrawable);
                return bitmap.sameAs(otherBitmap);
            }
        };
    }

    private static Bitmap getBitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
