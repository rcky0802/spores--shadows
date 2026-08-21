# Spores & Shadows 

**Spores & Shadows** es un mod para Minecraft (Fabric 1.21.1) que introduce un ecosistema dinámico, realista e implacable de decadencia ambiental para la madera. ¡Ninguna estructura está a salvo del tiempo y los elementos!

---

## 🌳 Descripción y Contenido

¿Alguna vez has construido una majestuosa cabaña de madera pensando que permanecería allí intacta, desafiando a los siglos, sin ninguna necesidad de mantenimiento? **Spores & Shadows** revoluciona esta certeza, transformando la madera de un simple bloque inerte a un material vivo, vulnerable y reactivo a su entorno.

El mod reemplaza de manera completamente transparente y silenciosa cada pieza de madera colocada por el jugador (o generada naturalmente en estructuras como naufragios y minas) con una variante "durmiente". Con el paso del tiempo, agentes atmosféricos como la lluvia, la humedad, la oscuridad e incluso el bioma en el que te encuentras decidirán el destino de tus construcciones, obligándote a proteger tus edificios o a presenciar impotente su inexorable decadencia.

### 🔢 Detalles Técnicos y Bloques Añadidos

A nivel técnico, el mod inyecta un ecosistema completo para cada variante de madera.

* **🧱 13 Formatos Arquitectónicos**: *Troncos*, *Troncos Sin Corteza*, *Madera (Wood)*, *Madera Sin Corteza*, *Tablones (Planks)*, *Escaleras*, *Losas*, *Vallas*, *Puertas de Valla*, *Puertas*, *Trampillas*, *Placas de Presión*, *Botones*.

Para cada uno de los 104 formatos base de madera, el mod añade **3 variantes enmohecidas** (Deteriorado, Enmohecido, Podrido). Además, para cada uno de estos bloques — incluido el bloque original Vanilla — se crea la respectiva **variante encerada**.

De esta manera, el juego pone a disposición **728 variantes únicas y obtenibles en Supervivencia**:
1. Las **104 Variantes Vanilla Enceradas**: La copia protegida y encerada del bloque base Vanilla.
2. Las **312 Variantes Enmohecidas**: Las tres etapas de decadencia natural.
3. Las **312 Variantes Enmohecidas Enceradas**: Los bloques decaídos pero detenidos en el tiempo por la cera.

Este sistema te permite obtener en supervivencia bloques parcialmente enmohecidos para luego "sellarlos" con un panal de miel, pudiendo usarlos con total seguridad para fines decorativos sin riesgo de infectar las construcciones cercanas.

---

## 🦠 El Ciclo del Moho

La madera atraviesa 4 etapas de decadencia: **Vanilla (0) ➔ Deteriorado (1) ➔ Enmohecido (2) ➔ Podrido (3)**.

El avance ocurre solo si el "Riesgo de Infección" (`R`), recalculado constantemente, supera el umbral fijo de **0.4**. La fórmula exacta es:
`R = ((Humedad * Luz * Susceptibilidad) + Contagio) * Temperatura`

### Factores y Variables Exactas
* 💧 **Humedad (Clima + Profundidad + Agua)**: 
  - El valor base depende de las precipitaciones del bioma (Lluvia/Nieve: `0.8`, Seco: `0.3`). 
  - **Penalización por Profundidad**: Al descender por debajo del nivel del mar (`Y < 64`), la humedad aumenta vertiginosamente en `+0.01` por cada bloque de descenso, haciendo de las minas entornos extremadamente húmedos.
  - **Penalización Local**: La adyacencia a bloques de agua (`+0.15`) o calderos (`+0.10`) suma humedad adicional al bloque.
* ☀️ **Luz (UV)**: Escala linealmente desde `0.0` (Luz 15, bloquea totalmente la infección) a `1.0` (Oscuridad total).
* 🪓 **Susceptibilidad del Material**: La madera sin corteza es extremadamente vulnerable (`x1.4`), los troncos crudos son estándar (`x1.0`), mientras que la madera procesada en tablones resiste un poco más (`x0.8`).
* 🌡️ **Temperatura (Bioma + Altitud/Profundidad)**: 
  - Actúa como un filtro de supervivencia. El moho prolifera **solo** si la temperatura local está entre `0.15` y `1.5`.
  - **En la Superficie**: Depende del bioma. Los climas extremos como desiertos o glaciares bloquean totalmente la infección al fijar el factor en `0.0`.
  - **Bajo Tierra (`Y < 64`)**: Independientemente del bioma de la superficie, al descender la temperatura se normaliza gradualmente, estabilizándose en el valor perfecto de `0.5` (templado) por debajo de `Y=48`. ¡Incluso en un desierto o en un bioma helado, las cavernas profundas desarrollarán moho!
  - **Alta Altitud (`Y > 128`)**: Al subir en altitud, la temperatura cae gradualmente, congelándose a `-0.5` en el nivel `Y=256`. Construir cabañas en alta montaña preservará la madera en casi cualquier lugar.
* ☣️ **Contagio (Catalizadores)**: Suma una penalización directa si la madera está en contacto con agentes infecciosos:
  - Madera infectada: Deteriorada (`+0.05`), Enmohecida (`+0.10`), Podrida (`+0.20`).
  - Entorno: Barro (`+0.05`), Podzol/Micelio (`+0.15`), Hongos (`+0.25`), Flor de Esporas (`+0.80`).

---

## 🛠️ Interacciones y Prevención

El jugador no está indefenso frente a la naturaleza. Equipando la herramienta adecuada y actuando en **modo sigilo (Sneaking / Shift)**, puedes interactuar directamente con el estado vital de la madera. 
*(El sigilo es obligatorio para evitar encerar o raspar por error los bloques interactivos, como puertas, trampillas o botones).*

* 🪓 **Uso del Hacha (Scrape)**: Haciendo *Shift + Clic Derecho* con un hacha:
  - Si el bloque está **Encerado**, el hacha eliminará la capa de cera restaurando el ciclo vital normal.
  - Si el bloque está **Deteriorado o Enmohecido**, el hacha raspará la capa superficial de hongos, reduciendo la decadencia en 1 etapa. Un bloque en la Etapa 1 volverá a estar perfectamente limpio (Etapa 0 Vanilla).
  *(Cada raspado consume durabilidad normalmente).*
* 🐝 **Uso del Panal (Waxing)**: Haciendo *Shift + Clic Derecho* con un panal de miel en un bloque en *cualquier* etapa, este se convertirá en **Encerado**. La madera encerada está sellada: se vuelve inmune al daño ambiental, congela su decadencia infinitamente y pierde la capacidad de infectar bloques cercanos. 

*Función Inteligente: ¡Si realizas estas acciones en un bloque múltiple (como la mitad superior o inferior de una Puerta), la actualización se aplicará instantáneamente y en total sincronía a toda la estructura!*

---

## ⚖️ Penalizaciones y Fabricación

Usar madera podrida para la fabricación no es sabio. La estructura interna del material está irremediablemente comprometida, introduciendo severas penalizaciones que castigan la pereza:

* 💥 **Integridad Estructural (Drop) y Excavación**:
  La herramienta preferida para minar estos bloques sigue siendo el **Hacha** (exactamente como en Vanilla), con la única excepción de los bloques en la Etapa 3, tan débiles que no tienen ninguna herramienta asociada (se desmoronan en un instante incluso con las manos desnudas).
  - Los bloques Vanilla y **Deteriorados** permanecen sólidos (siempre se obtienen al **100%**).
  - Los bloques **Enmohecidos** son frágiles: solo tienen un **50%** de probabilidad de obtenerse a sí mismos, de lo contrario se harán añicos en la nada.
  - Los bloques **Podridos** se desmoronan instantáneamente al tacto (probabilidad de obtención del **0%**).
  
  *(💡 **El secreto de la Cera**: Encerar un bloque consolida su estructura. Cualquier bloque del mod, incluso el Podrido, si está **Encerado** siempre tendrá un **100% de probabilidad de obtención**, ¡incluso sin usar Toque de Seda!)*
* 🛠️ **Rendimiento de Fabricación (Recuperación)**:
  Todavía puedes usar la madera infectada en la mesa de trabajo para fabricar objetos básicos (como Tablones, Losas, Escaleras o Palos). El objeto final siempre estará perfectamente limpio (**Vanilla**), pero dado que te ves obligado a descartar las partes podridas de la madera original, la cantidad de objetos obtenidos caerá drásticamente:

  | Calidad del Material | 🌳 Ej: Tronco ➔ Tablones | 🦯 Ej: Tablones ➔ Palos |
  | :--- | :---: | :---: |
  | 🌲 **Sano (Vanilla)** | 1 Tronco ➔ **4** Tablones | 2 Tablones ➔ **4** Palos |
  | 🟢 **Deteriorado** | 1 Tronco ➔ **2** Tablones | 2 Tablones ➔ **2** Palos |
  | 🦠 **Enmohecido** | 1 Tronco ➔ **1** Tablón | 2 Tablones ➔ **1** Palo |
  | ☠️ **Podrido** | *Receta Inválida* ❌ | *Receta Inválida* ❌ |

* 🔥 **Poder Combustible**: 
  - La madera Deteriorada se quema con eficiencia reducida a la mitad (**50%**).
  - La Enmohecida baja a un cuarto de eficiencia (**25%**).
  - La Podrida se quema en unos instantes (**12.5%**), haciéndola inútil como combustible.
* ♻️ **Compostador (El lado positivo de la Podredumbre)**:
  Si un bloque está demasiado podrido para construir con él, ¡recíclalo! Toda la madera del mod ha sido integrada con el Compostador Vanilla para generar Polvo de Hueso. Cuanto más degradada esté la madera (y rica en esporas), mayor será la probabilidad de éxito:
  - Madera Deteriorada: **50%**
  - Madera Enmohecida: **65%**
  - Madera Podrida: **85%** (¡Excelente fertilizante!)

*(💡 **Nota sobre los Bloques Encerados**: ¡La cera es un sellador ambiental, pero no bloquea el uso del objeto! Puedes usar los bloques encerados en la mesa de trabajo, quemarlos en el horno o tirarlos al compostador: se comportarán exactamente como su contraparte no encerada, manteniendo las mismas penalizaciones o bonificaciones vinculadas únicamente a su nivel interno de podredumbre).*

---

## 🗺️ Generación de Estructuras

El moho no se limita a los bloques colocados por el jugador. El mod intercepta el motor de generación de Minecraft para aplicar el desgaste del tiempo a todas las estructuras de madera que descubrirás en el mundo. 

Las estructuras se dividen en 4 niveles base de degradación:
1. 🏴‍☠️ **Degradación Crítica** (Alto porcentaje de madera Podrida): Naufragios Hundidos (`shipwreck`), Cabañas de Pantano (`swamp_hut`).
2. 🧟 **Degradación Alta** (Mixto Deteriorado y Enmohecido): Minas Abandonadas (`mineshaft`), Aldeas Zombis (`zombie_village`), Ruinas de Sendero (`trail_ruins`).
3. 🏹 **Degradación Moderada** (Principalmente Deteriorado): Puestos de Saqueadores (`pillager_outpost`), Portales en Ruinas (`ruined_portal`).
4. 🏡 **Degradación Mínima** (Casi totalmente sano): Aldeas normales (`village`), Mansiones del Bosque (`mansion`).

*(💡 **Factores Dinámicos**: ¡Durante la generación, el código analiza el entorno bloque por bloque! Si una pared del naufragio está expuesta al aire y al sol estará más intacta, mientras que los tablones hundidos en el fondo marino o bajo tierra estarán drásticamente más podridos).*

**🛡️ La Inmunidad de la Madera Natural y las Estructuras**:
Para no arruinar la experiencia de juego (evitando que los jugadores encuentren el mundo entero ya colapsado antes de poder explorarlo), hay dos excepciones a la decadencia automática:
* **Árboles Nativos**: Los árboles generados naturalmente (o crecidos de brotes) no generan moho porque la madera todavía está "viva". Solo la madera talada y procesada por el jugador comienza a pudrirse.
* **Estructuras Suspendidas**: Las estructuras se generan con el porcentaje de moho indicado arriba, pero luego se "congelan". Los bloques de las estructuras son nativamente inmunes al avance de la podredumbre, a menos que el jugador interactúe con ellos (ej. rompiéndolos, raspándolos o modificándolos). Esta protección salva a las aldeas de la destrucción espontánea. ¡Si quieres una experiencia súper extrema, puedes deshabilitar la inmunidad de las estructuras desde el menú de configuraciones!

---

## ⚙️ Configuración del Mod
El mod incluye un menú de configuración accesible directamente desde el juego (requiere **Cloth Config** y **ModMenu**) que te garantiza un control absoluto sobre cada mecánica individual. 
Las opciones se dividen en 7 categorías principales:

* 🛠️ **General**: ¡Desactiva el crecimiento del moho globalmente, cambia el umbral de infección, expande el radio de escaneo o **desactiva la inmunidad de las estructuras** para que las aldeas se pudran espontáneamente!
* 🌡️ **Entorno (Environment)**: Modifica los valores base para lluvia/seco, las bonificaciones para el agua, o personaliza a qué altitudes y temperaturas el moho debe congelarse o proliferar.
* 🪓 **Susceptibilidad (Susceptibility)**: Ajusta qué tan rápido se pudren los bloques procesados (tablones) en comparación con los crudos o sin corteza.
* ☣️ **Catalizadores (Catalysts)**: Equilibra la agresividad de los hongos, el barro, la *flor de esporas* (spore blossom) y los propios bloques de madera infectada.
* 🗺️ **Estructuras (Structures)**: Personaliza en detalle (porcentaje a porcentaje) cómo se generan los naufragios, las aldeas y las minas.
* 🔥 **Horno (Furnace Multipliers)**: Modifica la eficiencia de cocción de la madera para las diversas etapas de decadencia.
* 💥 **Obtención (Drop)**: Sube o baja la tasa de obtención (drop rate) de la madera frágil, si consideras que el mod es demasiado punitivo.
