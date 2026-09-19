# ⚙️ Automatizar la Limpieza

Los detectores manuales y el encerado preventivo cubren las situaciones cotidianas. Para estructuras grandes, entornos subterráneos o escenarios donde el jugador no puede estar siempre presente, el mod pone a disposición un sistema completo de automatización: sensores permanentes, maquinaria industrial y circuitos de Redstone.

---

## 📡 Sensores de Redstone Permanentes (Sistema Híbrido)

Ambos detectores pueden montarse en cualquier orientación sobre bloques sólidos (**suelo, pared o techo**). Además de la lectura visual y diagnóstica, integran un **sistema de Redstone híbrido** (Emisión Directa + Salida de Comparador):

**💧 Detector de Humedad (colocado)** — Monitoriza la humedad efectiva $H_{eff}$ y el microclima local:
- **Dial Visual**: 4 etapas graduadas (0 = Seco, 1 = Húmedo leve, 2 = Húmedo, 3 = Crítico).
- **Señal Directa**: Emite una potencia de Redstone proporcional a la etapa (**0, 5, 10, 15**), alimentando directamente el polvo adyacente, lámparas de señalización, maquinaria o el bloque soporte en la pared (permitiendo ocultar el cableado tras el muro).
- **Soporte de Comparador**: Cualquier comparador adyacente lee de forma nativa la misma señal (0, 5, 10, 15) para lógicas analógicas o umbrales de precisión.
- **Interrogación**: Al hacer clic derecho sobre el bloque (con la mano o con una herramienta), el dispositivo chasquea con un sonido mecánico y envía al chat el informe diagnóstico completo.

**☢️ Detector de Esporas (colocado)** — Analiza periódicamente el volumen BFS del aire de la habitación:
- **Dial Visual**: 4 etapas de alarma tóxica (0 = Limpio, 1 = Atención, 2 = Hambre, 3 = Veneno Letal).
- **Señal Directa**: Emite potencia de Redstone escalar (**0, 5, 10, 15**), permitiendo activar automáticamente Deshumidificadores y Purificadores de Aire en cuanto el miasma empieza a acumularse, sin necesidad de repetidores ni circuitos intermedios.
- **Soporte de Comparador**: Totalmente compatible con comparadores para crear alarmas por umbral y circuitos de emergencia avanzados.
- **Interrogación**: Al hacer clic derecho, chasquea con un sonido metálico y transmite al chat privado la telemetría volumétrica y la tendencia del miasma (en acumulación, estable o en purificación).

## 💣 Mecánica Redstone: Cofres Trampa y Fallos

El moho también compromete los mecanismos de emisión de Redstone integrados en los bloques de madera.

**Botones de Madera** — El hongo atrapa el resorte del perno, multiplicando el tiempo que el botón permanece presionado:
- Etapa 0 (Sano / Encerado): **1.5 segundos** (30 ticks — estándar Vanilla)
- Etapa 1 (Infectado): **3.0 segundos** (60 ticks)
- Etapa 2 (Mohoso): **7.5 segundos** (150 ticks)
- Etapa 3 (Podrido): **22.5 segundos** (450 ticks) — inutiliza cualquier sincronización de circuito

**Placas de Presión de Madera** — La biomasa fúngica ralentiza el retroceso de la placa tras bajarse la entidad:
- Etapa 0 (Sano / Encerado): **1.0 segundo** (20 ticks — estándar Vanilla)
- Etapa 1 (Infectado): **2.0 segundos** (40 ticks)
- Etapa 2 (Mohoso): **5.0 segundos** (100 ticks)
- Etapa 3 (Podrido): **15.0 segundos** (300 ticks) — la señal persiste durante 15 segundos tras pisarla

**Cofres Trampa — Atasco (*Jamming*)** — El moho oxida las láminas de la bisagra interna. En cada apertura, hay una probabilidad creciente de *fallo por atasco* (no emite señal y produce un clic en falso):
- Etapa 1 (Infectado): **15%** de probabilidad de fallo
- Etapa 2 (Mohoso): **50%** de probabilidad de fallo
- Etapa 3 (Podrido): **85%** de probabilidad de fallo

El estado del mecanismo está intencionalmente oculto en los HUD externos (Jade/WTHIT) para mantener el factor sorpresa.

**Librerías Cinceladas y Comparadores** — Los libros guardados en su interior sobreviven intactos al deterioro en cualquier etapa. La señal analógica emitida por el comparador posterior (de 1 a 6 según la última ranura con la que se interactuó) permanece plenamente determinista y fiel al comportamiento Vanilla, sin pérdida de señal alguna.

---

## 🌀 Deshumidificador

La maquinaria clave para el saneamiento atmosférico activo de la humedad. Mientras que el Purificador actúa río abajo neutralizando el Miasma ya formado, el Deshumidificador actúa río arriba previniendo la aparición y proliferación de cualquier moho al secar el aire de la habitación.

- **Poder de Secado (1.0)** — Cada Deshumidificador activo aplica un poder de secado de **1.0 punto** al algoritmo higrométrico de la sala:
  $$H_{\text{target}} = \max(0.0, \, H_{\text{raw}} - 1.0 \times N_{\text{deshumidificadores}})$$
  Dado que la humedad natural bruta $H_{\text{raw}}$ oscila entre $0.0$ y $1.0$, un único deshumidificador activo basta para reducir la humedad efectiva $H_{\text{eff}}$ a **$0.0$ ($0\%$)** en cualquier habitación sellada de hasta $2048\text{ m}^3$, convirtiendo el espacio en un oasis desértico inmune al deterioro biológico.
- **Alimentación Híbrida (32.000 FE / Combustible 4.0×)** — Consume **10 FE/tick** durante el funcionamiento activo ($0\text{ FE/tick}$ en espera/apagado). Admite recarga mediante cables de energía (hasta 500 FE/t en cualquier cara) o combustible sólido en la Ranura 0 con rendimiento cuadruplicado ($4.0\times$, un trozo de carbón proporciona $64.000\text{ FE}$, llenando el búfer entero).
- **Tanque de Condensación y Modos Operativos (2.000 mB)** — Provisto de depósito interno de fluidos:
  - **Modo Deshumidificación**: absorbe activamente la humedad y acumula condensación en el tanque a un ritmo base de **1 mB cada 24 ticks** (la condensación se acelera en habitaciones muy húmedas).
  - **En Espera con Tanque Lleno (FULL)**: al alcanzar el tope de 2.000 mB (2 cubos de agua), la máquina entra en modo de reposo para no malgastar energía ni combustible.
  - **Modo Vaporización / Humidificación (Humidify)**: polaridad invertida; consume el agua del depósito (1 mB cada 24 ticks) para esparcir vapor en el aire ($+1.0$ de humedad). Se detiene de forma automática si la humedad ambiental llega al 98% ($0.98$). Ideal para cultivos fúngicos controlados.
- **Automatización con Tolvas y Fluidos (SidedInventory & Fluid Transfer)**:
  - **Combustible Sólido**: la tolva puede insertar carburante desde **cualquier cara** del bloque en la Ranura 0.
  - **Extracción/Entrada de Agua**: admite extracción y llenado manual con cubo (1.000 mB por clic derecho) y conexión a tuberías automáticas de fluidos mediante *Fabric Transfer API*.
- **Interfaz, Redstone y Comparador**:
  - **Control de Redstone**: selector de tres estados (*Siempre Activo* `IGNORED`, *Activo con Redstone* `HIGH`, *Apagado con Redstone* `LOW`). Conectado a un Detector de Humedad en pared, se enciende únicamente cuando el aire supera el umbral crítico.
  - **Salida de Comparador**: emite una señal analógica de **$0$ a $15$** proporcional al nivel de llenado del depósito de agua interno (0 vacío, 15 lleno a 2.000 mB).

---

## 🌬️ Purificador de Aire

La maquinaria fundamental para la neutralización activa de la toxicidad atmosférica. Mientras que el Deshumidificador previene la infección secando el aire, el Purificador interviene neutralizando directamente las esporas volátiles del Miasma en habitaciones herméticas ya contaminadas.

- **Poder de Purificación (48.0)** — Cada Purificador activo resta **48.0 puntos** a la carga tóxica volumétrica de la habitación:
  $$\text{targetMiasma} = \max(0.0, \, \text{toxicScore} - \text{ventilationScore} - 48.0 \times N_{\text{purificadores}})$$
  Dado que un único tronco mohoso genera aproximadamente $2.25$ puntos de toxicidad, un solo purificador neutraliza las emanaciones de **más de 21 bloques infectados al mismo tiempo**, disipando por completo el Miasma en habitaciones cerradas de hasta $2048\text{ m}^3$.
- **Alimentación Híbrida (32.000 FE / Combustible 4.0×)** — Consume **10 FE/tick** durante la filtración activa ($0\text{ FE/tick}$ en espera/agotado). Admite recarga mediante cables de energía (hasta 500 FE/t en cualquier cara) o combustible sólido en la Ranura 0 con rendimiento cuadruplicado ($4.0\times$, un trozo de carbón proporciona $64.000\text{ FE}$, llenando el búfer entero).
- **Cartuchos de Filtro de Esporas y Desgaste Dinámico (Spore Filters)** — Alojados en la Ranura 1 (apilable hasta 64 unidades):
  - **Durabilidad Base**: **2.400 ticks (2 minutos continuos)** por filtro a razón de 1 punto/tick.
  - **Recarga Automática**: al agotarse el cartucho activo, la máquina toma de inmediato el siguiente filtro de la pila de reserva.
  - **Desgaste Acelerado bajo Miasma Letal**: si la habitación alcanza la concentración crítica *LETHAL_POISON*, el desgaste se duplica a **2 puntos/tick** (duración de 60 segundos) debido a la saturación extrema de microesporas.
  - **Alarma de Agotamiento (FILTER_DEPLETED)**: si se terminan los filtros, la máquina apaga la rejilla y emite un clic metálico en vacío (`BLOCK_DISPENSER_FAIL`).
- **Automatización con Tolvas (SidedInventory)**:
  - **Cara Superior (UP)**: la tolva inserta *exclusivamente* los **Filtros de Esporas** (Ranura 1).
  - **Caras Laterales e Inferior (NORTH, SOUTH, EAST, WEST, DOWN)**: aceptan *exclusivamente* el **Combustible sólido** (Ranura 0).
- **Interfaz, Redstone y Comparador**:
  - **Control de Redstone**: selector de tres estados (*Siempre Activo* `IGNORED`, *Activo con Redstone* `HIGH`, *Apagado con Redstone* `LOW`). Conectado a un Detector de Esporas en pared, se activa de forma automática solo ante la acumulación de miasma, evitando el desperdicio de filtros con la habitación purificada.
  - **Salida de Comparador**: emite una señal analógica de **$0$ a $15$** proporcional a la reserva de filtros restantes en la ranura (0 si está vacío, 15 con una pila completa de 64), idónea para activar luces de advertencia de reabastecimiento.

---

| | |
| :--- | ---: |
| [← Defenderse](05_defenderse.md) | [La Decadencia al Detalle →](07_la_decadencia_al_detalle.md) |
| [📑 Índice](README.md) | |
