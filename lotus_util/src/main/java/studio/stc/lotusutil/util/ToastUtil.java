package studio.stc.lotusutil.util;

import android.content.Context;
import android.widget.Toast;

// 自定義的Toast顯示
public class ToastUtil {
    private static Toast mOnlyToast;
    public static void OnlyToast(Context context, String text){
        //此方法只會顯示一個Toast
        if (mOnlyToast != null && mOnlyToast.getView().isShown()) {
            mOnlyToast.cancel();
        }
        mOnlyToast= Toast.makeText(context,text, Toast.LENGTH_SHORT);
        mOnlyToast.show();
    }

}
