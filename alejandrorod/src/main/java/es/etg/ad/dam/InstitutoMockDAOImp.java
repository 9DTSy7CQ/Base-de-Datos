package es.etg.ad.dam;

import java.util.ArrayList;
import java.util.List;

public class InstitutoMockDAOImp implements InstitutoDAO {

    private final List<Alumno> alumnos = new ArrayList<>();
    private final List<ClubFutbol> clubes = new ArrayList<>();
    private int nextAlumnoId = 1;
    private int nextClubId = 1;

    // ===== Crear tablas (simulado) =====
    @Override
    public void crearTablaAlumno() {
        alumnos.clear();
        nextAlumnoId = 1;
        System.out.println("[MOCK] Tabla alumno (memoria) preparada.");
    }

    @Override
    public void crearTablaClubFutbol() {
        clubes.clear();
        nextClubId = 1;
        System.out.println("[MOCK] Tabla club (memoria) preparada.");
    }

    // ===== Alumnos =====
    @Override
    public int insertarAlumno(Alumno alumno) {
        alumno.setId(nextAlumnoId++);
        alumnos.add(alumno);
        return 1;
    }

    @Override
    public int actualizarAlumno(Alumno alumno) {
        for (int i = 0; i < alumnos.size(); i++) {
            if (alumnos.get(i).getId() == alumno.getId()) {
                alumnos.set(i, alumno);
                return 1;
            }
        }
        return 0;
    }

    @Override
    public Alumno buscarAlumnoPorId(int id) {
        for (Alumno a : alumnos) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    @Override
    public int borrar(Alumno a) {
        return alumnos.removeIf(x -> x.getId() == a.getId()) ? 1 : 0;
    }

    @Override
    public List<Alumno> listarAlumnos() {
        return new ArrayList<>(alumnos);
    }

    @Override
    public List<Alumno> listarAlumnos(int edad) {
        List<Alumno> out = new ArrayList<>();
        for (Alumno a : alumnos) {
            if (a.getEdad() == edad) {
                out.add(a);
            }
        }
        return out;
    }

    // ===== Clubes =====
    @Override
    public int insertarClub(ClubFutbol c) {
        // comprobar FK: debe existir alumno con id = alumnoId
        boolean existe = alumnos.stream().anyMatch(a -> a.getId() == c.getAlumnoId());
        if (!existe) {
            System.out.println("Error: no existe alumno con ID " + c.getAlumnoId());
            return 0;
        }
        c.setId(nextClubId++);
        clubes.add(c);
        return 1;
    }

    @Override
    public int actualizarClub(ClubFutbol c) {
        for (int i = 0; i < clubes.size(); i++) {
            if (clubes.get(i).getId() == c.getId()) {
                clubes.set(i, c);
                return 1;
            }
        }
        return 0;
    }

    @Override
    public List<String> listarAlumnosConClub() {
        List<String> out = new ArrayList<>();
        for (ClubFutbol c : clubes) {
            Alumno a = alumnos.stream()
                    .filter(x -> x.getId() == c.getAlumnoId())
                    .findFirst()
                    .orElse(null);
            if (a != null) {
                out.add(a.getNombre() + " " + a.getApellido() + " → " + c.getEquipo());
            }
        }
        return out;
    }
}
