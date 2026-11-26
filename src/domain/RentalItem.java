package domain;

import java.io.Serializable;

public class RentalItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private MovieCopy copy;
    private int days;
    private double price;

    public RentalItem(MovieCopy copy, int days) {
        this.copy = copy;
        this.days = days;
        this.price = copy.getMovie().calculateRentalPrice(days);
    }

    public MovieCopy getCopy() {
        return copy;
    }

    public int getDays() {
        return days;
    }

    public double getPrice() {
        return price;
    }

    public Movie getMovie() {
        return copy.getMovie();
    }

    public double getSubtotal() {
        return price;
    }

    public double calculateLateFee(int daysLate) {
        return copy.getMovie().calculateLateFee(daysLate);
    }

    @Override
    public String toString() {
        return String.format("  %s (Copia: %s) - %d días - $%.2f",
                copy.getMovie().getTitle(), copy.getCode(), days, price);
    }
}
