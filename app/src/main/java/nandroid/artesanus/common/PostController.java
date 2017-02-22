package nandroid.artesanus.common;

import android.os.AsyncTask;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Nando on 20/02/2017.
 */

public class PostController extends AsyncTask<String, Long, String>
{
    private String _url = "http://192.168.1.38:5000";

    public final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    protected String doInBackground(String... params) {

        RequestBody body = RequestBody.create(JSON, params[1]);
        Request request = new Request.Builder()
                .url(_url+params[0])
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute())
        {
            String ret = response.body().string();
            return ret;
        }
        catch (HttpRequest.HttpRequestException exception)
        {
            return null;
        }
        catch (IOException ex)
        {
            return null;
        }

    }
}
