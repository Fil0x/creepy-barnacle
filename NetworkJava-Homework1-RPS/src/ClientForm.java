import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Timer;

public class ClientForm extends JFrame{
    private int name;
    private String ip_addr;
    private int port;
    private int timeLeft = 30;
    private String waiting = "Connect to another player or wait for a connection.";
    private final static String newline = "\n";

    private JPanel main_panel;
    private JLabel status_label;
    private Timer timer;
    private JTextArea connected;
    private JTextArea log;


    public ClientForm(int name, String ip_addr, int port) {
        this.name = name;
        this.ip_addr = ip_addr;
        this.port = port;

        this.init_components();
    }

    private void init_components() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("RPS Client(" + this.name + ", "  + this.ip_addr + ", " + this.port + ")");

        this.setLayout(new BorderLayout());

        this.create_menu();
        this.create_panel();
        this.create_status_bar();

        this.pack();
        this.setLocationRelativeTo(null); // center the window
    }

    public synchronized void update_player_list(ArrayList<Player> players) {
        // Clear the text area to rebuild it
        this.connected.setText("");

        for(Player p: players) {
            String line = p.getName() + "(" + p.getScore() + ")(" + p.getTotalScore() + ")";
            this.connected.append(line + this.newline);
        }
    }

    public synchronized void clear_player_list() {
        this.connected.setText("");
    }

    public synchronized void append_to_log(String msg) { this.log.append(msg+"\n"); }

    private void start_timer() {
        timer = new Timer();
        TimerTask timeRefreshTask = new TimerTask() {
            @Override
            public void run() {
                status_label.setText("Time left before end of round: " + Integer.toString(--timeLeft));
            }
        };
        timer.schedule(timeRefreshTask, 0, 1000);
    }

    private void create_status_bar() {
        JPanel status_panel = new JPanel();
        status_panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status_panel.setPreferredSize(new Dimension(this.getWidth(), 16));
        status_panel.setLayout(new BoxLayout(status_panel, BoxLayout.X_AXIS));
        status_label = new JLabel(this.waiting);
        status_label.setHorizontalAlignment(SwingConstants.LEFT);
        status_panel.add(status_label);

        this.add(status_panel, BorderLayout.SOUTH);
    }

    private void create_panel() {
        main_panel = new JPanel();
        main_panel.setLayout(new GridBagLayout());
        main_panel.setPreferredSize(new Dimension(300, 300));

        GridBagConstraints c = new GridBagConstraints();

        // ROCK button
        JButton rock_button = new JButton("Rock");
        rock_button.addActionListener(new ButtonClickListener());
        rock_button.setName("rock");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        main_panel.add(rock_button, c);

        // SCISSORS button
        JButton scissors_button = new JButton("Scissors");
        scissors_button.addActionListener(new ButtonClickListener());
        rock_button.setName("scissors");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        main_panel.add(scissors_button, c);

        // PAPER button
        JButton paper_button = new JButton("Paper");
        paper_button.addActionListener(new ButtonClickListener());
        rock_button.setName("paper");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        main_panel.add(paper_button, c);

        // LABELS
        JLabel log_label = new JLabel("Log:");
        c.gridx = 0;
        c.gridy = 1;
        main_panel.add(log_label, c);

        JLabel player_label = new JLabel("Players(X):");
        c.gridx = 2;
        c.gridy = 1;
        main_panel.add(player_label, c);

        // LOG
        log = new JTextArea();
        log.setEnabled(false);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 2;
        main_panel.add(scroll, c);

        // PLAYERS CONNECTED
        connected = new JTextArea();
        connected.setEnabled(false);
        JScrollPane conn_scroll = new JScrollPane(connected);
        conn_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 2;
        c.gridwidth = 2;
        c.gridy = 2;
        main_panel.add(conn_scroll, c);

        this.add(main_panel, BorderLayout.CENTER);
    }

    private void create_menu() {
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Actions");

        JMenuItem connect = new JMenuItem("Connect");
        connect.addActionListener(new ConnectAction());
        menu.add(connect);

        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.addActionListener(new DisconnectAction());
        menu.add(disconnect);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitAction());
        menu.add(exit);

        mb.add(menu);
        this.setJMenuBar(mb);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if( command.equals( "Rock" ))  {
                System.out.println("Rock");
            }
            else if( command.equals( "Scissors" ) )  {
                System.out.println("Scissors");
            }
            else if( command.equals("Paper")) {
                System.out.println("Paper");
            }
        }
    }

    private class ConnectAction extends AbstractAction {
        public ConnectAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 2));

            JLabel ip_label = new JLabel("Enter IP:");
            JTextField ip_input = new JTextField("127.0.0.1");
            JLabel port_label = new JLabel("Enter Port:");
            JTextField port_input = new JTextField();

            panel.add(ip_label);
            panel.add(ip_input);
            panel.add(port_label);
            panel.add(port_input);

            int okCxl = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(ClientForm.this),
                                                        panel,
                                                        "Connect to another player",
                                                        JOptionPane.OK_CANCEL_OPTION);

            if (okCxl == JOptionPane.OK_OPTION &&
                Utilities.verify_ip(ip_input.getText()) &&
                Utilities.verify_port(port_input.getText()))
            {
                String ip_to = ip_input.getText();
                String port_to = port_input.getText();
                BroadcasterMediator bm = new BroadcasterMediator(ClientForm.this);
                bm.connect(ip_to, port_to,
                        ClientForm.this.name, ClientForm.this.ip_addr, ClientForm.this.port);
            }
        }
    }

    private class DisconnectAction extends AbstractAction {
        public DisconnectAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BroadcasterMediator bm = new BroadcasterMediator(ClientForm.this);
            bm.disconnect(Integer.toString(ClientForm.this.name));
        }
    }

    private class ExitAction extends AbstractAction {
        public ExitAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public String getName(){
        return Integer.toString(name);
    }
}
