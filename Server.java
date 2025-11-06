import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaxiServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Taxi Server started. Waiting for clients...");
            
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    Socket socket;
    Connection con;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/taxi_db", "root", "your_password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("ADD")) {
                    // Example: ADD|Ravi|TN10AB1234|10|15
                    String[] parts = line.split("\\|");
                    String name = parts[1];
                    String taxi = parts[2];
                    double dist = Double.parseDouble(parts[3]);
                    double rate = Double.parseDouble(parts[4]);
                    double total = dist * rate;
                    String sql = "INSERT INTO rides (customer_name, taxi_number, distance_km, rate_per_km, total_fare, ride_date) VALUES (?, ?, ?, ?, ?, CURDATE())";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, name);
                    ps.setString(2, taxi);
                    ps.setDouble(3, dist);
                    ps.setDouble(4, rate);
                    ps.setDouble(5, total);
                    ps.executeUpdate();
                    out.println("Ride added successfully!");
                } 
                else if (line.equals("VIEW")) {
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM rides");
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getInt("ride_id")).append(",")
                          .append(rs.getString("customer_name")).append(",")
                          .append(rs.getString("taxi_number")).append(",")
                          .append(rs.getDouble("distance_km")).append(",")
                          .append(rs.getDouble("total_fare")).append("\n");
                    }
                    out.println(sb.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
