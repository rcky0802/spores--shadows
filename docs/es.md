# Spores & Shadows 

**Spores & Shadows** es un mod para Minecraft (Fabric 1.21.1) que introduce un ecosistema dinámico, realista e implacable de decadencia ambiental para la madera. ¡Ninguna estructura está a salvo del tiempo y los elementos!

---

## 🌳 Descripción y Contenido

¿Alguna vez has construido una majestuosa cabaña de madera pensando que permanecería allí intacta, desafiando a los siglos, sin ninguna necesidad de mantenimiento? **Spores & Shadows** revoluciona esta certeza, transformando la madera de un simple bloque inerte a un material vivo, vulnerable y reactivo a su entorno.

El mod reemplaza de manera completamente transparente y silenciosa cada pieza de madera colocada por el jugador (o generada naturalmente en estructuras como naufragios y minas) con una variante "durmiente". Con el paso del tiempo, agentes atmosféricos como la lluvia, la humedad, la oscuridad e incluso el bioma en el que te encuentras decidirán el destino de tus construcciones, obligándote a proteger tus edificios o a presenciar impotente su inexorable decadencia.

### 🔢 Detalles Técnicos y Bloques Añadidos

A nivel técnico, el mod inyecta un ecosistema completo para cada variante individual de madera (incluyendo las maderas carmesí y distorsionadas del Nether).

* **🧱 13 Formatos Arquitectónicos**: *Troncos*, *Troncos Sin Corteza*, *Madera (Wood)*, *Madera Sin Corteza*, *Tablones (Planks)*, *Escaleras*, *Losas*, *Vallas*, *Puertas de Valla*, *Puertas*, *Trampillas*, *Placas de Presión*, *Botones*.

Para cada uno de los 130 formatos base de madera, el mod añade **3 variantes enmohecidas** (Deteriorado, Enmohecido, Podrido). Además, para cada uno de estos bloques — incluido el bloque original Vanilla — se crea la respectiva **variante encerada**.

De esta manera, el juego pone a disposición **910 variantes únicas y obtenibles en Supervivencia**:
1. Las **130 Variantes Vanilla Enceradas**: La copia protegida y encerada del bloque base Vanilla.
2. Las **390 Variantes Enmohecidas**: Las tres etapas de decadencia natural.
3. Las **390 Variantes Enmohecidas Enceradas**: Los bloques decaídos pero detenidos en el tiempo por la cera.

Este sistema te permite obtener en supervivencia bloques parcialmente enmohecidos para luego "sellarlos" con un panal de miel, pudiendo usarlos con total seguridad para fines decorativos sin riesgo de infectar las construcciones cercanas.

---

## 🦠 El Ciclo del Moho

La madera atraviesa 4 etapas de decadencia: **Vanilla (0) ➔ Deteriorado (1) ➔ Enmohecido (2) ➔ Podrido (3)**.

El avance ocurre solo si el "Riesgo de Infección" (`R`), recalculado constantemente, supera el umbral fijo de **0.4** (configurable). La fórmula exacta es:
$$R = ((\text{Humedad} \times \text{Luz} \times \text{Susceptibilidad}) + \text{Contagio}) \times \text{Temperatura}$$

### Factores y Variables Exactas
* 💧 **Humedad (Clima + Profundidad + Agua)**: 
  - El valor base depende de las precipitaciones del bioma (Lluvia/Nieve: `0.8`, Seco: `0.3`). 
  - **Penalización por Profundidad**: Al descender por debajo del nivel del mar (`Y < 64`), la humedad aumenta vertiginosamente en `+0.01` por cada bloque de descenso, haciendo de las minas y cuevas entornos sumamente húmedos.
  - **Penalización Local**: La adyacencia a bloques de agua (`+0.15`) o calderos (`+0.10`) suma humedad adicional al bloque.
* ☀️ **Luz (UV)**: Escala linealmente desde `0.0` (Luz 15, bloquea totalmente la infección) a `1.0` (Oscuridad total).
* 🪓 **Susceptibilidad del Material**: La madera sin corteza es extremadamente vulnerable (`x1.4`), los troncos crudos son estándar (`x1.0`), mientras que la madera procesada en tablones resiste un poco más (`x0.8`).
* 🌡️ **Temperatura (Bioma + Altitud/Profundidad)**: 
  - Actúa como un filtro de supervivencia. El moho prolifera **solo** si la temperatura local está entre `0.15` y `1.5`.
  - **En la Superficie**: Depende del bioma. Climas extremos como desiertos o glaciares detienen por completo la infección, reduciendo el factor a `0.0`.
  - **Nether & End**: El calor extremo del Nether y el vacío helado del End son totalmente letales para el moho. La madera nunca se pudrirá en estas dimensiones.
  - **Bajo Tierra (`Y < 64`)**: Independientemente del bioma de la superficie, a medida que desciendes, la temperatura se normaliza gradualmente, estabilizándose en un perfecto `0.5` (templado) por debajo de `Y=48`. ¡Incluso en un desierto o bioma congelado, las cuevas profundas desarrollarán moho!
  - **Alta Altitud (`Y > 128`)**: Al subir en altitud, la temperatura cae gradualmente, congelándose a `-0.5` en el nivel `Y=256`. Construir cabañas en alta montaña preservará la madera en casi cualquier lugar.
* ☣️ **Contagio (Catalizadores)**: Suma una penalización directa si la madera está en contacto con agentes infecciosos:
  - Madera infectada: Deteriorada (`+0.05`), Enmohecida (`+0.10`), Podrida (`+0.20`).
  - Entorno: Barro (`+0.05`), Podzol/Micelio (`+0.15`), Hongos (`+0.25`), Flor de Esporas (`+0.80`).

---

## ☠️ Peligros Ambientales (Miasma Volumétrico)

- ✨ **Partículas de Esporas**: Los bloques en las etapas **Enmohecido** y **Podrido** emiten esporas visibles desde sus caras expuestas (desactivado bajo el agua).
- 🤢 **Miasma Tóxico (Algoritmo BFS Flood-Fill)**: Mediante un algoritmo volumétrico de *Flood-Fill* en 3D que parte de los ojos del jugador, el juego analiza la concentración de gases tóxicos en espacios cerrados:
  - **Dispersión al Aire Libre**: Para cada bloque de aire se verifica la altura atmosférica. Si el espacio comunica directamente con el cielo exterior (`openAir`), las esporas se dispersan al instante y el miasma se anula por completo.
  - **Volumen Confinado y Límite Manhattan**: El miasma se acumula en habitaciones de hasta 512 m³ de volumen de aire y dentro de un radio Manhattan de 8 bloques (`MAX_MANHATTAN_RADIUS = 8`), evitando propagaciones infinitas por galerías subterráneas.
  - **Ventilación Natural por Fisuras**: Los bloques no sólidos o parciales orientados al exterior (vallas, barrotes de hierro, rejas, losas) actúan como tomas de ventilación natural ($+3.0$ de ventilación por cada apertura), reduciendo drásticamente la toxicidad neta:
    $$\text{Net Miasma} = \text{Toxic Score} - \text{Ventilation Score}$$
  - **Efectos de Estado Escalonados**:
    - **Aviso Ambiental**: Micelio ligero y advertencia si $\text{Miasma Neto} \ge 3.0$ o $\text{Densidad} \ge 0.04$.
    - **Hambre**: Partículas de esporas leves si $\text{Miasma Neto} \ge 8.0$ (o $\text{Densidad} \ge 0.09$ y $\text{Miasma Neto} \ge 5.0$).
    - **Náusea + Veneno**: Partículas densas de flor de esporas si $\text{Miasma Neto} \ge 35.0$ (o $\text{Densidad} \ge 0.18$ y $\text{Miasma Neto} \ge 15.0$).
    
  *(💡 Todos los umbrales de activación, duraciones y potencias de efectos son 100% personalizables en Cloth Config. Usa el comando `/miasma` para diagnosticar la ventilación y toxicidad de tu habitación).*

---

## 🛠️ Interacciones y Prevención

El jugador no está indefenso frente a la naturaleza. Equipando la herramienta adecuada y actuando en **modo sigilo (Sneaking / Shift)**, puedes interactuar directamente con el estado vital de la madera.
*(El sigilo es obligatorio para evitar encerar o raspar por error los bloques interactivos, como puertas, trampillas o botones).*

* 🪓 **Uso del Hacha (Raspado / Scrape)**: Haciendo *Shift + Clic Derecho* con un hacha:
  - Si el bloque está **Encerado**, el hacha removerá la capa de cera restaurando el ciclo de decadencia normal.
  - Si el bloque está **Deteriorado o Enmohecido**, el hacha raspará la capa superficial de hongos, reduciendo la decadencia en 1 etapa. Un bloque en la Etapa 1 volverá a estar limpio (Etapa 0 Vanilla).
  *(Cada raspado consume durabilidad normalmente).*
* 🐝 **Uso del Panal (Encerado / Waxing)**: Haciendo *Shift + Clic Derecho* con un panal de miel en un bloque en *cualquier* etapa, este se convertirá en **Encerado**. La madera encerada está sellada: se vuelve inmune al daño ambiental, congela su decadencia indefinidamente, pierde la capacidad de infectar bloques vecinos y no genera miasma tóxico.

*Función Inteligente: ¡Si realizas estas acciones en un bloque múltiple (como la mitad superior o inferior de una Puerta), la actualización se aplicará instantáneamente y en total sincronía a toda la estructura!*

---

## 🔨 Física, Dureza y Comportamiento Mecánico

A medida que el moho descompone las fibras de celulosa y lignina, las propiedades físicas del bloque cambian de forma radical:

### 1. Dureza Progresiva Escalonada
La resistencia al picado disminuye drásticamente según la etapa de degradación:
* 🌲 **Etapa 0 (Sano / Vanilla)**: Dureza **2.0** ($100\%$)
* 🟢 **Etapa 1 (Deteriorado)**: Dureza **1.6** ($80\%$)
* 🦠 **Etapa 2 (Enmohecido)**: Dureza **1.0** ($50\%$)
* ☠️ **Etapa 3 (Podrido)**: Dureza **0.4** ($20\%$)

### 2. Fragilidad Extrema y Neutralización de Herramienta (Etapa 3)
En la madera en **Etapa 3 (Podrido)**, el colapso estructural interno es tan severo que el bloque pierde toda consistencia mecánica y no tiene ninguna herramienta asignada:
* Romper un bloque de Etapa 3 con un **Hacha de Diamante/Netherite** o con el **puño / manos desnudas** toma exactamente la **misma velocidad**.

### 3. Nube de Esporas y Sonidos Fúngicos al Romper
* Al romper bloques en **Etapa 2 (Enmohecido)** o **Etapa 3 (Podrido)** sin el encantamiento **Toque de Seda (Silk Touch)**, se libera una explosión reactiva de partículas orgánicas (`SPORE_BLOSSOM_AIR`, `FALLING_SPORE_BLOSSOM`, `MYCELIUM`) acompañada de crujidos y sonidos fúngicos (`BLOCK_FUNGUS_BREAK` / `BLOCK_SPORE_BLOSSOM_BREAK`).

### 4. Inflamabilidad y Propagación de Fuego Escalares
La madera seca y descompuesta arde mucho más rápido, mientras que la cera y las maderas del Nether poseen propiedades ignífugas especiales:
* **Bonificación de Inflamabilidad (Burn) y Propagación (Spread)**:
  - **Etapa 1 (Deteriorado)**: $+5$ Inflamabilidad / $+10$ Velocidad de Propagación
  - **Etapa 2 (Enmohecido)**: $+10$ Inflamabilidad / $+25$ Velocidad de Propagación
  - **Etapa 3 (Podrido)**: $+20$ Inflamabilidad / $+60$ Velocidad de Propagación
* **Bonificación de Cera**: Los bloques encerados obtienen una capa protectora adicional ($+5$ resistencia al fuego).
* **Inmunidad del Nether**: Los bloques de madera Carmesí (*Crimson*) y Distorsionada (*Warped*) mantienen su inmunidad total Vanilla contra el fuego ($0$ inflamabilidad / $0$ propagación en todas sus etapas).

### 5. Resistencia a Explosiones Escalonada
La capacidad de absorber ondas expansivas (TNT, Creepers, bolas de fuego de Ghast) se degrada con el avance del moho:
* **Etapa 0 (Sano)**: Resistencia base normal ($100\%$)
* **Etapa 1 (Deteriorado)**: $80\%$ de resistencia base (multiplicador $0.80$)
* **Etapa 2 (Enmohecido)**: $50\%$ de resistencia base (multiplicador $0.50$)
* **Etapa 3 (Podrido)**: $10\%$ de resistencia base (multiplicador $0.10$, se pulveriza ante cualquier detonación cercana)

---

## ⚖️ Penalizaciones, Drops y Fabricación

Usar madera podrida para la fabricación no es sabio. La estructura interna del material está irremediablemente comprometida, introduciendo severas penalizaciones:

* 💥 **Integridad Estructural y Reglas de Drop**:
  - **Sin Toque de Seda (y No Encerado)**:
    - **Etapa 0 (Vanilla)**: Drop garantizado al **100%**.
    - **Etapa 1 (Deteriorado)**: Drop garantizado al **100%**.
    - **Etapa 2 (Enmohecido)**: **50%** de probabilidad de dropear el bloque; el otro 50% se desintegra en polvo inerte.
    - **Etapa 3 (Podrido)**: **0%** de drop (se pulveriza en la nada al romperse).
  - **Con Toque de Seda (Silk Touch) o Bloque Encerado**:
    - **100% de drop garantizado** en todas las etapas ($0, 1, 2, 3$).
    
  *(💡 **El Secreto de la Cera**: Encerar un bloque consolida su estructura interna. ¡Cualquier bloque encerado, incluso en Etapa 3, garantiza el drop al 100% al romperse sin necesidad de Toque de Seda!).*

* 🛠️ **Rendimiento de Fabricación (Recuperación) y Crafteo Híbrido**:
  Puedes usar la madera infectada en la mesa de crafteo para fabricar derivados básicos (Tablones, Losas, Escaleras, Palos). El resultado final siempre será limpio (**Vanilla**), pero el rendimiento se reduce por las partes descartadas:

  | Calidad del Material | 🌳 Ej: Tronco ➔ Tablones | 🦯 Ej: Tablones ➔ Palos |
  | :--- | :---: | :---: |
  | 🌲 **Sano (Vanilla / Encerado)** | 1 Tronco ➔ **4** Tablones | 2 Tablones ➔ **4** Palos |
  | 🟢 **Deteriorado** | 1 Tronco ➔ **2** Tablones | 2 Tablones ➔ **2** Palos |
  | 🦠 **Enmohecido** | 1 Tronco ➔ **1** Tablón | 2 Tablones ➔ **1** Palo |
  | ☠️ **Podrido** | *Receta Inválida* ❌ | *Receta Inválida* ❌ |

  *(💡 **Crafteo Híbrido**: Gracias a las compatibilidades de recetas, puedes mezclar libremente bloques normales y encerados de la misma etapa en la misma cuadrícula de crafteo).*

* 🔥 **Poder Combustible y Soporte de Carbón Vegetal (Charcoal)**: 
  A medida que avanza la podredumbre, la madera pierde densidad y poder calorífico en el horno:
  - **Sano (Vanilla / Encerado)**: Eficiencia normal (**100%** / multiplicador `1.0`).
  - **Deteriorado**: Eficiencia reducida a la mitad (**50%** / multiplicador `0.5`).
  - **Enmohecido**: Cae a un cuarto de eficiencia (**25%** / multiplicador `0.25`).
  - **Podrido**: Eficiencia residual mínima (**12.5%** / multiplicador `0.125`).
  - **Cocción de Carbón Vegetal**: Todos los troncos degradados y encerados son compatibles con los tags `#minecraft:item/charcoal` y `#c:charcoal` para cocinarse en hornos y producir carbón vegetal.
  
* ♻️ **Compostador (El Lado Positivo de la Podredumbre)**:
  Toda la madera del mod puede depositarse en el Compostador Vanilla para producir Polvo de Hueso. Cuanto más descompuesta esté, más rica en materia orgánica y esporas será:
  - Madera Deteriorada: **50%**
  - Madera Enmohecida: **65%**
  - Madera Podrida: **85%** (¡Fertilizante de alta calidad!)

* 🔴 **Componentes de Redstone (Botones y Placas de Presión)**:
  El moho ralentiza los mecanismos internos de los pulsadores de madera:
  - Sano: **1,5 segundos** (30 ticks).
  - Deteriorado: **3 segundos** (60 ticks).
  - Enmohecido: **7,5 segundos** (150 ticks).
  - Podrido: **22,5 segundos** (450 ticks).

---

## 🗺️ Generación de Estructuras

El mod intercepta el motor de generación de Minecraft para aplicar el desgaste del tiempo a todas las estructuras de madera que descubrirás en el mundo:

1. 🏴‍☠️ **Degradación Crítica** (Alta proporción de madera Podrida): Naufragios Hundidos (`shipwreck`), Cabañas de Pantano (`swamp_hut`).
2. 🧟 **Degradación Alta** (Mezcla de Deteriorado y Enmohecido): Minas Abandonadas (`mineshaft`), Aldeas Zombis (`zombie_village`), Ruinas de Sendero (`trail_ruins`).
3. 🏹 **Degradación Moderada** (Principalmente Deteriorado): Puestos de Saqueadores (`pillager_outpost`), Portales en Ruinas (`ruined_portal`).
4. 🏡 **Degradación Mínima** (Casi totalmente sana): Aldeas normales (`village`), Mansiones del Bosque (`mansion`).

*(💡 **Factores Dinámicos**: La generación evalúa el entorno bloque por bloque. Las áreas expuestas al sol y al aire se conservan mejor, mientras que los bloques sumergidos o enterrados sufren mayor descomposición).*

**🛡️ Inmunidad Natural y de Estructuras Suspendidas**:
* **Árboles Vivos**: Los árboles generados o crecidos naturalmente nunca se pudren porque su madera está viva.
* **Estructuras Suspendidas**: Los bloques generados en estructuras permanecen congelados y no avanzan de etapa de moho de forma autónoma hasta que un jugador interactúa con ellos (rompiéndolos, raspándolos o colocándolos). Puedes desactivar esta protección desde la configuración para una experiencia hardcore.

---

## 📖 Integración con JEI (Just Enough Items)

El mod incluye soporte nativo y completo para **JEI** (`SporesShadowsJEIPlugin`):

1. 🐝 **Categoría de Encerado (*Waxing*)**:
   - Muestra todas las recetas de transformación: `Bloque de Madera + Panal de Miel ➔ Bloque Encerado` para los 130 formatos y variantes.
2. 🪓 **Categoría de Raspado con Hacha (*Axe Scraping*)**:
   - **Remoción de Cera**: `Bloque Encerado + Hacha ➔ Bloque No Encerado`.
   - **Curación y Limpieza**: `Etapa 2 (Enmohecido) + Hacha ➔ Etapa 1 (Deteriorado) + Hacha ➔ Etapa 0 (Vanilla)`.
3. ℹ️ **Pestañas de Información (*Info Tabs*)**:
   - Fichas explicativas y advertencias contextuales para todos los bloques e ítems en **Etapa 3 (Podrido)**, detallando su fragilidad extrema, la pérdida de recetas de tablones y la necesidad de Toque de Seda o Cera para su obtención.

---

## 📊 Integración de HUD, Comandos y Logros

* 🔍 **Integración con Jade / WTHIT**:
  - Al mirar cualquier bloque de madera, el HUD muestra con total precisión su variante (ej. *Waxed Tainted Oak Planks*), su icono y el porcentaje actual de riesgo de infección.
  - El riesgo evalúa al **0%** para bloques encerados y se oculta para bloques podridos. Cambia de color dinámicamente (**Gris = Seguro**, **Rojo = En Riesgo**).

* 💻 **Comandos de Consola**:
  - `/moldrisk` (alias `/moldyrisk`): Calcula y muestra en el chat el riesgo de infección del bloque que estás mirando.
  - `/moldrisk verbose` (alias `/moldyrisk verbose`): Desglose matemático exhaustivo de la fórmula ($H_{\text{eff}}$, $L_{\text{uv}}$, $S_{\text{mat}}$, Catalizadores, Temperatura efectiva y umbral de infección).
  - `/miasma` (o `/miasma <jugador>`): Escanea y analiza en tiempo real la atmósfera de la habitación: volumen confinado, puntuación de toxicidad por moho, puntuación de ventilación por aperturas, miasma neto y densidad de esporas.
  - `/spores reload`: Recarga en caliente los archivos de configuración de Cloth Config sin necesidad de reiniciar el cliente o servidor.

* 🏆 **Logros Personalizados**:
  - **Spores & Shadows**: Sobrevive a la decadencia de la naturaleza.
  - **Prevención Natural**: Usa un panal de miel para encerar un bloque de madera y detener el moho.
  - **Mano de Santo**: Raspa el moho de un bloque de madera utilizando un hacha.
  - **Falta de Aire**: Sufre el veneno del miasma por respirar demasiado moho.
  - **Polvo al Polvo**: Intenta picar un bloque de madera podrida y observa cómo se desmorona en la nada.

---

## ⚙️ Configuración del Mod (Cloth Config)

El mod incluye una interfaz gráfica de configuración completa y modular integrada con **Cloth Config** y **ModMenu**. Las opciones están organizadas en 12 categorías:

1. 🛠️ **General (`general`)**:
   - Activar o desactivar el crecimiento del moho globalmente (`enable_mold_growth`).
   - Umbral de riesgo de infección (`infection_threshold`, defecto `0.40`).
   - Radio de escaneo del entorno (`scan_radius`, $3\times3\times3$ o $5\times5\times5$).
   - Inmunidad de estructuras generadas (`structures_immune`).
   - Daño de durabilidad al raspar con hacha (`axe_scrape_damage`).
   - Depuración de chat (`show_debug_in_chat`).

2. 🪓 **Susceptibilidad (`susceptibility`)**:
   - Multiplicador para madera sin corteza (`stripped_wood_multiplier`, `1.4x`).
   - Multiplicador para tablones procesados (`planks_multiplier`, `0.8x`).
   - Multiplicador base por defecto (`default_multiplier`, `1.0x`).

3. ☣️ **Catalizadores (`catalysts`)**:
   - Bonificaciones de contagio por proximidad a barro (`0.05`), podzol/micelio (`0.15`), hongos (`0.25`) y flor de esporas (`0.80`).
   - Bonificaciones de contagio por bloques de madera deteriorados (`0.05`), enmohecidos (`0.10`) y podridos (`0.20`).

4. 🌡️ **Entorno (`environment`)**:
   - Humedad base en lluvia (`0.8`) y en seco (`0.3`).
   - Modificador y escala de profundidad bajo el nivel del mar (`Y < 64`).
   - Bonificación y radio de escaneo de agua adyacente (`+0.15`) y calderos (`+0.10`).
   - Rango de supervivencia de temperatura mínima (`0.15`) y máxima (`1.5`).
   - Normalización de temperatura en cuevas profundas (`0.5`) y congelación en alta montaña (`-0.5`).

5. 💥 **Obtención (`drops`)**:
   - Probabilidad de drop base para madera en Etapa 2 / Enmohecido (`stage_2_drop_chance`, defecto `0.50`).
   - Probabilidad de drop base para madera en Etapa 3 / Podrido (`stage_3_drop_chance`, defecto `0.00`).

6. 🗺️ **Estructuras (`structures`)**:
   - Configuración porcentual detallada de probabilidad de bloques deteriorados, enmohecidos y podridos para las 4 categorías estructurales (Crítica, Alta, Moderada y Baja).

7. 🔥 **Horno y Combustión (`furnace_multipliers`)**:
   - Multiplicador de tiempo de quemado en horno para cada etapa: Etapa 0 (`1.0x`), Etapa 1 (`0.5x`), Etapa 2 (`0.25x`), Etapa 3 (`0.125x`).

8. 🚒 **Inflamabilidad (`flammability`)**:
   - Habilitar escalado de inflamabilidad (`enable_flammability`).
   - Bonificaciones escalares de inflamabilidad y propagación para Etapa 1 ($+5 / +10$), Etapa 2 ($+10 / +25$) y Etapa 3 ($+20 / +60$).
   - Bonificación de resistencia para madera encerada ($+5$).

9. 🛡️ **Resistencia a Explosiones (`blast_resistance`)**:
   - Habilitar escalado de resistencia (`enable_blast_resistance_scaling`).
   - Multiplicadores de absorción de detonaciones: Etapa 1 (`0.80x`), Etapa 2 (`0.50x`), Etapa 3 (`0.10x`).

10. ⛏️ **Dureza (`hardness`)**:
    - Habilitar escalado dinámico de dureza (`enable_hardness_scaling`).
    - Multiplicadores de dureza de bloque: Etapa 1 (`0.80x` $\rightarrow 1.6$), Etapa 2 (`0.50x` $\rightarrow 1.0$), Etapa 3 (`0.20x` $\rightarrow 0.4$).
    - Habilitar nube de esporas y efectos al romper sin Silk Touch (`enable_break_spore_cloud`).

11. ☠️ **Toxicidad (`toxicity`)**:
    - Intervalo de comprobación en ticks (`check_interval_ticks`, defecto `40`).
    - Radio de escaneo (`scan_radius`, defecto `4`).
    - Umbrales de activación para Náusea (`threshold_nausea`, `15`) y Veneno (`threshold_poison`, `35`).
    - Duración y amplificadores de los efectos de estado.

12. 🖥️ **Cliente (`client`)**:
    - Ajuste de desplazamiento de renderizado Z (`mold_z_offset`, defecto `0.002f`) para compatibilidad perfecta con sombreadores modernos (Sodium / Iris) y evitar z-fighting.

