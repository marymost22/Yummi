package com.example.android.yummi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.yummi.data.ManejadorImagenes;

public class DetailActivity extends AppCompatActivity {

    public static final String ID_COMEDOR = "id";
    public static final String NOMBRE_COMEDOR = "nombre";
    public static final String DETAILACTIVITYFRAGMENT_TAG = "DAFTAG";

    private ManejadorImagenes manejadorImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) { // Habilitar up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.collapser);
        Long comedorId = (Long) getIntent().getExtras().get(ID_COMEDOR);
        String comedorNombre = (String) getIntent().getExtras().get(NOMBRE_COMEDOR);

        if ( comedorId != null){
            DetailActivityFragment detailFragment = new DetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(DetailActivityFragment.COMEDOR_ID, comedorId);
            bundle.putString(DetailActivityFragment.COMEDOR_NOMBRE, comedorNombre);
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(
                    R.id.detail_container, detailFragment,
                    DETAILACTIVITYFRAGMENT_TAG).commit();
            manejadorImagenes = new ManejadorImagenes(this, collapser, comedorId);
        } else{
            NotSelectedFragment notSelectedFragment = new NotSelectedFragment();
            getSupportFragmentManager().beginTransaction().add(
                    R.id.detail_container, notSelectedFragment,
                    NotSelectedFragment.NOTSELECTED_TAG).commit();
        }

        if( comedorNombre != null){
            collapser.setTitle( comedorNombre );
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(DetailActivity.this, PricesActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        manejadorImagenes.conseguirImagen();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(manejadorImagenes != null) {
            manejadorImagenes.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}







