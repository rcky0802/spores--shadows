# 🔬 Por Qué se Pudre la Madera

Cada bloque de madera en el mundo se evalúa de manera autónoma en cada tick. El resultado de esa evaluación es un número —el **Riesgo de Infección ($R$)**— que determina si el deterioro avanza o se detiene.

$$R = \Big( (H_{eff} \cdot L_{uv} \cdot S_{mat}) + C_{bonus} + M_{bonus} \Big) \cdot T_{mult}$$

Si $R > 0.50$, la etapa avanza. De lo contrario, el bloque permanece estable. Cada factor representa una condición ambiental real.

---

## 💧 Humedad Efectiva ($H_{eff}$)

Es el motor principal del deterioro. Para cualquier bloque completamente anegado (*waterlogged*), $H_{eff} = 1.0$ (máximo absoluto). En los demás casos, se calcula combinando la humedad atmosférica local, los catalizadores y el efecto secante del viento:

$$H_{eff} = \text{clamp}\Big( H_{current} + C_{humidity} - A_{drying}, \ 0.0, \ 1.0 \Big)$$

Donde la humedad atmosférica ($H_{current}$) converge dinámicamente hacia el objetivo ambiental de la habitación:

$$H_{target} = \text{clamp}\Big( H_{base} + D_{depth} + W_{water} + H_{humidifier} - D_{dehumidifier}, \ 0.0, \ 1.0 \Big)$$

- **Humedad Base del Bioma ($H_{base}$)**: biomas lluviosos o nevados parten de `0.80`; climas áridos o desérticos de `0.30`.
- **Modificador de Profundidad ($D_{depth}$)**: al descender por debajo de $Y = 64$, sube gradualmente hasta alcanzar el límite máximo de $+0.40$ en la cota $Y \le 48$, manteniéndose constante en toda la pizarra profunda (Deepslate) hasta $Y = -64$.
- **Fuentes de Agua en la Habitación ($W_{water}$)**: cada bloque de agua presente en la habitación añade $+0.15$ (hasta un máximo de $+0.60$).
- **Maquinaria ($H_{humidifier} / D_{dehumidifier}$)**: Nebulizadores ($+1.0$ c/u) o Deshumidificadores ($-1.0$ c/u).
- **Catalizadores Locales ($C_{humidity}$)**: bloques adyacentes ricos en agua (ej. lodo o calderos de agua) añaden $+0.10$.
- **Secado por Ventilación ($A_{drying}$)**: la ventilación local seca la superficie del bloque: $A_{drying} = \text{Ventilación} \cdot 0.50$.

## ☀️ Luz UV ($L_{uv}$)

La luz actúa como esterilizador. El nivel de iluminación se muestrea alrededor del bloque:
- **6 puntos** (las caras adyacentes) para bloques macizos y opacos (troncos, tablones).
- **7 puntos** (las 6 caras + el espacio interno del bloque) para manufacturas no macizas o transparentes (escaleras, losas, vallas, puertas, carteles).

La media de la luz detectada se escala entre `0.0` (luz máxima 15 — esterilización e infección bloqueada) y `1.0` (oscuridad total 0 — riesgo completo). Un bloque bien iluminado en una habitación abierta tiene un riesgo prácticamente nulo; ese mismo bloque en una mina oscura es sumamente vulnerable.

## 🪓 Susceptibilidad del Material ($S_{mat}$)

No todos los bloques tienen la misma vulnerabilidad biológica:

| Categoría | Multiplicador ($S_{mat}$) | Detalle de Bloques |
| :--- | :---: | :--- |
| **Madera sin corteza** | **1.4×** | Troncos, madera y tallos sin corteza (`stripped_*`). Al carecer de corteza protectora, las fibras vivas son las más vulnerables de todas. |
| **Por defecto / Troncos y Manufacturas** | **1.0×** | Troncos con corteza protectora, muebles, puertas, trampillas, vallas, carteles, cofres y mesas de trabajo. |
| **Madera Procesada de Construcción** | **0.8×** | Tablones (`*_planks`), Escaleras (`*_stairs`), Losas (`*_slab`) y Mosaico de bambú. Madera curada y escuadrada para construcción con resistencia estructural superior. |

## 🌡️ Temperatura y Ventana Biológica ($T_{mult}$)

Las esporas prosperan únicamente en la "Ventana Biológica" ($0.15 \le \text{Temp} \le 1.50$). Fuera de este intervalo, $T_{mult} = 0.0$ y la proliferación fúngica se detiene por completo:

- **Montañas y Gran Altitud**:
  - El enfriamiento progresivo comienza al subir por encima de **$Y = 128$**.
  - A medida que se asciende hacia **$Y = 256$**, la temperatura desciende gradualmente hasta alcanzar $-0.50$ (valor que permanece fijo hasta el límite del mundo en **$Y = 320$**).
  - El crecimiento de los mohos se **bloquea por completo ya cuando la temperatura cae por debajo de $0.15$** (típicamente entre $Y \approx 180$ e $Y \approx 220$ según el bioma), haciendo que las cabañas de montaña estén protegidas naturalmente por el frío y las heladas.
- **Subterráneo y Cuevas**:
  - Al descender bajo el nivel del mar (**$Y = 64$**), la temperatura se normaliza hacia el microclima húmedo de las cavernas.
  - A partir de **$Y \le 48$** y por toda la pizarra profunda hasta **$Y = -64$**, la temperatura se estabiliza constantemente en el valor ideal de **`0.50`**, garantizando que las minas abandonadas siempre se pudran, independientemente del clima en la superficie (incluso si arriba hay un desierto o una tundra).

## ☣️ Catalizadores Físicos ($C_{bonus}$) y Presión de Miasma ($M_{bonus}$)

Los bloques biológicos situados en el radio de escaneo circundante (cubo 3×3×3 alrededor del bloque) aceleran la infección añadiendo un bonus directo al riesgo $R$, o bien incrementando la humedad local:

| Catalizador | Bonus de Riesgo ($C_{bonus}$) | Bonus Humedad Local | Detalles y Comportamiento |
| :--- | :---: | :---: | :--- |
| **Flor de Esporas** (`Spore Blossom`) | **+0.80** (+80%) | — | **Extremadamente letal**: por sí sola supera el umbral de infección (0.50). ¡Muy desaconsejada como decoración cerca de vigas de madera! |
| **Hongos** (rojos, marrones, bloques gigantes) | **+0.25** (+25%) | — | Hongos en el suelo o bloques de hongo gigante liberan esporas continuas por contacto. |
| **Podzol y Micelio** | **+0.15** (+15%) | — | Terrenos orgánicos ricos en hifas fúngicas subterráneas. |
| **Lodo** (`Mud`) | **+0.05** (+5%) | **+0.10** | Retiene fuerte humedad y acelera la podredumbre en la base de los edificios. |
| **Caldero de Agua** | — | **+0.10** | Añade humedad local estancada en un radio de 3 bloques. |
| **Bloque Infectado no encerado** (Etapa 1) | **+0.03** c/u | — | Cada bloque enfermo cercano propaga pasivamente el contagio a los sanos adyacentes. |
| **Bloque Mohoso no encerado** (Etapa 2) | **+0.06** c/u | — | Presión de contagio duplicada respecto a la etapa 1. |
| **Bloque Podrido no encerado** (Etapa 3) | **+0.12** c/u | — | Elevada carga biológica infecciosa para todos los bloques colindantes. |

> [!NOTE]
> Los bloques de madera **encerados** (`waxed`) **no** actúan como catalizadores: la capa de cera de abeja sella por completo las esporas y anula su contribución infecciosa hacia los vecinos.

### 🌫️ Presión del Miasma Aéreo ($M_{bonus}$)
Además del contacto físico sólido, la madera expuesta al aire estancado de una habitación saturada de miasma sufre una contaminación aérea constante:
$$M_{bonus} = \text{ExposureIndex} \cdot 0.50$$
Un entorno asfixiante y tóxico (detallado en el [Capítulo 4](04_el_aire_que_mata.md)) empuja incluso a la madera que de otro modo estaría seca a sucumbir rápidamente por infección aérea.

---

| | |
| :--- | ---: |
| [← El Mundo que se Pudre](01_el_mundo_que_se_pudre.md) | [Construir con lo Podrido →](03_construir_con_lo_podrido.md) |
| [📑 Índice](README.md) | |
