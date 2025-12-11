package es.etg.ad.dam;

public class ClubFutbol {

    private int id;
    private int alumnoId;
    private String equipo;

    public ClubFutbol() {
    }

    public ClubFutbol(int id, int alumnoId, String equipo) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.equipo = equipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    @Override
    public String toString() {
        return String.format("ClubFutbol{id=%d, alumnoId=%d, equipo='%s'}", id, alumnoId, equipo);
    }
}
