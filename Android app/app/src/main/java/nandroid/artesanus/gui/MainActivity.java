package nandroid.artesanus.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends MenuActivity
{
    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_main_menu);

        // Set newCraft button for start new crafting process, so start first new process
        FloatingActionButton newCraft = (FloatingActionButton) findViewById(R.id.main_menu_new_crafting);
        newCraft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewBeerCraftingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
    }
    @Override
    public synchronized void onResume()
    {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
    }

    @Override
    public synchronized void onPause()
    {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

}
