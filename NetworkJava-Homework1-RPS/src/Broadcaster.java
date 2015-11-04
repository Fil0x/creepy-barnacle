import java.io.IOException;
import java.net.*;


public class Broadcaster implements Runnable {
    // This class sends an update to the other players-clients

    private String[] receivers; // (IP Port) list of the nodes needing this update.
    private String msg;

    public Broadcaster(String[] receivers, String msg) {
        this.receivers = new String[receivers.length];
        for (int i = 0; i < receivers.length; i++) {
            this.receivers[i] = receivers[i];
        }
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
                System.out.println("Sending packet");
                socket.send(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
