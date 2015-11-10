import java.sql.*;

public class DBMediator {
    private static DBMediator instance;
    private Connection connection;
    private PreparedStatement loginStatement;
    private PreparedStatement createStatement;

    public DBMediator(){
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/market", "root", "root");
            prepareStatements();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void prepareStatements() throws SQLException {
        loginStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        createStatement = connection.prepareStatement("INSERT INTO users VALUES(?,?, 0, 0)");
    }

    public static DBMediator getInstance(){
        if(instance == null){
            instance = new DBMediator();
        }
        return instance;
    }


    public boolean login(String username, String password){
        try {
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);
            ResultSet result = loginStatement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createClient(String username, String password){
        try {
            createStatement.setString(1, username);
            createStatement.setString(2, password);
            int rows = createStatement.executeUpdate();
            if (rows == 1) {
                System.out.println("(DBMediator) Client created: " + username);
            } else {
                throw new RejectedException("(DBMediator) Cannot create client :" + username);
            }
        } catch (SQLException | RejectedException e) {
            e.printStackTrace();
        }
    }
}
