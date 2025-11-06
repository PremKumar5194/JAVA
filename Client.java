import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class TaxiClient extends JFrame {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    JTextArea outputArea;

    public TaxiClient() {
        setTitle("🚕 Taxi Ride Client");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Connect to Server
        try {
            socket = new Socket("localhost", 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Server not connected!");
        }

        // Input Panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField name = new JTextField();
        JTextField taxi = new JTextField();
        JTextField dist = new JTextField();
        JTextField rate = new JTextField();

        JButton addBtn = new JButton("Add Ride");
        JButton viewBtn = new JButton("View Rides");

        panel.add(new JLabel("Customer Name:"));
        panel.add(name);
        panel.add(new JLabel("Taxi Number:"));
        panel.add(taxi);
        panel.add(new JLabel("Distance (km):"));
        panel.add(dist);
        panel.add(new JLabel("Rate per km:"));
        panel.add(rate);
        panel.add(addBtn);
        panel.add(viewBtn);

        outputArea = new JTextArea();
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Button Actions
        addBtn.addActionListener(e -> {
            String msg = String.format("ADD|%s|%s|%s|%s", 
                name.getText(), taxi.getText(), dist.getText(), rate.getText());
            out.println(msg);
            try {
                outputArea.append(in.readLine() + "\n");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        viewBtn.addActionListener(e -> {
            out.println("VIEW");
            try {
                outputArea.setText(in.readLine().replace(",", "\t") + "\n");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new TaxiClient();
    }
}
