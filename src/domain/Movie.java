package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private int releaseYear;
    private int durationMinutes;
    private boolean active;
    private Category category;
    private ArrayList<MovieCopy> copies;

    public Movie(String id, String title, int releaseYear, int durationMinutes, Category category) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.durationMinutes = durationMinutes;
        this.category = category;
        this.active = true;
        this.copies = new ArrayList<>();
    }

    public MovieCopy addCopy(String code) {
        MovieCopy copy = new MovieCopy(code, this);
        copies.add(copy);
        return copy;
    }

    public List<MovieCopy> getAvailableCopies() {
        return copies.stream()
                .filter(MovieCopy::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean isAvailable() {
        return active && !getAvailableCopies().isEmpty();
    }

    public void deactivate() {
        this.active = false;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public Category getCategory() {
        return category;
    }

    public ArrayList<MovieCopy> getCopies() {
        return copies;
    }

    // Abstract methods
    public abstract double calculateRentalPrice(int days);

    public abstract double calculateLateFee(int daysLate);

    public abstract int getMaxRentalDays();

    public abstract String getTypeName();

    @Override
    public String toString() {
        return id + " - " + title + " (" + releaseYear + ") [" + getTypeName() + "]";
    }
}
