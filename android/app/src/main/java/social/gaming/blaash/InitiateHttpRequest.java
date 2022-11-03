package social.gaming.blaash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class InitiateHttpRequest {
    private final OnHttpPostComplete context;

    protected InitiateHttpRequest(OnHttpPostComplete context) {
        this.context = context;
    }

    private String serialize(Object javaObjectToPost)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(javaObjectToPost);
    }

    void initiatePostRequest(String url,Object javaObjectToPost,int mode)
    {
        PostHttpRequest post = new PostHttpRequest(this.context,mode);
        post.execute(url,serialize(javaObjectToPost));
    }

    void initiateGetRequest(String url)
    {
        GetHttpRequest getHttpRequest = new GetHttpRequest((OnHttpGetComplete) this.context);
        getHttpRequest.execute(url);
    }

    void getViewersCountInfo(String url,Object javaObjectToPost)
    {
        initiatePostRequest(url,javaObjectToPost,3);
    }
}
