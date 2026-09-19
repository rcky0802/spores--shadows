# 🤿 Defenderse

Conocer los riesgos no basta: se necesitan las herramientas adecuadas. El mod introduce un conjunto coherente de mecánicas defensivas, desde la prevención pasiva hasta el equipamiento de protección activa.

---

## 🐝 Encerado Preventivo

Aplicar un **Panal** sobre cualquier bloque de madera lo sella con una capa de cera, congelando permanentemente su etapa actual.

Un bloque encerado:
- No se deteriora más, independientemente de las condiciones ambientales
- No emite esporas al aire circundante
- No puede contagiar a los bloques adyacentes
- Siempre suelta el **100%** de drop al romperse, incluso en la Etapa 3

La cera no cura el bloque —lo preserva en la etapa en que se encuentra. Un bloque Podrido encerado permanece Podrido, pero deja de ser una amenaza biológica activa.

## 🪓 Cura con el Hacha

Con **Agacharse (Sneak / Shift) + Clic Derecho** sosteniendo un hacha, es posible intervenir directamente sobre el bloque:

- **Desencerado**: retira el recubrimiento de cera (`ITEM_AXE_WAX_OFF`), reactivando el ciclo biológico a costa de 1 punto de durabilidad.
- **Cura del Moho**: en madera no encerada de Etapa 1 o 2, el hacha raspa las hifas superficiales (`ITEM_AXE_SCRAPE`), haciendo retroceder la infección una etapa ($2 \rightarrow 1 \rightarrow 0$) a costa de 1 punto de durabilidad.
- **Etapa 3 (Podrido) — Totalmente Incurable**: la estructura interna está irremediablemente comprometida. El hacha no surte ningún efecto en la madera podrida. La única manera de volverla inerte sin destruirla es sellarla con un panal (encerado).

---

## 😷 La Máscara Antiesporas

La `Spore Mask` (Máscara Antiesporas) es el único equipamiento que garantiza supervivencia pasiva en el miasma prolongado. Se equipa en la ranura del casco y presenta un modelo 3D detallado con visera, respiradores y cartucho de filtración.

**Protección y Combate**: anula por completo los efectos letales del miasma (Hambre, Náusea, Veneno). Funciona también como armadura ligera (proporciona **1 punto de armadura**, equivalente a un casco de cuero, con **165 puntos de durabilidad**): se desgasta normalmente al recibir impactos en **combate**, y consume 1 punto de durabilidad en cada ciclo que filtra aire tóxico en lugar de los pulmones del jugador.

**Reparación**: exclusivamente con los **Filtros de Esporas (Spore Filters)** en un yunque —fabricados con lana, carbón vegetal e hilo. Cada filtro restaura el 100% de durabilidad. Alternativamente, se pueden combinar dos máscaras desgastadas en la cuadrícula de crafteo para una reparación rápida de emergencia en el campo.

### 🔮 Encantabilidad de la Máscara Antiesporas
La Máscara Antiesporas tiene **Encantabilidad = 0** (no se puede encantar en la Mesa de Encantamientos). Solo puede recibir encantamientos **a través de libros encantados en un Yunque**, con estrictas restricciones de compatibilidad:

| Encantamiento | Compatibilidad de la Máscara | Efecto en la Máscara |
| :--- | :---: | :--- |
| **Irrompibilidad (Unbreaking I–III)** | ✅ **Permitido** | Reduce la probabilidad de desgaste tanto por golpes recibidos como por aire filtrado. |
| **Reparación (Mending)** | ✅ **Permitido** | Repara la durabilidad de la máscara al recoger orbes de experiencia. |
| **Maldición de Desaparición (Vanishing)** | ✅ **Permitido** | La máscara desaparece al morir el jugador en lugar de caer al suelo. |
| **Filtración de Esporas (Spore Filtration)** | ❌ **Incompatible** | **No aplicable**: la máscara ya filtra de forma nativa el miasma; el encantamiento es redundante. |
| **Protección / Respiración / Afinidad acuática / Espinas** | ❌ **Incompatible** | Rechazados: la máscara es un respirador técnico, no un casco de combate encantado. |

---

### ✨ Encantamiento para Cascos: Filtración de Esporas (`Spore Filtration`)

`Spore Filtration` es un encantamiento diseñado específicamente para **cualquier casco convencional** (de cuero, hierro, diamante, netherita, caparazón de tortuga). Permite a quien viste una armadura estándar respirar con seguridad dentro del miasma sin necesidad de equipar la Máscara Antiesporas, transfiriendo la carga tóxica a la durabilidad del casco:

| Nivel | Consumo de Durabilidad por Ciclo | Eficiencia de Ahorro | Comportamiento |
| :---: | :---: | :---: | :--- |
| **I** | **2 puntos** / ciclo | Estándar | Filtración tosca: neutraliza el miasma pero desgasta rápidamente el casco. |
| **II** | **1 punto** / ciclo | Optimizada | Filtración equilibrada: equipara la eficiencia a la de la Máscara Antiesporas básica. |
| **III** | **0 o 1 punto** (media 0.5) | **50% Protección** | Filtración avanzada: **50% de probabilidad de no consumir durabilidad** en cada ciclo de exposición. |

> [!TIP]
> ¡Aplicado a un casco de alta resistencia (como un casco de netherita combinado con *Irrompibilidad III* y *Reparación*), `Spore Filtration III` permite explorar y combatir con seguridad dentro de entornos con miasma letal manteniendo la máxima protección de una armadura pesada!

---

## 🧭 Detectores Portátiles

Para diagnosticar entornos desconocidos o planificar intervenciones:

**💧 Detector de Humedad** — Sostenido en la mano y usado en el aire (clic derecho), emite un chasquido mecánico y envía al chat privado un informe analítico detallado del Riesgo de Infección local ($H_{eff}$, luz, temperatura, catalizadores adyacentes). Ideal para comprender al instante por qué una habitación no deja de pudrirse.

**☢️ Detector de Esporas** — Sostenido en la mano y usado en el aire (clic derecho), emite un chasquido mecánico y ejecuta un escaneo instantáneo del aire alrededor de los ojos del jugador (volumen de la sala, ventilación activa, densidad de esporas, tendencia dinámica). Es completamente silencioso durante el movimiento (sin tictac pasivo continuo), garantizando la máxima discreción durante la exploración.

*El modo estacionario de estos detectores —como sensores de Redstone híbridos en pared/suelo/techo— se detalla en el [Capítulo 6](06_automatizar_la_limpieza.md).*

---

| | |
| :--- | ---: |
| [← El Aire que Mata](04_el_aire_que_mata.md) | [Automatizar la Limpieza →](06_automatizar_la_limpieza.md) |
| [📑 Índice](README.md) | |
