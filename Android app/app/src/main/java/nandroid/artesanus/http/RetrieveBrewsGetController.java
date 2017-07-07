package nandroid.artesanus.http;

/**
 * This class control HTTP POST request.
 */

public class RetrieveBrewsGetController extends GetController
{
    public IRetrieveBrewsAsyncHttpResponse delegate = null;

    public RetrieveBrewsGetController(IRetrieveBrewsAsyncHttpResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.ProcessRetrieveBrewsResponse(result);
    }

}
