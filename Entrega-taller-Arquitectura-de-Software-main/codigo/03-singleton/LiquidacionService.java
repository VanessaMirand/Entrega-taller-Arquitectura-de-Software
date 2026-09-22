import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Compare el constructor de esta clase con el de la sesion 1.
 *
 * En la sesion 1 el servicio recibia su fuente de precios por constructor, y
 * por eso bastaba leer la firma para saber de que dependia. Aqui el
 * constructor no recibe nada y la clase parece no depender de nada, pero
 * depende de PrecioDelDia y de lo que cualquier otro pedazo del programa le
 * haya hecho antes. Es la misma dependencia de la sesion 1, escondida.
 */
public class LiquidacionService {

    private static final BigDecimal KILOS_POR_CARGA = new BigDecimal("125");

    public BigDecimal liquidar(BigDecimal kilos, BigDecimal factorCalidad) {
        // La dependencia entra aqui, en medio del calculo, y no por constructor.
        long precio = PrecioDelDia.getInstance().precioPorCarga();

        // Se multiplica todo y se divide al final, una sola vez, redondeando
        // una sola vez. Dividir primero con escala 6 -- como lo hace todavia el
        // codigo de la sesion 1 -- da el mismo peso solo mientras los kilos
        // tengan tres decimales o menos, porque n/125000 es 8n/10^6 y cabe
        // exacto en seis decimales. Con un decimal mas la division redondea y
        // aparece un peso de diferencia. El bloque 6 de DineroDemo.java lo mide
        // con dos casos, y el laboratorio 3 del recurso interactivo lo avisa
        // cuando los kilos que uno teclea traen cuatro decimales.
        return kilos.multiply(new BigDecimal(precio))
                    .multiply(factorCalidad)
                    .divide(KILOS_POR_CARGA, 0, RoundingMode.HALF_UP);
    }

    public BigDecimal factorCalidad(Muestra muestra) {
        String tipo = muestra.tipo();
        BigDecimal humedad = muestra.humedad();

        if ("PERGAMINO".equals(tipo)) {
            BigDecimal exceso = humedad.subtract(new BigDecimal("11.0"));
            BigDecimal factor = BigDecimal.ONE.subtract(new BigDecimal("0.02").multiply(exceso));
            factor = factor.setScale(4, RoundingMode.HALF_UP);
            if (factor.compareTo(new BigDecimal("0.8500")) < 0) {
                factor = new BigDecimal("0.8500");
            }
            if (factor.compareTo(new BigDecimal("1.1000")) > 0) {
                factor = new BigDecimal("1.1000");
            }
            return factor;
        } else if ("EXCELSO".equals(tipo)) {
            BigDecimal factor = new BigDecimal("1.0000");
            if (humedad.compareTo(new BigDecimal("12.0")) > 0) {
                BigDecimal exceso = humedad.subtract(new BigDecimal("12.0"));
                factor = factor.subtract(new BigDecimal("0.01").multiply(exceso));
            }
            if (muestra.malla() >= 16) {
                factor = factor.add(new BigDecimal("0.0200"));
            }
            return factor.setScale(4, RoundingMode.HALF_UP);
        } else if ("ESPECIAL".equals(tipo)) {
            BigDecimal factor = factorCalidad(new Muestra("PERGAMINO", humedad, muestra.malla(), muestra.certificada()));
            if (muestra.certificada()) {
                factor = factor.add(new BigDecimal("0.0500"));
            }
            if (factor.compareTo(new BigDecimal("1.1000")) > 0) {
                factor = new BigDecimal("1.1000");
            }
            return factor;
        } else if ("ORGANICO".equals(tipo)) {
            // Formula del tipo ORGANICO
            BigDecimal exceso = humedad.subtract(new BigDecimal("11.0"));
            BigDecimal factor = BigDecimal.ONE.subtract(new BigDecimal("0.02").multiply(exceso));
            factor = factor.setScale(4, RoundingMode.HALF_UP);
            
            // Piso de 0.9000
            if (factor.compareTo(new BigDecimal("0.9000")) < 0) {
                factor = new BigDecimal("0.9000");
            }
            
            // Prima si esta certificada
            if (muestra.certificada()) {
                factor = factor.add(new BigDecimal("0.0600"));
            }
            
            // Techo de 1.1000
            if (factor.compareTo(new BigDecimal("1.1000")) > 0) {
                factor = new BigDecimal("1.1000");
            }
            return factor;
        } else {
            // Mensaje corregido para que enumere los 4 tipos validos
            throw new IllegalArgumentException("Tipo desconocido: " + tipo + ". Los cuatro validos son PERGAMINO, EXCELSO, ESPECIAL y ORGANICO");
        }
    }
}