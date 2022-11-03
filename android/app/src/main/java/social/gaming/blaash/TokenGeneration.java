package social.gaming.blaash;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.best_flutter_ui_templates.BuildConfig;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Random;


public class TokenGeneration {
    private final TokenFormat token;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected TokenGeneration(TokenFormat token) {
        this.token = token;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTokenToSend() throws UnsupportedEncodingException {
        Gson gson = new Gson();
        String plainToken = gson.toJson(token);

        Log.e("TAG", "getTokenToSend: json = " + plainToken);

        String[] characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        Random random = new Random();
        int charLen = characters.length;
        StringBuilder alphaNumeric = new StringBuilder();
        for (int i = 0; i < 180; i++) {
            alphaNumeric.append(characters[random.nextInt(charLen)]);
        }
        Crypto crypto = new Crypto(BuildConfig.PropertyPairs.get("CLIENT_SECRET"));
        String a = crypto.encryptAsBase64(plainToken.getBytes());
        return alphaNumeric + BuildConfig.PropertyPairs.get("CLIENT_ID") + a;
    }
}
