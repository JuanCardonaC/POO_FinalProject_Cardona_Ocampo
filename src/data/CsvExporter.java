package data;

import domain.*;
import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class CsvExporter {

    private static final String EXPORTS_DIR = "exports";

    private static void ensureDir() {
        new File(EXPORTS_DIR).mkdirs();
    }

    public static String exportMovies(List<Movie> movies) {
        ensureDir();
        String filename = EXPORTS_DIR + "/peliculas_" + LocalDate.now() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("ID,Titulo,Anio,Duracion(min),Tipo,Categoria,Copias Totales,Copias Disponibles,Activa");
            for (Movie m : movies) {
                pw.printf("%s,%s,%d,%d,%s,%s,%d,%d,%s%n",
                        m.getId(),
                        escape(m.getTitle()),
                        m.getReleaseYear(),
                        m.getDurationMinutes(),
                        m.getTypeName(),
                        escape(m.getCategory().getName()),
                        m.getCopies().size(),
                        m.getAvailableCopies().size(),
                        m.isActive() ? "Si" : "No");
            }
        } catch (IOException e) {
            System.err.println("  ✗ Error al exportar películas: " + e.getMessage());
            return null;
        }
        return filename;
    }

    public static String exportCustomers(List<Customer> customers) {
        ensureDir();
        String filename = EXPORTS_DIR + "/clientes_" + LocalDate.now() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("ID,Nombre,Telefono,Email,Alquileres Activos,Activo");
            for (Customer c : customers) {
                pw.printf("%s,%s,%s,%s,%d,%s%n",
                        c.getId(),
                        escape(c.getFullName()),
                        c.getPhone(),
                        c.getEmail(),
                        c.countActiveRentals(),
                        c.isActive() ? "Si" : "No");
            }
        } catch (IOException e) {
            System.err.println("  ✗ Error al exportar clientes: " + e.getMessage());
            return null;
        }
        return filename;
    }

    public static String exportRentals(List<Rental> rentals) {
        ensureDir();
        String filename = EXPORTS_DIR + "/alquileres_" + LocalDate.now() + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("ID,Cliente,Empleado,Fecha Alquiler,Fecha Limite,Fecha Devolucion,Estado,Total Base,Multas,Saldo");
            for (Rental r : rentals) {
                double lateFees = r.getReturnDate() != null
                        ? r.calculateLateFees(r.getReturnDate()) : 0;
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%.2f%n",
                        r.getId(),
                        escape(r.getCustomer().getFullName()),
                        escape(r.getEmployee().getFullName()),
                        r.getRentalDate(),
                        r.getDueDate(),
                        r.getReturnDate() != null ? r.getReturnDate() : "",
                        r.getStatus(),
                        r.calculateBaseTotal(),
                        lateFees,
                        r.getBalanceDue());
            }
        } catch (IOException e) {
            System.err.println("  ✗ Error al exportar alquileres: " + e.getMessage());
            return null;
        }
        return filename;
    }

    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
