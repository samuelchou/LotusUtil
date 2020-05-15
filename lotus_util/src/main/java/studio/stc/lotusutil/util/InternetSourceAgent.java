package studio.stc.lotusutil.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * InternetSourceAgent by STC
 * contact: stc.ntu@gmail.com
 * 使用Volley進行網路資料擷取的輕量化工具包。
 * 必須安裝Volley。安裝流程： https://developer.android.com/training/volley
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class InternetSourceAgent {
    private static final String TAG = "Internet Source Agent";

    private RequestQueue _RequestQueue;
    private String _RequestFrom; // 用來在取消時作用
    private boolean isCancelled;

    /**
     * 示範程式碼。只能做為撰寫code的參考，請勿直接呼叫。
     *
     * @param context
     */
    private static void Example(Context context) {
        InternetSourceAgent internetSourceAgent = new InternetSourceAgent(context, "PageExample");
        internetSourceAgent.RequestSingleData("https://www.google.com", new ResponseListener<JSONObject>() {
            @Override
            public void OnResponseSuccess(JSONObject object) {
                Log.i(TAG, "OnResponseSuccess: received object: " + object.toString());
            }

            @Override
            public void OnResponseError(VolleyError error) {
                Log.e(TAG, "OnResponseError: failed connecting https://google.com: " + error.networkResponse);
            }
        }, null);
    }

    public InternetSourceAgent(Context context, String requestFrom) {
        isCancelled = false;
        _RequestFrom = requestFrom;
        _RequestQueue = Volley.newRequestQueue(context);
    }

    public static class LogText {
        public static String MessageCannotLoadSpecificJSONVar(String varName) {
            return "載入資料失敗：無法讀取特定資料。請檢視參數命名是否正確(參數名稱：" + varName + ")";
        }

        public static String MessageRequestError(String requestDataName) {
            return "要求" + requestDataName + "失敗！請檢查網路連線或伺服器狀態。";
        }

        public static String MessageRequestDataFailure(String requestDataName) {
            return requestDataName + "載入失敗：資料沒有成功載入。請檢查程式邏輯或伺服器設計。";
        }
    }

    /**
     * 使用Volley與HTTP GET方法獲取單一JSONObject資料。
     *
     * @param targetURL        目標網址。
     * @param responseListener 回應監聽器。
     */
    public void GetSingleData(String targetURL, final ResponseListener<JSONObject> responseListener) {
        GetSingleData(targetURL, responseListener, null);
    }

    /**
     * 使用Volley與HTTP GET方法獲取單一JSONObject資料。
     *
     * @param targetURL        目標網址。
     * @param responseListener 回應監聽器。
     * @param bundledData      附加的資料（可以為null）
     */
    public void GetSingleData(String targetURL, final ResponseListener<JSONObject> responseListener, @Nullable final JSONObject bundledData) {
        String bundledDataString = ".";
        try {
            if (bundledData != null)
                bundledDataString = ", bundled with data\n" + bundledData.toString(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "GetSingleData: start getting data from " + targetURL + bundledDataString);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                targetURL,
                bundledData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseListener.OnResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.OnResponseError(error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = null;
                try {
                    headers = jsonToMap(bundledData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (headers != null) Log.v(TAG, "getHeaders: headers are " + headers.toString());
                return headers;
            }
        };
        _RequestQueue.add(jsonObjectRequest); // 使用queue方式查詢
    }

    /**
     * 使用Volley與HTTP PATCH方法獲取單一JSONObject資料。
     *
     * @param targetURL        目標網址。
     * @param responseListener 回應監聽器。
     * @param bundledData      附加的資料（可以為null）
     */
    public void PatchSingleData(String targetURL, final ResponseListener<JSONObject> responseListener, @Nullable final JSONObject bundledData) {
        String bundledDataString = ".";
        try {
            if (bundledData != null)
                bundledDataString = ", bundled with data\n" + bundledData.toString(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "PatchSingleData: start patching data from " + targetURL + bundledDataString);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                targetURL,
                bundledData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseListener.OnResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.OnResponseError(error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = null;
                try {
                    headers = jsonToMap(bundledData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (headers != null) Log.v(TAG, "getHeaders: headers are " + headers.toString());
                return headers;
            }
        };
        _RequestQueue.add(jsonObjectRequest); // 使用queue方式
    }

    /**
     * 使用Volley獲取單一JSONObject資料。
     *
     * @param targetURL        目標網址。
     * @param responseListener 回應監聽器。
     * @param bundledData      附加的資料。若為null會使用GET方法；否則，使用POST方法。
     */
    public void RequestSingleData(String targetURL, final ResponseListener<JSONObject> responseListener, @Nullable JSONObject bundledData) {
        String bundledDataString = ".";
        try {
            if (bundledData != null)
                bundledDataString = ", bundled with data\n" + bundledData.toString(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "RequestSingleData: start requesting data from " + targetURL + bundledDataString);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                targetURL,
                bundledData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response: " + response.toString());
                        responseListener.OnResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.OnResponseError(error);
                    }
                }
        );
        _RequestQueue.add(jsonObjectRequest); // 使用queue方式查詢
    }

    /**
     * 使用Volley(Get Method)獲取JSONArray資料。
     *
     * @param targetURL        目標網址。
     * @param responseListener 回應監聽器。
     */
    public void RequestArrayData(String targetURL, final ResponseListener<JSONArray> responseListener) {
        RequestArrayData(targetURL, responseListener, null);
    }

    /**
     * 使用Volley獲取JSONArray資料。
     *
     * @param targetURL        目標網址。
     * @param responseListener 回應監聽器。
     * @param bundledData      附加的資料。若為null會使用Get方法；反之，會使用Post方法。
     */
    public void RequestArrayData(String targetURL, final ResponseListener<JSONArray> responseListener, @Nullable JSONArray bundledData) {
        String bundledDataString = ".";
        try {
            if (bundledData != null)
                bundledDataString = ", bundled with data\n" + bundledData.toString(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "RequestArrayData: start requesting data from " + targetURL + bundledDataString);
        JsonArrayRequest jsonArrayRequest;
        jsonArrayRequest = new JsonArrayRequest(
                bundledData == null ? Request.Method.GET : Request.Method.POST,
                targetURL,
                bundledData,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        responseListener.OnResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.OnResponseError(error);
                    }
                }
        );
        _RequestQueue.add(jsonArrayRequest); // 使用queue方式查詢
    }

    // maxWidth / maxHeight: 如果指定圖檔大於此值，則壓縮圖檔。指定成0的話就不會壓縮。
// decodeConfig: 指定圖片的顏色屬性(Bitmap.Config系列)。常用： ARGB_8888(頂規，4 char per pixel) / RGB_565 (2 char per pixel)
    private void RequestImage(String targetURL, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig) {
        Log.i(TAG, "RequestImage: start requesting image from " + targetURL);
        ImageRequest request = new ImageRequest(
                targetURL, listener, maxWidth, maxHeight,
                ImageView.ScaleType.CENTER_INSIDE, decodeConfig, errorListener);
        _RequestQueue.add(request);
    }

    public void RequestImage(String targetURL, final ResponseListener<Bitmap> listener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig) {
        RequestImage(targetURL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        listener.OnResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.OnResponseError(error);
                    }
                },
                maxWidth, maxHeight, decodeConfig
        );
    }

    public void RequestImage(String targetURL, final ResponseListener<Bitmap> listener, int maxWidth, int maxHeight) {
        RequestImage(targetURL, listener, maxWidth, maxHeight, Bitmap.Config.RGB_565);
    }

    public void RequestImage(String targetURL, final ResponseListener<Bitmap> listener) {
        RequestImage(targetURL, listener, 0, 0, Bitmap.Config.RGB_565);
    }

    public void RequestImage(String targetURL, ImageView imageView, @DrawableRes int defaultImage, @DrawableRes int failedImage) {
        ImageLoader imageLoader = new ImageLoader(_RequestQueue, new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, defaultImage, failedImage);
        imageLoader.get(targetURL, listener);
    }

    public void CancelRequest() {
        CancelRequest(_RequestFrom);
    }

    void CancelRequest(String cancelTag) {
        if (!cancelTag.equals("") && _RequestQueue != null) {
            Log.v(TAG, "CancelRequest: start cancelling in-queue request tagged with \"" + cancelTag + "\"");
            _RequestQueue.cancelAll(cancelTag);
            isCancelled = true;
        } else {
            Log.e(TAG, "CancelRequest: tag not assigned, or queue is null");
        }
    }

    public boolean IsCancelled() {
        return isCancelled;
    }

    public interface ResponseListener<T> {
        // 透過覆寫此介面來決定OnResponse時要做些什麼
        void OnResponseSuccess(T object);

        void OnResponseError(VolleyError error);
    }

    /**
     * 來自Vikas Gupta的方法。參見： https://stackoverflow.com/a/24012023/9735961
     *
     * @param json 要轉換的JSONObject。
     * @return 可以做為Header的Map。
     */
    private static HashMap<String, String> jsonToMap(JSONObject json) throws JSONException {
        HashMap<String, String> retMap = new HashMap<>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    private static HashMap<String, String> toMap(JSONObject object) throws JSONException {
        HashMap<String, String> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value.toString());
        }
        return map;
    }

    private static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public class BitmapCache implements ImageLoader.ImageCache {

        //TODO: 研究圖片Cache具體而言如何實現
        //網址: https://blog.csdn.net/guolin_blog/article/details/17482165
        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }


}
