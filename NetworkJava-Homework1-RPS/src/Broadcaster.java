import java.io.IOException;
import java.net.*;


public class Broadcaster implements Runnable {
    // This class sends an update to the other players-clients

    private String[] receivers; // (IP Port) list of the nodes needing this update.
    private String msg;

    public Broadcaster(String[] receivers, String msg) {
        this.receivers = receivers;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            for (int i = 0; i < this.receivers.length; i++) {
                String[] split = this.receivers[i].split(" ");
                String ip = split[0];
                int port = Integer.parseInt(split[1]);
                InetAddress addr = InetAddress.getByName(ip);

                DatagramPacket packet = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, addr, port);
                System.out.println("Sending packet");
                socket.send(packet);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
