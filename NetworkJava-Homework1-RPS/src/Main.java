import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class Main
{
    public static void main(String [] args) throws UnknownHostException {
        Random random = new Random();
        int name = random.nextInt(1000) + 1;
        String ip_addr;
        int port = 8000;

        if(args.length == 0) {
            ip_addr = "127.0.0.1";
        }
        else if(args.length == 1) {
            // Port is given
            ip_addr = "127.0.0.1";
            port = Integer.parseInt(args[0]);
        }
        else {
            // We have both
            ip_addr = args[0];
            port = Integer.parseInt(args[1]);
        }

        // START THE UI
        ClientForm cf = new ClientForm(name, ip_addr, port);
        cf.setVisible(true);

        // START THE SERVER(LISTEN THREAD)
        Server s = new Server(Integer.toString(name), InetAddress.getByName(ip_addr), port, cf);
        s.startServer(port);
    }

}