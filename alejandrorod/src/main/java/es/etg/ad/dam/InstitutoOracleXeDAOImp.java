package es.etg.ad.dam;

import java.sql.Connection;
import java.sql.DriverManager;

public class InstitutoOracleXeDAOImp extends InstitutoSQLiteDAOImp {

    public InstitutoOracleXeDAOImp() {
        super(); // no se usa la conexión sqlite aquí, reemplazaremos
        try {
            // Cerrar la conexión sqlite abierta por el super() y abrir Oracle
            if (this.conn != null && !this.conn.isClosed()) {
                this.conn.close();
            }

            // Ajusta usuario/contraseña y host/servicio según tu entorno
            String user = "SYSTEM";
            String pass = "secret";
            String url = String.format("jdbc:oracle:thin:%s/%s@localhost:1521/XEPDB1", user, pass);

            // carga driver si es necesario (depende de tu driver)
            Connection oracleConn = DriverManager.getConnection(url);
            // asignamos la conexión (reflexión simple: campo protected conn heredado)
            // NOTA: en algunos entornos necesitas re-factorear para permitir inyección de conn.
            java.lang.reflect.Field f = InstitutoSQLiteDAOImp.class.getDeclaredField("conn");
            f.setAccessible(true);
            f.set(this, oracleConn);
            // activar FK no aplica en Oracle (Oracle maneja restricciones definidas)
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
