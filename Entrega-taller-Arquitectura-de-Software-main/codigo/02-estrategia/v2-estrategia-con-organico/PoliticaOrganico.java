import java.math.BigDecimal;
import java.math.RoundingMode;

public class PoliticaOrganico implements PoliticaDeCalidad {

    @Override
    public String tipo() {
        return "ORGANICO";
    }

    @Override
    public BigDecimal factor(Muestra muestra) {
        BigDecimal exceso = muestra.humedad().subtract(new BigDecimal("11.0"));
        BigDecimal factor = BigDecimal.ONE.subtract(new BigDecimal("0.02").multiply(exceso));
        
        factor = factor.setScale(4, RoundingMode.HALF_UP);
        
        if (factor.compareTo(new BigDecimal("0.9000")) < 0) {
            factor = new BigDecimal("0.9000");
        }
        
        if (muestra.certificada()) {
            factor = factor.add(new BigDecimal("0.0600"));
        }
        
        if (factor.compareTo(new BigDecimal("1.1000")) > 0) {
            factor = new BigDecimal("1.1000");
        }
        
        return factor;
    }
}