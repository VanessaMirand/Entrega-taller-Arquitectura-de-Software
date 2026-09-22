import java.math.BigDecimal;
import java.math.RoundingMode;

public class LiquidacionService {

    public BigDecimal liquidar(BigDecimal kilos, BigDecimal factorCalidad) {
        BigDecimal precioCarga = new BigDecimal("2850000");
        return kilos.multiply(precioCarga)
                    .multiply(factorCalidad)
                    .divide(new BigDecimal("125"), 0, RoundingMode.HALF_UP);
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
                factor = factor.add(new BigDecimal("0.0300"));
            }
            return factor.setScale(4, RoundingMode.HALF_UP);

        } else if ("ESPECIAL".equals(tipo)) {
            BigDecimal factorPergamino = factorCalidad(new Muestra("PERGAMINO", muestra.kilos(), humedad, muestra.malla(), muestra.certificada()));
            BigDecimal factor = factorPergamino;
            if (muestra.certificada()) {
                factor = factor.add(new BigDecimal("0.0800"));
            }
            if (factor.compareTo(new BigDecimal("1.1000")) > 0) {
                factor = new BigDecimal("1.1000");
            }
            return factor;

        } else if ("ORGANICO".equals(tipo)) {
            BigDecimal exceso = humedad.subtract(new BigDecimal("11.0"));
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

        } else {
            throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        }
    }
}