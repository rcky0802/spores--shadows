# ⚙️ Sanierung automatisieren

Manuelle Detektoren und vorbeugendes Wachsen decken alltägliche Aufgaben ab. Für ausgedehnte Bauten, unterirdische Hallen oder Situationen, in denen der Spieler nicht dauernd vor Ort sein kann, stellt die Mod ein vollständiges Automatisierungssystem bereit: permanente Sensoren, Industriemaschinen und integrierte Redstone-Schaltungen.

---

## 📡 Permanente Redstone-Sensoren (Hybrides System)

Beide Detektoren können in beliebiger Ausrichtung auf festen Blöcken montiert werden (**Boden, Wand oder Decke**). Neben der visuellen Anzeige und Diagnosefunktion besitzen sie ein **hybrides Redstone-System** (direkte Signalabgabe + Komparator-Ausgang):

**💧 Feuchtigkeitsdetektor (platziert)** — Überwacht die effektive Feuchtigkeit $H_{eff}$ und das lokale Mikroklima:
- **Visuelle Skala**: 4 abgestufte Zustände (0 = Trocken, 1 = Mäßig, 2 = Feucht, 3 = Kritisch).
- **Direktes Signal**: Gibt eine zur Stufe proportionale Redstone-Stärke ab (**0, 5, 10, 15**). Es versorgt direkt anliegendes Redstone-Pulver, Warnlampen, Maschinen oder den tragenden Wandblock (wodurch Kabel unsichtbar hinter der Wand verlegt werden können).
- **Komparator-Unterstützung**: Jeder anliegende Komparator liest nativ dasselbe Signal (0, 5, 10, 15) für analoge Schwellenwert-Schaltungen aus.
- **Abfrage**: Bei Rechtsklick auf den Block (mit freier Hand oder Werkzeug) rastet das Gerät mechanisch ein und sendet den vollständigen Diagnosebericht in den Chat.

**☢️ Sporendetektor (platziert)** — Scannt periodisch das BFS-Luftvolumen des Raumes:
- **Visuelle Skala**: 4 Gefahrenstufen (0 = Reine Luft, 1 = Warnung, 2 = Mäßiges Risiko / Hunger, 3 = Tödliche Gefahr / Gift).
- **Direktes Signal**: Gibt ein proportionales Redstone-Signal ab (**0, 5, 10, 15**), wodurch Luftentfeuchter und Luftreiniger automatisch anspringen, sobald sich Miasma anstaut — ganz ohne zwischengeschaltete Verstärker.
- **Komparator-Unterstützung**: Voll kompatibel mit Komparatoren zur Erstellung komplexer Alarm- und Notfallsysteme.
- **Abfrage**: Bei Rechtsklick ertönt ein metallisches Klicken und die volumetrische Telemetrie samt Miasma-Trend (ansteigend, stabil oder abklingend) wird in den Chat übertragen.

## 💣 Redstone-Mechanik: Redstone-Truhen und Klemmen

Schimmel beeinträchtigt auch die holzeigenen Redstone-Mechanismen.

**Holzknöpfe** — Das Pilzgeflecht verklebt die Rückstellfeder und verlängert die Betätigungsdauer drastisch:
- Stufe 0 (Sauber / Gewachst): **1.5 Sekunden** (30 Ticks — Vanilla-Standard)
- Stufe 1 (Befallen): **3.0 Sekunden** (60 Ticks)
- Stufe 2 (Schimmelig): **7.5 Sekunden** (150 Ticks)
- Stufe 3 (Verrottet): **22.5 Sekunden** (450 Ticks) — zerstört jedes präzise Schaltungstiming

**Holzdruckplatten** — Die Pilzbiomasse verlangsamt das Zurückfedern nach Verlassen der Platte:
- Stufe 0 (Sauber / Gewachst): **1.0 Sekunde** (20 Ticks — Vanilla-Standard)
- Stufe 1 (Befallen): **2.0 Sekunden** (40 Ticks)
- Stufe 2 (Schimmelig): **5.0 Sekunden** (100 Ticks)
- Stufe 3 (Verrottet): **15.0 Sekunden** (300 Ticks) — das Signal bleibt 15 Sekunden nach Betreten bestehen

**Redstone-Truhen — Klemmen (Jamming)** — Schimmel korrodiert die Kontaktscharniere. Bei jedem Öffnen besteht ein steigendes Risiko, dass der Kontakt versagt (Klemmen mit leisem Leerklicken ohne Signalabgabe):
- Stufe 1 (Befallen): **15%** Ausfallwahrscheinlichkeit
- Stufe 2 (Schimmelig): **50%** Ausfallwahrscheinlichkeit
- Stufe 3 (Verrottet): **85%** Ausfallwahrscheinlichkeit

Dieser Defekt wird vor externen HUDs (Jade/WTHIT) bewusst verheimlicht, um den Überraschungseffekt zu wahren.

**Gemeißelte Bücherregale und Komparatoren** — Darin gelagerte Bücher überstehen den Zerfall auf jeder Stufe unversehrt. Das vom rückwärtigen Komparator abgegebene Signal (1 bis 6, je nach zuletzt genutztem Slot) bleibt absolut deterministisch und entspricht fehlerfrei dem Vanilla-Verhalten.

---

## 🌀 Luftentfeuchter

Die Schlüsselmaschine zur aktiven Klimaregulierung. Während der Luftreiniger erst eingreift, wenn Miasma bereits entstanden ist, wirkt der Luftentfeuchter präventiv: Er entzieht dem Raum Feuchtigkeit und entzieht Pilzen damit die Lebensgrundlage.

- **Entfeuchtungsleistung (1.0)** — Jeder aktive Luftentfeuchter wendet eine Trocknungsleistung von **1.0 Punkt** auf die Raumklimaberechnung an:
  $$H_{\text{target}} = \max(0.0, \, H_{\text{raw}} - 1.0 \times N_{\text{entfeuchter}})$$
  Da die natürliche Rohfeuchtigkeit $H_{\text{raw}}$ zwischen $0.0$ und $1.0$ liegt, reicht bereits ein einziger aktiver Luftentfeuchter aus, um die effektive Feuchtigkeit $H_{\text{eff}}$ in jedem versiegelten Raum bis zu $2048\text{ m}^3$ auf **$0.0$ ($0\%$)** zu senken. Der Raum wird zu einer trockenen Oase, immun gegen jeden Verfall.
- **Hybride Energieversorgung (32.000 FE / 4.0× Brennstoff)** — Verbraucht im Betrieb **10 FE/Tick** ($0\text{ FE/Tick}$ im Standby oder abgeschaltet). Unterstützt elektrische Energie über Kabel (bis zu 500 FE/t an jeder Blockseite) oder Festbrennstoff im Slot 0 mit vierfacher Brenndauer ($4.0\times$, ein Stück Kohle liefert $64.000\text{ FE}$ und füllt den internen Puffer komplett).
- **Kondenswassertank & Betriebsmodi (2.000 mB)** — Ausgestattet mit einem internen Flüssigkeitstank:
  - **Entfeuchtungsmodus**: Zieht Feuchtigkeit aus der Luft und sammelt Kondenswasser mit einer Basisrate von **1 mB pro 24 Ticks** (in feuchteren Räumen beschleunigt sich die Kondensation).
  - **Standby bei vollem Tank (FULL)**: Bei Erreichen von 2.000 mB (2 Wassereimern) pausiert das Gerät automatisch, um keinen Strom und Brennstoff zu verschwenden.
  - **Befeuchtungsmodus (Humidify)**: Umgekehrte Arbeitsweise; verbraucht Tankwasser (1 mB alle 24 Ticks), um Wasserdampf in den Raum abzugeben ($+1.0$ Feuchtigkeit). Schaltet bei Erreichen von 98% ($0.98$) Raumfeuchte automatisch ab. Nützlich für kontrollierte Pilzzuchten.
- **Trichter- und Flüssigkeitsautomatisierung (SidedInventory & Fluid Transfer)**:
  - **Fester Brennstoff**: Trichter können Brennstoff über **jede beliebige Seite** in Slot 0 einspeisen.
  - **Wasserentnahme/-einspeisung**: Unterstützt manuelle Eimerbefüllung (1.000 mB per Rechtsklick) und Rohranschlüsse über die *Fabric Transfer API*.
- **Bedienoberfläche, Redstone und Komparator**:
  - **Redstone-Steuerung**: Dreistufiger Schalter (*Immer aktiv* `IGNORED`, *Aktiv bei Signal* `HIGH`, *Pausiert bei Signal* `LOW`). Gekoppelt an einen Feuchtigkeitsdetektor schaltet sich das Gerät nur ein, wenn die Luft zu feucht wird.
  - **Komparator-Ausgang**: Sendet ein Signal von **$0$ bis $15$**, proportional zum Füllstand des internen Wassertanks (0 bei leerem Tank, 15 bei vollen 2.000 mB).

---

## 🌬️ Luftreiniger

Das Spezialgerät zur aktiven Beseitigung atmosphärischer Giftstoffe. Während der Luftentfeuchter durch Trocknung vorbeugt, neutralisiert der Luftreiniger umherfliegende Miasmasporen in kontaminierten, luftdichten Räumen.

- **Reinigungskraft (48.0)** — Jeder aktive Luftreiniger zieht **48.0 Punkte** von der toxischen Belastung des Raumes ab:
  $$\text{targetMiasma} = \max(0.0, \, \text{toxicScore} - \text{ventilationScore} - 48.0 \times N_{\text{reiniger}})$$
  Da ein befallener Stamm etwa $2.25$ Toxizitätspunkte erzeugt, neutralisiert ein einzelner Reiniger die Ausdünstungen von **über 21 befallenen Blöcken gleichzeitig** und reinigt Räume bis zu $2048\text{ m}^3$ vollständig.
- **Hybride Energieversorgung (32.000 FE / 4.0× Brennstoff)** — Verbraucht im Betrieb **10 FE/Tick** ($0\text{ FE/Tick}$ im Standby oder bei erschöpftem Filter). Unterstützt Stromeinspeisung (bis zu 500 FE/t an allen Seiten) oder Festbrennstoff in Slot 0 mit vierfacher Ausbeute ($4.0\times$, ein Stück Kohle liefert $64.000\text{ FE}$).
- **Sporenfilter-Kartuschen & dynamischer Verschleiß (Spore Filters)** — Eingelegt in Slot 1 (stapelbar bis 64 Stück):
  - **Basishaltbarkeit**: **2.400 Ticks (2 Minuten Dauerbetrieb)** pro Filter bei einer Rate von 1 Punkt/Tick.
  - **Automatisches Nachladen**: Ist ein Filter verbraucht, zieht die Maschine sofort den nächsten Filter aus dem Vorratsstapel ein.
  - **Beschleunigter Verschleiß bei tödlichem Miasma**: Erreicht der Raum die kritische Giftkonzentration *LETHAL_POISON*, verdoppelt sich der Abrieb durch die Sporenüberlastung auf **2 Punkte/Tick** (60 Sekunden Standzeit).
  - **Erschöpfungswarnung (FILTER_DEPLETED)**: Sind alle Filter aufgebraucht, stoppt die Maschine und signalisiert den Mangel mit einem metallischen Leerklicken (`BLOCK_DISPENSER_FAIL`).
- **Trichter-Automatisierung (SidedInventory)**:
  - **Oberseite (UP)**: Trichter speisen *ausschließlich* **Sporenfilter** (Slot 1) ein.
  - **Seiten und Unterseite (NORTH, SOUTH, EAST, WEST, DOWN)**: Akzeptieren *ausschließlich* **festen Brennstoff** (Slot 0).
- **Bedienoberfläche, Redstone und Komparator**:
  - **Redstone-Steuerung**: Dreistufiger Schalter (*Immer aktiv* `IGNORED`, *Aktiv bei Signal* `HIGH`, *Pausiert bei Signal* `LOW`). Gekoppelt an einen Sporendetektor läuft der Reiniger nur bei Schadstoffbelastung und spart Filter in sauberer Luft.
  - **Komparator-Ausgang**: Gibt ein Signal von **$0$ bis $15$** proportional zur Reserve an Sporenfiltern ab (0 bei leerem Slot, 15 bei vollem 64er-Stapel) — optimal für Nachfüllanzeigen.

---

| | |
| :--- | ---: |
| [← Sich verteidigen](05_sich_verteidigen.md) | [Zerfall im letzten Detail →](07_zerfall_im_letzten_detail.md) |
| [📑 Inhaltsverzeichnis](README.md) | |
