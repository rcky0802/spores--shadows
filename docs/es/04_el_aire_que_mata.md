# ☠️ El Aire que Mata

La madera que se pudre no es un problema estático: libera activamente esporas en el aire circundante. En espacios cerrados y mal ventilados, esa contaminación se acumula hasta volverse letal.

---

## 🌫️ El Miasma Volumétrico

El juego evalúa constantemente el aire alrededor de la cabeza del jugador utilizando un algoritmo BFS (Breadth-First Search) tridimensional.

- **Volumen analizado**: hasta **2048 m³** de aire contiguo.
- **Radio máximo**: **16 bloques** de distancia euclidiana desde la cabeza del jugador.
- **Cuevas abiertas**: si el volumen supera los 2048 m³ sin encontrar paredes cerradas, el entorno se clasifica como *abierto* y el miasma se dispersa instantáneamente —como si hubiera una corriente continua de aire libre.

### Qué bloquea el Miasma y Puntuaciones de Ventilación (Base 6)

| Elemento | Comportamiento | Puntuación de Caudal ($V$) |
| :--- | :--- | :---: |
| **Bloques macizos, bloques de cristal** | Barrera hermética estanca | `0.0` |
| **Puertas cerradas, trampillas cerradas** | Barrera hermética | `0.0` |
| **Bloques anegados (waterlogged)** | **Sifón hidráulico** — barrera estanca perfecta | `0.0` |
| **Paneles de cristal ($\ge 2$ lados conectados)** | Ventana continua o en esquina: barrera hermética | `0.0` |
| **Paneles de cristal ($1$ lado conectado)** | Ventana parcial/abierta: ventilación intermedia | **`+12.0`** / bloque |
| **Paneles de cristal (0 conexiones)** | Panel aislado tipo poste: punto abierto | **`+24.0`** / bloque |
| **Paneles de cristal (en vertical)** | Eje UP / DOWN: el aire fluye libremente por encima/debajo | **`+24.0`** / bloque |
| **Muros ($\ge 2$ lados conectados)** | Muro continuo o en esquina: barrera hermética | `0.0` |
| **Muros ($1$ lado conectado)** | Muro saliente/parcial: ventilación intermedia | **`+12.0`** / bloque |
| **Muros (0 conexiones)** | Poste de muro aislado: pequeña abertura | **`+6.0`** / bloque |
| **Muros (en vertical)** | Eje UP / DOWN: el aire pasa libremente por encima/debajo | **`+18.0`** / bloque |
| **Vallas / Fences (en vertical)** | Eje UP / DOWN: el aire fluye libremente | **`+18.0`** / bloque |
| **Vallas / Fences ($\ge 2$ lados conectados)** | Huecos entre travesaños (derecha e izquierda) | **`+12.0`** / bloque |
| **Vallas / Fences ($1$ lado conectado o 0)** | Abertura amplia (un solo lado o poste aislado) | **`+18.0`** / bloque |
| **Cielo abierto directo** | Chimenea atmosférica natural | **`+24.0`** / bloque |
| **Puertas/Trampillas abiertas, Rejas de cobre, Hojas** | Vías de ventilación primarias | **`+18.0`** / bloque |
| **Losas (Slabs)** | Vías parciales semillenas | **`+12.0`** / bloque |
| **Escaleras (Stairs)** | Fisuras y aberturas menores | **`+6.0`** / bloque |

La saturación de la habitación no es instantánea: crece con inercia temporal (`saturation_speed = 0.15`) cuando se sellan las aberturas, y se disipa mucho más rápidamente (`dissipation_speed = 0.35`) con solo abrir una ventana o abertura.

### 🧮 Fórmula de Generación del Miasma

En cualquier entorno confinado, el miasma objetivo de la habitación ($M_{target}$) se calcula como el balance entre la producción biológica y la renovación del aire:

$$M_{target} = \max\Big(0.0, \ \text{Score}_{\text{tóxico}} - \text{Score}_{\text{ventilación}} - \text{Poder}_{\text{purificadores}}\Big)$$

- **$\text{Score}_{\text{tóxico}}$**: cada bloque de madera infectado no encerado expuesto al aire de la habitación emite esporas según su etapa:
  - 🟢 Etapa 1 (Infectado): **$+1.0$**
  - 🦠 Etapa 2 (Mohoso): **$+2.0$**
  - ☠️ Etapa 3 (Podrido): **$+4.0$**  
  *(Los bloques encerados no liberan esporas y su contribución es cero).*
- **$\text{Score}_{\text{ventilación}}$**: caudal de aire acumulativo garantizado por las aberturas hacia el exterior (resuelto mediante algoritmo *Max-Flow*).
- **$\text{Poder}_{\text{purificadores}}$**: cada Purificador de Aire activo en la habitación reduce **$-48.0$** puntos tóxicos.

El **Miasma Neto ($M_{net}$)** converge dinámicamente hacia $M_{target}$ en cada ciclo de actualización. La **Densidad de Esporas** determina la opacidad de la niebla y la carga pulmonar por $m^3$:

$$\text{Densidad} = \frac{M_{net}}{\text{Volumen de Aire } (m^3)}$$

### Efectos de Toxicidad en el Jugador (Umbrales Base 6)

| Miasma Neto | Densidad de Esporas | Estado y Síntomas en el Jugador |
| :---: | :---: | :--- |
| **$\ge 2.0$** | $\ge 0.0417$ ($1/24$) | **Aviso**: partículas de micelio visibles en el aire, ruidos orgánicos graves |
| **$\ge 6.0$** | $\ge 0.0833$ ($2/24$) | **Peligro**: efecto de estado **Hambre** (el cuerpo quema energías) |
| **$\ge 18.0$** | $\ge 0.1667$ ($4/24$) | **Letal**: contracción de **Náusea** seguida de **Veneno Letal** |

---

| | |
| :--- | ---: |
| [← Construir con lo Podrido](03_construir_con_lo_podrido.md) | [Defenderse →](05_defenderse.md) |
| [📑 Índice](README.md) | |
