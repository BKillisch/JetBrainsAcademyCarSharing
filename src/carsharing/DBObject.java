package carsharing;

public abstract class DBObject {
    private int id;
    private String name;

    public DBObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public DBObject(String name) {
        this(-1, name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
