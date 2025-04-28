package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private static MyDataBase instance;
    private final String url = "jdbc:mysql://localhost:3306/digifarm2";
    private final String user = "root";
    private final String pws = "";
    private Connection connection;

    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(url, user, pws);
            System.out.println("connecter a la base de données");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();

        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}

