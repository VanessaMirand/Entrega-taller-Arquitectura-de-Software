import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        List<Muestra> muestras = List.of(
            new Muestra("PERGAMINO", new BigDecimal("500"), new BigDecimal("11.5"), 15, false),
            new Muestra("PERGAMINO", new BigDecimal("500"), new BigDecimal("14.0"), 15, false),
            new Muestra("EXCELSO",   new BigDecimal("500"), new BigDecimal("11.5"), 16, false),
            new Muestra("ESPECIAL",  new BigDecimal("500"), new BigDecimal("11.5"), 15, true),
            new Muestra("ORGANICO",  new BigDecimal("500"), new BigDecimal("14.0"), 15, true)
        );

        LiquidacionService servicio = new LiquidacionService();
        System.out.println("Version con condicionales");
        for (Muestra m : muestras) {
            BigDecimal factor = servicio.factorCalidad(m);
            System.out.println(String.format(Locale.ROOT,
                    "%-10s humedad %-5s malla %d cert %-5s -> paga %,d",
                    m.tipo(), m.humedad().toPlainString(), m.malla(), m.certificada(),
                    servicio.liquidar(m.kilos(), factor).longValueExact()));
        }
    }
}