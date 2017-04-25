package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.Date;

import nandroid.artesanus.common.Event;
import nandroid.artesanus.http.GetController;
import nandroid.artesanus.http.HTTPController;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.http.PostController;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.http.PutController;

/**
 * This class handles the Monitor tab fragment for Fermentation process
 */
public class MonitorFermentationTabFragment extends Fragment implements IAsyncHttpResponse
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate( R.layout.fragment_monitoring_fermenter, container, false);
        final ImageView imgDensityButton = (ImageView)view.findViewById(R.id.monitor_add_density_icon);
        final EditText densityEditText = (EditText)view.findViewById(R.id.monitor_density_edit_text);
        final ImageView imgConfirmButton = (ImageView)view.findViewById(R.id.monitor_add_density_confirm);
        final TextView txSecondValue = (TextView)view.findViewById(R.id.monitor_second_value);

        imgConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide and show the views
                densityEditText.setVisibility(View.INVISIBLE);
                imgConfirmButton.setVisibility(View.INVISIBLE);
                imgDensityButton.setVisibility(View.VISIBLE);
                txSecondValue.setVisibility(View.VISIBLE);

                // Add density to database
                GsonBuilder builder = new GsonBuilder();
                builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                Gson gson = builder.create();
                Event event = new Event();

                //event.setId_process();
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                event.setTime(date);
                event.setSource("fermentation");

                // TODO modificar para que el id_process sea el correcto
                event.setId_process(1);
                
                event.setValue(Double.parseDouble(densityEditText.getText().toString()));
                event.setData("density");
                event.setType("data");

                String json = gson.toJson(event);
                PostController controller = new PostController();
                controller.execute("/insert/last_density/fermentation/", json);
                //new PutController(MonitorFermentationTabFragment.this).execute("/insert/last_density/fermentation", json);

                // Show to user the operation has been performed
                StringBuilder strBuild = new StringBuilder();
                strBuild.append(getResources().getString(R.string.density_added));
                strBuild.append(event.getValue());
                strBuild.append(getResources().getString(R.string.density_measure));

                Snackbar.make(v,
                        strBuild.toString(),
                        Snackbar.LENGTH_SHORT)
                        .show();

                // Modify view
                txSecondValue.setText(Double.toString(event.getValue()));

                // Hide and show the views
                densityEditText.setVisibility(View.INVISIBLE);
                imgConfirmButton.setVisibility(View.INVISIBLE);
                imgDensityButton.setVisibility(View.VISIBLE);
                txSecondValue.setVisibility(View.VISIBLE);
            }
        });

        imgDensityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                densityEditText.setVisibility(View.VISIBLE);
                imgConfirmButton.setVisibility(View.VISIBLE);
                imgDensityButton.setVisibility(View.INVISIBLE);
                txSecondValue.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    public synchronized void onResume() {
        super.onResume();
    }

    @Override
    public void ProcessFinish(String output) {
        //

    }
}
