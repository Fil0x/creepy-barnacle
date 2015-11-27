package se.kth.id2212.hw5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GameActivity extends ActionBarActivity {
    private static GameActivity instance = null;
    private String name;
    private String ip_addr;
    private int port;
    private Button rock_button, scissors_button, paper_button;
    private EditText connected;
    private EditText log;

    private TextView status_label;

    private Server server;

    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        instance = GameActivity.this;

        mHandler = new  Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String message = (String) inputMessage.obj; //Extract the string from the Message
                String [] splitted = message.split(",");
                String command = splitted[0];
                String data = splitted[1];
                switch (command) {
                    case "connected":
                        connected.append(data);
                        break;
                    case "clear_players":
                        connected.setText("");
                        break;
                    case "log":
                        log.append(data);
                        break;
                    case "num_players":
                        status_label.setText(data);
                        break;
                    case "buttons_active":
                        rock_button.setEnabled(true);
                        scissors_button.setEnabled(true);
                        paper_button.setEnabled(true);
                        break;
                    case "buttons_inactive":
                        rock_button.setEnabled(false);
                        scissors_button.setEnabled(false);
                        paper_button.setEnabled(false);
                        break;


                }
            }
        };

        rock_button = (Button) findViewById(R.id.rock_button);
        scissors_button = (Button) findViewById(R.id.scissors_button);
        paper_button = (Button) findViewById(R.id.paper_button);

        status_label = (TextView) findViewById(R.id.status_text);
        connected = (EditText) findViewById(R.id.players_edit_text);
        log = (EditText) findViewById(R.id.log_edit_text);


        //retrieve info strings from caller intent
        Intent intent = getIntent();
        name = intent.getStringExtra(ConnectActivity.EXTRA_MESSAGE_NAME);
        String ip_addr_to = intent.getStringExtra(ConnectActivity.EXTRA_MESSAGE_IP_TO);
        int port_to = Integer.parseInt(intent.getStringExtra(ConnectActivity.EXTRA_MESSAGE_PORT_TO));
        ip_addr = intent.getStringExtra(ConnectActivity.EXTRA_MESSAGE_IP);
        port = Integer.parseInt(intent.getStringExtra(ConnectActivity.EXTRA_MESSAGE_PORT));
        System.out.println(name + " connected to " + ip_addr + " port " + port);

        TextView ip = (TextView) findViewById(R.id.ip_text);
        ip.setText(ip_addr + " on port: " + port);

        // START THE SERVER(LISTEN THREAD)
        Server s;
        try {
            s = new Server(name, InetAddress.getByName(ip_addr), port, GameActivity.getInstance());
            s.startServer(port);
            GameActivity.getInstance().setServer(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        BroadcasterMediator bm = new BroadcasterMediator(getInstance(), server);
        bm.connect(ip_addr_to, Integer.toString(port_to), name,
                getInstance().ip_addr, getInstance().port);

        if(server.arePlayersConnected()) set_connected();
        else set_disconnected();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.action_exit) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (id == R.id.action_connect){
            BroadcasterMediator bm = new BroadcasterMediator(getInstance(), server);
            bm.disconnect(getInstance().name);
            intent = new Intent(this, GreetingsActivity.class);
            server.closeServer();
            finish();
        } else if (id == R.id.action_disconnect){
            BroadcasterMediator bm = new BroadcasterMediator(getInstance(), server);
            bm.disconnect(getInstance().name);
            intent = new Intent(this, GreetingsActivity.class);
            server.closeServer();
            finish();
        }
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }


    public void update_player_list(ArrayList<Player> players) {
        Message m1 = Message.obtain(); // Creates an new Message instance


        // Clear the text area to rebuild it
        m1.obj = "clear_players, "; // Put the string into Message, into "obj" field.
        m1.setTarget(mHandler); // Set the Handler
        m1.sendToTarget(); //Send the message
        for(Player p: players) {
            Message m2 = Message.obtain();
            String line = p.getName() + " : " + p.getScore() + " total: " + p.getTotalScore()+"\n";
            m2.obj = "connected,"+line;
            m2.setTarget(mHandler);
            m2.sendToTarget();
        }
    }

    public synchronized void append_to_log(String msg) {
        Message m = Message.obtain(); // Creates an new Message instance
        m.obj = "log," + msg + "\n"; // Put the string into Message, into "obj" field.
        m.setTarget(mHandler); // Set the Handler
        m.sendToTarget(); //Send the message
    }

    public synchronized void update_player_label(int num_players) {
        Message m = Message.obtain();
        m.obj = "num_players,"+ num_players + " players are playing";
        m.setTarget(mHandler);
        m.sendToTarget();
    }

    public synchronized void game_panel_active(boolean state) {
        Message m = Message.obtain();
        if(state)
            m.obj = "buttons_active, ";
        else
            m.obj = "buttons_inactive, ";
        m.setTarget(mHandler);
        m.sendToTarget();
    }

    public synchronized void clear_player_list() {
        Message m = Message.obtain();
        m.obj = "clear_players, ";
        m.setTarget(mHandler);
        m.sendToTarget();
    }


    public void set_connected() {
        this.game_panel_active(true);
    }

    public void set_disconnected() {
        game_panel_active(false);
    }

    public void setServer(Server server){
        this.server = server;
    }

    public void onPaper(View view) {
        BroadcasterMediator bm = new BroadcasterMediator(getInstance(), server);
        bm.send_move(getInstance().getName(), "Paper");
    }

    public void onScissors(View view) {
        BroadcasterMediator bm = new BroadcasterMediator(getInstance(), server);
        bm.send_move(getInstance().getName(), "Scissors");
    }

    public void onRock(View view) {
        BroadcasterMediator bm = new BroadcasterMediator(getInstance(), server);
        bm.send_move(getInstance().getName(), "Rock");
    }

    public static GameActivity getInstance() {
        if(instance==null)
            throw new IllegalStateException("THIS SHOULD NOT HAPPEN");
        return instance;
    }

    public String getName(){
        return name;
    }
}
