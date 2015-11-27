package se.kth.id2212.hw5;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE_NAME = "se.kth.hw5.MESSAGE_NAME";
    public final static String EXTRA_MESSAGE_IP_TO = "se.kth.hw5.MESSAGE_IP_TO";
    public final static String EXTRA_MESSAGE_PORT_TO = "se.kth.hw5.MESSAGE_PORT_TO";
    public final static String EXTRA_MESSAGE_IP = "se.kth.hw5.MESSAGE_IP";
    public final static String EXTRA_MESSAGE_PORT = "se.kth.hw5.MESSAGE_PORT";
    private final int PORT = 36173;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        TextView ip = (TextView) findViewById(R.id.ip_text);
        ip.setText("Server runs on : " + getIpAddress() + " on port: " + PORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_greetings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onConnect(View view) {
        EditText nameEditText = (EditText) findViewById(R.id.name_text_edit);
        EditText ipEditText = (EditText) findViewById(R.id.ip_text_edit);
        EditText portEditText = (EditText) findViewById(R.id.port_text_edit);

        String name = nameEditText.getText().toString();
        String ip = ipEditText.getText().toString();
        String port = portEditText.getText().toString();
        if(Utilities.verify_ip(ip) && Utilities.verify_port(port)) {


            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(EXTRA_MESSAGE_NAME, name);
            intent.putExtra(EXTRA_MESSAGE_IP_TO, ip);
            intent.putExtra(EXTRA_MESSAGE_PORT_TO, port);
            intent.putExtra(EXTRA_MESSAGE_IP, getIpAddress());
            intent.putExtra(EXTRA_MESSAGE_PORT, Integer.toString(PORT));
            startActivity(intent);
        }
    }

    public String getIpAddress(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }
}
