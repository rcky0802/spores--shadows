# 🌳 El Mundo que se Pudre

Cuando instalas Spores & Shadows, el mundo de Minecraft no cambia de aspecto de la noche a la mañana. Cambia con el tiempo —y a menudo te das cuenta demasiado tarde.

El mod reemplaza de forma transparente cada bloque de madera con una variante dinámica. El efecto es invisible al principio: el bloque tiene el mismo aspecto, las mismas propiedades. Pero las condiciones ambientales actúan sobre él a cada tick, y tarde o temprano el moho se impone.

---

## 🪵 Qué se Deteriora

Todos los bloques pertenecientes a cualquier especie de madera de Minecraft están sujetos al ciclo: troncos, tablones, escaleras, losas, vallas, puertas de valla, puertas, trampillas, placas de presión, botones, carteles —en las 11 especies de madera presentes en el juego. Ningún formato es inmune.

## 🦠 Las Cuatro Etapas

El deterioro es una secuencia unidireccional e imparable si no se interviene:

| Etapa | Nombre | Aspecto | Riesgo de Infección |
| :---: | :--- | :--- | :--- |
| **0** | Sano (Vanilla) | Aspecto original del bloque | Ninguno |
| **1** | Infectado | Ligeras manchas de micelio en la superficie | Moderado |
| **2** | Mohoso | Hifas densas, color apagado, superficie orgánica | Alto |
| **3** | Podrido | Estructura colapsada, polvo fúngico, textura disgregada | Irreversible |

La transición de una etapa a la siguiente ocurre en los *random block ticks* del servidor cada vez que el Riesgo de Infección supera el umbral del **50%** (ver [Capítulo 2](02_por_que_se_pudre_la_madera.md)).

Cada etapa existe también en variante **encerada**: un bloque sellado con panal de abeja congela su deterioro en la etapa actual, pero no retrocede.

### 🔊 Inmersión Sensorial: Sonidos y Partículas Personalizadas
El deterioro no es solo una textura diferente: cada etapa posee su propia identidad sensorial:
- **Audio de Rotura**: romper bloques degradados (Etapa 2 y 3) reproduce un sonido sordo y desgarrador de rotura orgánica (`BLOCK_FUNGUS_BREAK`), sustituyendo el clásico golpe seco de la madera sana.
- **Nubes de Esporas**: destruir bloques avanzados sin *Toque de Seda* (*Silk Touch*) desata una explosión visual de partículas fúngicas en el aire (esporas aéreas, esporas que caen y fragmentos de micelio — 42 partículas en la Etapa 2 y nada menos que 80 en la Etapa 3).

## 🗺️ Estructuras Generadas en el Mundo

Las estructuras naturales aparecen ya pre-envejecidas, según su historial ambiental simulado:

- **Degradación Crítica** — Naufragios, Cabañas de Bruja: presencia masiva de Etapa 3.
- **Degradación Alta** — Pozos de mina abandonados, Aldeas Zombi, Ruinas: mezcla de Etapas 1 y 2.
- **Degradación Moderada** — Puestos de saqueadores, Portales en Ruinas: predominantemente Etapa 1.
- **Degradación Mínima** — Aldeas, Mansiones del Bosque: casi intactos.

Los árboles vivos son inmunes mientras no sean talados. Una vez que cae un tronco, el deterioro puede comenzar.

---

| | |
| :--- | ---: |
| [📑 Índice](README.md) | [Por Qué se Pudre la Madera →](02_por_que_se_pudre_la_madera.md) |
