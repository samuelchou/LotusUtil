package studio.stc.lotusutil.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

public final class ResourceUtil {

    private static final String TAG = "ResourceUtil";

    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }

    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            Log.e(TAG, "uriToBitmap: cannot get bitmap from uri: " + uri.toString(), e);
            return null;
        }
    }

}
