package nandroid.artesanus.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import nandroid.artesanus.common.ProcessHelper;

public class NewProcessActivity extends AppCompatActivity {

    ProcessHelper.CRAFTING_PROCESS mProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_process);

        mProcess = (ProcessHelper.CRAFTING_PROCESS)getIntent().getSerializableExtra("PROCESS");
        // TODO Put this String into Toolbar title?
    }

    //Onclick start process button launch this method
    public void StartProcess(View v)
    {
        EditText timeED = (EditText)findViewById(R.id.new_et_time);
        Integer time = Integer.parseInt(timeED.getText().toString());
        if (time != null)
        {
            // Get minutes and pass via Intent
            Intent intent = new Intent(this, MonitorActivity.class);
            intent.putExtra("Time", time);
            intent.putExtra("PROCESS", mProcess);

            startActivity(intent);
        }
        else
        {
            Toast.makeText(getBaseContext(), R.string.new_missing_minutes, Toast.LENGTH_SHORT).show();
        }

    }
}
