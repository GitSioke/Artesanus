package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nandroid.artesanus.common.BrewProcess;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.gui.MonitoringActivity;
import nandroid.artesanus.http.GetController;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.http.PostController;
import nandroid.artesanus.gui.R;

/**
 * This class handles the Monitor tab fragment for Fermentation process
 */
public class MonitorFermentationTabFragment extends Fragment implements IAsyncHttpResponse
{
    private int _idProcess;
    private GraphView _graph;
    private TextView txtViewPrimaryValue;

    // Debugging
    private static final String TAG = "MonitorFermentationTabFragment";
    private static final boolean D = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        MonitoringActivity act = (MonitoringActivity)getActivity();
        _idProcess = act.GetIdFermentation();

        View view  = inflater.inflate( R.layout.fragment_monitoring_fermenter, container, false);
        _graph = (GraphView)view.findViewById(R.id.graph);

        // Get all events related to process and brew crafting from server
        String json = "";
        new GetController(this).execute("/retrieve/events/"+_idProcess, json);

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

                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();

                Event event = new Event.Builder()
                        .time(date)
                        .source("fermentation")
                        .data("density")
                        .value(Double.parseDouble(densityEditText.getText().toString()))
                        .type("data")
                        // TODO modificar para que el id_process sea el correcto
                        .id_process(1)
                        .build();


                String json = gson.toJson(event);
                PostController controller = new PostController();
                controller.execute("/insert/last_density/fermentation/", json);

                // Show to user the operation has been performed
                StringBuilder strBuild = new StringBuilder()
                        .append(getResources().getString(R.string.density_added))
                        .append(event.getValue())
                        .append(getResources().getString(R.string.density_measure));

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

        txtViewPrimaryValue = (TextView) view.findViewById(R.id.monitor_main_value);

        return view;
    }

    public synchronized void onResume() {
        super.onResume();
    }

    @Override
    public void ProcessFinish(String output)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // String format in database
        Gson gson = builder.create();
        try {
            BrewProcess mashingBrewProcess = gson.fromJson(output, BrewProcess.class);
            List<Event> events = mashingBrewProcess.getEvents();

            txtViewPrimaryValue.setText(String.valueOf(events.get(events.size()-1).getValue())+"º");
            LineGraphSeries series = new LineGraphSeries<DataPoint>();
            for (Event event : events ) {
                series.appendData(new DataPoint(event.getTime(), event.getValue()), true, events.size());
            }

            _graph.getGridLabelRenderer().setHumanRounding(false);
            _graph.addSeries(series);

            _graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
            _graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            _graph.getViewport().setScrollable(true); // enables horizontal scrolling
            _graph.getViewport().setScrollableY(true); // enables vertical scrolling
            _graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
            _graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        }
        catch (Exception ex)
        {
            if(D) Log.e(TAG, ex.getMessage());
        }
    }
}
