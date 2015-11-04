import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    volatile static ArrayList<Player> players = new ArrayList<>();

    // This class receives data from the other players-clients
    public Server(String name, InetAddress ip_address, int port){
        players.add(new Player(name, ip_address, port));
    }

    public void startServer(int port) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);


        Runnable serverTask = () -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(port);
                byte[] recv_data = new byte[1024];
//                    byte[] reply_data = new byte[1024];

                System.out.println("(Server) Waiting for clients to connect...");
                while (true) {
                    DatagramPacket recv_packet = new DatagramPacket(recv_data, recv_data.length);
                    serverSocket.receive(recv_packet);
                    clientProcessingPool.submit(new ClientTask(recv_packet));
                }
            } catch (SocketException e) {
                System.err.println("Unable to process client request");
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
            if (Objects.equals(command, "connect")){
                try {
                    // connect(name, ip, port)
                    String name = table[1];
                    InetAddress ip_address = InetAddress.getByName(table[2]);
                    int port = Integer.parseInt(table[3]);
                    players.add(new Player(name, ip_address, port));
                    Server.printPlayers();
                    //Broadcaster b = new Broadcaster( , "new_view");
                    //(new Thread(b)).start();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            // Do whatever required to process the client's request
        }
    }

}