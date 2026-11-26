package ui;

import domain.*;
import data.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleUI {

    private final Scanner scanner;
    private Store store;
    private Employee currentEmployee;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    // START

    public void start() {
        printHeader();

        store = DataStore.load();
        if (store == null) {
            store = new Store("Movie Rental Store");
            initializeDefaultData();
            System.out.println("  » Sistema iniciado con datos de ejemplo.\n");
        }

        login();

        boolean running = true;
        while (running) {
            showMainMenu();
            int option = readInt("  Seleccione una opción: ");
            System.out.println();
            switch (option) {
                case 1:
                    menuCategories();
                    break;
                case 2:
                    menuMovies();
                    break;
                case 3:
                    menuCustomers();
                    break;
                case 4:
                    menuEmployees();
                    break;
                case 5:
                    menuRentals();
                    break;
                case 6:
                    menuReports();
                    break;
                case 7:
                    menuRecommendations();
                    break;
                case 0:
                    DataStore.save(store);
                    System.out.println("\n  ¡Hasta luego, " + currentEmployee.getFullName() + "!");
                    running = false;
                    break;
                default:
                    System.out.println("  ⚠ Opción no válida.");
            }
        }
        scanner.close();
    }

    // HEADER & MAIN MENU

    private void printHeader() {
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      MOVIE RENTAL MANAGEMENT SYSTEM          ║");
        System.out.println("║      Sistema de Alquiler de Películas        ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }

    private void showMainMenu() {
        System.out.println("\n═══════════ MENÚ PRINCIPAL ═══════════");
        System.out.println("  1. Gestión de Categorías");
        System.out.println("  2. Gestión de Películas");
        System.out.println("  3. Gestión de Clientes");
        System.out.println("  4. Gestión de Empleados");
        System.out.println("  5. Gestión de Alquileres");
        System.out.println("  6. Reportes");
        System.out.println("  7. Recomendaciones");
        System.out.println("  0. Guardar y Salir");
        System.out.println("══════════════════════════════════════");
    }

    // LOGIN

    private void login() {
        System.out.println("── Inicio de Sesión (Empleado) ──");
        while (currentEmployee == null) {
            String empId = readLine("  ID del empleado: ");
            Employee emp = store.findEmployeeById(empId);
            if (emp == null) {
                System.out.println("   Empleado no encontrado.");
            } else if (!emp.isActive()) {
                System.out.println("   El empleado está inactivo.");
            } else {
                currentEmployee = emp;
                System.out.println("\n   Bienvenido/a, " + emp.getFullName()
                        + " (" + emp.getPosition() + ")\n");
            }
        }
    }

    // 1. CATEGORIES

    private void menuCategories() {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── Gestión de Categorías ───");
            System.out.println("  1. Listar categorías");
            System.out.println("  2. Agregar categoría");
            System.out.println("  3. Editar categoría");
            System.out.println("  4. Eliminar categoría");
            System.out.println("  0. Volver");
            int opt = readInt("  Opción: ");
            System.out.println();
            switch (opt) {
                case 1:
                    listCategories();
                    break;
                case 2:
                    addCategory();
                    break;
                case 3:
                    editCategory();
                    break;
                case 4:
                    removeCategory();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("  ⚠ Opción no válida.");
            }
        }
    }

    private void listCategories() {
        List<Category> cats = store.getCategories();
        if (cats.isEmpty()) {
            System.out.println("  No hay categorías registradas.");
            return;
        }
        System.out.println("  ┌───────────────────────────────────────────────┐");
        System.out.printf("  │ %-6s │ %-15s │ %-15s │%n", "ID", "Nombre", "Descripción");
        System.out.println("  ├───────────────────────────────────────────────┤");
        for (Category c : cats) {
            System.out.printf("  │ %-6s │ %-15s │ %-15s │%n",
                    c.getId(), c.getName(), c.getDescription());
        }
        System.out.println("  └───────────────────────────────────────────────┘");
    }

    private void addCategory() {
        String id = readLine("  ID: ");
        if (store.findCategoryById(id) != null) {
            System.out.println("  ⚠ Ya existe una categoría con ese ID.");
            return;
        }
        String name = readLine("  Nombre: ");
        String desc = readLine("  Descripción: ");
        store.addCategory(new Category(id, name, desc));
        System.out.println("  ✓ Categoría agregada.");
    }

    private void editCategory() {
        String id = readLine("  ID de la categoría a editar: ");
        Category cat = store.findCategoryById(id);
        if (cat == null) {
            System.out.println("  ⚠ Categoría no encontrada.");
            return;
        }
        System.out.println("  Actual: " + cat.getName() + " - " + cat.getDescription());
        String name = readLine("  Nuevo nombre (Enter para mantener): ");
        String desc = readLine("  Nueva descripción (Enter para mantener): ");
        if (!name.isEmpty())
            cat.setName(name);
        if (!desc.isEmpty())
            cat.setDescription(desc);
        System.out.println("  ✓ Categoría actualizada.");
    }

    private void removeCategory() {
        String id = readLine("  ID de la categoría a eliminar: ");
        try {
            store.removeCategory(id);
            System.out.println("  ✓ Categoría eliminada.");
        } catch (IllegalStateException e) {
            System.out.println("  ⚠ " + e.getMessage());
        }
    }

    // 2. MOVIES

    private void menuMovies() {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── Gestión de Películas ───");
            System.out.println("  1. Listar películas");
            System.out.println("  2. Registrar película");
            System.out.println("  3. Agregar copia a película");
            System.out.println("  4. Ver copias de película");
            System.out.println("  5. Consultar disponibilidad por categoría");
            System.out.println("  6. Desactivar película");
            System.out.println("  7. Marcar copia como dañada / retirada");
            System.out.println("  0. Volver");
            int opt = readInt("  Opción: ");
            System.out.println();
            switch (opt) {
                case 1:
                    listMovies();
                    break;
                case 2:
                    registerMovie();
                    break;
                case 3:
                    addCopyToMovie();
                    break;
                case 4:
                    viewMovieCopies();
                    break;
                case 5:
                    moviesByCategory();
                    break;
                case 6:
                    deactivateMovie();
                    break;
                case 7:
                    manageCopyStatus();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("  ⚠ Opción no válida.");
            }
        }
    }

    private void listMovies() {
        List<Movie> movies = store.getMovies();
        if (movies.isEmpty()) {
            System.out.println("  No hay películas registradas.");
            return;
        }
        System.out.println("  ┌───────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("  │ %-6s │ %-25s │ %-5s │ %-10s │ %-12s │ %-6s │%n",
                "ID", "Título", "Año", "Tipo", "Categoría", "Disp.");
        System.out.println("  ├───────────────────────────────────────────────────────────────────────────┤");
        for (Movie m : movies) {
            String available = m.isActive()
                    ? m.getAvailableCopies().size() + "/" + m.getCopies().size()
                    : "INACT.";
            System.out.printf("  │ %-6s │ %-25s │ %-5d │ %-10s │ %-12s │ %-6s │%n",
                    m.getId(),
                    truncate(m.getTitle(), 25),
                    m.getReleaseYear(),
                    m.getTypeName(),
                    truncate(m.getCategory().getName(), 12),
                    available);
        }
        System.out.println("  └───────────────────────────────────────────────────────────────────────────┘");
    }

    private void registerMovie() {
        if (store.getCategories().isEmpty()) {
            System.out.println("  ⚠ Primero debe registrar al menos una categoría.");
            return;
        }
        String id = readLine("  ID: ");
        if (store.findMovieById(id) != null) {
            System.out.println("  ⚠ Ya existe una película con ese ID.");
            return;
        }
        String title = readLine("  Título: ");
        int year = readInt("  Año de estreno: ");
        int duration = readInt("  Duración (minutos): ");

        System.out.println("  Tipo de película:");
        System.out.println("    1. Estreno (NewRelease)");
        System.out.println("    2. Regular");
        int type = readInt("  Seleccione tipo: ");

        System.out.println("  Categorías disponibles:");
        listCategories();
        String catId = readLine("  ID de categoría: ");
        Category cat = store.findCategoryById(catId);
        if (cat == null) {
            System.out.println("  ⚠ Categoría no encontrada.");
            return;
        }

        Movie movie;
        if (type == 1) {
            movie = new NewReleaseMovie(id, title, year, duration, cat);
        } else {
            movie = new RegularMovie(id, title, year, duration, cat);
        }
        store.registerMovie(movie);

        int numCopies = readInt("  ¿Cuántas copias iniciales? ");
        for (int i = 1; i <= numCopies; i++) {
            String code = id + "-C" + String.format("%02d", i);
            movie.addCopy(code);
        }
        System.out.println("  ✓ Película registrada con " + numCopies + " copia(s).");
    }

    private void addCopyToMovie() {
        String movieId = readLine("  ID de la película: ");
        Movie movie = store.findMovieById(movieId);
        if (movie == null) {
            System.out.println("  ⚠ Película no encontrada.");
            return;
        }
        String code = readLine("  Código de la nueva copia: ");
        if (store.findCopyByCode(code) != null) {
            System.out.println("  ⚠ Ya existe una copia con ese código.");
            return;
        }
        movie.addCopy(code);
        System.out.println("  ✓ Copia " + code + " agregada a " + movie.getTitle() + ".");
    }

    private void viewMovieCopies() {
        String movieId = readLine("  ID de la película: ");
        Movie movie = store.findMovieById(movieId);
        if (movie == null) {
            System.out.println("  ⚠ Película no encontrada.");
            return;
        }
        System.out.println("  Copias de: " + movie.getTitle());
        if (movie.getCopies().isEmpty()) {
            System.out.println("  (sin copias)");
            return;
        }
        for (MovieCopy c : movie.getCopies()) {
            System.out.println("    " + c.getCode() + " → " + c.getStatus());
        }
    }

    private void moviesByCategory() {
        if (store.getCategories().isEmpty()) {
            System.out.println("  No hay categorías.");
            return;
        }
        listCategories();
        String catId = readLine("  ID de categoría: ");
        Category cat = store.findCategoryById(catId);
        if (cat == null) {
            System.out.println("  ⚠ Categoría no encontrada.");
            return;
        }
        List<Movie> movies = store.getMoviesByCategory(cat);
        if (movies.isEmpty()) {
            System.out.println("  No hay películas activas en esta categoría.");
            return;
        }
        System.out.println("  Películas en \"" + cat.getName() + "\":");
        for (Movie m : movies) {
            String avail = m.isAvailable()
                    ? "✓ Disponible (" + m.getAvailableCopies().size() + " copias)"
                    : "✗ Sin copias disponibles";
            System.out.println("    " + m.getId() + " - " + m.getTitle() + " → " + avail);
        }
    }

    private void deactivateMovie() {
        String id = readLine("  ID de la película a desactivar: ");
        try {
            store.deactivateMovie(id);
            System.out.println("  ✓ Película desactivada.");
        } catch (Exception e) {
            System.out.println("  ⚠ " + e.getMessage());
        }
    }

    private void manageCopyStatus() {
        String code = readLine("  Código de la copia: ");
        MovieCopy copy = store.findCopyByCode(code);
        if (copy == null) {
            System.out.println("  ⚠ Copia no encontrada.");
            return;
        }
        System.out.println("  Copia: " + copy);
        System.out.println("    1. Marcar como dañada");
        System.out.println("    2. Retirar");
        int opt = readInt("  Opción: ");
        if (opt == 1) {
            copy.markAsDamaged();
            System.out.println("  ✓ Copia marcada como DAMAGED.");
        } else if (opt == 2) {
            copy.retire();
            System.out.println("  ✓ Copia marcada como RETIRED.");
        }
    }

    // 3. CUSTOMERS

    private void menuCustomers() {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── Gestión de Clientes ───");
            System.out.println("  1. Listar clientes");
            System.out.println("  2. Registrar cliente");
            System.out.println("  3. Editar cliente");
            System.out.println("  4. Desactivar cliente");
            System.out.println("  5. Ver historial de alquileres");
            System.out.println("  0. Volver");
            int opt = readInt("  Opción: ");
            System.out.println();
            switch (opt) {
                case 1:
                    listCustomers();
                    break;
                case 2:
                    addCustomer();
                    break;
                case 3:
                    editCustomer();
                    break;
                case 4:
                    deactivateCustomer();
                    break;
                case 5:
                    viewCustomerHistory();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("  ⚠ Opción no válida.");
            }
        }
    }

    private void listCustomers() {
        List<Customer> customers = store.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("  No hay clientes registrados.");
            return;
        }
        System.out.println("  ┌────────────────────────────────────────────────────────────────────┐");
        System.out.printf("  │ %-8s │ %-20s │ %-12s │ %-8s │ %-6s │%n",
                "ID", "Nombre", "Teléfono", "Activos", "Estado");
        System.out.println("  ├────────────────────────────────────────────────────────────────────┤");
        for (Customer c : customers) {
            System.out.printf("  │ %-8s │ %-20s │ %-12s │ %-8d │ %-6s │%n",
                    c.getId(),
                    truncate(c.getFullName(), 20),
                    c.getPhone(),
                    c.countActiveRentals(),
                    c.isActive() ? "Act." : "Inact.");
        }
        System.out.println("  └────────────────────────────────────────────────────────────────────┘");
    }

    private void addCustomer() {
        String id = readLine("  ID (cédula): ");
        if (store.findCustomerById(id) != null) {
            System.out.println("  ⚠ Ya existe un cliente con ese ID.");
            return;
        }
        String name = readLine("  Nombre completo: ");
        String phone = readLine("  Teléfono: ");
        String email = readLine("  Email: ");
        store.registerCustomer(new Customer(id, name, phone, email));
        System.out.println("  ✓ Cliente registrado.");
    }

    private void editCustomer() {
        String id = readLine("  ID del cliente: ");
        Customer c = store.findCustomerById(id);
        if (c == null) {
            System.out.println("  ⚠ Cliente no encontrado.");
            return;
        }
        System.out.println("  Actual: " + c.getFullName() + " | " + c.getPhone() + " | " + c.getEmail());
        String phone = readLine("  Nuevo teléfono (Enter para mantener): ");
        String email = readLine("  Nuevo email (Enter para mantener): ");
        if (!phone.isEmpty())
            c.setPhone(phone);
        if (!email.isEmpty())
            c.setEmail(email);
        System.out.println("  ✓ Cliente actualizado.");
    }

    private void deactivateCustomer() {
        String id = readLine("  ID del cliente a desactivar: ");
        try {
            store.deactivateCustomer(id);
            System.out.println("  ✓ Cliente desactivado.");
        } catch (Exception e) {
            System.out.println("  ⚠ " + e.getMessage());
        }
    }

    private void viewCustomerHistory() {
        String id = readLine("  ID del cliente: ");
        Customer c = store.findCustomerById(id);
        if (c == null) {
            System.out.println("  ⚠ Cliente no encontrado.");
            return;
        }
        List<Rental> history = c.getRentalHistory();
        if (history.isEmpty()) {
            System.out.println("  El cliente no tiene historial de alquileres.");
            return;
        }
        System.out.println("  Historial de " + c.getFullName() + ":");
        for (Rental r : history) {
            System.out.println("    " + r);
            for (RentalItem item : r.getItems()) {
                System.out.println("      " + item);
            }
        }
    }

    // 4. EMPLOYEES

    private void menuEmployees() {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── Gestión de Empleados ───");
            System.out.println("  1. Listar empleados");
            System.out.println("  2. Registrar empleado");
            System.out.println("  3. Editar empleado");
            System.out.println("  4. Desactivar empleado");
            System.out.println("  0. Volver");
            int opt = readInt("  Opción: ");
            System.out.println();
            switch (opt) {
                case 1:
                    listEmployees();
                    break;
                case 2:
                    addEmployee();
                    break;
                case 3:
                    editEmployee();
                    break;
                case 4:
                    deactivateEmployee();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("  ⚠ Opción no válida.");
            }
        }
    }

    private void listEmployees() {
        List<Employee> emps = store.getEmployees();
        if (emps.isEmpty()) {
            System.out.println("  No hay empleados registrados.");
            return;
        }
        for (Employee e : emps) {
            System.out.printf("    %s - %s | %s | %s%n",
                    e.getId(), e.getFullName(), e.getPosition(),
                    e.isActive() ? "Activo" : "Inactivo");
        }
    }

    private void addEmployee() {
        String id = readLine("  ID: ");
        if (store.findEmployeeById(id) != null) {
            System.out.println("  ⚠ Ya existe un empleado con ese ID.");
            return;
        }
        String name = readLine("  Nombre completo: ");
        String phone = readLine("  Teléfono: ");
        String email = readLine("  Email: ");
        String position = readLine("  Cargo: ");
        store.registerEmployee(new Employee(id, name, phone, email, position));
        System.out.println("  ✓ Empleado registrado.");
    }

    private void editEmployee() {
        String id = readLine("  ID del empleado: ");
        Employee e = store.findEmployeeById(id);
        if (e == null) {
            System.out.println("  ⚠ Empleado no encontrado.");
            return;
        }
        System.out.println("  Actual: " + e.getFullName() + " | " + e.getPosition());
        String phone = readLine("  Nuevo teléfono (Enter para mantener): ");
        String email = readLine("  Nuevo email (Enter para mantener): ");
        String pos = readLine("  Nuevo cargo (Enter para mantener): ");
        if (!phone.isEmpty())
            e.setPhone(phone);
        if (!email.isEmpty())
            e.setEmail(email);
        if (!pos.isEmpty())
            e.setPosition(pos);
        System.out.println("  ✓ Empleado actualizado.");
    }

    private void deactivateEmployee() {
        String id = readLine("  ID del empleado a desactivar: ");
        Employee e = store.findEmployeeById(id);
        if (e == null) {
            System.out.println("  ⚠ Empleado no encontrado.");
            return;
        }
        if (e.getId().equals(currentEmployee.getId())) {
            System.out.println("  ⚠ No puede desactivarse a sí mismo.");
            return;
        }
        e.deactivate();
        System.out.println("  ✓ Empleado desactivado.");
    }

    // 5. RENTALS

    private void menuRentals() {
        boolean back = false;
        while (!back) {
            System.out.println("\n");
            System.out.println("─── Gestión de Alquileres ───");
            System.out.println("  1. Listar alquileres activos");
            System.out.println("  2. Crear alquiler");
            System.out.println("  3. Registrar devolución");
            System.out.println("  4. Cancelar alquiler");
            System.out.println("  5. Registrar pago");
            System.out.println("  6. Ver detalle de alquiler");
            System.out.println("  0. Volver");
            int opt = readInt("  Opción: ");
            System.out.println();
            switch (opt) {
                case 1:
                    listActiveRentals();
                    break;
                case 2:
                    createRental();
                    break;
                case 3:
                    returnRental();
                    break;
                case 4:
                    cancelRental();
                    break;
                case 5:
                    registerPayment();
                    break;
                case 6:
                    viewRentalDetail();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("  ⚠ Opción no válida.");
            }
        }
    }

    private void listActiveRentals() {
        List<Rental> actives = store.getRentals().stream()
                .filter(r -> r.getStatus() == RentalStatus.ACTIVE)
                .collect(Collectors.toList());
        if (actives.isEmpty()) {
            System.out.println("  No hay alquileres activos.");
            return;
        }
        System.out.println("  Alquileres activos:");
        for (Rental r : actives) {
            String overdue = r.isOverdue(LocalDate.now()) ? "  VENCIDO" : "";
            System.out.println("    " + r + overdue);
        }
    }

    private void createRental() {
        // Select customer
        String custId = readLine("  ID del cliente: ");
        Customer customer = store.findCustomerById(custId);
        if (customer == null) {
            System.out.println("   Cliente no encontrado.");
            return;
        }
        if (!customer.canRent()) {
            System.out.println("   El cliente no puede alquilar (inactivo o límite alcanzado).");
            return;
        }

        // Select copies
        List<MovieCopy> selectedCopies = new ArrayList<>();
        System.out.println("  Películas disponibles:");
        List<Movie> available = store.getAvailableMovies();
        if (available.isEmpty()) {
            System.out.println("   No hay películas disponibles.");
            return;
        }
        for (Movie m : available) {
            System.out.println("    " + m + " — Copias: "
                    + m.getAvailableCopies().stream()
                            .map(MovieCopy::getCode)
                            .collect(Collectors.joining(", ")));
        }

        System.out.println("  Ingrese los códigos de las copias (separados por coma):");
        String codesInput = readLine("  Códigos: ");
        String[] codes = codesInput.split(",");
        for (String code : codes) {
            code = code.trim();
            MovieCopy copy = store.findCopyByCode(code);
            if (copy == null) {
                System.out.println("   Copia '" + code + "' no encontrada. Operación cancelada.");
                return;
            }
            if (!copy.isAvailable()) {
                System.out.println("   Copia '" + code + "' no disponible. Operación cancelada.");
                return;
            }
            selectedCopies.add(copy);
        }

        if (selectedCopies.isEmpty()) {
            System.out.println("   No se seleccionaron copias.");
            return;
        }

        // Show max days allowed
        int maxDays = selectedCopies.stream()
                .mapToInt(c -> c.getMovie().getMaxRentalDays())
                .min().orElse(7);
        System.out.println("  Máximo de días permitido: " + maxDays);
        int days = readInt("  Días de alquiler: ");

        try {
            Rental rental = store.createRental(customer, currentEmployee,
                    selectedCopies, days);
            System.out.println("    Alquiler creado exitosamente:");
            System.out.println("    ID: " + rental.getId());
            System.out.println("    Total: $" + String.format("%.2f", rental.calculateBaseTotal()));
            System.out.println("    Fecha límite: " + rental.getDueDate());
            for (RentalItem item : rental.getItems()) {
                System.out.println("    " + item);
            }
        } catch (Exception e) {
            System.out.println("   " + e.getMessage());
        }
    }

    private void returnRental() {
        String rentalId = readLine("  ID del alquiler: ");
        try {
            double lateFees = store.returnRental(rentalId, LocalDate.now());
            Rental rental = store.findRentalById(rentalId);
            System.out.println("    Devolución registrada.");
            System.out.println("    Total base: $" + String.format("%.2f", rental.calculateBaseTotal()));
            if (lateFees > 0) {
                System.out.println("      Multa por retraso (" + rental.getDaysLate(LocalDate.now())
                        + " días): $" + String.format("%.2f", lateFees));
            }
            System.out.println("    Saldo pendiente: $" + String.format("%.2f", rental.getBalanceDue()));
        } catch (Exception e) {
            System.out.println("    " + e.getMessage());
        }
    }

    private void cancelRental() {
        String rentalId = readLine("  ID del alquiler: ");
        String reason = readLine("  Motivo de cancelación: ");
        try {
            store.cancelRental(rentalId, reason);
            System.out.println("    Alquiler cancelado.");
        } catch (Exception e) {
            System.out.println("    " + e.getMessage());
        }
    }

    private void registerPayment() {
        String rentalId = readLine("  ID del alquiler: ");
        Rental rental = store.findRentalById(rentalId);
        if (rental == null) {
            System.out.println("  ⚠ Alquiler no encontrado.");
            return;
        }
        double balance = rental.getBalanceDue();
        System.out.println("  Saldo pendiente: $" + String.format("%.2f", balance));
        if (balance <= 0) {
            System.out.println("  ✓ No hay saldo pendiente.");
            return;
        }

        double amount = readDouble("  Monto a pagar: ");
        if (amount <= 0) {
            System.out.println("  ⚠ Monto inválido.");
            return;
        }

        System.out.println("  Tipo de pago:");
        System.out.println("    1. RENTAL (alquiler)");
        System.out.println("    2. LATE_FEE (multa)");
        int typeOpt = readInt("  Opción: ");
        PaymentType type = (typeOpt == 2) ? PaymentType.LATE_FEE : PaymentType.RENTAL;

        System.out.println("  Método de pago:");
        System.out.println("    1. CASH (efectivo)");
        System.out.println("    2. CARD (tarjeta)");
        System.out.println("    3. TRANSFER (transferencia)");
        int methodOpt = readInt("  Opción: ");
        PaymentMethod method;
        switch (methodOpt) {
            case 2:
                method = PaymentMethod.CARD;
                break;
            case 3:
                method = PaymentMethod.TRANSFER;
                break;
            default:
                method = PaymentMethod.CASH;
                break;
        }

        String paymentId = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment = new Payment(paymentId, amount, LocalDate.now(), type, method);
        rental.addPayment(payment);
        System.out.println("  ✓ Pago registrado: " + payment);
        System.out.println("  Nuevo saldo: $" + String.format("%.2f", rental.getBalanceDue()));
    }

    private void viewRentalDetail() {
        String rentalId = readLine("  ID del alquiler: ");
        Rental rental = store.findRentalById(rentalId);
        if (rental == null) {
            System.out.println("  ⚠ Alquiler no encontrado.");
            return;
        }
        System.out.println("\n");
        System.out.println("  ┌─── Detalle del Alquiler ───");
        System.out.println("  │ ID:          " + rental.getId());
        System.out.println("  │ Cliente:     " + rental.getCustomer().getFullName());
        System.out.println("  │ Empleado:    " + rental.getEmployee().getFullName());
        System.out.println("  │ Fecha:       " + rental.getRentalDate());
        System.out.println("  │ Fecha límite:" + rental.getDueDate());
        System.out
                .println("  │ Devolución:  " + (rental.getReturnDate() != null ? rental.getReturnDate() : "Pendiente"));
        System.out.println("  │ Estado:      " + rental.getStatus());
        if (rental.getCancellationReason() != null) {
            System.out.println("  │ Motivo canc.:" + rental.getCancellationReason());
        }
        System.out.println("  │");
        System.out.println("  │ Items:");
        for (RentalItem item : rental.getItems()) {
            System.out.println("  │   " + item);
        }
        System.out.println("  │");
        System.out.println("  │ Total base:  $" + String.format("%.2f", rental.calculateBaseTotal()));
        if (rental.getReturnDate() != null) {
            double fees = rental.calculateLateFees(rental.getReturnDate());
            if (fees > 0) {
                System.out.println("  │ Multas:      $" + String.format("%.2f", fees));
            }
        } else if (rental.isOverdue(LocalDate.now())) {
            System.out.println("  │ ⚠ VENCIDO - Multas estimadas: $"
                    + String.format("%.2f", rental.calculateLateFees(LocalDate.now())));
        }
        System.out.println("  │ Pagos:");
        if (rental.getPayments().isEmpty()) {
            System.out.println("  │   (sin pagos)");
        } else {
            for (Payment p : rental.getPayments()) {
                System.out.println("  │   " + p);
            }
        }
        System.out.println("  │ Saldo:       $" + String.format("%.2f", rental.getBalanceDue()));
        System.out.println("  └──────────────────────────────");
    }

    // 6. REPORTS

    private void menuReports() {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── Reportes ───");
            System.out.println("  1. Alquileres vencidos");
            System.out.println("  2. Ingresos totales");
            System.out.println("  3. Resumen general");
            System.out.println("  4. Exportar películas a CSV");
            System.out.println("  5. Exportar clientes a CSV");
            System.out.println("  6. Exportar alquileres a CSV");
            System.out.println("  0. Volver");
            int opt = readInt("  Opción: ");
            System.out.println();
            switch (opt) {
                case 1:
                    reportOverdue();
                    break;
                case 2:
                    reportIncome();
                    break;
                case 3:
                    reportSummary();
                    break;
                case 4:
                    String f1 = CsvExporter.exportMovies(store.getMovies());
                    if (f1 != null)
                        System.out.println("  ✓ Exportado a: " + f1);
                    break;
                case 5:
                    String f2 = CsvExporter.exportCustomers(store.getCustomers());
                    if (f2 != null)
                        System.out.println("  ✓ Exportado a: " + f2);
                    break;
                case 6:
                    String f3 = CsvExporter.exportRentals(store.getRentals());
                    if (f3 != null)
                        System.out.println("  ✓ Exportado a: " + f3);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("  ⚠ Opción no válida.");
            }
        }
    }

    private void reportOverdue() {
        List<Rental> overdue = store.getOverdueRentals(LocalDate.now());
        if (overdue.isEmpty()) {
            System.out.println("  ✓ No hay alquileres vencidos.");
            return;
        }
        System.out.println("  ⚠ Alquileres vencidos (" + overdue.size() + "):");
        for (Rental r : overdue) {
            int daysLate = r.getDaysLate(LocalDate.now());
            double fees = r.calculateLateFees(LocalDate.now());
            System.out.printf("    %s | %s | Vencido hace %d día(s) | Multa: $%.2f%n",
                    r.getId(), r.getCustomer().getFullName(), daysLate, fees);
        }
    }

    private void reportIncome() {
        double total = store.calculateTotalIncome();
        System.out.printf("  Ingresos totales registrados: $%.2f%n", total);

        long totalRentals = store.getRentals().size();
        long active = store.getRentals().stream()
                .filter(r -> r.getStatus() == RentalStatus.ACTIVE).count();
        long returned = store.getRentals().stream()
                .filter(r -> r.getStatus() == RentalStatus.RETURNED).count();
        long cancelled = store.getRentals().stream()
                .filter(r -> r.getStatus() == RentalStatus.CANCELLED).count();

        System.out.println("  Alquileres totales: " + totalRentals);
        System.out.println("    Activos:    " + active);
        System.out.println("    Devueltos:  " + returned);
        System.out.println("    Cancelados: " + cancelled);
    }

    private void reportSummary() {
        System.out.println("  ┌─── Resumen General ───");
        System.out.println("  │ Tienda:      " + store.getName());
        System.out.println("  │ Categorías:  " + store.getCategories().size());
        System.out.println("  │ Películas:   " + store.getMovies().size()
                + " (activas: " + store.getMovies().stream().filter(Movie::isActive).count() + ")");
        long totalCopies = store.getMovies().stream()
                .flatMap(m -> m.getCopies().stream()).count();
        long availCopies = store.getMovies().stream()
                .flatMap(m -> m.getCopies().stream())
                .filter(MovieCopy::isAvailable).count();
        System.out.println("  │ Copias:      " + totalCopies + " (disponibles: " + availCopies + ")");
        System.out.println("  │ Clientes:    " + store.getCustomers().size()
                + " (activos: " + store.getCustomers().stream().filter(Customer::isActive).count() + ")");
        System.out.println("  │ Empleados:   " + store.getEmployees().size());
        System.out.println("  │ Alquileres:  " + store.getRentals().size());
        System.out.printf("  │ Ingresos:    $%.2f%n", store.calculateTotalIncome());
        System.out.println("  └───────────────────────────");
    }

    // 7. RECOMMENDATIONS

    private void menuRecommendations() {
        String custId = readLine("  ID del cliente: ");
        Customer c = store.findCustomerById(custId);
        if (c == null) {
            System.out.println("  ⚠ Cliente no encontrado.");
            return;
        }

        List<Movie> recs = store.getRecommendations(c);
        if (recs.isEmpty()) {
            System.out.println("  No hay recomendaciones disponibles para " + c.getFullName() + ".");
            return;
        }

        List<Category> rentedCats = c.getRentedCategories();
        if (!rentedCats.isEmpty()) {
            System.out.println("  Categorías preferidas: "
                    + rentedCats.stream().map(Category::getName)
                            .collect(Collectors.joining(", ")));
        }
        System.out.println("  Recomendaciones para " + c.getFullName() + ":");
        for (Movie m : recs) {
            System.out.println("    ★ " + m.getTitle() + " (" + m.getReleaseYear()
                    + ") [" + m.getTypeName() + "] - " + m.getCategory().getName()
                    + " — " + m.getAvailableCopies().size() + " copia(s) disponible(s)");
        }
    }

    // DEFAULT DATA

    private void initializeDefaultData() {
        // Categories
        Category action = new Category("CAT01", "Acción", "Películas de acción y aventura");
        Category comedy = new Category("CAT02", "Comedia", "Películas de comedia");
        Category drama = new Category("CAT03", "Drama", "Películas de drama");
        Category horror = new Category("CAT04", "Terror", "Películas de terror");
        Category scifi = new Category("CAT05", "Ciencia Ficción", "Películas de ciencia ficción");
        store.addCategory(action);
        store.addCategory(comedy);
        store.addCategory(drama);
        store.addCategory(horror);
        store.addCategory(scifi);

        // Employees
        Employee emp1 = new Employee("EMP01", "Admin Sistema", "3001234567", "admin@moviestore.com", "Administrador");
        Employee emp2 = new Employee("EMP02", "María García", "3009876543", "maria@moviestore.com", "Cajero");
        store.registerEmployee(emp1);
        store.registerEmployee(emp2);

        // Movies
        NewReleaseMovie m1 = new NewReleaseMovie("MOV01", "Avengers: Endgame", 2025, 181, action);
        m1.addCopy("MOV01-C01");
        m1.addCopy("MOV01-C02");
        m1.addCopy("MOV01-C03");
        store.registerMovie(m1);

        RegularMovie m2 = new RegularMovie("MOV02", "The Hangover", 2009, 100, comedy);
        m2.addCopy("MOV02-C01");
        m2.addCopy("MOV02-C02");
        store.registerMovie(m2);

        NewReleaseMovie m3 = new NewReleaseMovie("MOV03", "Dune: Part Three", 2026, 155, scifi);
        m3.addCopy("MOV03-C01");
        m3.addCopy("MOV03-C02");
        store.registerMovie(m3);

        RegularMovie m4 = new RegularMovie("MOV04", "The Shawshank Redemption", 1994, 142, drama);
        m4.addCopy("MOV04-C01");
        store.registerMovie(m4);

        RegularMovie m5 = new RegularMovie("MOV05", "The Conjuring", 2013, 112, horror);
        m5.addCopy("MOV05-C01");
        m5.addCopy("MOV05-C02");
        store.registerMovie(m5);

        // Customers
        store.registerCustomer(new Customer("CLI01", "Juan Pérez", "3101112233", "juan@email.com"));
        store.registerCustomer(new Customer("CLI02", "Ana López", "3204445566", "ana@email.com"));
        store.registerCustomer(new Customer("CLI03", "Carlos Ruiz", "3157778899", "carlos@email.com"));
    }

    // INPUT HELPERS

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("  ⚠ Ingrese un número válido: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("  ⚠ Ingrese un número válido: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return value;
    }

    private String truncate(String text, int maxLen) {
        if (text.length() <= maxLen)
            return text;
        return text.substring(0, maxLen - 2) + "..";
    }
}
