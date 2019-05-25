package studio.stc.lotusutil.util;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public final class JsonUtil {

    private static final String TAG = "JsonUtil";

    /**
     * 錯誤與提示訊息
     */
    public static class LogText {
        static String FailLoadingJSONVar(String varName) {
            return "載入資料失敗：無法讀取特定JSON資料。請檢視參數命名是否正確(參數名稱：" + varName + ")。";
        }

        static String FailLoadingJSONVar(String varName, String alterValue) {
            return FailLoadingJSONVar(varName) + "使用替代值：\"" + alterValue + "\"";
        }

        static String NullJson() {
            return "載入資料失敗：無法讀取特定JSON資料，因為JSONObject為null。";
        }
    }

    public static JSONObject toJSONObject(Object object){
        try {
            return new JSONObject(object.toString());
        } catch (JSONException e) {
            return null;
        }
    }

    public static Object getValue(JSONObject jsonObject, String tag, Object defaultValue) {
        if (jsonObject == null){
            Log.e(TAG, "getValue: " + LogText.NullJson());
            return defaultValue;
        }
        try {
            return jsonObject.get(tag);
        } catch (JSONException e) {
            Log.e(TAG, "getValue: " + (defaultValue == null ? LogText.FailLoadingJSONVar(tag) : LogText.FailLoadingJSONVar(tag, String.valueOf(defaultValue))));
            return defaultValue;
        }
    }

    // https://www.jianshu.com/p/6e5e5eb2c482
    public static void readMessage(JsonReader jsReader) throws IOException {
        jsReader.beginObject();
        while (jsReader.hasNext()) {
            String tagName = jsReader.nextName();
            if (tagName.equals("id")) {
                System.out.println("name:" + jsReader.nextLong());
            } else if (tagName.equals("text")) {
                System.out.println("text:" + jsReader.nextString());
            } else if (tagName.equals("geo") && jsReader.peek() != JsonToken.NULL) {
                readDoubleArray(jsReader);
            } else if (tagName.equals("user")) {
                readUser(jsReader);
            } else {
                //跳过当前值
                jsReader.skipValue();
                System.out.println("skip======>");
            }
        }
        jsReader.endObject();
    }

    //解析geo中的数据
    public static void readDoubleArray(JsonReader jsReader) throws IOException {
        jsReader.beginArray();
        while (jsReader.hasNext()) {
            System.out.println(jsReader.nextDouble());
        }
        jsReader.endArray();
    }

    //由于读取user中的数据
    public static void readUser(JsonReader jsReader) throws IOException {
        String userName = null;
        int followsCount = -1;
        jsReader.beginObject();
        while (jsReader.hasNext()) {
            String tagName = jsReader.nextName();
            if (tagName.equals("name")) {
                userName = jsReader.nextString();
                System.out.println("user_name:" + userName);
            } else if (tagName.equals("followers_count")) {
                followsCount = jsReader.nextInt();
                System.out.println("followers_count:" + followsCount);
            }
        }
        jsReader.endObject();
    }
}
