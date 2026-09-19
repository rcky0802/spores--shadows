# 🎭 La Decadencia al Detalle

El moho no se detiene en la superficie de los bloques. Penetra en las interfaces de usuario, altera los sonidos ambientales, degrada la utilidad mágica de las librerías y —curiosamente— no perturba a los aldeanos en su rutina laboral cotidiana.

---

## 🖥️ Decadencia Visual de las Interfaces (Superposiciones GUI)

Abrir una estación de trabajo infectada refleja en pantalla completa el deterioro del bloque en el mundo, mediante superposiciones gráficas a resolución nativa que se acoplan sobre la interfaz vanilla:

- **Mesas de Trabajo**: la cuadrícula 3×3 se llena de manchas orgánicas y junturas corroídas.
- **Cofres y Barriles**: las ranuras 9×3 y 9×6 muestran bordes desgastados por el musgo.
- **Telares y Mesas de Cartografía**: el lienzo absorbe humedad a lo largo de todos los márgenes de la interfaz.
- **Atriles**: leer un libro en un atril infectado proyecta una superposición orgánica en los bordes de las propias páginas.

El renderizado de los cofres ha sido minuciosamente perfeccionado: el clásico *Z-fighting* (parpadeo en la unión entre la base y la tapa) se resuelve mediante un ajuste milimétrico de la escala del modelo que realiza una transición fluida durante la animación de apertura.

## 🎶 Alteración Acústica

El moho se infiltra en las cajas de resonancia, modificando profundamente la acústica de los instrumentos musicales.

- **Bloques de Notas**: cada golpe produce notas desafinadas, con un cambio de tono (*pitch-shift*) oscuro y lúgubre, emitiendo partículas fúngicas en lugar de las notas gráficas habituales.
- **Tocadiscos (Jukebox)**: los discos musicales vanilla se reproducen a velocidad ralentizada y con afinación degradada, proporcional a la etapa de infección. El efecto es deliberadamente inquietante —perfecto para ambientaciones de terror o mazmorras.

## 📚 Decadencia de las Librerías: Magia y Drops

Las **Librerías estándar** sufren una doble degradación conforme avanza la infección fúngica: pierden potencia mágica de cara a la Mesa de Encantamientos y, al romperse sin *Toque de Seda*, liberan una cantidad decreciente de libros (debido a que el moho desintegra el papel y la encuadernación).

| Etapa | Poder de Encantamiento por bloque | Libros Soltados al Romper *(Sin Toque de Seda)* | Con Toque de Seda |
| :---: | :---: | :---: | :---: |
| **0 — Sano** *(o Encerado)* | **1.0** (completo) | **3 libros** *(Vanilla)* | Suelta la librería sana |
| **1 — Infectado** | **0.66** | **2 libros** | Suelta la librería infectada |
| **2 — Mohoso** | **0.33** | **1 libro** | Suelta la librería mohosa |
| **3 — Podrido** | **0.0** *(sin contribución)* | **0 libros** *(papel podrido)* | Suelta la librería podrida |

> [!NOTE]
> Al estar enceradas (*Waxed*), las librerías congelan su etapa actual: mantienen inalterado el drop de libros de su etapa y garantizan pleno poder de encantamiento (1.0) si no están deterioradas. Con *Toque de Seda* (*Silk Touch*), siempre se recupera el respectivo bloque de librería (encerado o no encerado) de la etapa correspondiente.

### 📖 Librerías Cinceladas (Chiseled Bookshelves)
El comportamiento de las **Librerías Cinceladas** es radicalmente distinto y salvaguarda el trabajo del jugador:
- **Conservación de Libros**: los volúmenes guardados en sus ranuras (libros comunes, escritos o encantados) permanecen 100% protegidos e intactos en cualquier etapa de infección, incluso durante la transición a Podrido o durante el encerado y desencerado con el hacha.
- **Rotura**: al destruir el bloque, todos los libros almacenados caen intactos al suelo (`ItemScatterer`), acompañados por el bloque de la librería cincelada.
- **Comparador**: la señal analógica de Redstone posterior (1..6 según la última ranura interactuada) se mantiene fiel al estándar Vanilla sin fallos ni distorsión alguna (véase también el [Capítulo 6](06_automatizar_la_limpieza.md)).

## 👨‍🌾 Aldeanos y Puntos de Interés

A pesar del deterioro estético visible, los aldeanos nunca abandonan sus puestos de trabajo. Pescadores, Granjeros, Pastores, Cartógrafos, Flecheros y Bibliotecarios reconocen de manera nativa incluso los bloques de Etapa 3 como estaciones de trabajo legítimas, vinculándose a ellas, trabajando y ofreciendo intercambios comerciales con total compatibilidad.

## 🚪 Fragilidad al Uso de Bloques Funcionales

La madera podrida carece por completo de integridad mecánica y sujeción en sus bisagras y pernos:
- **Riesgo de Rotura por Uso**: cada vez que un jugador interactúa con un bloque funcional no encerado de **Etapa 3 (Podrido)** —como abrir una **puerta**, accionar una **trampilla**, abrir una **puerta de valla** o presionar un **botón** de madera— existe un **10%** de probabilidad (`rotten_break_chance_on_use`) de que el mecanismo colapse al instante.
- En caso de fallo, el bloque se quiebra con un chasquido de madera rota (`BLOCK_WOOD_BREAK`) y es **destruido sin soltar ningún drop**.
- **Solución**: aplicar preventivamente un panal (**encerado**) refuerza la estructura e impide su colapso accidental durante el uso habitual.

---

| | |
| :--- | ---: |
| [← Automatizar la Limpieza](06_automatizar_la_limpieza.md) | [Configuración y Técnica →](08_configuracion_y_tecnica.md) |
| [📑 Índice](README.md) | |
