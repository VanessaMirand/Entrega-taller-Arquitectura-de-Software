# Código de la sesión 2

> Tres ejemplos, todos con el JDK solo y sin dependencias externas. **Nada de esta carpeta necesita el proyecto de Spring Boot**, que ya no se genera en clase sino en la casa, en la semana posterior a esta sesión, siguiendo `00-Curso/Preparacion-del-Entorno.md`; el bloque 7 de la sesión solo comprueba que funcionó. La razón del cambio está medida al comienzo de `Guia-Sesion-02.md` y son 55 megabytes por máquina.

Todas las salidas que aparecen en `Guia-Sesion-02.md` salieron de ejecutar estos archivos el 26 de agosto de 2026 con **OpenJDK 17.0.20**. Los tres ejemplos usan `record`, `var` no aparece y lo más nuevo del lenguaje es el `record` de Java 16, así que con un JDK 21 o 25 la salida debería ser la misma. **La única salida de esta carpeta que cambia entre corridas es el conteo de instancias del ejemplo 3, y cambia a propósito**: está explicado más abajo y en la guía.

## 01-dinero

Un solo archivo con seis demostraciones numeradas sobre por qué el dinero no se representa con `double`, y sobre lo que `BigDecimal` no arregla solo. Se compila y se corre en el sitio, sin directorio de salida aparte, porque no hay colisión de nombres con nada.

```bash
cd 01-dinero
javac -encoding UTF-8 DineroDemo.java && java DineroDemo
```

El bloque que importa es el segundo, y conviene saber por qué está escrito así. La versión anterior de este archivo intentaba demostrar el problema por acumulación —mil entregas sumadas con `double` frente a las mismas mil con `BigDecimal`— y al ejecutarla la diferencia salió **exactamente cero**, porque con 12,5 kg, 2.850.000 y factor 0,96 el `double` da por casualidad el resultado exacto. La demostración probaba lo contrario de lo que decía. Se cambió por el caso del redondeo en la frontera, que sí falla con una sola liquidación, y la acumulación se quedó en el bloque 3 con los dos escenarios lado a lado, incluido el que no falla, porque **el hecho de que el error no siempre aparezca es la razón por la que este defecto sobrevive años en producción**.

El bloque 5 imprime el mensaje de una `ArithmeticException` capturada. **No es un fallo del programa**: es la demostración de que `divide` sin escala revienta cuando el cociente es periódico. El programa termina con código de salida 0.

El bloque 6 se agregó después, y conviene saber por qué existe y por qué está al final en lugar de al lado del bloque 2, que es donde encajaría por tema. Existe porque los tres archivos `LiquidacionService.java` de este curso —el de las dos versiones de esta sesión y las copias de las sesiones 3, 4 y 5— llevan un comentario sobre su propia línea de cálculo que dice que multiplicar todo y dividir una sola vez al final no es estilo, y que la guía de la sesión 2 lo muestra con números; durante un tiempo esa medida no existía en ninguna parte y el comentario prometía algo que no estaba. Está al final, y no en su lugar temático, porque los bloques 1 a 5 se citan por número en `Guia-Sesion-02.md`, en el recurso interactivo y en las diapositivas: **insertarlo en el medio habría renumerado los cinco y desactualizado tres documentos de golpe**. Lo que mide es que la misma cuenta escrita en los dos órdenes posibles, las dos veces con `BigDecimal`, da un peso distinto cuando los kilos traen cuatro decimales, y el mismo peso cuando traen tres o menos.

## 02-estrategia

Dos carpetas que calculan la misma liquidación para cuatro muestras de café de tres tipos distintos. `v1-condicionales` tiene las tres fórmulas de calidad dentro del método que calcula la plata; `v2-estrategia` las tiene en tres clases que ese método no conoce. **Las dos imprimen exactamente los mismos cuatro números**, y eso es parte de la demostración: reorganizar no es cambiar el comportamiento.

```bash
cd 02-estrategia
javac -encoding UTF-8 -d /tmp/out1 v1-condicionales/*.java && java -cp /tmp/out1 Main
javac -encoding UTF-8 -d /tmp/out2 v2-estrategia/*.java   && java -cp /tmp/out2 Main
diff -rq v1-condicionales v2-estrategia
```

Igual que en la sesión 1, las dos carpetas tienen clases con el mismo nombre y **hay que compilarlas a directorios de salida distintos**. Compilar las dos al mismo destino deja un revoltijo donde la versión con condicionales parece tener estrategias.

La medida del costo del cambio —cuánto cuesta agregar un cuarto tipo de café en cada diseño— **no está en esta carpeta a propósito: es el taller 1**. La solución de referencia está en `docente/soluciones/sesion-02/` y no se reparte.

Hay una cosa que conviene saber antes de empezar el taller, porque es la que hace perder la primera media hora. **El tipo de café es un `String` y la comparación distingue mayúsculas.** En `v1-condicionales` es una cadena de `if` con `equals`, y en `v2-estrategia` es una búsqueda en las claves de un `Map<String, PoliticaDeCalidad>`; en los dos casos, `"pergamino"` en minúscula no encuentra nada y el programa lanza un `IllegalArgumentException` en tiempo de ejecución. Esto es lo que sale, y las dos versiones dan el mismo diagnóstico con palabras distintas porque una tiene el mapa y la otra no:

```
tipo de cafe desconocido: 'pergamino'. Los conocidos ahora mismo son [PERGAMINO, EXCELSO, ESPECIAL], y la comparacion distingue mayusculas porque el tipo es un String y no un enum.
```

```
tipo de cafe desconocido: 'pergamino'. Los tres validos son PERGAMINO, EXCELSO y ESPECIAL, en mayuscula: la comparacion distingue mayusculas porque el tipo es un String y no un enum.
```

Ese conjunto de tres valores cerrados debería ser un `enum`, y con un `enum` el error de arriba no compilaría en vez de aparecer corriendo. **Que no lo sea es deliberado y no un descuido del ejemplo**: el sistema de la cooperativa lleva catorce años guardando ese tipo como texto, así que cambiarlo en Java es lo fácil y decidir qué se hace con las filas viejas que no encajan es una migración de datos. Queda anotado como deuda y vuelve en la **sesión 4**, en el ADR 0001 del proyecto por capas, que explica el intercambio completo. Para el taller, la consecuencia práctica es una sola: **escriba el tipo del cuarto café en mayúscula sostenida, igual que los otros tres.**

## 03-singleton

Cuatro archivos que muestran tres consecuencias de tener un Singleton, ninguna de las cuales se ve leyendo la clase `PrecioDelDia`, que está escrita correctamente según el patrón.

```bash
cd 03-singleton
javac -encoding UTF-8 -d /tmp/out3 *.java && java -cp /tmp/out3 Main
```

Las dos primeras demostraciones son deterministas: las mismas dos pruebas pasan las dos en un orden y una de ellas falla en el otro. La tercera no lo es, y ahí está el punto. Lanza ocho hilos que piden `getInstance()` al mismo tiempo e informa cuántas instancias distintas se crearon. **En treinta corridas seguidas de la máquina de verificación, que tiene dos núcleos, el número estuvo siempre entre 7 y 8, y ninguna de las treinta imprimió 1.** Ese rango es lo único publicable, y conviene decir por qué: con cuántas de las treinta sale cada valor depende de cuántos núcleos tenga la máquina y de qué más esté corriendo al lado, así que un porcentaje medido aquí sería un dato de esta máquina disfrazado de dato del ejemplo, y habría que volver a medirlo cada semestre para nada. Lo que sí es del ejemplo es la comparación con las dos demostraciones de arriba: **esas dos imprimen exactamente lo mismo todas las veces y esta no**, y un patrón cuya única promesa es que hay una sola instancia no tiene permitido que el resultado dependa de la máquina. El número que salga en clase puede ser cualquiera entre 1 y 8; lo que no va a salir es una explicación de por qué. Si alguna vez imprime 1, no es que el código esté bien: es que esa corrida tuvo suerte.

El `Thread.sleep(50)` del constructor de `ConfiguracionLenta` está puesto para que la carrera se vea. Es una trampa deliberada y hay que decirlo en clase: **el defecto no lo causa el retardo, el retardo solo lo hace probable**. Un constructor que lee un archivo de configuración o consulta el precio del día en un servicio del gremio tarda más que eso sin necesidad de ningún `sleep`.
