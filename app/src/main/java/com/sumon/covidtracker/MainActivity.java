package com.sumon.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mTodayTotal, mTotal, mActive, mTodayActive, mRecovered, mTodayRecovered, mDeaths, mTodayDeaths;

    String country;
    TextView mFilter;
    Spinner spinner;
    String[] types = {"case", "deaths", "recovered", "active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart pieChart;
    private RecyclerView recyclerView;
    com.sumon.covidtracker.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        countryCodePicker = findViewById(R.id.ccPicker);
        mTodayTotal = findViewById(R.id.todayTotal);
        mTotal = findViewById(R.id.totalCase);
        mActive = findViewById(R.id.activeCase);
        mTodayActive = findViewById(R.id.todayActive);
        mRecovered = findViewById(R.id.recoveredCase);
        mTodayRecovered = findViewById(R.id.todayRecover);
        mDeaths = findViewById(R.id.totalDeath);
        mTodayDeaths = findViewById(R.id.todayDeath);
        pieChart = findViewById(R.id.pieChart);
        spinner = findViewById(R.id.spinner);
        mFilter = findViewById(R.id.filter);
        recyclerView = findViewById(R.id.recyclerView);
        modelClassList = new ArrayList<>();
        modelClassList2 = new ArrayList<>();


        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, types);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(), modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });

        fetchdata();

    }

    private void fetchdata() {

        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for (int i= 0; i<modelClassList.size(); i++){

                    if (modelClassList.get(i).getCountry().equals(country)){

                        mActive.setText((modelClassList.get(i).getActive()));
                        mTodayDeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        mTodayRecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mTodayTotal.setText((modelClassList.get(i).getTodayCases()));
                        mTotal.setText((modelClassList.get(i).getCases()));
                        mDeaths.setText((modelClassList.get(i).getDeaths()));
                        mRecovered.setText((modelClassList.get(i).getRecovered()));

                        int active, total, recovered, deaths;
                        active= Integer.parseInt(modelClassList.get(i).getActive());
                        total= Integer.parseInt(modelClassList.get(i).getCases());
                        recovered= Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths= Integer.parseInt(modelClassList.get(i).getDeaths());

                        updateGraph(active, total, recovered, deaths);


                        
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updateGraph(int active, int total, int recovered, int deaths) {

        pieChart.clearChart();

        pieChart.addPieSlice(new PieModel("Total", total, Color.parseColor("#FFB701")));
        pieChart.addPieSlice(new PieModel("Active", active, Color.parseColor("#FF4caf50")));
        pieChart.addPieSlice(new PieModel("Recovered", recovered, Color.parseColor("#38ACCD")));
        pieChart.addPieSlice(new PieModel("Deaths", deaths, Color.parseColor("#F55c47")));
        pieChart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String item = types[i];
        mFilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}