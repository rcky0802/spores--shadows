# 🧱 Construir con lo Podrido

Una vez que la madera comienza a pudrirse, su valor como material se desploma rápidamente. El mod impone consecuencias concretas en el crafteo, la resistencia física y el uso en el horno —todo con una lógica interna coherente: cuanto más comprometida esté la madera, menos provecho podrás sacarle.

---

## 🧱 Degeneración Mecánica, Térmica y Fragilidad

A medida que las hifas fúngicas consumen la celulosa y la lignina, la madera pierde cohesión estructural y retiene polvos finos secos. Todas sus propiedades físicas y térmicas decaen en paralelo:

| Propiedad | 🌲 Etapa 0 (Sano) | 🟢 Etapa 1 (Infectado) | 🦠 Etapa 2 (Mohoso) | ☠️ Etapa 3 (Podrido) |
| :--- | :---: | :---: | :---: | :---: |
| **Dureza del Bloque** | `2.0` (100%) | `1.6` (80%) | `1.0` (50%) | `0.4` (20%) |
| **Resistencia a Explosiones (TNT)** | 100% | 80% | 50% | **10%** |
| **Eficacia de Herramientas** | Normal (Hacha) | Normal (Hacha) | Normal (Hacha) | **Anulada (Puño = Hacha)** |
| **Drop en Supervivencia** | `100%` | `100%` | `50%` (Mitad perdida) | **`0%` (Desmoronamiento)** |
| **Drop con Toque de Seda / Cera** | `100%` | `100%` | `100%` | `100%` |
| **Bonus Inflamabilidad (Ignición)** | $+0$ (Vanilla) | $+5$ | $+10$ | $+20$ |
| **Bonus Propagación del Fuego** | $+0$ (Vanilla) | $+10$ | $+25$ | $+60$ |
| **Poder Combustible (Horno)** | `100%` (1.0×) | `50%` (0.5×) | `25%` (0.25×) | `12.5%` (0.125×) |
| **Probabilidad de Compostaje** | — (No compostable) | `50%` | `65%` | **`85%`** (Excelente fertilizante) |

> [!WARNING]
> **Etapa 3 — Extrema Fragilidad**: Romper un bloque podrido anula por completo la ventaja del hacha: tanto con un hacha de netherita como con los puños desnudos tardarás el mismo tiempo, y el bloque se desmoronará sin dejar ningún drop (a menos que uses *Toque de Seda* o lo hayas encerado previamente).  
> **Cocción de Carbón Vegetal**: Los troncos infectados (Etapas 1, 2, 3 —tanto normales como encerados) **no se pueden cocinar para producir carbón vegetal**: la materia fúngica dañada impide la carbonización. Solo los troncos sanos de Etapa 0 del Overworld (vanilla o encerados sanos) se pueden cocinar en el horno para obtener carbón vegetal. Los troncos infectados solo se pueden usar como combustible (con duración reducida).

---

## 📐 Reglas de Crafteo

**Solo los tablones sanos o encerados se pueden usar para fabricar objetos complejos** (puertas, cofres, escaleras, mesas de trabajo, etc.). Los tablones infectados no son aceptados en las recetas de objetos elaborados.

La conversión de tronco infectado a tablones sanos sigue la **reducción progresiva a la mitad**:

| Tronco | Tablones Sanos Obtenibles |
| :--- | :---: |
| Sano / Encerado | 4 |
| Infectado | 2 |
| Mohoso | 1 |
| Podrido | 0 — irrecuperable |

**Limpieza de tablones en cuadrícula**: si has desmontado una estructura antigua y recuperado tablones infectados, puedes purificarlos en la mesa de trabajo:
- 2 Tablones Infectados → 1 Tablón Sano
- 4 Tablones Mohosos → 1 Tablón Sano

*Las variantes normales y enceradas de la misma etapa se pueden mezclar libremente en la cuadrícula.*

---

| | |
| :--- | ---: |
| [← Por Qué se Pudre la Madera](02_por_que_se_pudre_la_madera.md) | [El Aire que Mata →](04_el_aire_que_mata.md) |
| [📑 Índice](README.md) | |
