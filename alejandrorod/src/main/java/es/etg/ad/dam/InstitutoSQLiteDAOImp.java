package es.etg.ad.dam;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstitutoSQLiteDAOImp implements InstitutoDAO {

    private static final String DATABASE_NAME = "mybasedatos.db";
    private static final String JDBC_URL_TEMPLATE = "jdbc:sqlite:%s";
    protected final Connection conn;

    public InstitutoSQLiteDAOImp() {
        try {
            URL resource = InstitutoSQLiteDAOImp.class.getResource("/" + DATABASE_NAME);
            String url = resource == null
                    ? String.format(JDBC_URL_TEMPLATE, DATABASE_NAME)
                    : String.format(JDBC_URL_TEMPLATE, new File(resource.toURI()).getAbsolutePath());
            conn = DriverManager.getConnection(url);
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===== Crear tablas =====
    @Override
    public void crearTablaAlumno() {
        try (Statement st = conn.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS alumno (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        apellido TEXT NOT NULL,
                        edad INTEGER NOT NULL
                    );
                    """;
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void crearTablaClubFutbol() {
        try (Statement st = conn.createStatement()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS club_futbol (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        alumnoId INTEGER NOT NULL,
                        equipo TEXT NOT NULL,
                        FOREIGN KEY(alumnoId) REFERENCES alumno(id) ON DELETE CASCADE
                    );
                    """;
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ===== Alumnos =====
    @Override
    public int insertarAlumno(Alumno a) {
        String sql = "INSERT INTO alumno(nombre, apellido, edad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellido());
            ps.setInt(3, a.getEdad());
            int rows = ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    a.setId(gk.getInt(1));
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int actualizarAlumno(Alumno a) {
        String sql = "UPDATE alumno SET nombre = ?, apellido = ?, edad = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellido());
            ps.setInt(3, a.getEdad());
            ps.setInt(4, a.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int borrar(Alumno a) {
        String sql = "DELETE FROM alumno WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Alumno buscarAlumnoPorId(int id) {
        String sql = "SELECT id, nombre, apellido, edad FROM alumno WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Alumno(rs.getInt("id"), rs.getString("nombre"),
                            rs.getString("apellido"), rs.getInt("edad"));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Alumno> listarAlumnos() {
        List<Alumno> out = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, edad FROM alumno";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Alumno(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("apellido"), rs.getInt("edad")));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Alumno> listarAlumnos(int edad) {
        List<Alumno> out = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, edad FROM alumno WHERE edad = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, edad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Alumno(rs.getInt("id"), rs.getString("nombre"),
                            rs.getString("apellido"), rs.getInt("edad")));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ===== Clubes =====
    @Override
    public int insertarClub(ClubFutbol c) {
        // verificar FK
        String check = "SELECT COUNT(1) FROM alumno WHERE id = ?";
        try (PreparedStatement psCheck = conn.prepareStatement(check)) {
            psCheck.setInt(1, c.getAlumnoId());
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO club_futbol(alumnoId, equipo) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getAlumnoId());
            ps.setString(2, c.getEquipo());
            int rows = ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    c.setId(gk.getInt(1));
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int actualizarClub(ClubFutbol c) {
        String sql = "UPDATE club_futbol SET alumnoId = ?, equipo = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getAlumnoId());
            ps.setString(2, c.getEquipo());
            ps.setInt(3, c.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> listarAlumnosConClub() {
        List<String> out = new ArrayList<>();
        String sql = """
                SELECT a.nombre, a.apellido, c.equipo
                FROM alumno a
                JOIN club_futbol c ON c.alumnoId = a.id
                ORDER BY a.id;
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(rs.getString("nombre") + " " + rs.getString("apellido") + " → " + rs.getString("equipo"));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ===== Método para precargar datos =====
    public void insertarDatosIniciales() {
        insertarAlumno(new Alumno(0, "Mark", "Evans", 22));
        insertarAlumno(new Alumno(0, "Axel", "Blaze", 22));
        insertarAlumno(new Alumno(0, "Byron", "Love", 21));
        insertarAlumno(new Alumno(0, "Aitor", "Cazador", 23));
        insertarAlumno(new Alumno(0, "Harper", "Evans", 20));
        insertarAlumno(new Alumno(0, "Victor", "Blade", 24));
        insertarAlumno(new Alumno(0, "Sol", "Daystar", 22));

        insertarClub(new ClubFutbol(0, 1, "Instituto Raimon"));
        insertarClub(new ClubFutbol(0, 2, "Royal Academy"));
        insertarClub(new ClubFutbol(0, 3, "Phoenix Club"));
        insertarClub(new ClubFutbol(0, 4, "Dragon Team"));
        insertarClub(new ClubFutbol(0, 5, "Star Eagles"));
        insertarClub(new ClubFutbol(0, 6, "Shadow Wolves"));
        insertarClub(new ClubFutbol(0, 7, "Sun Strikers"));
    }
}
