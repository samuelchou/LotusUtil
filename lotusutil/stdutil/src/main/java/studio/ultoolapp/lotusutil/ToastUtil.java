package studio.ultoolapp.lotusutil;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastUtil {
    private static final String TAG = "ToastUtil";
    private static Toast mOnlyToast;

    public static void OnlyToast(Context context, @StringRes int stringId) {
        OnlyToast(context, context.getString(stringId));
    }

    public static void OnlyToast(Context context, String text) {
        //此方法只會顯示一個Toast
        Log.i(TAG, "OnlyToast: \"" + text + "\"");
        if (context == null) {
            Log.e(TAG, "OnlyToast: null context! won't show toast.", new NullPointerException("輸入了空的context!"));
            return;
        }
        if (mOnlyToast != null)
            if (mOnlyToast.getView() != null && mOnlyToast.getView().isShown()) {
                mOnlyToast.cancel();
            }
        mOnlyToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        mOnlyToast.show();
    }
}
