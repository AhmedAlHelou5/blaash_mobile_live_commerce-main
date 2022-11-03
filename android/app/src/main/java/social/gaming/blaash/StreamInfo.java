package social.gaming.blaash;

import android.widget.TextView;

import org.json.JSONObject;

public class StreamInfo {
    private String ChannelArn;
    private String Health;
    private String PlaybackUrl;
    private String State;
    private String StreamId;
    private String ViewerCount;

    public String getChannelArn() {
        return ChannelArn;
    }

    public String getHealth() {
        return Health;
    }

    public String getPlaybackUrl() {
        return PlaybackUrl;
    }

    public String getState() {
        return State;
    }

    public String getStreamId() {
        return StreamId;
    }

    public String getViewerCount() {
        return ViewerCount;
    }
}
