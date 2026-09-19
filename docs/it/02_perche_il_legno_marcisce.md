# 🔬 Perché il Legno Marcisce

Ogni blocco di legno nel mondo viene valutato autonomamente ad ogni tick. Il risultato di quella valutazione è un numero — il **Rischio di Infezione ($R$)** — che determina se il decadimento avanza o si ferma.

$$R = \Big( (H_{eff} \cdot L_{uv} \cdot S_{mat}) + C_{bonus} + M_{bonus} \Big) \cdot T_{mult}$$

Se $R > 0.50$, lo stadio avanza. Altrimenti, il blocco rimane stabile. Ogni fattore rappresenta una condizione ambientale reale.

---

## 💧 Umidità Effettiva ($H_{eff}$)

È il motore primario del decadimento. Per qualsiasi blocco completamente allagato (*waterlogged*), $H_{eff} = 1.0$ (massimo assoluto). Negli altri casi, si calcola combinando l'umidità atmosferica locale, i catalizzatori e l'effetto essiccante del vento:

$$H_{eff} = \text{clamp}\Big( H_{current} + C_{humidity} - A_{drying}, \ 0.0, \ 1.0 \Big)$$

Dove l'umidità atmosferica ($H_{current}$) converge dinamicamente verso il target ambientale della stanza:

$$H_{target} = \text{clamp}\Big( H_{base} + D_{depth} + W_{water} + H_{humidifier} - D_{dehumidifier}, \ 0.0, \ 1.0 \Big)$$

- **Umidità Base del Bioma ($H_{base}$)**: biomi piovosi o innevati partono da `0.80`; climi aridi o desertici da `0.30`.
- **Modificatore Profondità ($D_{depth}$)**: scendendo sotto $Y = 64$, sale gradualmente fino a raggiungere il cap massimo di $+0.40$ a quota $Y \le 48$, rimanendo costante in tutto il Deepslate fino a $Y = -64$.
- **Sorgenti d'Acqua nella Stanza ($W_{water}$)**: ogni blocco d'acqua presente nella stanza aggiunge $+0.15$ (fino a un massimo di $+0.60$).
- **Macchinari ($H_{humidifier} / D_{dehumidifier}$)**: Nebulizzatori ($+1.0$ cad.) o Deumidificatori ($-1.0$ cad.).
- **Catalizzatori Locali ($C_{humidity}$)**: blocchi adiacenti ricchi d'acqua (es. fango o calderoni d'acqua) aggiungono $+0.10$.
- **Essiccamento da Aerazione ($A_{drying}$)**: la ventilazione locale asciuga la superficie del blocco: $A_{drying} = \text{Aerazione} \cdot 0.50$.

## ☀️ Luce UV ($L_{uv}$)

La luce agisce da sterilizzatore. Il livello di illuminazione viene campionato attorno al blocco:
- **6 punti** (le facce adiacenti) per i blocchi pieni e opachi (tronchi, assi).
- **7 punti** (le 6 facce + lo spazio interno del blocco) per manufatti non pieni o trasparenti (scale, lastre, staccionate, porte, cartelli).

La media della luce rilevata viene scalata tra `0.0` (luce massima 15 — sterilizzazione e infezione bloccata) e `1.0` (buio totale 0 — rischio pieno). Un blocco ben illuminato in una stanza aperta ha un rischio quasi azzerato; lo stesso blocco in una miniera buia è vulnerabilissimo.

## 🪓 Suscettibilità del Materiale ($S_{mat}$)

Non tutti i blocchi hanno la stessa vulnerabilità biologica:

| Categoria | Moltiplicatore ($S_{mat}$) | Dettaglio Blocchi |
| :--- | :---: | :--- |
| **Legno Scortecciato** | **1.4×** | Tronchi, legni e fusti scortecciati (`stripped_*`). Privati della corteccia protettiva, le fibre vive sono le più vulnerabili in assoluto. |
| **Default / Tronchi e Manufatti** | **1.0×** | Tronchi con corteccia protettiva, arredi, porte, botole, staccionate, cartelli, casse e banchi di lavoro. |
| **Legno Lavorato da Costruzione** | **0.8×** | Assi (`*_planks`), Scale (`*_stairs`), Lastre (`*_slab`) e Mosaico di bambù. Legname stagionato e squadrato da costruzione con resistenza strutturale superiore. |

## 🌡️ Temperatura e Finestra Biologica ($T_{mult}$)

Le spore prosperano solo nella "Finestra Biologica" ($0.15 \le \text{Temp} \le 1.50$). Al di fuori di questo intervallo, $T_{mult} = 0.0$ e la proliferazione fungina si arresta completamente:

- **Montagne e Alta Quota**:
  - Il raffreddamento progressivo inizia salendo sopra **$Y = 128$**.
  - Man mano che si sale verso **$Y = 256$**, la temperatura cala gradualmente fino a raggiungere $-0.50$ (valore che rimane fisso fino al limite del mondo di **$Y = 320$**).
  - La crescita delle muffe si **blocca completamente già quando la temperatura scende sotto $0.15$** (tipicamente tra $Y \approx 180$ e $Y \approx 220$ a seconda del bioma), rendendo le baite montane naturalmente protette dal gelo.
- **Sotterranei e Caverne**:
  - Scendendo sotto il livello del mare (**$Y = 64$**), la temperatura si normalizza verso il microclima umido delle grotte.
  - A partire da **$Y \le 48$** e per tutto il Deepslate fino a **$Y = -64$**, la temperatura si stabilizza costantemente al valore ideale di **`0.50`**, garantendo che le miniere abbandonate marciscano sempre, indipendentemente dal clima superficiale (anche se sopra c'è un deserto o una tundra).

## ☣️ Catalizzatori Fisici ($C_{bonus}$) e Pressione Miasma ($M_{bonus}$)

I blocchi biologici situati nel raggio di scansione circostante (cubo 3×3×3 attorno al blocco) accelerano l'infezione aggiungendo un bonus diretto al rischio $R$, oppure incrementando l'umidità locale:

| Catalizzatore | Bonus Rischio ($C_{bonus}$) | Bonus Umidità Locale | Dettagli & Comportamento |
| :--- | :---: | :---: | :--- |
| **Fiore delle Spore** (`Spore Blossom`) | **+0.80** (+80%) | — | **Estremamente letale**: da solo supera la soglia di infezione (0.50). Altamente sconsigliato come decorazione vicino a travi in legno! |
| **Funghi** (rossi, marroni, blocchi giganti) | **+0.25** (+25%) | — | Funghi a terra o blocchi di fungo gigante rilasciano spore continue per contatto. |
| **Podzol & Micelio** | **+0.15** (+15%) | — | Terreni organici ricchi di ife fungine sotterranee. |
| **Fango** (`Mud`) | **+0.05** (+5%) | **+0.10** | Trattiene forte umidità e accelera il marciume alla base degli edifici. |
| **Calderone d'Acqua** | — | **+0.10** | Aggiunge umidità locale ristagnante entro 3 blocchi. |
| **Blocco Intaccato non cerato** (Stadio 1) | **+0.03** cad. | — | Ogni blocco malato vicino diffonde passivamente il contagio a quelli sani adiacenti. |
| **Blocco Ammuffito non cerato** (Stadio 2) | **+0.06** cad. | — | Pressione di contagio raddoppiata rispetto allo stadio 1. |
| **Blocco Marcio non cerato** (Stadio 3) | **+0.12** cad. | — | Elevata carica biologica infettiva per tutti i blocchi confinanti. |

> [!NOTE]
> I blocchi di legno **cerati** (`waxed`) **non** fungono da catalizzatori: la patina di cera d'api sigilla completamente le spore e azzera il loro contributo infettivo verso i vicini.

### 🌫️ Pressione del Miasma Aereo ($M_{bonus}$)
Oltre al contatto fisico solido, il legno esposto all'aria stagnante di una stanza satura di miasma subisce una contaminazione aerea costante:
$$M_{bonus} = \text{ExposureIndex} \cdot 0.50$$
Un ambiente asfittico e tossico (approfondito nel [Capitolo 4](04_laria_che_uccide.md)) spinge anche il legname altrimenti asciutto a cedere rapidamente per infezione aerea.

---

| | |
| :--- | ---: |
| [← Il Mondo che Marcisce](01_il_mondo_che_marcisce.md) | [Costruire con il Marcio →](03_costruire_con_il_marcio.md) |
| [📑 Indice](README.md) | |
