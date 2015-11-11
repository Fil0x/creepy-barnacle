import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Timer;

public class ClientForm extends JFrame{
    @Override
    public String getName() {
        return name;
    }

    private String name;
    private Client rmi_client;
    private String balance = "Balance: 0.0 SEK";
    private final static String newline = "\n";

    private JPanel main_panel;
    private JLabel status_label;
    private JTextArea log;
    private JTable table;
    private JButton refreshButton, buyButton, sellButton, placeWishButton;
    private MyTableModel mytablemodel;

    public ClientForm(String name, Client c) {
        this.name = name;
        this.rmi_client = c;

        this.init_components();
    }

    public void quit() {
        ClientMediator.getInstance().unregister(name);

        System.exit(0);
    }

    private void init_components() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Virtual Marketplace - Connected as " + name + "- Client 0.1a");

        this.setLayout(new BorderLayout());

        this.create_menu();
        this.create_panel();
        this.create_status_bar();

        this.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null); // center the window

        // Get the initial balance
        ClientMediator c = ClientMediator.getInstance();
        this.update_status_label(c.getBalance(name));
        this.refresh_table(c.refresh());
    }

    public synchronized void append_to_log(String msg) { this.log.append(msg + newline); }

    public synchronized void update_status_label(float value) {
        this.status_label.setText("Balance: " + value + " SEK");
    }

    private void create_status_bar() {
        JPanel status_panel = new JPanel();
        status_panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status_panel.setPreferredSize(new Dimension(this.getWidth(), 16));
        status_panel.setLayout(new BoxLayout(status_panel, BoxLayout.X_AXIS));
        status_label = new JLabel(this.balance);
        status_label.setHorizontalAlignment(SwingConstants.LEFT);
        status_panel.add(status_label);

        this.add(status_panel, BorderLayout.SOUTH);
    }

    private void create_panel() {
        main_panel = new JPanel();
        main_panel.setLayout(new GridBagLayout());
        main_panel.setPreferredSize(new Dimension(600, 500));

        GridBagConstraints c = new GridBagConstraints();

        // TABLE for the marketplace
        mytablemodel = new MyTableModel();
        table = new JTable(mytablemodel);
        //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        JScrollPane scrollPane =  new JScrollPane(table);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        main_panel.add(scrollPane, c);

        // REFRESH button
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ButtonClickListener());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0.0;
        main_panel.add(refreshButton, c);

        // BUY button
        buyButton = new JButton("Buy item");
        buyButton.addActionListener(new ButtonClickListener());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.0;
        main_panel.add(buyButton, c);

        // SELL button
        sellButton = new JButton("Sell item");
        sellButton.addActionListener(new ButtonClickListener());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.0;
        main_panel.add(sellButton, c);

        // PLACE WISH button
        placeWishButton = new JButton("Place wish");
        placeWishButton.addActionListener(new ButtonClickListener());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.0;
        main_panel.add(placeWishButton, c);

        // LABELS
        JLabel log_label = new JLabel("Log:");
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        main_panel.add(log_label, c);

        JLabel table_label = new JLabel("Available items:");
        c.gridx = 0;
        c.gridy = 1;
        main_panel.add(table_label, c);

        // LOG
        log = new JTextArea();
        log.setEditable(false);
        DefaultCaret caret = (DefaultCaret)log.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 2;
        main_panel.add(scroll, c);

        this.add(main_panel, BorderLayout.CENTER);
    }

    private void create_menu() {
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Actions");

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitAction());
        menu.add(exit);

        mb.add(menu);
        this.setJMenuBar(mb);
    }

    private void createSellPopup() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JLabel itemName_label = new JLabel("Item name:");
        JTextField itemName_input = new JTextField("");
        JLabel price_label = new JLabel("Price: ");
        JTextField price_input = new JTextField();

        panel.add(itemName_label);
        panel.add(itemName_input);
        panel.add(price_label);
        panel.add(price_input);

        int okCxl = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(ClientForm.this),
                panel,
                "(" + name + ") Sell an item",
                JOptionPane.OK_CANCEL_OPTION);

        if (okCxl == JOptionPane.OK_OPTION)
        {
            ClientMediator c = ClientMediator.getInstance();
            c.sell(name, itemName_input.getText(), Float.parseFloat(price_input.getText()));
            this.refresh_table(c.refresh());
        }
    }

    public void refresh_table(HashMap<String, Item> refresh) {
        MyTableModel t = (MyTableModel) this.table.getModel();
        t.updateData(refresh);
    }

    private void createWishPopup() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JLabel itemName_label = new JLabel("Filter:");
        JTextField itemName_input = new JTextField();
        JLabel price_label = new JLabel("Max price:");
        JTextField price_input = new JTextField();

        panel.add(itemName_label);
        panel.add(itemName_input);
        panel.add(price_label);
        panel.add(price_input);

        int okCxl = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(ClientForm.this),
                panel,
                "(" + name + ") Add an item to your wishlist",
                JOptionPane.OK_CANCEL_OPTION);

        if (okCxl == JOptionPane.OK_OPTION)
        {
            ClientMediator c = ClientMediator.getInstance();
            float price = Float.parseFloat(price_input.getText());
            if(c.placeWishList(name, itemName_input.getText().trim(), price))
                append_to_log("Added to wishlist: [" + itemName_input.getText().trim() + "] < " + price);
            else
                append_to_log("Add to wishlist failed");
        }
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String choice = e.getActionCommand();

            int selectedRow = table.getSelectedRow();

            ClientMediator clientMediator = ClientMediator.getInstance();

            if (choice.equals("Refresh")) {
                ClientForm.this.refresh_table(clientMediator.refresh());
            }
            else if (choice.equals("Buy item")) {
                if (selectedRow == -1) {
                    ClientForm.this.append_to_log("Choose an item to buy.");
                    return;
                }

                String id = (String) table.getModel().getValueAt(selectedRow, 0);
                Item i = clientMediator.buy(name, id);
                if (i != null) {
                    // Transaction succeeded
                    append_to_log("Bought [" + i.getItem_name() + "] for " + i.getPrice() + " SEK");
                    ClientForm.this.update_status_label(clientMediator.getBalance(name));
                    ClientForm.this.refresh_table(clientMediator.refresh());
                }
                else {
                    // Not enough credits
                    ClientForm.this.append_to_log("Error: Not enough funds or own item.");
                }
            }
            else if (choice.equals("Sell item")) {
                createSellPopup();
            }
            else if (choice.equals("Place wish")) {
                createWishPopup();

            }
            else {
                throw new IllegalArgumentException("(ClientMediator) Invalid choice.");
            }

        }
    }

    private class ExitAction extends AbstractAction {
        public ExitAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClientForm.this.quit();
        }
    }
}

class MyTableModel extends AbstractTableModel {

    private String[] columnNames = {"ItemID", "Name", "Price"};

    private List<Object[]> data;

    public MyTableModel() {
        super();
        data = new ArrayList<>();
    }

    public void addRow(String itemid, String itemname, float value) {
        data.add(new Object[]{itemid, itemname, value});
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.data.get(rowIndex)[columnIndex];
    }

    public void updateData(HashMap<String, Item> items) {
        data = new ArrayList<>();
        for(Item i: items.values()) {
            data.add(i.toObjectArray());
        }

        this.fireTableRowsInserted(0, data.size()-1);
    }

    public void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data.get(i)[j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
}
