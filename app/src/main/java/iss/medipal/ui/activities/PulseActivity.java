package iss.medipal.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import iss.medipal.R;
import iss.medipal.dao.MeasurementDao;
import iss.medipal.dao.impl.MeasurementDaoImpl;
import iss.medipal.model.Measurement;
import iss.medipal.model.Pulse;
import iss.medipal.model.Weight;
import iss.medipal.ui.adapters.RecyclerAdapter;

public class PulseActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,View.OnClickListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MeasurementDao measurementDao;
    private ImageView imageBack;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse);

        toolbar = (Toolbar) findViewById(R.id.toolbarPulse);
        toolbar.setTitle("Pulse");
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);

        setUpRecyclerView();

    }
    @Override
    public void onClick(View v) {

    }

    private void setListeners() {
        ImageView.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click", String.valueOf(v));
                finish();
            }
        };
        imageBack.setOnClickListener(clickListener);
    }

    private void setUpRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerAdapter adapter = new RecyclerAdapter(this, getData());
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.linearViewHorizontal:
                showColorDialog();
                break;

            case R.id.add_blood:
                Intent intent = new Intent(PulseActivity.this, AddPulseActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.home:

                finish();
                break;
        }
        return true;

    }
    public void showColorDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help_mes);
        dialog.setTitle("Color Bands");
        dialog.show();

    }

    public List<Measurement> getData() {

        List<Measurement> data = new ArrayList<>();
        measurementDao = MeasurementDaoImpl.newInstance(PulseActivity.this);
        List<Pulse> pulseList = measurementDao.getPulseValues();

        int[] images = {
                R.drawable.heart_pulse,
                R.drawable.pulse,
                R.drawable.oil_temperature,
                R.drawable.weight_kilogram
        };

        String[] Categories = {"Blood Pressure", "Pulse", "Temperature", "Weight"};

        if (!pulseList.isEmpty()) {

            for (int i = 0; i < pulseList.size(); i++) {

                Measurement pulse = new Pulse(pulseList.get(i).getId(), images[1], pulseList.get(i).getPulse(),
                        pulseList.get(i).getMeasuredOn());
                data.add(pulse);
            }
        }

        return data;
    }

}
