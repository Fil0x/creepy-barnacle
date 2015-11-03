
public class Main
{
    public static void main(String [] args)
    {
        String ip_addr = null;
        int port = 80;

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

        ClientForm cf = new ClientForm(ip_addr, port);
        cf.setVisible(true);
    }

}