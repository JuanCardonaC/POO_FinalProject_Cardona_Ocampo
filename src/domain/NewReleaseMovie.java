package domain;

public class NewReleaseMovie extends Movie {
    private static final long serialVersionUID = 1L;

    private static final double DAILY_RATE = 5.00;
    private static final double LATE_FEE_PER_DAY = 7.50;
    private static final int MAX_RENTAL_DAYS = 3;

    public NewReleaseMovie(String id, String title, int releaseYear, int durationMinutes, Category category) {
        super(id, title, releaseYear, durationMinutes, category);
    }

    @Override
    public double calculateRentalPrice(int days) {
        return DAILY_RATE * days;
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
        return "Estreno";
    }
}
