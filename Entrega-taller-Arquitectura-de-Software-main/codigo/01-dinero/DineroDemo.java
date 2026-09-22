import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Por que el dinero no se representa con double.
 *
 * No es una opinion de estilo ni una mania de purista: el double no puede
 * representar 0.1 exactamente, porque en binario 0.1 es periodico igual que
 * 1/3 en decimal. De ahi salen dos consecuencias distintas, y la segunda es
 * la que de verdad genera reclamos.
 *
 * La primera es la que todo el mundo cuenta: el error se acumula. Es cierta,
 * pero es debil, porque en pesos colombianos hacen falta cientos de miles de
 * operaciones para que la deriva llegue a valer un peso. El bloque 3 lo mide
 * y muestra que con el escenario mas obvio de la cooperativa la deriva es
 * exactamente cero.
 *
 * La segunda es la que importa: cuando el resultado cae justo en la frontera
 * de redondeo, el double esta un pelo por debajo y redondea para el otro lado.
 * Ahi no hay acumulacion ni volumen: una sola liquidacion, un peso de
 * diferencia, y el caficultor que rehizo la cuenta a mano tiene razon.
 *
 * Y hay una tercera cosa, que no es culpa del double sino de como se usa el
 * BigDecimal: el orden de las operaciones. Dividir primero y multiplicar
 * despues cuesta otro peso, con BigDecimal en las dos cuentas. El bloque 6 lo
 * mide, y es el que sostiene el comentario que LiquidacionService tiene sobre
 * su propia linea de calculo en las sesiones 2, 3, 4 y 5.
 *
 * Todas las cifras que imprime este programa salen de ejecutarlo.
 */
public class DineroDemo {

    /** Una carga de cafe son 125 kilos. Es la unidad en que se cotiza el precio. */
    private static final BigDecimal KILOS_POR_CARGA = new BigDecimal("125");

    public static void main(String[] args) {

        // --- 1. El problema en su forma mas corta -------------------------
        System.out.println("1. La suma que todo el mundo cree que da 0.3");
        System.out.println("   0.1 + 0.2         = " + (0.1 + 0.2));
        System.out.println("   ¿es igual a 0.3?  = " + (0.1 + 0.2 == 0.3));
        System.out.println("   con BigDecimal    = "
                + new BigDecimal("0.1").add(new BigDecimal("0.2")));
        System.out.println("   ¿es igual a 0.3?  = "
                + new BigDecimal("0.1").add(new BigDecimal("0.2"))
                        .equals(new BigDecimal("0.3")));

        double diez = 0.0;
        for (int i = 0; i < 10; i++) {
            diez += 0.1;
        }
        System.out.printf(Locale.ROOT, "   sumar 0.1 diez veces = %.20f%n", diez);
        System.out.println("   ¿es igual a 1.0?     = " + (diez == 1.0));
        System.out.println();

        // --- 2. Una liquidacion, un peso ----------------------------------
        // Este es el caso que hay que saberse. Una entrega de 30,9 kg con
        // factor de calidad 0,9625 al precio de referencia de 2.850.000 por
        // carga vale exactamente 678.100,5 pesos. Como se liquida en pesos
        // enteros hay que redondear, y ahi los dos caminos se separan.
        BigDecimal kilos = new BigDecimal("30.9");
        BigDecimal precio = new BigDecimal("2850000");
        BigDecimal factor = new BigDecimal("0.9625");

        // El orden es el mismo de LiquidacionService y no es estilo: se
        // multiplica todo y se divide una sola vez al final. La division se pide
        // sin escala a proposito, y eso normalmente es un error -- el bloque 5
        // muestra que revienta con 10/3 -- pero aqui no puede reventar: 125 es 5
        // al cubo, asi que dividir por 125 nunca da un periodico y el cociente
        // exacto existe siempre. El setScale(10) solo agrega ceros para que la
        // cifra quede alineada con el %.10f del double de tres lineas mas abajo;
        // va sin modo de redondeo para que, si algun dia el valor exacto
        // necesitara mas de diez decimales, este programa reviente en lugar de
        // imprimir un numero redondeado bajo el rotulo "valor exacto".
        BigDecimal exacto = kilos.multiply(precio)
                .multiply(factor)
                .divide(KILOS_POR_CARGA)
                .setScale(10);
        BigDecimal pagoExacto = exacto.setScale(0, RoundingMode.HALF_UP);

        double kilosD = 30.9;
        double conDouble = (kilosD / 125.0) * 2_850_000.0 * 0.9625;
        long pagoDouble = Math.round(conDouble);

        System.out.println("2. Una entrega de 30,9 kg, factor 0,9625, a 2.850.000 la carga");
        System.out.println("   valor exacto              : " + exacto.toPlainString());
        System.out.printf(Locale.ROOT, "   el double en el informe   : %.2f   <-- aqui nadie ve nada%n", conDouble);
        System.out.printf(Locale.ROOT, "   el double con 10 decimales: %.10f%n", conDouble);
        System.out.println("   el double, de verdad      : " + new BigDecimal(conDouble).toPlainString());
        System.out.println("   BigDecimal, HALF_UP       : " + pagoExacto.toPlainString());
        System.out.println("   double, Math.round        : " + pagoDouble);
        System.out.println("   se le pago de menos       : "
                + pagoExacto.subtract(new BigDecimal(pagoDouble)).toPlainString());
        System.out.println();

        // --- 3. La acumulacion, que a veces no aparece --------------------
        // El error de acumulacion existe, pero no siempre se manifiesta, y esa
        // es justamente la razon por la que el double es peligroso: el sistema
        // pasa las pruebas durante meses y descuadra el dia que cambia un
        // parametro. Los dos escenarios de abajo usan el mismo codigo.
        System.out.println("3. Un millon de entregas, dos escenarios, el mismo codigo");
        acumular("12,5 kg  · 2.850.000 · 0,96  ", "12.5", "2850000", "0.96", 1_000_000);
        acumular("37,3 kg  · 2.871.450 · 0,9635", "37.3", "2871450", "0.9635", 1_000_000);
        System.out.println();

        // --- 4. Las dos trampas de BigDecimal -----------------------------
        System.out.println("4. Las dos trampas de BigDecimal");
        System.out.println("   new BigDecimal(0.1)      = " + new BigDecimal(0.1));
        System.out.println("   new BigDecimal(\"0.1\")    = " + new BigDecimal("0.1"));
        BigDecimal x = new BigDecimal("2.50");
        BigDecimal y = new BigDecimal("2.5");
        System.out.println("   \"2.50\".equals(\"2.5\")     = " + x.equals(y));
        System.out.println("   compareTo == 0           = " + (x.compareTo(y) == 0));
        System.out.println();

        // --- 5. La division que revienta ----------------------------------
        System.out.println("5. La division sin escala explicita");
        try {
            System.out.println(new BigDecimal("10").divide(new BigDecimal("3")));
        } catch (ArithmeticException ex) {
            System.out.println("   ArithmeticException: " + ex.getMessage());
        }
        System.out.println("   con escala y modo        = "
                + new BigDecimal("10").divide(new BigDecimal("3"), 6, RoundingMode.HALF_UP));
        System.out.println();

        // --- 6. El orden de las operaciones -------------------------------
        // Este bloque no habla del double: las tres cuentas de abajo son de
        // BigDecimal. Habla de dividir primero contra dividir al final, que es
        // la diferencia entre el codigo de la sesion 1 y el de la sesion 2, y
        // que hasta ahora estaba explicada en un comentario sin ningun numero
        // que la sostuviera. Los kilos de cuatro decimales no salen de una
        // bascula: salen de una cuenta anterior, que es como llegan de verdad
        // -- aqui, de repartir una carga de 100 kg entre tres socios -- o de una
        // columna declarada con cuatro decimales.
        System.out.println("6. El orden de las operaciones, que vale otro peso");
        System.out.println("   100 kg repartidos entre tres socios, factor 0,9625");
        ordenDeOperaciones("con los kilos a tres decimales, 33,333 kg",
                "33.333", "2850000", "0.9625");
        ordenDeOperaciones("con los kilos a cuatro decimales, 33,3333 kg",
                "33.3333", "2850000", "0.9625");
    }

    /**
     * Liquida la misma entrega por los dos ordenes posibles y compara. El valor
     * exacto se calcula multiplicando primero y dividiendo una sola vez al
     * final, sin escala, que con divisor 125 siempre es exacto.
     *
     * Con tres decimales o menos los dos ordenes dan el mismo peso, y no por
     * casualidad: kilos/125 es 8*kilos/1000, asi que un numero de tres decimales
     * dividido por 125 cabe entero en seis decimales y la escala 6 no redondea
     * nada. Con cuatro decimales ya no cabe, la escala 6 redondea, y el peso
     * cambia. Ninguna prueba del proyecto lo delata, porque las muestras del
     * ejemplo tienen un decimal.
     */
    private static void ordenDeOperaciones(String rotulo, String kilos,
                                           String precio, String factor) {
        BigDecimal k = new BigDecimal(kilos);
        BigDecimal p = new BigDecimal(precio);
        BigDecimal f = new BigDecimal(factor);

        BigDecimal exacto = k.multiply(p).multiply(f).divide(KILOS_POR_CARGA);
        BigDecimal alFinal = exacto.setScale(0, RoundingMode.HALF_UP);
        BigDecimal primero = k.divide(KILOS_POR_CARGA, 6, RoundingMode.HALF_UP)
                .multiply(p).multiply(f)
                .setScale(0, RoundingMode.HALF_UP);

        System.out.println("   " + rotulo);
        System.out.println("     valor exacto                : " + exacto.toPlainString());
        System.out.println("     dividiendo primero, escala 6: " + primero.toPlainString());
        System.out.println("     multiplicando y al final    : " + alFinal.toPlainString());
        System.out.println("     diferencia                  : "
                + alFinal.subtract(primero).toPlainString());
    }

    /**
     * Suma la misma liquidacion n veces por los dos caminos e informa la
     * diferencia. No redondea en cada iteracion a proposito: lo que se quiere
     * medir es el error de representacion, no el de redondeo.
     *
     * Aqui la cuenta de BigDecimal divide primero, al contrario del bloque 2, y
     * eso esta bien porque los dos escenarios traen kilos de uno y de tres
     * decimales: con tres decimales o menos los dos ordenes dan exactamente el
     * mismo valor, con los mismos digitos. Se dejo asi para que la comparacion
     * entre este bloque y el 6 se pueda hacer leyendo, y para que quede dicho lo
     * que importa: que los dos ordenes coincidan es una propiedad de estos
     * datos, no del codigo.
     */
    private static void acumular(String rotulo, String kilos, String precio,
                                 String factor, int n) {
        BigDecimal una = new BigDecimal(kilos)
                .divide(KILOS_POR_CARGA, 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(precio))
                .multiply(new BigDecimal(factor));
        BigDecimal totalExacto = una.multiply(new BigDecimal(n));

        double cargas = Double.parseDouble(kilos) / 125.0;
        double unaD = cargas * Double.parseDouble(precio) * Double.parseDouble(factor);
        double totalDouble = 0.0;
        for (int i = 0; i < n; i++) {
            totalDouble += unaD;
        }

        BigDecimal deriva = new BigDecimal(totalDouble).subtract(totalExacto);
        System.out.println("   " + rotulo);
        System.out.println("     una entrega vale exactamente : " + una.toPlainString());
        System.out.println("     total exacto                 : " + totalExacto.toPlainString());
        System.out.printf(Locale.ROOT, "     total con double             : %.6f%n", totalDouble);
        System.out.printf(Locale.ROOT, "     deriva                       : %.6f%n", deriva);
    }
}
