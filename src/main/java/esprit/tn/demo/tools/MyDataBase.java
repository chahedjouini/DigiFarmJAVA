package esprit.tn.demo.tools;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private static Connection connection;
    private static MyDataBase instance;

    private final String URL = "jdbc:mysql://localhost:3306/digifarm2";
    private final String USER = "root";
    private final String PASSWORD = "";

    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("DB Connected");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}