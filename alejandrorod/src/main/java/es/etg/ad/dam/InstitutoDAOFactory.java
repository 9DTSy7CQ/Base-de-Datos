package es.etg.ad.dam;

public class InstitutoDAOFactory {

    public static InstitutoDAO obtenerDAO(Modo modo) throws Exception {
        return switch (modo) {
            case SQLITE ->
                new InstitutoSQLiteDAOImp();
            case ORACLE ->
                new InstitutoOracleXeDAOImp();
            default ->
                new InstitutoMockDAOImp();
        };
    }
}
