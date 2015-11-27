package se.kth.id2212.hw5;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    volatile static ArrayList<Player> players = new ArrayList<>();
    volatile static Round round;

    private final String NAME;
    private final InetAddress IP_ADD;
    private final int PORT;
    private final GameActivity gameActivity;
    private Thread serverThread;
    private DatagramSocket serverSocket;
    private ServerRunnable serverTask;

    // This class receives data from the other players-clients
    public Server(String name, InetAddress ip_address, int port, GameActivity gameActivity){
        this.gameActivity = gameActivity;
        this.NAME = name;
        this.IP_ADD = ip_address;
        this.PORT = port;
        // Add ourselves in the player list
        players.add(new Player(NAME, IP_ADD, PORT));
    }


    class ServerRunnable implements Runnable{
        private boolean isRunning = true;
        private int port;
        private final ExecutorService clientProcessingPool;

        public ServerRunnable(int port, ExecutorService clientProcessingPool){
            this.port = port;
            this.clientProcessingPool = clientProcessingPool;
        }

        @Override
        public void run() {
            try {
                serverSocket = new DatagramSocket(port);
                byte[] recv_data = new byte[1024];
                append_log("(INFO) Waiting for players to connect...");
                while (isRunning) {
                    DatagramPacket recv_packet = new DatagramPacket(recv_data, recv_data.length);
                    serverSocket.receive(recv_packet);
                    clientProcessingPool.submit(new ClientTask(recv_packet));
                }
            } catch (SocketException e) {
                //append_log("(ERROR) Unable to process client request");
                e.printStackTrace();
            } catch (InterruptedIOException e){
                System.out.println("Thread interrupted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void terminate(){
            serverSocket.close();
            isRunning = false;
        }

    }

    public void startServer(final int port) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
        serverTask = new ServerRunnable(port, clientProcessingPool);
        serverThread= new Thread(serverTask);
        serverThread.start();
    }

    public static synchronized void printPlayers(){
        for(Player p: players){
            System.out.println(p.toString());
        }
    }

    public synchronized void addMove(String name, String choice){
        if(round == null){
            round = new Round();
            append_log("(INFO) NEW ROUND STARTED.");
            append_log("(INFO) " + name + " has played.");
            round.addMove(name, choice);
        } else {
            append_log("(INFO) " + name + " has played.");
            round.addMove(name, choice);
        }
    }

    public synchronized void finalizeRound(){
        if(round.isRoundFinished()) {
            HashMap<String, Integer> results = round.getResults();
            System.out.println("(Server) Round completed.");
            append_log("(INFO) RESULTS:");
            for(Player p : players){
                p.setScore(results.get(p.getName()));
                append_log("(INFO) " + p.getName() + " : " + p.getScore() + " points. TOTAL: " + p.getTotalScore() );
            }
            clearRound();
            gameActivity.game_panel_active(true);
            append_log("(INFO) Ready to start a new round.");
            update_player_list();
        }
    }

    public synchronized void update_player_list() {
        gameActivity.update_player_list(Server.players);
    }

    public synchronized void append_log(String msg) { this.gameActivity.append_to_log(msg); }

    public void clearRound(){ round=null; }

    private class ClientTask implements Runnable {
        private final DatagramPacket recv_packet;

        private ClientTask(DatagramPacket packet) {
            this.recv_packet = packet;
        }

        @Override
        public void run() {
            String from = this.recv_packet.getAddress().getHostAddress() + ", " + this.recv_packet.getPort();
            String data = new String(this.recv_packet.getData(), 0, this.recv_packet.getLength());
            String [] table = data.split(",");
            String command = table[0];
            Server.printPlayers();
            if (command.equals("connect")){
                try {
                    // A new player wants to enter the game
                    String name = table[1];
                    InetAddress ip_address = InetAddress.getByName(table[2]);
                    int port = Integer.parseInt(table[3]);
                    // Add him
                    players.add(new Player(name, ip_address, port));
                    // Update our UI
                    update_player_list();
                    gameActivity.update_player_label(Server.players.size());
                    append_log("(INFO) Player connected: " + name + "(" + from + ")");
                    // Inform all the clients that the group view has changed
                    ArrayList<String> destinations = new ArrayList<>();
                    String msg = "new_view";
                    for(Player p: players) {
                        // Message
                        msg = msg.concat("," + p.toString());
                        // Destinations
                        String player_data = p.getIp_address().getHostAddress() + " " + p.getPort();
                        destinations.add(player_data);
                    }
                    // Broadcast it
                    BroadcasterMediator bm = new BroadcasterMediator(gameActivity, Server.this);
                    bm.new_view(destinations, msg);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            }
            else if (command.equals("new_view")) {
                players.clear();
                for (int i = 1; i < table.length; i++) {
                    try {
                        String[] p_data = table[i].split(" ");
                        Player p = new Player(p_data[0],
                                InetAddress.getByName(p_data[1]),
                                Integer.parseInt(p_data[2]));
                        p.setAbsoluteScore(Integer.parseInt(p_data[3]));
                        p.setTotalScore(Integer.parseInt(p_data[4]));
                        players.add(p);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }

                if(players.size() == 1){
                    clearRound();
                    players.clear();
                    players.add(new Player(NAME, IP_ADD, PORT));
                    gameActivity.clear_player_list();
                    gameActivity.game_panel_active(false);
                    gameActivity.update_player_label(1);
                } else{
                    Server.this.update_player_list();
                    Server.this.gameActivity.update_player_label(Server.players.size());
                    gameActivity.set_connected();
                    assert players.size() >=1;
                    if(round.isRoundFinished()) finalizeRound();
                }
            }
            else if (command.equals("choice")) {
                String name = table[1];
                String choice = table[2];
                addMove(name, choice);
                finalizeRound();
            }
        }

    }

    public void closeServer(){
        serverTask.terminate();
        serverThread.interrupt();
    }

    public boolean arePlayersConnected(){
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).toString());
        }
        return ! (players.size() <= 1);
    }
}