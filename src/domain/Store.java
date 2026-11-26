package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Store implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private ArrayList<Category> categories;
    private ArrayList<Movie> movies;
    private ArrayList<Customer> customers;
    private ArrayList<Employee> employees;
    private ArrayList<Rental> rentals;

    public Store(String name) {
        this.name = name;
        this.categories = new ArrayList<>();
        this.movies = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.rentals = new ArrayList<>();
    }

    // CATEGORY MANAGEMENT

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(String id) {
        boolean hasMovies = movies.stream()
                .anyMatch(m -> m.isActive() && m.getCategory() != null
                        && m.getCategory().getId().equals(id));
        if (hasMovies) {
            throw new IllegalStateException(
                    "No se puede eliminar: la categoría tiene películas activas asociadas.");
        }
        categories.removeIf(c -> c.getId().equals(id));
    }

    public Category findCategoryById(String id) {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);
    }

    // MOVIE MANAGEMENT

    public void registerMovie(Movie movie) {
        movies.add(movie);
    }

    public Movie findMovieById(String id) {
        return movies.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst().orElse(null);
    }

    public void deactivateMovie(String id) {
        Movie movie = findMovieById(id);
        if (movie == null) {
            throw new IllegalArgumentException("Película no encontrada: " + id);
        }
        boolean hasActiveRentals = rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.ACTIVE)
                .flatMap(r -> r.getItems().stream())
                .anyMatch(item -> item.getMovie().getId().equals(id));
        if (hasActiveRentals) {
            throw new IllegalStateException(
                    "No se puede desactivar: la película tiene alquileres activos.");
        }
        movie.deactivate();
    }

    public List<Movie> getMoviesByCategory(Category category) {
        return movies.stream()
                .filter(m -> m.isActive()
                        && m.getCategory().getId().equals(category.getId()))
                .collect(Collectors.toList());
    }

    public List<Movie> getAvailableMovies() {
        return movies.stream()
                .filter(Movie::isAvailable)
                .collect(Collectors.toList());
    }

    public MovieCopy findCopyByCode(String code) {
        return movies.stream()
                .flatMap(m -> m.getCopies().stream())
                .filter(c -> c.getCode().equals(code))
                .findFirst().orElse(null);
    }

    // CUSTOMER MANAGEMENT

    public void registerCustomer(Customer customer) {
        customers.add(customer);
    }

    public Customer findCustomerById(String id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);
    }

    public void deactivateCustomer(String id) {
        Customer customer = findCustomerById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Cliente no encontrado: " + id);
        }
        if (customer.hasActiveRentals()) {
            throw new IllegalStateException(
                    "No se puede desactivar: el cliente tiene alquileres activos.");
        }
        customer.deactivate();
    }

    // EMPLOYEE MANAGEMENT

    public void registerEmployee(Employee employee) {
        employees.add(employee);
    }

    public Employee findEmployeeById(String id) {
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst().orElse(null);
    }

    // RENTAL MANAGEMENT

    public Rental createRental(Customer c, Employee e,
            List<MovieCopy> copies, int days) {
        if (!c.canRent()) {
            throw new IllegalStateException(
                    "El cliente no puede alquilar (inactivo o límite de "
                            + Customer.getMaxActiveRentals() + " alquileres activos alcanzado).");
        }

        int maxDays = copies.stream()
                .mapToInt(copy -> copy.getMovie().getMaxRentalDays())
                .min().orElse(days);

        if (days > maxDays) {
            throw new IllegalArgumentException(
                    "Los días solicitados (" + days
                            + ") exceden el máximo permitido (" + maxDays + ").");
        }

        String rentalId = "R-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(days);
        Rental rental = new Rental(rentalId, c, e, today, dueDate);

        for (MovieCopy copy : copies) {
            if (!copy.isAvailable()) {
                throw new IllegalStateException(
                        "La copia " + copy.getCode() + " no está disponible.");
            }
            RentalItem item = new RentalItem(copy, days);
            rental.addItem(item);
            copy.markAsRented();
        }

        c.addRental(rental);
        rentals.add(rental);
        return rental;
    }

    public double returnRental(String rentalId, LocalDate returnDate) {
        Rental rental = findRentalById(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException("Alquiler no encontrado: " + rentalId);
        }
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("El alquiler no está activo.");
        }
        double lateFees = rental.calculateLateFees(returnDate);
        rental.registerReturn(returnDate);
        return lateFees;
    }

    public void cancelRental(String rentalId, String reason) {
        Rental rental = findRentalById(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException("Alquiler no encontrado: " + rentalId);
        }
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("El alquiler no está activo.");
        }
        rental.cancel(reason);
    }

    public Rental findRentalById(String id) {
        return rentals.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst().orElse(null);
    }

    public List<Rental> getOverdueRentals(LocalDate today) {
        return rentals.stream()
                .filter(r -> r.isOverdue(today))
                .collect(Collectors.toList());
    }

    public double calculateTotalIncome() {
        return rentals.stream()
                .flatMap(r -> r.getPayments().stream())
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    // RECOMMENDATIONS

    public List<Movie> getRecommendations(Customer customer) {
        List<Category> rentedCategories = customer.getRentedCategories();

        // Movies already rented by this customer (by ID)
        List<String> rentedMovieIds = customer.getRentalHistory().stream()
                .flatMap(r -> r.getItems().stream())
                .map(item -> item.getMovie().getId())
                .distinct()
                .collect(Collectors.toList());

        if (!rentedCategories.isEmpty()) {
            // Recommend available movies from the same categories, not yet rented
            List<Movie> recs = movies.stream()
                    .filter(Movie::isAvailable)
                    .filter(m -> !rentedMovieIds.contains(m.getId()))
                    .filter(m -> rentedCategories.stream()
                            .anyMatch(cat -> cat.getId().equals(m.getCategory().getId())))
                    .collect(Collectors.toList());
            if (!recs.isEmpty())
                return recs;
        }

        // Fallback: recommend all available movies not yet rented
        return movies.stream()
                .filter(Movie::isAvailable)
                .filter(m -> !rentedMovieIds.contains(m.getId()))
                .collect(Collectors.toList());
    }

    // Getters
    public String getName() {
        return name;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public ArrayList<Rental> getRentals() {
        return rentals;
    }
}
