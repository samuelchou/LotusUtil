package studio.ultoolapp.lotusutil;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class IOUtil {
    /**
     * 收起鍵盤。(如果有的話)
     *
     * @param context 當下所在的Context.
     * @param view    當下所在的view.
     */
    public static void hideKeyboardFrom(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 獲取程式碼的操作路徑。
     *
     * @return 操作路徑。
     */
    public static String GetPathString() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < stackTraces.length; i++) {
            builder.append(stackTraces[i]).append("\n");
        }
        return builder.toString();
    }

    /**
     * 獲取程式碼的操作路徑。
     *
     * @param maxLine 最大行數。
     * @return 操作路徑。
     */
    public static String GetPathString(int maxLine) {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < stackTraces.length && i < maxLine; i++) {
            builder.append(stackTraces[i]).append("\n");
        }
        if (stackTraces.length > maxLine)
            builder.append("(collapse ").append(stackTraces.length - maxLine).append(" lines)");
        return builder.toString();
    }

    /**
     * 判定是否為email.
     *
     * @param sequence 檢查的字串。
     * @return 是否為email.
     */
    public static boolean isEmail(CharSequence sequence) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(sequence).matches();
    }

    /**
     * 複製文字，讓使用者得以(在任何文字輸入處)貼上內容。
     */
    public static void CopyText(Context context, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", textToCopy);
        Objects.requireNonNull(clipboard).setPrimaryClip(clip);
    }
}
