import java.math.BigDecimal;

/**
 * Lo que el caficultor entrega y la bodega pesa y clasifica.
 *
 * La humedad es BigDecimal y no double por la razon del bloque 2: entra en la
 * formula del factor, y el factor multiplica plata.
 */
public record Muestra(String tipo,
                      BigDecimal kilos,
                      BigDecimal humedad,
                      int malla,
                      boolean certificada) {
}
