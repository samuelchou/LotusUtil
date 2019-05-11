package studio.stc.lotusutil.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.HashMap;

public final class MediaUtil {

    private final static String TAG = "MediaUtil";

    /**
     * 獲取Media時間長度。(使用Retriever)
     *
     * @param metadata 目標的MediaMetadata。
     * @return 時間長度(以毫秒顯示)。
     */
    public static long GetMediaTimeInMS(Context context, MediaMetadataCompat metadata) {
        return (metadata.getDescription() != null
                ? GetMediaTimeInMS(context, metadata.getDescription().getMediaUri())
                : 0);
    }

    /**
     * 獲取Media時間長度。(使用Retriever)
     *
     * @param context  上下文，用來設定MediaMetadataRetriever。
     * @param mediaUri 目標的MediaMetadata。
     * @return 時間長度(以毫秒顯示)。
     */
    public static long GetMediaTimeInMS(Context context, Uri mediaUri) {
        //https://stackoverflow.com/questions/3936396/how-to-get-duration-of-a-video-file
        if (mediaUri == null) {
            Log.e(TAG, "GetMediaTimeInMS called from " + context.getClass().getSimpleName() + ": 獲取Uri對象為null。無法計算時間長度。");
            return 0;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(context, mediaUri); //use one of overloaded setDataSource() functions to set your data source
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "GetMediaTimeInMS: 無法使用setDataSource(context, uri)於uri=\"" + mediaUri + "\"的場合。改使用setDataSource(string)");
            try {
                retriever.setDataSource(mediaUri.getPath(), new HashMap<String, String>());
            } catch (Exception ec) {
                Log.e(TAG, "GetMediaTimeInMS: 無法使用setDataSource(string)於\"" + mediaUri + "\"的場合。回傳長度0", ec);
                return 0;
            }
        }
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release();
        return Long.parseLong(time);
    }

    /**
     * 獲取Media時間長度。
     *
     * @param metadata 目標的MediaMetadata。
     * @return 時間長度(以毫秒顯示)。
     * @deprecated 必須要求metadata自行放入DURATION。出錯率高，不建議使用。建議改用 {@link #GetMediaTimeInMS(Context, MediaMetadataCompat)}.
     */
    @Deprecated
    public static long GetMediaTimeInMS(MediaMetadataCompat metadata) {
        return (metadata != null
                ? metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                : 0);
    }

    public static MediaMetadataCompat bundleToMetadata(Bundle bundle) {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, bundle.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, bundle.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, bundle.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        return builder.build();
    }

    public static Bundle mediaDescriptionToBundle(MediaDescriptionCompat descriptionCompat) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, descriptionCompat.getMediaId());
        bundle.putCharSequence(MediaMetadataCompat.METADATA_KEY_TITLE, descriptionCompat.getTitle());
        bundle.putCharSequence(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, descriptionCompat.getMediaUri() != null ? descriptionCompat.getMediaUri().toString() : null);
        bundle.putCharSequence(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, descriptionCompat.getIconUri() != null ? descriptionCompat.getIconUri().toString() : null);
        bundle.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, descriptionCompat.getExtras() != null ? descriptionCompat.getExtras().getLong(MediaMetadataCompat.METADATA_KEY_DURATION) : 0);
        return bundle;
    }

    public static MediaMetadataCompat mediaItemToMediaMetadata(MediaBrowserCompat.MediaItem mediaItem) {
        return bundleToMetadata(mediaDescriptionToBundle(mediaItem.getDescription()));
    }
}
