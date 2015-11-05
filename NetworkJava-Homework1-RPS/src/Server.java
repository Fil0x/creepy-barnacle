import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    volatile static ArrayList<Player> players = new ArrayList<>();
    private final int port;
    private final InetAddress ip_addr;
    private final String name;
    private final ClientForm client_ui;

    // This class receives data from the other players-clients
    public Server(String name, InetAddress ip_address, int port, ClientForm cf){
        this.name = name;
        this.ip_addr = ip_address;
        this.port = port;
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
//                    byte[] reply_data = new byte[1024];

                System.out.println("(Server) Waiting for clients to connect...");   //TODO del
                append_log("(Server) Waiting for clients to connect...");
                while (true) {
                    DatagramPacket recv_packet = new DatagramPacket(recv_data, recv_data.length);
                    serverSocket.receive(recv_packet);
                    clientProcessingPool.submit(new ClientTask(recv_packet));
                }
            } catch (SocketException e) {
                System.err.println("(Server) Unable to process client request");    //TODO del
                append_log("(Server) Unable to process client request");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    public static synchronized void printPlayers(){
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i));
        }
    }

    public synchronized void update_player_list() {
        this.client_ui.update_player_list(Server.players);
    }

    public synchronized void clear_player_list() {
        this.client_ui.clear_player_list();
    }

    public synchronized void append_log(String msg) { this.client_ui.append_to_log(msg); }

    private class ClientTask implements Runnable {
        private final DatagramPacket recv_packet;

        private ClientTask(DatagramPacket packet) {
            this.recv_packet = packet;
        }

        @Override
        public void run() {
            String from = this.recv_packet.getAddress().getHostAddress() + ", " + this.recv_packet.getPort();
            String data = new String(this.recv_packet.getData(), 0, this.recv_packet.getLength());
            //System.out.println("(Worker) Received(" + from + "): " + data);
            String [] table = data.split(",");
            String command = table[0];
            System.out.println("(Server) Received command: " + command); //TODO Del
            System.out.println(" printing before ...");
            Server.printPlayers();
            append_log("(Server) Received command: " + command);
            if (Objects.equals(command, "connect")){
                try {
                    // connect(name, new_ip, new_port)
                    // A new player wants to enter the game
                    String name = table[1];
                    InetAddress ip_address = InetAddress.getByName(table[2]);
                    int port = Integer.parseInt(table[3]);
                    // Add him
                    players.add(new Player(name, ip_address, port));
                    // Update our UI
                    Server.this.update_player_list();
                    // Inform all the clients that the group view has changed
                    ArrayList<String> destinations = new ArrayList<>();
                    String msg = "new_view";
                    for(Player p: players) {
                        // Message
                        msg = msg.concat("," + p.toString());
                        //if (p.getIp_address().equals(Server.this.ip_addr) && p.getPort() == Server.this.port)
                        //    continue;
                        // Destinations
                        String player_data = p.getIp_address().getHostAddress() + " " + p.getPort();
                        destinations.add(player_data);
                    }
                    // Broadcast it
                    BroadcasterMediator bm = new BroadcasterMediator(client_ui);
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
                        p.setScore(Integer.parseInt(p_data[3]));
                        p.setTotalScore(Integer.parseInt(p_data[4]));
                        players.add(p);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }

                if(players.size() == 1){
                    client_ui.clear_player_list();
                } else{
                    Server.this.update_player_list();
                    assert players.size() >=1;
                }
            }
        }
    }
}