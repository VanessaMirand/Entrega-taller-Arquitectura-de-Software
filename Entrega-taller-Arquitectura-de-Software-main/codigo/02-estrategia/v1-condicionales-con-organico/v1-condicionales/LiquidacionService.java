import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * La version que escribe todo el mundo, y con razon: cuando habia un solo tipo
 * de cafe esto era una linea, y cuando aparecio el segundo agregar un if fue
 * mas rapido que pensar. El problema no es el if; es que ya son tres formulas
 * distintas viviendo dentro del metodo que calcula la plata.
 */
public class LiquidacionService {

    private static final BigDecimal KILOS_POR_CARGA = new BigDecimal("125");
    private static final BigDecimal PRECIO_POR_CARGA = new BigDecimal("2850000");

    public BigDecimal liquidar(Muestra m) {
        BigDecimal factor;

        if (m.tipo().equals("PERGAMINO")) {
            // Penaliza dos puntos porcentuales por cada punto de humedad
            // por encima de 11, con un piso de 0,85.
            BigDecimal exceso = m.humedad().subtract(new BigDecimal("11"));
            factor = BigDecimal.ONE.subtract(exceso.multiply(new BigDecimal("0.02")))
                    .setScale(4, RoundingMode.HALF_UP)
                    .max(new BigDecimal("0.8500"));

        } else if (m.tipo().equals("EXCELSO")) {
            // No mira la humedad: premia el tamano de grano, con techo de 1,10.
            BigDecimal sobreMalla = new BigDecimal(m.malla() - 15);
            factor = BigDecimal.ONE.add(sobreMalla.multiply(new BigDecimal("0.015")))
                    .setScale(4, RoundingMode.HALF_UP)
                    .min(new BigDecimal("1.1000"));

        } else if (m.tipo().equals("ESPECIAL")) {
            // La formula del pergamino mas una prima por certificacion.
            BigDecimal exceso = m.humedad().subtract(new BigDecimal("11"));
            factor = BigDecimal.ONE.subtract(exceso.multiply(new BigDecimal("0.02")))
                    .setScale(4, RoundingMode.HALF_UP)
                    .max(new BigDecimal("0.8500"));
            if (m.certificada()) {
                factor = factor.add(new BigDecimal("0.0800")).min(new BigDecimal("1.1000"));
            }

        } else {
            throw new IllegalArgumentException(
                    "tipo de cafe desconocido: '" + m.tipo() + "'. Los tres validos"
                    + " son PERGAMINO, EXCELSO y ESPECIAL, en mayuscula: la"
                    + " comparacion distingue mayusculas porque el tipo es un"
                    + " String y no un enum.");
        }

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
