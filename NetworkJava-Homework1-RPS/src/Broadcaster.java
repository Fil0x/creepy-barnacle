import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class Broadcaster implements Runnable {
    // This class sends an update to the other players-clients

    private ArrayList<String> receivers; // (IP Port) list of the nodes needing this update.
    private String msg;

    public Broadcaster(ArrayList<String> receivers, String msg) {
        this.receivers = (ArrayList<String>) receivers.clone();
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            for (String receiver : this.receivers) {
                String[] split = receiver.split(" ");
                String ip = split[0];
                int port = Integer.parseInt(split[1]);
                InetAddress addr = InetAddress.getByName(ip);

                DatagramPacket packet = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, addr, port);
                System.out.println("(Broadcaster) Sending to: " + ip + ", " + port + ":" + msg);
                socket.send(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
