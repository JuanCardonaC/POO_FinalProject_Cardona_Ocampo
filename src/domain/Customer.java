package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Customer extends Person {
    private static final long serialVersionUID = 1L;

    private static final int MAX_ACTIVE_RENTALS = 3;
    private boolean active;
    private ArrayList<Rental> rentalHistory;

    public Customer(String id, String fullName, String phone, String email) {
        super(id, fullName, phone, email);
        this.active = true;
        this.rentalHistory = new ArrayList<>();
    }

    public boolean isActive() {
        return active;
    }

    public boolean canRent() {
        return active && countActiveRentals() < MAX_ACTIVE_RENTALS;
    }

    public int countActiveRentals() {
        return (int) rentalHistory.stream()
                .filter(r -> r.getStatus() == RentalStatus.ACTIVE)
                .count();
    }

    public boolean hasActiveRentals() {
        return countActiveRentals() > 0;
    }

    public void addRental(Rental rental) {
        rentalHistory.add(rental);
    }

    public List<Category> getRentedCategories() {
        return rentalHistory.stream()
                .flatMap(r -> r.getItems().stream())
                .map(item -> item.getMovie().getCategory())
                .distinct()
                .collect(Collectors.toList());
    }

    public void deactivate() {
        this.active = false;
    }

    public ArrayList<Rental> getRentalHistory() {
        return rentalHistory;
    }

    public static int getMaxActiveRentals() {
        return MAX_ACTIVE_RENTALS;
    }

    @Override
    public String getRole() {
        return "Cliente";
    }
}
