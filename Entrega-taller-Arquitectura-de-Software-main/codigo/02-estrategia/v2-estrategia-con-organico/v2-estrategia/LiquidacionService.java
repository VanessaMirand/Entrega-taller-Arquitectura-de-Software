import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * El mismo calculo, sin una sola formula de calidad adentro.
 *
 * Comparelo con la version acoplada: no es que este metodo sea mas corto por
 * elegancia, es que las tres formulas que estaban aqui ahora estan en tres
 * archivos que este metodo no conoce, y por eso agregar la cuarta no lo obliga
 * a recompilarse ni a volverse a probar.
 */
public class LiquidacionService {

    private static final BigDecimal KILOS_POR_CARGA = new BigDecimal("125");
    private static final BigDecimal PRECIO_POR_CARGA = new BigDecimal("2850000");

    private final Politicas politicas;

    public LiquidacionService(Politicas politicas) {
        this.politicas = politicas;
    }

    public BigDecimal liquidar(Muestra m) {
        BigDecimal factor = politicas.para(m.tipo()).factor(m);
        // Se multiplica todo y se divide al final, una sola vez, redondeando
        // una sola vez. Dividir primero con escala 6 -- como lo hace todavia el
        // codigo de la sesion 1 -- da el mismo peso solo mientras los kilos
        // tengan tres decimales o menos, porque n/125000 es 8n/10^6 y cabe
        // exacto en seis decimales. Con un decimal mas la division redondea y
        // aparece un peso de diferencia. El bloque 6 de DineroDemo.java lo mide
        // con dos casos, y el laboratorio 3 del recurso interactivo lo avisa
        // cuando los kilos que uno teclea traen cuatro decimales.
        return m.kilos().multiply(PRECIO_POR_CARGA)
                        .multiply(factor)
                        .divide(KILOS_POR_CARGA, 0, RoundingMode.HALF_UP);
    }
}
