package cam.geekbrains.java.jdbc;

import java.sql.*;
import java.util.Scanner;

public class TableApp {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            connect();                /* Создание подключения к БД */
            addUser();                /* Добвление нового пользователя */
            deletePerName();          /* Метод удаления пользователя по имени */
//            showUsersByAge(20, 34);   /* Отображение списка пользователей в указанном диапозоне возрастов (min/max) */
//            creatDb();                /* Создание БД */
//            deleteDb();               /* Полное удаление БД */


            try (ResultSet rs = statement.executeQuery("SELECT * FROM Users;")){
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String Name = rs.getString("Имя");
                    int Age = rs.getInt("Возраст");
                    String email = rs.getString("email");
                    System.out.println(id + " " + Name + " " + Age + " " + email);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }


    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to connect to database");
        }
    }


    private static void creatDb() throws SQLException{
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS Users(" +
                "ID      INTEGER PRIMARY KEY ON CONFLICT IGNORE " +
                "UNIQUE ON CONFLICT IGNORE," +
                "Имя     TEXT NOT NULL," +
                "Возраст INTEGER NOT NULL," +
                "email   TEXT  NOT NULL  UNIQUE ON CONFLICT REPLACE)");
    }


    public static void showUsersByAge(int min, int max) {
        System.out.println("Пользователи в возрасте от " + min + " до " + max +" :");

        try (ResultSet rs = statement.executeQuery("SELECT * FROM Users;")) {
            while (rs.next()) {
                int id = rs.getInt(1);
                String Name = rs.getString("Имя");
                int Age = rs.getInt("Возраст");
                String email = rs.getString("email");
                if ((min <= Age) && (Age <= max)) {
                    System.out.println(id + " " + Name + " " + Age + " " + email);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("----------------------------");
    }


    public static void addUser() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:jdbc.db")) {
            System.out.println("Введите имя нового пользователя: ");
            String name = scanner.nextLine();

            System.out.println("Введите возраст: ");
            int age = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Введите почту: ");
            String mail = scanner.nextLine();

            System.out.println("Пользователь " + name + " успешно добавлен!");
            String add = "INSERT INTO Users (Имя, Возраст, email) Values (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(add);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, mail);

            int rows = preparedStatement.executeUpdate();

        } catch (Exception ex) {
            System.out.println("Connection failed...");

            System.out.println(ex);
        }
    }


    public static void deletePerName() throws SQLException{
        Scanner scannerForDelete = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:jdbc.db")) {
            System.out.println("Введите имя пользователя, которого необходимо удалить: ");
            String name = scannerForDelete.nextLine();

            System.out.println("Пользователь " + name + " успешно удален!");
            String del = "DELETE FROM Users WHERE Имя = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(del);
            preparedStatement.setString(1, name);

            int rows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void deleteDb() throws SQLException {
        statement.executeUpdate("DELETE FROM Users;");
    }


    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}