package nandroid.artesanus.gui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nandroid.artesanus.common.Brew;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.path;

public class SplashActivity extends AppCompatActivity {

    private String _url = "http://192.168.1.38:5000";
    private String TAG = "SplashActivity";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*LoadFilmTask task = new LoadFilmTask();
                task.execute(_url+"/create");*/

                updateBrew();
            }
        });
    }

    private void updateBrew()
    {
        Brew brew = new Brew();
        brew.setStartDate(Calendar.getInstance().getTime());
        brew.setEndDate(Calendar.getInstance().getTime());

        PostExample example = new PostExample();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = (gson.toJson(brew));
        //String json = example.bowlingJson("Jesse", "Jake");

        example.execute(_url+"/update_brew/1", json);


    }

    private class PostExample extends AsyncTask<String, Long, String> {
        public final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        protected String doInBackground(String... params) {

            RequestBody body = RequestBody.create(JSON, params[1]);
            Request request = new Request.Builder()
                    .url(params[0])
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

        /*String bowlingJson(String player1, String player2) {
            return "{'winCondition':'HIGH_SCORE',"
                    + "'name':'Bowling',"
                    + "'round':4,"
                    + "'lastSaved':1367702411696,"
                    + "'dateStarted':1367702378785,"
                    + "'players':["
                    + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                    + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                    + "]}";
        }*/
    }


    private class LoadFilmTask extends AsyncTask<String, Long, String> {
        protected String doInBackground(String... urls) {
            try {
                HttpRequest request = HttpRequest.get(urls[0]);
                if (request.ok()) {
                    return request.body();
                }

            } catch (HttpRequest.HttpRequestException exception)
            {
                return null;
            }

            return null;
        }
    }

    protected void onPostExecute(String response) {
        Log.i(TAG, response);
    }

    public void testing()
    {
        Snackbar.make(findViewById(R.id.ly_splash), "TESTING BUTTON", Snackbar.LENGTH_LONG).show();
    }

}
