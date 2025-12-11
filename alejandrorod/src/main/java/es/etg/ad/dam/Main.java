package es.etg.ad.dam;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        try (Scanner sc = new Scanner(System.in)) {
            InstitutoDAO dao = null;

            while (true) {
                System.out.println("====== MENU 1: TIPO DE CONEXIÓN ======");
                System.out.println("1. Mock");
                System.out.println("2. SQLite");
                System.out.println("3. Oracle");
                System.out.println("0. Salir");
                System.out.print("Elige opción: ");

                int op = leerInt(sc);
                if (op == 0) {
                    break;
                }

                dao = seleccionarDAO(op);
                if (dao == null) {
                    continue;
                }

                // Precargar datos automáticamente si usamos SQLite
                if (op == 2) {
                    ((InstitutoSQLiteDAOImp) dao).crearTablaAlumno();
                    ((InstitutoSQLiteDAOImp) dao).crearTablaClubFutbol();
                    ((InstitutoSQLiteDAOImp) dao).insertarDatosIniciales();
                    System.out.println("\n*** Tablas creadas y datos precargados ***\n");
                }

                System.out.println("\n*** Conexión establecida ***\n");
                menuSecundario(sc, dao);
            }
        }
    }

    private static int leerInt(Scanner sc) {
        while (true) {
            try {
                int num = sc.nextInt();
                sc.nextLine(); // limpiar buffer
                return num;
            } catch (InputMismatchException e) {
                System.out.print("Entrada inválida. Introduce un número: ");
                sc.nextLine(); // limpiar buffer
            }
        }
    }

    private static InstitutoDAO seleccionarDAO(int op) throws Exception {
        return switch (op) {
            case 1 ->
                new InstitutoMockDAOImp();
            case 2 ->
                new InstitutoSQLiteDAOImp();
            case 3 ->
                new InstitutoOracleXeDAOImp();
            default -> {
                System.out.println("Opción inválida");
                yield null;
            }
        };
    }

    private static void menuSecundario(Scanner sc, InstitutoDAO dao) {
        while (true) {
            System.out.println("\n====== MENU 2 ======");
            System.out.println("1. Crear tablas");
            System.out.println("2. Insertar alumno");
            System.out.println("3. Insertar club");
            System.out.println("4. Actualizar alumno");
            System.out.println("5. Actualizar club");
            System.out.println("6. Listar alumnos");
            System.out.println("7. Listar alumnos con club (JOIN)");
            System.out.println("8. Buscar alumno por ID");
            System.out.println("0. Volver");
            System.out.print("Elige opción: ");

            int op2 = leerInt(sc);
            if (op2 == 0) {
                break;
            }

            switch (op2) {
                case 1 -> {
                    dao.crearTablaAlumno();
                    dao.crearTablaClubFutbol();
                    System.out.println("Tablas creadas correctamente.");
                }
                case 2 ->
                    insertarAlumno(sc, dao);
                case 3 ->
                    insertarClub(sc, dao);
                case 4 ->
                    actualizarAlumno(sc, dao);
                case 5 ->
                    actualizarClub(sc, dao);
                case 6 ->
                    dao.listarAlumnos().forEach(System.out::println);
                case 7 ->
                    dao.listarAlumnosConClub().forEach(System.out::println);
                case 8 ->
                    buscarAlumno(sc, dao);
                default ->
                    System.out.println("Opción inválida");
            }
        }
    }

    private static void insertarAlumno(Scanner sc, InstitutoDAO dao) {
        System.out.print("ID: ");
        int id = leerInt(sc);
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        System.out.print("Edad: ");
        int edad = leerInt(sc);

        dao.insertarAlumno(new Alumno(id, nombre, apellido, edad));
        System.out.println("Alumno insertado correctamente.");
    }

    private static void insertarClub(Scanner sc, InstitutoDAO dao) {
        System.out.print("ID: ");
        int id = leerInt(sc);
        System.out.print("ID Alumno (FK): ");
        int alumnoId = leerInt(sc);
        System.out.print("Equipo: ");
        String equipo = sc.nextLine();

        dao.insertarClub(new ClubFutbol(id, alumnoId, equipo));
        System.out.println("Club insertado correctamente.");
    }

    private static void actualizarAlumno(Scanner sc, InstitutoDAO dao) {
        System.out.print("ID alumno: ");
        int id = leerInt(sc);
        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Nuevo apellido: ");
        String apellido = sc.nextLine();
        System.out.print("Nueva edad: ");
        int edad = leerInt(sc);

        dao.actualizarAlumno(new Alumno(id, nombre, apellido, edad));
        System.out.println("Alumno actualizado correctamente.");
    }

    private static void actualizarClub(Scanner sc, InstitutoDAO dao) {
        System.out.print("ID club: ");
        int id = leerInt(sc);
        System.out.print("Nuevo alumnoId: ");
        int alumnoId = leerInt(sc);
        System.out.print("Nuevo equipo: ");
        String equipo = sc.nextLine();

        dao.actualizarClub(new ClubFutbol(id, alumnoId, equipo));
        System.out.println("Club actualizado correctamente.");
    }

    private static void buscarAlumno(Scanner sc, InstitutoDAO dao) {
        System.out.print("Introduce ID: ");
        int id = leerInt(sc);
        Alumno alumno = dao.buscarAlumnoPorId(id);
        if (alumno != null) {
            System.out.println(alumno);
        } else {
            System.out.println("Alumno no encontrado.");
        }
    }
}
