package domain;

public class Employee extends Person {
    private static final long serialVersionUID = 1L;

    private String position;
    private boolean active;

    public Employee(String id, String fullName, String phone, String email, String position) {
        super(id, fullName, phone, email);
        this.position = position;
        this.active = true;
    }

    public String getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public String getRole() {
        return "Empleado";
    }
}
