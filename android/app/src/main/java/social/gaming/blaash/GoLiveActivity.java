package social.gaming.blaash;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amazonaws.ivs.broadcast.BroadcastException;
import com.amazonaws.ivs.broadcast.BroadcastSession;
import com.amazonaws.ivs.broadcast.Device;
import com.amazonaws.ivs.broadcast.ImageDevice;
import com.amazonaws.ivs.broadcast.ImagePreviewView;
import com.amazonaws.ivs.broadcast.Presets;
import com.example.best_flutter_ui_templates.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class GoLiveActivity extends AppCompatActivity implements OnHttpPostComplete, OnHttpGetComplete {
    private static final String TAG = "GoLiveActivity";
    EditText searchKey;
    ImageView search;
    TextView productName;
    TextView errorMsg;
    ImageView productImage;
    ConstraintLayout productView;
    ConstraintLayout searchView;
    ImageButton goLive;
    private ProductDetailsToPost productDetailsToPost;
    private LinearLayout previewHolder;
    private BroadcastSession broadcastSession;
    boolean isLive = false;
    static boolean didSearchSucceed = false;
    ConstraintLayout stopLayout;
    Button stopStream;
    TextView viewCount;
    RadioGroup filter;
    private long productId;
    ProgressBar progressBarProductView;
    ProgressBar progressBarLive;
    ImageView backButton;
    private String arn;
    private ChannelBroadcastSettings streamCredentials;
    public static boolean connected = false;
    private Timer timer;
    public static String customerId;
    public static String customerName;
    ConstraintLayout loadingChannel;
    TextView statusMessage;
    ProgressBar statusProgressBar;
    private boolean channelConfigured = false;
    String[] requiredPermissions;
    InitiateHttpRequest initiateHttpRequest;
    private boolean kickStartLive = false;

    @Override
    protected void onPause() {
        super.onPause();
        broadcastSession.stop();
        shutTimer();
        notifyBroadcastStatus(2);
        layoutAfterStoppingBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastSession.release();
        shutTimer();
        notifyBroadcastStatus(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_live);

        Bundle bundle = getIntent().getExtras();
        customerId = bundle.getString("customerId");
        customerName = bundle.getString("customerName");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        @SuppressLint("InflateParams") View customView = getLayoutInflater().inflate(R.layout.action_bar, null);
        actionBar.setCustomView(customView);
        actionBar.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.action_bar_bg));

        searchKey = findViewById(R.id.searchProduct);
        search = findViewById(R.id.searchButton);
        productName = findViewById(R.id.productName);
        productImage = findViewById(R.id.productImage);
        productView = findViewById(R.id.productView);
        searchView = findViewById(R.id.searchView);
        goLive = findViewById(R.id.goLive);
        filter = findViewById(R.id.filter);
        stopLayout = findViewById(R.id.stopLayout);
        stopStream = findViewById(R.id.stopStream);
        viewCount = findViewById(R.id.viewCountTextView);
        progressBarProductView = findViewById(R.id.progressBarProductView);
        progressBarLive = findViewById(R.id.progressBarLive);
        errorMsg = findViewById(R.id.errorMsg);
        backButton = findViewById(R.id.backButton);
        loadingChannel = findViewById(R.id.loadingChannel);
        previewHolder = findViewById(R.id.previewHolder);
        statusMessage = findViewById(R.id.statusMessage);
        statusProgressBar = findViewById(R.id.statusProgressBar);
        arn = "";
        initiateHttpRequest = new InitiateHttpRequest(this);

        productView.setVisibility(GONE);
        goLive.setVisibility(GONE);
        stopLayout.setVisibility(GONE);


        requiredPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GoLiveActivity.this, "Grant all the permissions to go live", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, requiredPermissions, 0x100);
                break;
            }
        }

        BroadcastSession.Listener broadcastListener = new BroadcastSession.Listener() {
            @Override
            public void onStateChanged(@NonNull BroadcastSession.State state) {
//                Log.d(TAG, "onStateChanged: State = " + state);

                GoLiveActivity.connected = state == BroadcastSession.State.CONNECTED;
                if (state == BroadcastSession.State.CONNECTED) {
//                    Log.e(TAG, "onStateChanged: Channel connected");
                    animate(stopLayout, R.anim.slide_up, View.VISIBLE);
                    notifyBroadcastStatus(1);
                } else if (state == BroadcastSession.State.DISCONNECTED) {
//                    Log.e(TAG, "onStateChanged: Channel disconnected");
                    notifyBroadcastStatus(2);
                } else if (state == BroadcastSession.State.ERROR) {
//                    Log.e(TAG, "onStateChanged: Cannot start stream!!");
                    errorGettingStreamCredentials();
                }
            }

            @Override
            public void onError(@NonNull BroadcastException e) {
//                Log.d(TAG, "onError: Error = " + e);
                GoLiveActivity.connected = false;
                notifyBroadcastStatus(2);
                animate(stopLayout, R.anim.slide_up, GONE);
            }
        };

        Context ctx = null;
        try {
            ctx = getApplicationContext();
        } catch(Exception e)
        {
            Log.e(TAG, "onCreate: Previous stream has not yet closed or not closed properly");
        }

        try {
            if (ctx != null) {
                broadcastSession = new BroadcastSession(ctx,
                        broadcastListener,
                        Presets.Configuration.STANDARD_PORTRAIT,
                        Presets.Devices.FRONT_CAMERA(ctx));
                startLivePreview();
            }

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Unable to initiate aws for live stream");
        }

        search.setOnClickListener(v ->
        {
            productDetailsToPost = new ProductDetailsToPost();
            layoutDuringSearch();
            String key = searchKey.getText().toString();
            if (!key.isEmpty()) {
                progressBarProductView.setVisibility(View.VISIBLE);
                if (didSearchSucceed) productView.setVisibility(View.VISIBLE);
                else animate(productView, R.anim.slide_up, View.VISIBLE);

                RadioButton option = findViewById(filter.getCheckedRadioButtonId());
                if (findViewById(R.id.sku).equals(option)) {
                    productDetailsToPost.setSku(key);
                } else if (findViewById(R.id.id).equals(option)) {
                    productDetailsToPost.setPortalProductId(key);
                } else if (findViewById(R.id.url).equals(option)) {
                    productDetailsToPost.setURL(key);
                } else {
                    productDetailsToPost.setPortalProductId(key);
                }
                Log.e(TAG, "onCreate: Initiating product search");

                initiateHttpRequest.initiatePostRequest(getString(R.string.getProductDetailsApi), productDetailsToPost, 1);
            } else {
                animate(goLive, R.anim.go_live_animation, GONE);
                animate(productView, R.anim.slide_up, GONE);
                Toast.makeText(this, "Enter product details to search", Toast.LENGTH_SHORT).show();
            }
        });

        Context finalCtx = ctx;
        goLive.setOnClickListener(v ->
        {
            if (!checkPermissions()) {
                Toast.makeText(finalCtx, "Camera & Audio Permissions not yet applied, please relaunch the page.", Toast.LENGTH_SHORT).show();
                return;
            }
            getChannelCredentials();
        });

        stopStream.setOnClickListener(v ->
        {
            shutTimer();
            try {
                broadcastSession.stop();
                layoutAfterStoppingBroadcast();
            } catch (Exception e) {
                Log.e(TAG, "onCreate: Unable to stop the live stream");
            }
        });
        backButton.setOnClickListener(v -> finish());
    }

    private void animate(final View view, int animationResource, int finalVisibility) {
        Animation animation = AnimationUtils.loadAnimation(this, animationResource);
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(finalVisibility);
            }
        });
        view.startAnimation(animation);
    }

    @Override
    public void notify(String s, int mode) {
//        Log.e(TAG, "notify: Response = " + s);
        try {
            switch (mode) {
                case 1:
//                Log.e(TAG, "notify: response in GoLiveActivity = " + s);
                    progressBarProductView.setVisibility(GONE);
                    if (s != null) {
                        ProductDetailsFromApi details = new Gson().fromJson(s, ProductDetailsFromApi.class);
                        if (details.getData() != null && details.getData().size() != 0) {
                            updateProductInfo(details);
                            onGettingProductDetails();
                        } else
                            onNotGettingProductDetails();
                    } else
                        onNotGettingProductDetails();
                    break;
                case 2:
//                    Log.e(TAG, "notify: updated status, " + s);
                    break;
                case 3:
                    if (s != null) {
                        StreamInfoFromApi streamInfoFromApi = new Gson().fromJson(s, StreamInfoFromApi.class);
                        viewCount.setText(streamInfoFromApi.getData().getViewerCount());
                    }
                    Log.e(TAG, "notify: updating viewCount");
                    break;
            }
        } catch(Exception ignore){
            Log.e(TAG, "notify: error");
        }
    }

    @Override
    public void getStreamData(ChannelBroadcastSettings data) {
        if (data != null) {
            try {
                loadingChannel.setVisibility(GONE);
                Log.e(TAG, "getStreamData: Channel details : " + data);
                streamCredentials = data;
                isLive = true;
                channelConfigured = true;
                startBroadcast(data);
            } catch (Exception e) {
                errorGettingStreamCredentials();
            }
        } else errorGettingStreamCredentials();
    }

    private void notifyBroadcastStatus(int status) {
        ChannelStatusJson channelStatusJson;
        switch (status) {
            case 1:
                channelStatusJson = new ChannelStatusJson(GoLiveActivity.customerId, GoLiveActivity.customerName, arn, "1", productId);
                updateViewersCount();
                break;
            case 2:
                channelStatusJson = new ChannelStatusJson(GoLiveActivity.customerId, GoLiveActivity.customerName, arn, "2", productId);
                shutTimer();
                break;
            default:
                channelStatusJson = null;
        }
        initiateHttpRequest.initiatePostRequest(getString(R.string.updateChannelStatusApi), channelStatusJson, 2);
    }

    private void updateViewersCount() {
        ChannelArn arnObj = new ChannelArn(this.arn);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "run: Fetching viewers count");
                initiateHttpRequest.getViewersCountInfo(getString(R.string.viewCountApi), arnObj);
            }
        }, 1000, 10000);
    }

    private void startLivePreview() {
//        previewHolder.removeAllViews();
        broadcastSession.awaitDeviceChanges(() -> {
            for (Device device : broadcastSession.listAttachedDevices()) {
                if (device.getDescriptor().type == Device.Descriptor.DeviceType.CAMERA) {
                    ImagePreviewView preview = ((ImageDevice) device).getPreviewView();
                    preview.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    previewHolder.addView(preview);
                }
            }
        });
    }

    private void startBroadcast(ChannelBroadcastSettings data)
    {
        if (channelConfigured) {
            layoutDuringLive();
            try {
                arn = data.getChannelArn();
                broadcastSession.start(data.getIngestEndpoint(), data.getStreamKey());

            } catch (Exception e) {
                Log.e(TAG, "onCreate: Cannot start broadcast");
                animate(goLive, R.anim.slide_down, GONE);
            }
        }
    }

    private void onGettingProductDetails() {
        errorMsg.setVisibility(GONE);
        productImage.setVisibility(View.VISIBLE);
        productName.setVisibility(View.VISIBLE);

        if (didSearchSucceed) goLive.setVisibility(View.VISIBLE);
        else {
            didSearchSucceed = true;
            animate(goLive, R.anim.go_live_animation, View.VISIBLE);
        }
    }

    private void onNotGettingProductDetails() {
        didSearchSucceed = false;
        animate(goLive, R.anim.go_live_animation, GONE);
        errorMsg.setVisibility(View.VISIBLE);
    }

    private void updateProductInfo(ProductDetailsFromApi details) {
        Product product = details.getData().get(0);
        productId = product.getProductId();
        productName.setText(product.getProductdisplayName());
        Picasso.get()
                .load(product.getBaseImageURL())
                .into(productImage);
    }

    private void errorGettingStreamCredentials() {
        statusMessage.setText(getString(R.string.channelErrorMessage));
        animate(productView, R.anim.slide_up, VISIBLE);
        animate(searchView, R.anim.slide_down, View.VISIBLE);
        statusProgressBar.setVisibility(GONE);
        animate(goLive, R.anim.slide_up, View.VISIBLE);
        animate(previewHolder,R.anim.slide_up,GONE);
        animate(loadingChannel,R.anim.slide_up,VISIBLE);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void layoutDuringSearch() {
        productImage.setVisibility(GONE);
        productName.setVisibility(GONE);
        errorMsg.setVisibility(GONE);
    }

    private void layoutDuringLive() {
        previewHolder.setVisibility(View.VISIBLE);
        animate(productView, R.anim.slide_up, GONE);
        animate(searchView, R.anim.slide_up, GONE);
//        animate(stopLayout, R.anim.slide_up, View.VISIBLE);
        animate(goLive, R.anim.slide_down, GONE);
    }

    private void layoutAfterStoppingBroadcast() {
        if (didSearchSucceed) animate(productView, R.anim.slide_down, View.VISIBLE);
        animate(searchView, R.anim.slide_down, View.VISIBLE);
        animate(goLive, R.anim.go_live_animation, View.VISIBLE);
        animate(stopLayout, R.anim.slide_up, GONE);
    }

    private void getChannelCredentials()
    {
        previewHolder.setVisibility(GONE);
        statusMessage.setVisibility(View.VISIBLE);
        statusProgressBar.setVisibility(View.VISIBLE);
        try {
            initiateHttpRequest.initiateGetRequest(getString(R.string.getStreamCredsApi));
        } catch (Exception ignore) {
            errorGettingStreamCredentials();
            Log.e(TAG, "onCreate: Unable to go live");
        }
    }

    private void shutTimer() {
        try {
            timer.cancel();
            timer.purge();
        } catch (NullPointerException ignore) {
        }
    }
}