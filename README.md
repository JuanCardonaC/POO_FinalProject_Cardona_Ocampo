# Movie Rental System

Proyecto Final — Programación Orientada a Objetos (Universidad de Caldas)

## Integrantes

| Nombre | Código | GitHub |
|---|---|---|
| Juan Cardona | _por definir_ | [@JuanCardonaC](https://github.com/JuanCardonaC) |
| _por definir_ | _por definir_ | [_por definir_](_por definir_) |

## Descripción general

Aplicación de uso interno para los empleados de una tienda de alquiler de películas. Permite registrar películas y copias, gestionar clientes, registrar alquileres y devoluciones, calcular tarifas y multas, consultar disponibilidad por categoría, generar reportes y recomendar películas a los clientes.

## Estructura del proyecto

```
src/
├── Main.java                → punto de entrada
├── ui/
│   └── ConsoleUI.java       → interfaz de consola interactiva
├── domain/
│   ├── Category.java        → categoría de película
│   ├── Movie.java           → clase abstracta de película
│   ├── NewReleaseMovie.java → película tipo estreno
│   ├── RegularMovie.java    → película tipo regular
│   ├── MovieCopy.java       → copia física de una película
│   ├── CopyStatus.java      → enum: AVAILABLE, RENTED, DAMAGED, RETIRED
│   ├── Person.java          → clase abstracta persona
│   ├── Customer.java        → cliente
│   ├── Employee.java        → empleado
│   ├── Rental.java          → alquiler
│   ├── RentalItem.java      → ítem dentro de un alquiler
│   ├── RentalStatus.java    → enum: ACTIVE, RETURNED, CANCELLED
│   ├── Payment.java         → pago registrado
│   ├── PaymentType.java     → enum: RENTAL, LATE_FEE
│   ├── PaymentMethod.java   → enum: CASH, CARD, TRANSFER
│   └── Store.java           → fachada principal de la tienda
└── data/
    ├── DataStore.java        → persistencia por serialización (.ser)
    └── CsvExporter.java      → exportación de datos a CSV
```

## Instrucciones para ejecutar

### Requisitos
- Java JDK 8 o superior

### Compilar
```bash
cd POO_FinalProject_Cardona_Ocampo
javac -d out -encoding UTF-8 src/domain/*.java src/data/*.java src/ui/*.java src/Main.java
```

### Ejecutar
```bash
java -cp out Main
```

> **Nota:** Al iniciar por primera vez se cargan datos de ejemplo. El ID del empleado por defecto es `EMP01`.

## Ejemplo de entrada/salida

```
╔══════════════════════════════════════════════╗
║      MOVIE RENTAL MANAGEMENT SYSTEM         ║
║      Sistema de Alquiler de Películas        ║
╚══════════════════════════════════════════════╝

  » Sistema iniciado con datos de ejemplo.

── Inicio de Sesión (Empleado) ──
  ID del empleado: EMP01
  ✓ Bienvenido/a, Admin Sistema (Administrador)

═══════════ MENÚ PRINCIPAL ═══════════
  1. Gestión de Categorías
  2. Gestión de Películas
  3. Gestión de Clientes
  4. Gestión de Empleados
  5. Gestión de Alquileres
  6. Reportes
  7. Recomendaciones
  0. Guardar y Salir
══════════════════════════════════════
```

### Crear un alquiler
```
  ID del cliente: CLI01
  Películas disponibles:
    MOV01 - Avengers: Endgame (2025) [Estreno] — Copias: MOV01-C01, MOV01-C02, MOV01-C03
    MOV02 - The Hangover (2009) [Regular] — Copias: MOV02-C01, MOV02-C02
    ...
  Ingrese los códigos de las copias (separados por coma):
  Códigos: MOV01-C01, MOV02-C01
  Máximo de días permitido: 3
  Días de alquiler: 3
  ✓ Alquiler creado exitosamente:
    ID: R-A1B2C3D4
    Total: $24.00
    Fecha límite: 2026-11-28
```

## Entidades con CRUD y restricciones

| Entidad | Crear | Leer | Actualizar | Desactivar/Eliminar | Restricciones |
|---|---|---|---|---|---|
| **Category** | ✓ | ✓ | ✓ | ✓ Eliminar | No se elimina si tiene películas activas |
| **Movie** (NewRelease / Regular) | ✓ | ✓ | Cambiar categoría | ✓ Desactivar | No se desactiva con alquileres activos |
| **MovieCopy** | ✓ Agregar a película | ✓ | Marcar dañada/retirada | — | Se valida disponibilidad al alquilar |
| **Customer** | ✓ | ✓ | ✓ Teléfono/Email | ✓ Desactivar | No se desactiva con alquileres activos; máx. 3 alquileres activos |
| **Employee** | ✓ | ✓ | ✓ Teléfono/Email/Cargo | ✓ Desactivar | No puede desactivarse a sí mismo |
| **Rental** | ✓ Crear | ✓ | Devolución / Cancelar | — | Valida disponibilidad de copias, límite de cliente, y días máximos |
| **Payment** | ✓ Registrar en alquiler | ✓ | — | — | Se valida saldo pendiente |

### Tarifas

| Tipo | Tarifa diaria | Máx. días | Multa por día | Descuento |
|---|---|---|---|---|
| **Estreno (NewRelease)** | $5.00 | 3 | $7.50 | — |
| **Regular** | $3.00 | 7 | $4.00 | 15% si > 3 días |

## Diagrama de clases
El diagrama de clases se realizó en lenguaje javascript en la herramienta mermaid
[link diagrama de clases](https://mermaid.ai/d/cd78483c-32b4-4296-9536-853794799c7c)
