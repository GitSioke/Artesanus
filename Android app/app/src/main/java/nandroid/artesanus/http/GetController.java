package nandroid.artesanus.http;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class control HTTP POST request.
 */

public class GetController extends HTTPController
{
    public IAsyncHttpResponse delegate = null;

    public IIsOpenValveAsyncHttpResponse _isOpenValveDelegate = null;

    public GetController(IAsyncHttpResponse delegate){
        this.delegate = delegate;
    }

    public GetController(){};

    @Override
    protected String doInBackground(String... params) {

       Request request = new Request.Builder()
                .url(_url+params[0])
                .build();
        try (Response response = client.newCall(request).execute())
        {
            String ret = response.body().string();
            return ret;
        }
        catch (HttpRequest.HttpRequestException exception)
        {
            return "";
        }
        catch (IOException ex)
        {
            return "";
        }
        catch(Exception e)
        {
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.ProcessFinish(result);
    }

}
