package nandroid.artesanus.http;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class control HTTP POST request.
 */

public class PutController extends HTTPController
{
    public IAsyncHttpResponse delegate = null;

    public PutController(IAsyncHttpResponse delegate){
        this.delegate = delegate;
    }
    @Override
    protected String doInBackground(String... params) {

        RequestBody body = RequestBody.create(JSON, params[1]);
        Request request = new Request.Builder()
                .url(_url+params[0])
                .put(body)
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

    @Override
    protected void onPostExecute(String result) {
        delegate.ProcessFinish(result);
    }

}
