package es.etg.ad.dam;

import java.util.List;

public interface InstitutoDAO {

    void crearTablaAlumno();

    void crearTablaClubFutbol();

    int insertarAlumno(Alumno alumno);

    int actualizarAlumno(Alumno alumno);

    int borrar(Alumno alumno);

    Alumno buscarAlumnoPorId(int id);

    List<Alumno> listarAlumnos();

    List<Alumno> listarAlumnos(int edad);

    int insertarClub(ClubFutbol club);

    int actualizarClub(ClubFutbol club);

    List<String> listarAlumnosConClub();
}
