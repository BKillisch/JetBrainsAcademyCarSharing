package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {
    private final String dbFileName;

    public DBHandler(String dbFileName) {
        this.dbFileName = dbFileName;

        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/" + dbFileName);
            conn.setAutoCommit(true);
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS company (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50) UNIQUE NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS car (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50) UNIQUE NOT NULL, company_id INTEGER NOT NULL, FOREIGN KEY (company_id) REFERENCES company(id) )");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS customer (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50) UNIQUE NOT NULL, rented_car_id INTEGER DEFAULT NULL, FOREIGN KEY (rented_car_id) REFERENCES car(id) )");
            st.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error while accessing the database!");
        }
    }

    public List<DBObject> getAll(String classname) {
        List<DBObject> res = new ArrayList<>();

        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/" + dbFileName);
            conn.setAutoCommit(true);
            Statement st = conn.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM " + classname);

            switch (classname) {
                case "car":
                    while (resultSet.next()) {
                        res.add(new Car(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
                    }
                    break;
                case "company":
                    while (resultSet.next()) {
                        res.add(new Company(resultSet.getInt(1), resultSet.getString(2)));
                    }
                    break;
                case "customer":
                    while (resultSet.next()) {
                        res.add(new Customer(resultSet.getInt(1), resultSet.getString(2)));
                    }
                    break;
            }

            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while accessing the database!");
        }

        return res;
    }

    public DBObject get(int id, String classname) {
        DBObject res = null;

        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/" + dbFileName);
            conn.setAutoCommit(true);
            Statement st = conn.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM " + classname + " WHERE id=" + id);

            switch (classname) {
                case "car":
                    while (resultSet.next()) {
                        res = new Car(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
                    }
                    break;
                case "company":
                    while (resultSet.next()) {
                        res = new Company(resultSet.getInt(1), resultSet.getString(2));
                    }
                    break;
                case "customer":
                    while (resultSet.next()) {
                        res = new Customer(resultSet.getInt(1), resultSet.getString(2));
                    }
                    break;
            }

            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while accessing the database!");
        }

        return res;
    }

    public void add(DBObject dbObject) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/" + dbFileName);
            conn.setAutoCommit(true);
            Statement st = conn.createStatement();

            String id = "null";

            if (dbObject.getId() != -1) {
                id = Integer.toString(dbObject.getId());
            }

            StringBuilder sql = new StringBuilder("INSERT INTO ")
                    .append(dbObject.getClass().getSimpleName().toLowerCase())
                    .append(" VALUES (")
                    .append(id)
                    .append(", '")
                    .append(dbObject.getName())
                    .append("'");

            switch (dbObject.getClass().getSimpleName()) {
                case "Car":
                    sql.append(", ")
                            .append(((Car) dbObject).getCompany_id());
                    break;
                case "Customer":
                    String rid = Integer.toString(((Customer) dbObject).getRented_car_id());

                    if (rid.equals("-1")) {
                        rid = "null";
                    }
                    sql.append(", ")
                            .append(rid);
                    break;
            }

            st.executeUpdate(sql.append(")").toString());

            st.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error while accessing the database!");
        }
    }

    public void updateUserRentStatus(int carId, int userId) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/" + dbFileName);
            conn.setAutoCommit(true);
            Statement st = conn.createStatement();

            String id = String.valueOf(carId);

            if (id.equals("-1")) {
                id = "null";
            }

            st.executeUpdate("UPDATE customer SET rented_car_id = " + id + " WHERE id=" + userId);

            st.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error while accessing the database!");
        }
    }

    public List<Car> getAvailableCarsAtComp(int companyId) {
        List<Car> res = new ArrayList<>();

        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/" + dbFileName);
            conn.setAutoCommit(true);
            Statement st = conn.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM car WHERE company_id = " + companyId
                    + " AND id NOT IN (SELECT rented_car_id AS id FROM customer WHERE rented_car_id IS NOT NULL)");

            while (resultSet.next()) {
                res.add(new Car(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
            }

            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while accessing the database!");
        }

        return res;
    }
}
