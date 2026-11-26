package domain;

import java.io.Serializable;

public class MovieCopy implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private Movie movie;
    private CopyStatus status;

    public MovieCopy(String code, Movie movie) {
        this.code = code;
        this.movie = movie;
        this.status = CopyStatus.AVAILABLE;
    }

    public String getCode() {
        return code;
    }

    public Movie getMovie() {
        return movie;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == CopyStatus.AVAILABLE;
    }

    public void markAsRented() {
        this.status = CopyStatus.RENTED;
    }

    public void markAsReturned() {
        this.status = CopyStatus.AVAILABLE;
    }

    public void markAsDamaged() {
        this.status = CopyStatus.DAMAGED;
    }

    public void retire() {
        this.status = CopyStatus.RETIRED;
    }

    @Override
    public String toString() {
        return code + " [" + status + "] - " + movie.getTitle();
    }
}
