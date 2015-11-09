import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class Broadcaster implements Runnable {
    // This class sends an update to the other players-clients

    private ArrayList<String> receivers = new ArrayList<>(); // (IP Port) list of the nodes needing this update.
    private String msg;

    public Broadcaster(ArrayList<String> receivers, String msg) {
        this.receivers.addAll(receivers.stream().collect(Collectors.toList()));
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            // TODO: If ip and port doesnt exist it still connects
            DatagramSocket socket = new DatagramSocket();
            for (String receiver : this.receivers) {
                String[] split = receiver.split(" ");
                String ip = split[0];
                int port = Integer.parseInt(split[1]);
                InetAddress addr = InetAddress.getByName(ip);

                DatagramPacket packet = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, addr, port);
                socket.send(packet);
            }

        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
