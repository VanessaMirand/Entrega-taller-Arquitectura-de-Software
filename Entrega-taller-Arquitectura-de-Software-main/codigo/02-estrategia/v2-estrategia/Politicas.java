import java.util.LinkedHashMap;
import java.util.Map;

/**
 * El punto de cableado: lo unico que sabe que existen cuatro politicas.
 */
public class Politicas {

    private final Map<String, PoliticaDeCalidad> porTipo = new LinkedHashMap<>();

    public Politicas() {
        registrar(new PoliticaPergamino());
        registrar(new PoliticaExcelso());
        registrar(new PoliticaEspecial());
        registrar(new PoliticaOrganico()); // <-- LÍNEA AGREGADA
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