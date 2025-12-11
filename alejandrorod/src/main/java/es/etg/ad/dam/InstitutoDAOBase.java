package es.etg.ad.dam;

import java.sql.Connection;
import java.util.List;

public abstract class InstitutoDAOBase implements InstitutoDAO {

    protected Connection conn;

    public Connection getConnection() {
        return conn;
    }

    @Override
    public void crearTablaAlumno() {}
    @Override
    public void crearTablaClubFutbol() {}

    @Override
    public int insertarAlumno(Alumno alumno) { return 0; }
    @Override
    public int actualizarAlumno(Alumno alumno) { return 0; }
    @Override
    public int borrar(Alumno alumno) { return 0; }

    @Override
    public Alumno buscarAlumnoPorId(int id) { return null; }
    @Override
    public List<Alumno> listarAlumnos() { return List.of(); }
    @Override
    public List<Alumno> listarAlumnos(int edad) { return List.of(); }

    @Override
    public int insertarClub(ClubFutbol club) { return 0; }
    @Override
    public int actualizarClub(ClubFutbol club) { return 0; }

    @Override
    public List<String> listarAlumnosConClub() { return List.of(); }
}
