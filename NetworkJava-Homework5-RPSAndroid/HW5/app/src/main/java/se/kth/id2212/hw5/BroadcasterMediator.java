package se.kth.id2212.hw5;

import java.util.ArrayList;

public class BroadcasterMediator {

    private final GameActivity gameActivity;
    private final Server server;

    public BroadcasterMediator(GameActivity gameActivity, Server server){
        this.gameActivity = gameActivity;
        this.server = server;
    }

    public void connect(String dest_ip_addr, String dest_port, String name, String ip_addr, int port) {

        if(dest_ip_addr.equals(ip_addr) && dest_port.equals(Integer.toString(port)) ) {
            gameActivity.append_to_log("(WARN) You trying to connect to yourself.");
            return;
        }
        if(Server.players.size()!=1){
            gameActivity.append_to_log("(WARN) Already connected to a game with " +
                    Server.players.size() + " players");
            return;
        }
        String msg = "connect," + name + "," + ip_addr + "," + port;
        ArrayList<String> dest = new ArrayList<>();
        dest.add(dest_ip_addr + " " + dest_port);
        Broadcaster b = new Broadcaster(dest, msg);
        (new Thread(b)).start();
    }

    public void disconnect(String name) {
        for(Player p: Server.players) {
            if(p.getName().equals(name)) {
                Server.players.remove(p);
                // Broadcast it
                ArrayList<String> destinations = new ArrayList<>();
                String msg = "new_view";
                for(Player pp: Server.players) {
                    // Message
                    msg = msg.concat("," + pp.toString());
                    // Destinations
                    String player_data = pp.getIp_address().getHostAddress() + " " + pp.getPort();
                    destinations.add(player_data);
                }
                //gameActivity.clear_player_list();

                Broadcaster b = new Broadcaster(destinations, msg);
                (new Thread(b)).start();
                Server.players.clear();
                server.clearRound();
                return;
            }
        }
        throw new IllegalArgumentException("Name not found");
    }

    public void new_view(ArrayList<String> destinations, String message) {
        Broadcaster b = new Broadcaster(destinations, message);
        (new Thread(b)).start();
    }

    public void send_move(String name, String choice){
        ArrayList<String> destinations = new ArrayList<>();
        // Destinations
        for (Player p : Server.players) {
            if(!(p.getName().equals(name))) {
                // Destinations
                String player_data = p.getIp_address().getHostAddress() + " " + p.getPort();
                destinations.add(player_data);
            }
        }

        String msg = "choice," + name + "," + choice;
        gameActivity.game_panel_active(false);
        server.addMove(name, choice);
        server.finalizeRound();

        Broadcaster b = new Broadcaster(destinations, msg);
        (new Thread(b)).start();
    }
}
