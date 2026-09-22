import java.math.BigDecimal;

/**
 * El puerto de este ejemplo, igual que TarifaRepositorio lo fue en la sesion 1:
 * nombra lo que el calculo necesita -- un factor -- sin decir con que formula
 * se obtiene.
 *
 * Una advertencia que conviene tener presente antes de aplicar Strategy en
 * cualquier parte: si lo unico que cambiara entre los tipos de cafe fuera una
 * constante, esto seria sobre-ingenieria y un Map<String, BigDecimal> bastaria.
 * Strategy se gana el sueldo aqui porque las tres formulas no solo tienen
 * numeros distintos: miran datos distintos de la muestra. El pergamino mira la
 * humedad, el excelso la ignora y mira la malla, y el especial mira la humedad
 * y ademas la certificacion. Son tres conjuntos distintos de campos, no tres
 * constantes distintas, y por eso no hay un mapa que exprese esto.
 */
public interface PoliticaDeCalidad {

    /** El codigo con el que la bodega clasifica la muestra. */
    String tipo();

    BigDecimal factor(Muestra m);
}
