package social.gaming.blaash;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.best_flutter_ui_templates.BuildConfig;
import com.example.best_flutter_ui_templates.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;

public class AdvancedWebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    private static final String TAG = "AdvancedWebViewActivity";
    AdvancedWebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Bundle bundle = getIntent().getExtras();
        String token = bundle.getString("token");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        @SuppressLint("InflateParams") View customView = getLayoutInflater().inflate(R.layout.action_bar,null);
        actionBar.setCustomView(customView);
        actionBar.setBackgroundDrawable(AppCompatResources.getDrawable(this,R.drawable.action_bar_bg));

        ImageView backBtn = findViewById(R.id.backButton);
        backBtn.setVisibility(View.GONE);

        webView = findViewById(R.id.advancedWebView);
        FloatingActionButton launchLive = findViewById(R.id.launchLive);

        launchLive.setOnClickListener(v ->
        {
            Intent i = new Intent(AdvancedWebViewActivity.this, GoLiveActivity.class);
            i.putExtra("customerId", bundle.getString("customerId"));
            i.putExtra("customerName", bundle.getString("customerName"));
            startActivity(i);
        });

        webView.setListener(this, this);

        String url = BuildConfig.PropertyPairs.get("UI_URL") + "/?token=" + token + "&pid=78342";
//        Log.e(TAG, "onCreate: url = " + url);
        webView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.canGoBack()) {webView.goBack();}
                else {finish();}
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        webView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
//        Log.e(TAG, "onPageStarted: Site launched");
    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}