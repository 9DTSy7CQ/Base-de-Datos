package es.etg.ad.dam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstitutoOracleXeDAOImp implements InstitutoDAO {

    private final Connection conn;

    public InstitutoOracleXeDAOImp() {
        try {
            String host = System.getenv().getOrDefault("ORACLE_HOST", "localhost");
            String port = System.getenv().getOrDefault("ORACLE_PORT", "1521");
            String service = System.getenv().getOrDefault("ORACLE_SERVICE", "XEPDB1");
            String user = System.getenv().getOrDefault("ORACLE_USER", "SYSTEM");
            String pass = System.getenv().getOrDefault("ORACLE_PASSWORD", "secret");

            String url = String.format("jdbc:oracle:thin:@//%s:%s/%s", host, port, service);

            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Conectado a Oracle XE correctamente.");

        } catch (SQLException e) {
            throw new RuntimeException("Error conectando a Oracle XE", e);
        }
    }

    // ===========================
    //  CREAR TABLAS (ORACLE)
    // ===========================
    @Override
    public void crearTablaAlumno() {
        String sql = """
                CREATE TABLE alumno (
                    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    nombre VARCHAR2(100) NOT NULL,
                    apellido VARCHAR2(100) NOT NULL,
                    edad NUMBER NOT NULL
                )
                """;

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            if (!e.getMessage().contains("ORA-00955")) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void crearTablaClubFutbol() {
        String sql = """
                CREATE TABLE club_futbol (
                    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    alumnoId NUMBER NOT NULL,
                    equipo VARCHAR2(100) NOT NULL,
                    CONSTRAINT fk_alumno FOREIGN KEY (alumnoId)
                        REFERENCES alumno(id)
                        ON DELETE CASCADE
                )
                """;

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            if (!e.getMessage().contains("ORA-00955")) {
                throw new RuntimeException(e);
            }
        }
    }

    // ===========================
    //        ALUMNOS
    // ===========================
    @Override
    public int insertarAlumno(Alumno a) {
        String sql = "INSERT INTO alumno(nombre, apellido, edad) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
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
                    return new Alumno(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getInt("edad")
                    );
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
        String sql = "SELECT id, nombre, apellido, edad FROM alumno ORDER BY id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new Alumno(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getInt("edad")
                ));
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
                    out.add(new Alumno(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getInt("edad")
                    ));
                }
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ===========================
    //        CLUBES
    // ===========================
    @Override
    public int insertarClub(ClubFutbol c) {
        // Verificar FK
        String check = "SELECT COUNT(1) FROM alumno WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, c.getAlumnoId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return 0; // alumno no existe
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO club_futbol(alumnoId, equipo) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
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
                ORDER BY a.id
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(rs.getString("nombre") + " "
                        + rs.getString("apellido") + " → "
                        + rs.getString("equipo"));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
