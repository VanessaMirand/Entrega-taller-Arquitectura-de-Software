import java.math.BigDecimal;
import java.math.RoundingMode;

/** Penaliza la humedad por encima de 11, con piso de 0,85. */
public class PoliticaPergamino implements PoliticaDeCalidad {

    static final BigDecimal PISO = new BigDecimal("0.8500");

    @Override
    public String tipo() {
        return "PERGAMINO";
    }

    @Override
    public BigDecimal factor(Muestra m) {
        BigDecimal exceso = m.humedad().subtract(new BigDecimal("11"));
        return BigDecimal.ONE.subtract(exceso.multiply(new BigDecimal("0.02")))
                .setScale(4, RoundingMode.HALF_UP)
                .max(PISO);
    }
}
