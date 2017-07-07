package nandroid.artesanus.http;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class control HTTP POST request.
 */

public class IsOpenValveGetController extends GetController
{
    public IIsOpenValveAsyncHttpResponse delegate = null;

    public IsOpenValveGetController(IIsOpenValveAsyncHttpResponse delegate)
    {
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.ProcessIsOpenValveResponse(result);
    }

}
