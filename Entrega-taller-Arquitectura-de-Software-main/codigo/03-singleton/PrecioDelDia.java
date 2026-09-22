/**
 * El Singleton de libro, escrito como lo escribe todo el mundo la primera vez.
 *
 * Tiene las tres piezas del patron: constructor privado, campo estatico y un
 * getInstance() que crea la instancia la primera vez que alguien la pide. Nada
 * de eso esta mal escrito. El problema es lo que el patron le hace al resto del
 * sistema, y eso no se ve mirando esta clase: se ve mirando quien la usa.
 */
public class PrecioDelDia {

    private static PrecioDelDia instancia;

    private long precioPorCarga = 2_850_000L;

    private PrecioDelDia() {
    }

    public static PrecioDelDia getInstance() {
        if (instancia == null) {                 // <-- aqui esta la carrera
            instancia = new PrecioDelDia();
        }
        return instancia;
    }

    public long precioPorCarga() {
        return precioPorCarga;
    }

    /**
     * El metodo que convierte el patron en un defecto: cualquier parte del
     * programa puede cambiar el precio para todas las demas, y ninguna firma
     * de metodo lo anuncia.
     */
    public void cambiarPrecio(long nuevo) {
        this.precioPorCarga = nuevo;
    }
}
