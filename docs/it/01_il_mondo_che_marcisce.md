# 🌳 Il Mondo che Marcisce

Quando installi Spores & Shadows, il mondo di Minecraft non cambia aspetto dall'oggi al domani. Cambia nel tempo — e spesso te ne accorgi troppo tardi.

La mod sostituisce in modo trasparente ogni blocco di legno con una variante dinamica. L'effetto è invisibile all'inizio: il blocco ha lo stesso aspetto, le stesse proprietà. Ma le condizioni ambientali agiscono su di lui ogni tick, e prima o poi la muffa vince.

---

## 🪵 Cosa Decade

Tutti i blocchi appartenenti a qualsiasi specie di legno di Minecraft sono soggetti al ciclo: tronchi, assi, scale, lastre, staccionate, cancelletti, porte, botole, pedane, pulsanti, cartelli — in tutte le 11 specie di legno presenti nel gioco. Nessun formato è immune.

## 🦠 I Quattro Stadi

Il decadimento è una sequenza unidirezionale e inarrestabile se non si interviene:

| Stadio | Nome | Aspetto | Rischio Infezione |
| :---: | :--- | :--- | :--- |
| **0** | Sano (Vanilla) | Aspetto originale del blocco | Nessuno |
| **1** | Intaccato | Leggere chiazze di micelio sulla superficie | Moderato |
| **2** | Ammuffito | Ife dense, colore spento, superficie organica | Alto |
| **3** | Marcio | Struttura crollata, polvere fungina, texture disgregata | Irreversibile |

La transizione da uno stadio al successivo avviene sui *random block tick* del server ogni volta che il Rischio di Infezione supera la soglia del **50%** (vedi [Capitolo 2](02_perche_il_legno_marcisce.md)).

Ogni stadio esiste anche in variante **cerata**: un blocco sigillato con la cera d'api congela il suo decadimento nello stadio corrente, ma non torna indietro.

### 🔊 Immersione Sensoriale: Suoni e Particelle Personalizzate
Il decadimento non è solo una texture diversa: ogni stadio possiede una propria identità sensoriale:
- **Audio di Rottura**: rompere blocchi degradati (Stadio 2 e 3) riproduce un suono sordo e lacerante di rottura organica (`BLOCK_FUNGUS_BREAK`), sostituendo il classico colpo secco del legno sano.
- **Nuvole di Spore**: distruggere blocchi avanzati senza *Tocco di Velluto* (*Silk Touch*) scatena un'esplosione visiva di particelle fungine nell'aria (spore aeree, spore cadenti e frammenti di micelio — 42 particelle allo Stadio 2 e ben 80 allo Stadio 3).

## 🗺️ Le Strutture Generate nel Mondo

Le strutture naturali compaiono già pre-invecchiate, a seconda della loro storia ambientale simulata:

- **Degrado Critico** — Relitti, Capanne delle Streghe: presenza massiccia di Stadio 3.
- **Degrado Alto** — Miniere abbandonate, Villaggi Zombie, Rovine: mix di Stadi 1 e 2.
- **Degrado Moderato** — Avamposti, Portali in Rovina: prevalentemente Stadio 1.
- **Degrado Minimo** — Villaggi, Magioni della Foresta: quasi integri.

Gli alberi vivi sono immuni finché non vengono abbattuti. Una volta che un tronco cade, il degrado può iniziare.

---

| | |
| :--- | ---: |
| [📑 Indice](README.md) | [Perché il Legno Marcisce →](02_perche_il_legno_marcisce.md) |
