import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // This class receives data from the other players-clients

    public Server() {}

    public void startServer(int port) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);


        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
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
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

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
            System.out.println("(Worker) Received(" + from + "): " + data);

            // Do whatever required to process the client's request
        }
    }

}