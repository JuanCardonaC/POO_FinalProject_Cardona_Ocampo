package domain;

public class RegularMovie extends Movie {
    private static final long serialVersionUID = 1L;

    private static final double DAILY_RATE = 3.00;
    private static final double LONG_RENTAL_DISCOUNT = 0.15;
    private static final double LATE_FEE_PER_DAY = 4.00;
    private static final int MAX_RENTAL_DAYS = 7;

    public RegularMovie(String id, String title, int releaseYear, int durationMinutes, Category category) {
        super(id, title, releaseYear, durationMinutes, category);
    }

    @Override
    public double calculateRentalPrice(int days) {
        double total = DAILY_RATE * days;
        if (days > 3) {
            total *= (1 - LONG_RENTAL_DISCOUNT);
        }
        return total;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return LATE_FEE_PER_DAY * daysLate;
    }

    @Override
    public int getMaxRentalDays() {
        return MAX_RENTAL_DAYS;
    }

    @Override
    public String getTypeName() {
        return "Regular";
    }
}
