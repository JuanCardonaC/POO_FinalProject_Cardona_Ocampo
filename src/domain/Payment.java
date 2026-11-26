package domain;

import java.io.Serializable;
import java.time.LocalDate;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private double amount;
    private LocalDate date;
    private PaymentType type;
    private PaymentMethod method;

    public Payment(String id, double amount, LocalDate date, PaymentType type, PaymentMethod method) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public PaymentType getType() {
        return type;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return String.format("Pago %s: $%.2f (%s, %s) - %s", id, amount, type, method, date);
    }
}
