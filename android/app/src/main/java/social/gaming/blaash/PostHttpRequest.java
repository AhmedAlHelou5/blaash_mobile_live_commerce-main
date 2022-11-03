package social.gaming.blaash;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.best_flutter_ui_templates.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostHttpRequest extends AsyncTask<String,Void,String> {
    private final OnHttpPostComplete apiCallComplete;
    private int mode;

    protected PostHttpRequest(OnHttpPostComplete apiCallComplete, int mode) {
        this.apiCallComplete = apiCallComplete;
        this.mode = mode;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (apiCallComplete != null) apiCallComplete.notify(s,mode);
    }

    @Override
    protected String doInBackground(String... params) {
        Request req = new Request.Builder()
                .url(params[0])
                .addHeader("x-tenant-key", Objects.requireNonNull(BuildConfig.PropertyPairs.get("CLIENT_ID")))
                .addHeader("x-api-key", Objects.requireNonNull(BuildConfig.PropertyPairs.get("API_KEY")))
                .post(RequestBody.create(MediaType.parse("application.json;charset=utf-8"),params[1]))
                .build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30,TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = builder.build();

        Call call = okHttpClient.newCall(req);
        Response response;
        try
        {
            response = call.execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
