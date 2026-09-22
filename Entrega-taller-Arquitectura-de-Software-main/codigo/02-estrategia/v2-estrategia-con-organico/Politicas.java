import java.util.LinkedHashMap;
import java.util.Map;

/**
 * El punto de cableado: lo unico que sabe que existen tres politicas.
 *
 * Este es el archivo que se toca cuando entra un tipo nuevo de cafe, y es
 * deliberadamente lo mas tonto posible -- una tabla y una busqueda -- para que
 * quien lo edite no pueda romper una formula por accidente. En la sesion 3
 * este archivo desaparece: el contenedor de inyeccion de dependencias de
 * Spring descubre las implementaciones solo.
 *
 * El constructor no recibe las politicas por parametro, y eso es una decision
 * tomada a proposito, no un descuido. Un constructor
 * Politicas(PoliticaDeCalidad... politicas) se ve mas flexible y ademas es mas
 * facil de probar, porque una prueba podria construir este objeto con una sola
 * politica. Tiene un costo que en esta sesion pesa mas: mueve la lista de las
 * tres implementaciones a quien lo llame -- aqui seria el Main -- y entonces el
 * punto de cableado deja de estar en este archivo y pasa a estar mezclado con
 * los datos de prueba. Hoy el punto es que ese conocimiento viva en un archivo
 * que se pueda senalar con el dedo. En la sesion 3, cuando Spring descubra las
 * implementaciones solo, la discusion cambia y este archivo se va.
 */
public class Politicas {

    private final Map<String, PoliticaDeCalidad> porTipo = new LinkedHashMap<>();

    public Politicas() {
        registrar(new PoliticaPergamino());
        registrar(new PoliticaExcelso());
        registrar(new PoliticaEspecial());
        registrar(new PoliticaOrganico());
    }

    /**
     * Entra un tipo nuevo de cafe, se agrega una linea al constructor de arriba.
     * Eso es todo el cambio en esta version, y es lo que el taller 1 mide.
     */
    private void registrar(PoliticaDeCalidad p) {
        porTipo.put(p.tipo(), p);
    }

    public PoliticaDeCalidad para(String tipo) {
        PoliticaDeCalidad p = porTipo.get(tipo);
        if (p == null) {
            throw new IllegalArgumentException(
                    "tipo de cafe desconocido: '" + tipo + "'. Los conocidos"
                    + " ahora mismo son " + porTipo.keySet() + ", y la comparacion"
                    + " distingue mayusculas porque el tipo es un String y no un enum.");
        }
        return p;
    }
}
