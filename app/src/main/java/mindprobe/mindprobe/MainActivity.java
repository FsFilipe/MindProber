package mindprobe.mindprobe;

import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONException;

import java.net.MalformedURLException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Context context;
    FragmentTransaction fragmentTransaction;
    public static Button start;
    static boolean server_connection;
    public static BluetoothManager bluetoothManager;
    public static float[] xvalues, yvalues_hr, yvalues_gsr;
    public static double yvalues_gsr_temp;
    public static int pos;
    public static int cycles;
    public static int hr_value, gsr_value;
    static boolean start_acquisition;
    public static int count_connections;
    public static int global_score;
    public static boolean mScanning;
    public NavigationView navigationView;
    private Boolean exit = false;
    public static String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pos = 0;
        cycles = 0;
        hr_value = 0;
        gsr_value = 0;
        server_connection = false;
        start_acquisition = false;
        count_connections = 0;
        start = (Button) findViewById(R.id.start);

        host = "http://192.168.1.111:3701/";

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (LoginActivity.user == 1) {
            navigationView.getMenu().findItem(R.id.nav_signals).setVisible(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Abrirá a gestão de conteúdos...Pode ter outra acção", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,new GestaoConteudos());
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Gestão de Estudos");
                navigationView.getMenu().getItem(1).setChecked(true);
            }
        });

        xvalues = new float[60];
        yvalues_hr = new float[60];
        yvalues_gsr = new float[60];
        for (int i = 0; i < 60; i++) {
            float time = 0;
            xvalues[i] = time;
            yvalues_hr[i] = 0;
            yvalues_gsr[i] = 0;
        }
    }

    public void button_start(View view) throws JSONException {
        Bluetooth ble = new Bluetooth(context,MainActivity.this);
        writeLine(host);
    }

    public void button_save_settings(View view){

        Definicoes.user_host_ip = Definicoes.mHostIP.getText().toString();
        Definicoes.user_host_port = Definicoes.mHostPort.getText().toString();

        String temp_host = Definicoes.user_host_ip.concat(":");
        temp_host = temp_host.concat(Definicoes.user_host_port);

        host = temp_host;
        writeLine(temp_host);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (exit){
                finish();
        }else {
            //super.onBackPressed();
            writeLine("Pressione novamente para sair");
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
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
            //return true;
            writeLine("Funcionalidade em desenvolvimento...");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new GestaoConteudos());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Gestão de Estudos");
            item.setChecked(true);
        } else if (id == R.id.nav_gallery) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new AvaliacaoConteudos());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Avaliação de Conteúdos");
            item.setChecked(true);
        } else if (id == R.id.nav_slideshow) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new FAQ());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Ajuda/FAQ");
            item.setChecked(true);
        } else if (id == R.id.nav_signals) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new signals_visualization());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Sinais (ADMIN)");
            item.setChecked(true);
        } else if (id == R.id.nav_home) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new HomeFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("MindProber");
            item.setChecked(true);
        } else if (id == R.id.nav_settings) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new Definicoes());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Definições");
            item.setChecked(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
