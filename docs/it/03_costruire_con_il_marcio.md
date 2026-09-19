# 🧱 Costruire con il Marcio

Una volta che il legno ha iniziato a marcire, il suo valore come materiale crolla rapidamente. La mod impone conseguenze concrete sul crafting, sulla resistenza fisica e sull'utilizzo in fornace — il tutto con una logica interna coerente: più il legno è compromesso, meno puoi farne.

---

## 🧱 Degrado Meccanico, Termico e Fragilità

Man mano che le ife fungine consumano la cellulosa e la lignina, il legno perde coesione strutturale e trattiene polveri sottili asciutte. Tutte le sue proprietà fisiche e termiche decadono in parallelo:

| Proprietà | 🌲 Stadio 0 (Sano) | 🟢 Stadio 1 (Intaccato) | 🦠 Stadio 2 (Ammuffito) | ☠️ Stadio 3 (Marcio) |
| :--- | :---: | :---: | :---: | :---: |
| **Durezza Blocco** | `2.0` (100%) | `1.6` (80%) | `1.0` (50%) | `0.4` (20%) |
| **Resistenza Esplosioni (TNT)** | 100% | 80% | 50% | **10%** |
| **Efficacia Strumenti** | Normale (Ascia) | Normale (Ascia) | Normale (Ascia) | **Annullata (Pugno = Ascia)** |
| **Drop in Sopravvivenza** | `100%` | `100%` | `50%` (Metà persa) | **`0%` (Sbriciolamento)** |
| **Drop con Tocco di Velluto / Cera** | `100%` | `100%` | `100%` | `100%` |
| **Bonus Innesco Fuoco** | $+0$ (Vanilla) | $+5$ | $+10$ | $+20$ |
| **Bonus Diffusione Fiamme** | $+0$ (Vanilla) | $+10$ | $+25$ | $+60$ |
| **Potere Combustibile (Fornace)** | `100%` (1.0×) | `50%` (0.5×) | `25%` (0.25×) | `12.5%` (0.125×) |
| **Probabilità Compostaggio** | — (Non compostabile) | `50%` | `65%` | **`85%`** (Ottimo fertilizzante) |

> [!WARNING]
> **Stadio 3 — Estrema Friabilità**: Rompere un blocco marcio annulla completamente il vantaggio dell'ascia: sia con un'ascia di Netherite che a pugni nudi ci vorrà lo stesso tempo, e il blocco si sbriciolerà senza lasciare alcun drop (a meno di usare *Tocco di Velluto* o averlo preventivamente cerato).  
> **Cottura della Carbonella**: I tronchi infetti (Stadi 1, 2, 3 — sia normali che cerati) **non possono essere cotti per produrre carbonella**: la materia fungina compromessa impedisce la carbonizzazione. Solo i tronchi sani di Stadio 0 dell'Overworld (vanilla o cerati sani) possono essere cotti in fornace per ottenere carbonella. I tronchi infetti possono essere usati esclusivamente come combustibile (con durata ridotta).

---

## 📐 Regole di Crafting

**Solo le assi sane o cerate possono essere usate per fabbricare oggetti complessi** (porte, casse, scale, banchi da lavoro, ecc.). Le assi infette non sono accettate nelle ricette di oggetti finiti.

La conversione da tronco infetto ad assi sane segue il **dimezzamento progressivo**:

| Tronco | Assi Sane Ottenibili |
| :--- | :---: |
| Sano / Cerato | 4 |
| Intaccato | 2 |
| Ammuffito | 1 |
| Marcio | 0 — irrecuperabile |

**Pulizia delle assi in griglia**: se hai smontato una vecchia struttura e recuperato delle assi infette, puoi purificarle sul banco:
- 2 Assi Intaccate → 1 Asse Sana
- 4 Assi Ammuffite → 1 Asse Sana

*Le varianti normali e cerate dello stesso stadio si possono mescolare liberamente nella griglia.*

---

| | |
| :--- | ---: |
| [← Perché il Legno Marcisce](02_perche_il_legno_marcisce.md) | [L'Aria che Uccide →](04_laria_che_uccide.md) |
| [📑 Indice](README.md) | |
