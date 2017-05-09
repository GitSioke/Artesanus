package nandroid.artesanus.http;

import android.os.AsyncTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * This super class represents the HTTP methods.
 */

public class HTTPController extends AsyncTask<String, Long, String>
{
    protected String _url = "http://192.168.1.40:5000";

    protected final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected OkHttpClient client = new OkHttpClient();

    protected String doInBackground(String... params)
    {
        return "";
    }

    public void setIP(String ip)
    {
        _url = "http://"+ip+":5000";
    }

}
