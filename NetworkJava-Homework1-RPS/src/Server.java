import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    volatile static ArrayList<Player> players = new ArrayList<>();
    volatile static Round round;

    private final ClientForm client_ui;


    // This class receives data from the other players-clients
    public Server(String name, InetAddress ip_address, int port, ClientForm cf){
        this.client_ui = cf;
        // Add ourselves in the player list
        players.add(new Player(name, ip_address, port));
    }

    public void startServer(int port) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = () -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(port);
                byte[] recv_data = new byte[1024];
                append_log("(INFO) Waiting for players to connect...");
                while (true) {
                    DatagramPacket recv_packet = new DatagramPacket(recv_data, recv_data.length);
                    serverSocket.receive(recv_packet);
                    clientProcessingPool.submit(new ClientTask(recv_packet));
                }
            } catch (SocketException e) {
                append_log("(ERROR) Unable to process client request");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    public static synchronized void printPlayers(){
        players.forEach(System.out::println);
    }

    public synchronized void addMove(String name, String choice){
        if(round == null){
            round = new Round();
            client_ui.start_timer();
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
            client_ui.stop_timer();
            client_ui.resetTimer();
            HashMap<String, Integer> results = round.getResults();
            System.out.println("(Server) Round completed.");
            append_log("(INFO) RESULTS:");
            for(Player p : players){
                p.setScore(results.get(p.getName()));
                append_log("(INFO) " + p.getName() + " : " + p.getScore() + " points. TOTAL: " + p.getTotalScore() );
            }
            clearRound();
            client_ui.game_panel_active(true);
            append_log("(INFO) Ready to start a new round.");
            update_player_list();
        }
    }

    public synchronized void update_player_list() {
        this.client_ui.update_player_list(Server.players);
    }

    public synchronized void append_log(String msg) { this.client_ui.append_to_log(msg); }

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
            if (Objects.equals(command, "connect")){
                try {
                    // A new player wants to enter the game
                    String name = table[1];
                    InetAddress ip_address = InetAddress.getByName(table[2]);
                    int port = Integer.parseInt(table[3]);
                    // Add him
                    players.add(new Player(name, ip_address, port));
                    // Update our UI
                    Server.this.update_player_list();
                    Server.this.client_ui.update_player_label(Server.players.size());
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
                    BroadcasterMediator bm = new BroadcasterMediator(client_ui, Server.this);
                    bm.new_view(destinations, msg);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            }
            else if (Objects.equals(command, "new_view")) {
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
                    players.forEach(Player::resetScore);
                    client_ui.clear_player_list();
                    client_ui.set_disconnected();
                    client_ui.update_player_label(1);
                } else{
                    Server.this.update_player_list();
                    Server.this.client_ui.update_player_label(Server.players.size());
                    client_ui.set_connected();
                    assert players.size() >=1;
                    if(round.isRoundFinished()) finalizeRound();
                }
            }
            else if (Objects.equals(command, "choice")) {
                String name = table[1];
                String choice = table[2];
                addMove(name, choice);
                finalizeRound();
            }
        }
    }
}