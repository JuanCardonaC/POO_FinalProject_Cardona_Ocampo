package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Rental implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Customer customer;
    private Employee employee;
    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private RentalStatus status;
    private String cancellationReason;
    private ArrayList<RentalItem> items;
    private ArrayList<Payment> payments;

    public Rental(String id, Customer customer, Employee employee,
                  LocalDate rentalDate, LocalDate dueDate) {
        this.id = id;
        this.customer = customer;
        this.employee = employee;
        this.rentalDate = rentalDate;
        this.dueDate = dueDate;
        this.status = RentalStatus.ACTIVE;
        this.items = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public void addItem(RentalItem item) {
        items.add(item);
    }

    public double calculateBaseTotal() {
        return items.stream()
                .mapToDouble(RentalItem::getSubtotal)
                .sum();
    }

    public double calculateLateFees(LocalDate date) {
        int daysLate = getDaysLate(date);
        if (daysLate <= 0) return 0;
        return items.stream()
                .mapToDouble(item -> item.calculateLateFee(daysLate))
                .sum();
    }

    public int getDaysLate(LocalDate date) {
        return (int) Math.max(0, ChronoUnit.DAYS.between(dueDate, date));
    }

    public boolean isOverdue(LocalDate today) {
        return status == RentalStatus.ACTIVE && today.isAfter(dueDate);
    }

    public void registerReturn(LocalDate date) {
        this.returnDate = date;
        this.status = RentalStatus.RETURNED;
        for (RentalItem item : items) {
            item.getCopy().markAsReturned();
        }
    }

    public void cancel(String reason) {
        this.cancellationReason = reason;
        this.status = RentalStatus.CANCELLED;
        for (RentalItem item : items) {
            item.getCopy().markAsReturned();
        }
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    public double getBalanceDue() {
        double totalDue = calculateBaseTotal();
        if (returnDate != null) {
            totalDue += calculateLateFees(returnDate);
        }
        double totalPaid = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        return totalDue - totalPaid;
    }

    // ── Getters ──────────────────────────────────────────
    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public ArrayList<RentalItem> getItems() {
        return items;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    @Override
    public String toString() {
        return String.format("Alquiler %s | %s | %s | %s → %s%s",
                id, customer.getFullName(), status, rentalDate, dueDate,
                returnDate != null ? " | Devuelto: " + returnDate : "");
    }
}
