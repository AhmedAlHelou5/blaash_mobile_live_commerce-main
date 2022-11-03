package social.gaming.blaash;

import android.os.AsyncTask;
import android.util.Log;

import com.example.best_flutter_ui_templates.BuildConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetHttpRequest extends AsyncTask<String,Void,String> {
    private static final String TAG = "GetHttpRequest";
    private OnHttpGetComplete onHttpGetComplete;
    private OkHttpClient httpClient;
    public GetHttpRequest(OnHttpGetComplete onHttpGetComplete) {
        this.onHttpGetComplete = onHttpGetComplete;
    }

    @Override
    protected String doInBackground(String... strings) {
        Request request = new Request.Builder()
                .url(strings[0])
                .addHeader("x-tenant-key", Objects.requireNonNull(BuildConfig.PropertyPairs.get("TENANT_KEY")))
                .addHeader("x-api-key", Objects.requireNonNull(BuildConfig.PropertyPairs.get("API_KEY")))
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS);

        httpClient = builder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected response code " + response);
            return Objects.requireNonNull(response.body()).string();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e(TAG, "onPostExecute: Response = " + s);
        GetChannelBroadcastSettingsResponse response = null;
        try {
            response = new Gson().fromJson(s,GetChannelBroadcastSettingsResponse.class);

        } catch(Exception e)
        {
            Log.e(TAG, "onPostExecute: Cannot deserialize because -> " + e.getMessage());
        }
        onHttpGetComplete.getStreamData(response == null ? null : response.getData());
    }
}
