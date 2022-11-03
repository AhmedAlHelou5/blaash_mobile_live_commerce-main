package social.gaming.blaash;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.best_flutter_ui_templates.BuildConfig;

public class TokenFormat {

    private final long created_on_timestamp;
    private final String portal_customerId;
    private final String emailId;
    private final String first_name;
    private final String last_name;
    private final String clientId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected TokenFormat(@NonNull String portal_customerId, @NonNull String emailId,
                          @NonNull String first_name, @NonNull String last_name) {

        this.created_on_timestamp = System.currentTimeMillis() / 1000;
        this.portal_customerId = portal_customerId;
        this.emailId = emailId;
        this.first_name = first_name;
        this.last_name = last_name;
        this.clientId = BuildConfig.PropertyPairs.get("CLIENT_ID");
    }
}
