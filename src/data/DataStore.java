package data;

import domain.Store;
import java.io.*;

public class DataStore {

    private static final String DATA_FILE = "store.ser";

    public static void save(Store store) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(store);
            System.out.println("  ✓ Datos guardados en " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("  ✗ Error al guardar datos: " + e.getMessage());
        }
    }

    public static Store load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            Store store = (Store) ois.readObject();
            System.out.println("  ✓ Datos cargados desde " + DATA_FILE);
            return store;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("  ✗ Error al cargar datos: " + e.getMessage());
            return null;
        }
    }
}
