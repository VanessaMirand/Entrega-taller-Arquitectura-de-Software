import java.math.BigDecimal;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Tres cosas que le pasan a un sistema por tener un Singleton, ninguna de las
 * cuales se ve leyendo la clase PrecioDelDia.
 */
public class Main {

    private static final BigDecimal QUINIENTOS = new BigDecimal("500");
    private static final BigDecimal FACTOR = new BigDecimal("0.96");

    public static void main(String[] args) throws InterruptedException {

        // --- 1. Dos pruebas que se pisan --------------------------------
        // Ninguna de las dos es una prueba mal escrita. La segunda falla por
        // lo que hizo la primera, y la primera no dice en ninguna parte que
        // deje algo cambiado.
        System.out.println("1. Dos pruebas, en este orden");
        pruebaPrecioNormal();
        pruebaPrecioDeAlza();

        System.out.println();
        System.out.println("2. Las mismas dos pruebas, en el orden contrario");
        reiniciarLoQueSePueda();
        pruebaPrecioDeAlza();
        pruebaPrecioNormal();

        // --- 3. La carrera de la inicializacion perezosa -----------------
        // Ocho hilos que esperan en la misma barrera y piden la instancia al
        // mismo tiempo. El chequeo de null de getInstance no es atomico.
        System.out.println();
        System.out.println("3. Ocho hilos pidiendo getInstance() al mismo tiempo");
        Set<ConfiguracionLenta> vistas =
                Collections.newSetFromMap(new IdentityHashMap<ConfiguracionLenta, Boolean>());
        CountDownLatch partida = new CountDownLatch(1);
        Thread[] hilos = new Thread[8];
        for (int i = 0; i < hilos.length; i++) {
            hilos[i] = new Thread(() -> {
                try {
                    partida.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                ConfiguracionLenta c = ConfiguracionLenta.getInstance();
                synchronized (vistas) {
                    vistas.add(c);
                }
            });
            hilos[i].start();
        }
        partida.countDown();
        for (Thread h : hilos) {
            h.join();
        }
        System.out.println("   instancias distintas creadas : " + vistas.size());
        System.out.println("   (un Singleton que produce mas de una instancia sigue");
        System.out.println("    llamandose Singleton en el codigo y en la documentacion)");
    }

    private static void pruebaPrecioNormal() {
        BigDecimal pago = new LiquidacionService().liquidar(QUINIENTOS, FACTOR);
        boolean pasa = pago.compareTo(new BigDecimal("10944000")) == 0;
        System.out.println(String.format(Locale.ROOT,
                "   precio normal  espera 10,944,000  obtiene %,d  -> %s",
                pago.longValueExact(), pasa ? "PASA" : "FALLA"));
    }

    private static void pruebaPrecioDeAlza() {
        // Esta prueba necesita otro precio y la unica forma de conseguirlo es
        // cambiarlo para todo el programa.
        PrecioDelDia.getInstance().cambiarPrecio(3_000_000L);
        BigDecimal pago = new LiquidacionService().liquidar(QUINIENTOS, FACTOR);
        boolean pasa = pago.compareTo(new BigDecimal("11520000")) == 0;
        System.out.println(String.format(Locale.ROOT,
                "   precio de alza espera 11,520,000  obtiene %,d  -> %s",
                pago.longValueExact(), pasa ? "PASA" : "FALLA"));
    }

    /**
     * No se puede reiniciar: el campo estatico es privado y no hay setter que
     * lo vuelva null. Lo unico que queda es devolver el valor a mano, que es
     * exactamente el remiendo que aparece en los proyectos con Singletons.
     */
    private static void reiniciarLoQueSePueda() {
        PrecioDelDia.getInstance().cambiarPrecio(2_850_000L);
    }
}
