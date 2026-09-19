# 💻 Configuración y Técnica

Una referencia para creadores de modpacks, administradores de servidores y jugadores avanzados que deseen comprender o modificar el comportamiento del mod.

---

## 📖 Integración JEI (Just Enough Items)

El mod integra 7 categorías nativas en JEI para documentar las mecánicas directamente dentro del juego, sin necesidad de consultar wikis externas:

1. **Encerado** — todas las transformaciones de bloque → bloque encerado mediante panal
2. **Raspado** — retirada de cera y cura del moho ($2 \rightarrow 1 \rightarrow 0$) con el hacha
3. **Recuperación de Tablones** — cuadrículas de purificación y compresión de tablones infectados
4. **Ciclo de Infección** — visualización de la progresión natural ($0 \rightarrow 1 \rightarrow 2 \rightarrow 3$)
5. **Fichas Informativas** — fragilidad, pérdida de drops y reglas especiales de la Etapa 3
6. **Deshumidificador** — consumos de energía, radio de acción y modos de operación
7. **Purificador de Aire** — recetas de filtros y capacidad de purificación volumétrica

## 🔍 Integración Jade / WTHIT

La ventana emergente contextual muestra en cada bloque enfocado:

- Etapa de moho y estado de encerado
- Riesgo de Infección local ($R\%$) con código de color dinámico (gris = estable, rojo = en riesgo)
- Nivel de llenado de los compostadores
- Estado de energía y modo activo de la maquinaria

## 🏆 Progresos (Advancements)

El mod incluye un árbol completo de **11 progresos** organizados entre supervivencia, monitoreo tecnológico y grandes obras de saneamiento:

### 🌿 Supervivencia y Cura del Moho
- **Spores & Shadows** *(Raíz)* — Sobrevive a la decadencia de la naturaleza en tu mundo.
- **Prevención Natural** (*Natural Prevention*) — Usa un panal de miel para encerar un bloque de madera y detener el moho para siempre.
- **Grasa de Codo** (*Elbow Grease*) — Raspa un nivel de moho de un bloque de madera con un hacha.
- **Respiración Corta** (*Short Breath*) — Sufre el veneno del miasma al respirar demasiado moho.
- **Polvo al Polvo** (*Dust to Dust*) — Intenta romper un bloque de madera podrido (Etapa 3) no encerado y mira cómo se desmorona en la nada sin dejar drop.

### 🧭 Instrumentación y Monitoreo
- **Detección de Humedad** (*Moisture Detector*) — Fabrica un Detector de Humedad para controlar la humedad de la habitación.
- **Centinela del Aire** (*Air Sentry*) — Fabrica un Detector de Esporas para monitorear la calidad del aire y el miasma tóxico.

### ⚙️ Ingeniería y Grandes Desafíos de Limpieza
- **Control del Clima** (*Climate Control*) — Fabrica un Deshumidificador para secar habitaciones cerradas y recolectar agua condensada.
- **Búnker Hermético** (*Hermetic Bunker*) — Fabrica un Purificador de Aire para purgar el miasma y hacer respirables las habitaciones cerradas.
- 🏆 **Oasis Subterráneo** (*Dry Oasis*, Desafío) — Seca una habitación subterránea profunda ($Y \le 40$) por debajo del 15% de humedad usando un Deshumidificador.
- 🏆 **Aire Puro en las Profundidades** (*Pure Air in the Depths*, Desafío) — Descontamina por completo una habitación subterránea en las profundidades ($Y \le 0$) infestada de moho, restableciendo el aire a nivel `CLEAN`.

---

## ⚙️ Configuración (ModMenu & Cloth Config)

Spores & Shadows cuenta con 19 categorías de configuración modificables en caliente desde el menú de ModMenu (almacenadas en `config/spores_and_shadows.json`):

| Categoría | Qué controla |
| :--- | :--- |
| **General** | Interruptor global de deterioro, radio de escaneo, umbral de infección (por defecto 0.50), desgaste de hachas, rotura de bloques por interacción (10%) |
| **Susceptibility** | Multiplicadores de vulnerabilidad por formato ($S_{mat}$): sin corteza (1.4×), tablones/escaleras/losas/mosaicos (0.8×), por defecto (1.0×) |
| **Catalysts** | Pesos de catalizadores ($C_{bonus}$): lodo (+0.05), podzol/micelio (+0.15), hongos (+0.25), Spore Blossom (+0.80), bloques infectados (+0.03 / +0.06 / +0.12) |
| **Environment** | Humedad base lluvia/seco, gradiente de profundidad, aporte de agua (+0.15 hasta +0.60), ventilación y velocidad de saturación/disipación de humedad |
| **Drops** | Probabilidad de drop sin Toque de Seda: Etapa 2 (50%) y Etapa 3 (0%) |
| **Structures** | Pre-deterioro de estructuras vanilla generadas en el mundo y bonus ambientales (bajo el agua, profundidad, contacto con el suelo) |
| **Furnace Multipliers** | Rendimiento calorífico del combustible según la etapa (1.0×, 0.5×, 0.25×, 0.125×) |
| **Flammability** | Bonus de ignición (+5, +10, +20) y propagación de fuego (+10, +25, +60) para las etapas 1, 2 y 3 |
| **Blast Resistance** | Multiplicadores de resistencia a explosiones (TNT) por etapa (80%, 50%, 10%) |
| **Hardness** | Escala de dureza de los bloques (80%, 50%, 20%) |
| **Redstone** | Duración prolongada de botones/placas y porcentaje de fallos por atasco en cofres trampa (15%, 50%, 85%) |
| **Composter** | Probabilidad de éxito en el compostador por etapa (50%, 65%, 85%) |
| **Particles** | Conteo y tipos de partículas emitidas al romper bloques infectados |
| **Spore Detector** | Intervalo en ticks (inicial/periódico), multiplicador de señal Redstone (5× por etapa por defecto), tiempo de reutilización, estadísticas de Máscara |
| **Moisture Detector** | Intervalo en ticks (inicial/periódico), multiplicador de señal Redstone (5× por etapa por defecto), tiempo de reutilización de uso manual |
| **Dehumidifier** | Capacidad del depósito (2000 mB), ticks por mB (24), eficiencia de combustible (4.0×), poder deshumidificador (1.0), capacidad y consumo de FE |
| **Air Purifier** | Poder de purificación tóxica (48.0), duración de filtros Spore Filter (2400 ticks / 2 min), eficiencia de combustible (4.0×), capacidad y consumo de FE |
| **Toxicity** | Volumen BFS (2048 m³), radio (16 bloques), ventilación de nodos (Base 6/24), 3 umbrales de toxicidad (6, 12, 18), Máscara y Spore Filtration |
| **Client** | Desplazamiento Z anti-z fighting en renderizado de bloques (0.002) e intensidad de superposición gráfica de moho en GUI (1.0) |

## 💻 Comandos Administrativos

Requieren nivel 2 de operador:

- `/miasma` — Escaneo BFS en tiempo real: volumen de la sala, puntuación tóxica, ventilación activa, clasificación ambiental (Abierto / Confinado).
- `/moldrisk` — Desglose completo del Riesgo $R$ para el bloque enfocado: $H_{eff}$, $L_{uv}$, $S_{mat}$, catalizadores detectados, $M_{bonus}$, $T_{mult}$ y valor de $R$ final.

---

| | |
| :--- | ---: |
| [← La Decadencia al Detalle](07_la_decadencia_al_detalle.md) | [📑 Índice](README.md) |
