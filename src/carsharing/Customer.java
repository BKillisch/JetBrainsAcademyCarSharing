package carsharing;

public class Customer extends DBObject{
    private int rented_car_id = -1;

    public Customer(int id, String name, int rented_car_id) {
        super(id, name);
        this.rented_car_id = rented_car_id;
    }

    public Customer(int id, String name) {
        super(id, name);
    }

    public Customer(String name) {
        super(name);
    }

    public int getRented_car_id() {
        return rented_car_id;
    }

    public void setRented_car_id(int rented_car_id) {
        this.rented_car_id = rented_car_id;
    }
}
