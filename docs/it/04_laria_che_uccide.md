# ☠️ L'Aria che Uccide

Il legno che marcisce non è un problema statico: rilascia attivamente spore nell'aria circostante. In spazi chiusi e mal ventilati, quella contaminazione si accumula fino a diventare letale.

---

## 🌫️ Il Miasma Volumetrico

Il gioco valuta costantemente l'aria attorno alla testa del giocatore usando un algoritmo BFS (Breadth-First Search) tridimensionale.

- **Volume analizzato**: fino a **2048 m³** di aria contigua.
- **Raggio massimo**: **16 blocchi** di distanza euclidea dalla testa del giocatore.
- **Caverne aperte**: se il volume supera i 2048 m³ senza incontrare pareti chiuse, l'ambiente viene classificato come *aperto* e il Miasma si disperde istantaneamente — come se ci fosse corrente d'aria.

### Cosa blocca il Miasma & Punteggi di Ventilazione (Base 6)

| Elemento | Comportamento | Punteggio Portata ($V$) |
| :--- | :--- | :---: |
| **Blocchi pieni, vetro** | Barriera ermetica | `0.0` |
| **Porte chiuse, botole chiuse** | Barriera ermetica | `0.0` |
| **Blocchi allagati (waterlogged)** | **Sifone idraulico** — barriera a tenuta stagna perfetta | `0.0` |
| **Cielo aperto diretto** | Camino atmosferico naturale | **`+24.0`** / blocco |
| **Porte/Botole aperte, Grate di rame, Foglie** | Varchi di ventilazione primari | **`+18.0`** / blocco |
| **Lastre (Slabs)** | Varchi parziali semipieni | **`+12.0`** / blocco |
| **Scale, Staccionate (Fences)** | Fessure e aperture minori | **`+6.0`** / blocco |

La saturazione della stanza non è istantanea: cresce con inerzia temporale (`saturation_speed = 0.15`) quando i varchi vengono sigillati, e si dissipa molto più rapidamente (`dissipation_speed = 0.35`) aprendo anche una sola finestra o varco.

### 🧮 Formula di Generazione del Miasma

In qualsiasi ambiente confinato, il miasma obiettivo della stanza ($M_{target}$) è calcolato come bilancio tra la produzione biologica e lo smaltimento dell'aria:

$$M_{target} = \max\Big(0.0, \ \text{Score}_{\text{tossico}} - \text{Score}_{\text{ventilazione}} - \text{Potere}_{\text{purificatori}}\Big)$$

- **$\text{Score}_{\text{tossico}}$**: ogni blocco di legno infetto non cerato affacciato sull'aria della stanza emette spore in base al suo stadio:
  - 🟢 Stadio 1 (Intaccato): **$+1.0$**
  - 🦠 Stadio 2 (Ammuffito): **$+2.0$**
  - ☠️ Stadio 3 (Marcio): **$+4.0$**  
  *(I blocchi cerati non rilasciano spore e hanno contributo zero).*
- **$\text{Score}_{\text{ventilazione}}$**: portata d'aria cumulativa garantita dai varchi verso l'esterno (risolta con algoritmo *Max-Flow*).
- **$\text{Potere}_{\text{purificatori}}$**: ogni Purificatore d'Aria attivo nella stanza abbatte **$-48.0$** punti tossici.

Il **Miasma Netto ($M_{net}$)** converge dinamicamente verso $M_{target}$ ad ogni ciclo di aggiornamento. La **Densità di Spore** determina l'opacità della nebbia e il carico polmonare per $m^3$:

$$\text{Densità} = \frac{M_{net}}{\text{Volume Aria } (m^3)}$$

### Effetti di Tossicità sul Giocatore (Soglie Base 6)

| Miasma Netto | Densità Spore | Stato e Sintomi sul Giocatore |
| :---: | :---: | :--- |
| **$\ge 2.0$** | $\ge 0.0417$ ($1/24$) | **Avviso**: particelle di micelio visibili nell'aria, rumori organici cupi |
| **$\ge 6.0$** | $\ge 0.0833$ ($2/24$) | **Pericolo**: status effect **Fame** (il corpo brucia energie) |
| **$\ge 18.0$** | $\ge 0.1667$ ($4/24$) | **Letale**: contrazione di **Nausea** seguita da **Veleno Letale** |

---

| | |
| :--- | ---: |
| [← Costruire con il Marcio](03_costruire_con_il_marcio.md) | [Difendersi →](05_difendersi.md) |
| [📑 Indice](README.md) | |
