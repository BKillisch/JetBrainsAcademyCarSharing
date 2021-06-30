package carsharing;

public class Company extends DBObject{
    public Company(int id, String name) {
        super(id, name);
    }

    public Company(String name) {
        super(name);
    }
}
