package social.gaming.blaash;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.best_flutter_ui_templates.BuildConfig;

import java.util.Map;
import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "flutter/goLive";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("goLive")) {
                                Map<String, String> params = call.arguments();
                                launchGoLiveActivity(params.get("customerId"), params.get("firstName"));
                            } else if (call.method.equals("returnUrl")) {
                                Map<String, String> params = call.arguments();
                                System.out.println(params);
                                assert params != null;
                                launchApp(params.get("portal_customerId"), params.get("first_name"),
                                        params.get("last_name"), params.get("emailId"));
                            } else if (call.method.equals("configTenant")) {
                                result.success(BuildConfig.PropertyPairs.get("TENANT_KEY"));
                            } else if (call.method.equals("configApiKey")) {
                                result.success(BuildConfig.PropertyPairs.get("API_KEY"));
                            } else if (call.method.equals("launchSite")) {
                                Map<String, Object> params = call.arguments();
//                                System.out.println(params.get("token"));

                            }
                        }
                );
    }

    private void launchGoLiveActivity(String customerId, String customerName) {
        Intent i = new Intent(MainActivity.this, GoLiveActivity.class);
        i.putExtra("customerId", customerId);
        i.putExtra("customerName", customerName);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void launchApp(String portal_customerId, String first_name, String last_name, String emailId) {
        TokenFormat format = new TokenFormat(portal_customerId, emailId, first_name, last_name);
        TokenGeneration generateToken = new TokenGeneration(format);
        try {
            String token = generateToken.getTokenToSend();
//            Log.e("MainActivity", "returnToken: token = " + token);
            Intent intent = new Intent(MainActivity.this, AdvancedWebViewActivity.class);
            intent.putExtra("customerId",portal_customerId);
            intent.putExtra("customerName",first_name);
            intent.putExtra("token", Objects.requireNonNull(token));
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }
}
