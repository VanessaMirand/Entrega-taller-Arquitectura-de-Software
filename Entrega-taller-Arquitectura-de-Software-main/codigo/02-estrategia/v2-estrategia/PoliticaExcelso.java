import java.math.BigDecimal;
import java.math.RoundingMode;

/** Premia el tamano de grano y no mira la humedad. Techo de 1,10. */
public class PoliticaExcelso implements PoliticaDeCalidad {

    static final BigDecimal TECHO = new BigDecimal("1.1000");

    @Override
    public String tipo() {
        return "EXCELSO";
    }

    @Override
    public BigDecimal factor(Muestra m) {
        BigDecimal sobreMalla = new BigDecimal(m.malla() - 15);
        return BigDecimal.ONE.add(sobreMalla.multiply(new BigDecimal("0.015")))
                .setScale(4, RoundingMode.HALF_UP)
                .min(TECHO);
    }
}
