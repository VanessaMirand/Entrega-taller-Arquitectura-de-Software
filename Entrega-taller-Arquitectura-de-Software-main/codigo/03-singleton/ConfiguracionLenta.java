/**
 * El mismo Singleton, con una sola diferencia: su constructor tarda. Leer un
 * archivo de configuracion, abrir una conexion o consultar el precio del dia
 * en un servicio del gremio tardan mas que esto.
 *
 * Ese retardo es lo que hace visible la carrera. Sin el, el defecto sigue ahi:
 * lo que cambia es la probabilidad de que aparezca en una corrida.
 */
public class ConfiguracionLenta {

    private static ConfiguracionLenta instancia;

    private ConfiguracionLenta() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static ConfiguracionLenta getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionLenta();
        }
        return instancia;
    }
}
