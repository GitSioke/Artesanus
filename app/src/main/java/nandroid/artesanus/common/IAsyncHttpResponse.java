package nandroid.artesanus.common;

/**
 * This interface declare a contract to have  a response from a http asynchronous response
 */

public interface IAsyncHttpResponse
{
    void ProcessFinish(String output);
}
