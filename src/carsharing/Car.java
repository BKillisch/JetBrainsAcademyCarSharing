package carsharing;

public class Car extends DBObject{
    private int company_id;

    public Car(int id, String name, int company_id) {
        super(id, name);
        this.company_id = company_id;
    }

    public Car(String name, int company_id) {
        super(name);
        this.company_id = company_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }
}
