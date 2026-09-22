import java.math.BigDecimal;

/**
 * La politica del pergamino mas una prima por certificacion.
 *
 * Reusa PoliticaPergamino por composicion en vez de heredar de ella. Es una
 * decision, no un detalle: si heredara, cualquier cambio en la formula del
 * pergamino cambiaria la del especial sin que nadie lo pida, y el dia que la
 * cooperativa negocie el piso del pergamino habria que revisar las dos.
 */
public class PoliticaEspecial implements PoliticaDeCalidad {

    private final PoliticaPergamino base = new PoliticaPergamino();

    @Override
    public String tipo() {
        return "ESPECIAL";
    }

    @Override
    public BigDecimal factor(Muestra m) {
        BigDecimal factor = base.factor(m);
        if (m.certificada()) {
            factor = factor.add(new BigDecimal("0.0800"))
                           .min(PoliticaExcelso.TECHO);
        }
        return factor;
    }
}
