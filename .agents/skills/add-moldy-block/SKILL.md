---
name: add-moldy-block
description: Istruzioni e standard per aggiungere un nuovo blocco al ciclo di decadimento di Spores & Shadows.
---

# Aggiunta di un nuovo Blocco (Spores & Shadows)

Questa skill definisce la procedura obbligatoria per aggiungere un nuovo tipo di blocco (es. librerie, recinzioni speciali, nuovi tipi di legno) all'ecosistema di **Spores & Shadows**, garantendo che la meccanica di decadimento ambientale sia coerente e funzionante.

## 1. Creare la Classe del Blocco
Crea una nuova classe in `moldmod.block` (es. `MoldyBookshelfBlock`) che estenda il blocco Vanilla corrispondente.
**Requisiti obbligatori per la classe:**
- Devi iniettare i 3 Block States centrali usando `MoldyLogBlock`: `STAGE` (Int), `WAXED` (Bool) e `STRUCTURAL` (Bool).
- Nel costruttore, imposta lo stato di default del blocco con `STAGE=0`, `WAXED=false`, e `STRUCTURAL=false`.
- Devi sovrascrivere il metodo `randomTick` per delegare il decadimento all'helper:
  ```java
  @Override
  public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      MoldyBlockHelper.randomTick(state, world, pos, random, this);
  }
  ```
- (Opzionale ma raccomandato) Gestisci l'interazione del giocatore (Tasto Destro + Ascia per raschiare, o Tasto Destro + Favo per incerare) modificando i valori `STAGE` e `WAXED`, se il blocco lo richiede esplicitamente (generalmente copiando l'approccio di `MoldyLogBlock` o affidandoti a logiche comuni).

## 2. Istanziare e Registrare il Blocco (`ModBlocks.java`)
Apri il file `src/main/java/moldmod/block/ModBlocks.java`.
Trova il punto adeguato (generalmente dentro `registerWoodSet` se è una variante per tipo di legno).
- Usa il metodo interno `registerBlock` copiando i Settings del blocco originale, ma assicurandoti di invocare `.ticksRandomly()`:
  ```java
  Block nuovoBlocco = registerBlock("moldy_" + prefix + "_nomeblocco", new MoldyNomeBlocco(AbstractBlock.Settings.copy(vanillaBlock).ticksRandomly()));
  ```

## 3. Mappare il Blocco in `VANILLA_TO_MOLDY`
Per far sapere al sistema quale blocco Vanilla corrisponde alla tua nuova variante, **devi** aggiungerlo alla mappa globale:
```java
VANILLA_TO_MOLDY.put(vanillaBlock, nuovoBlocco);
```

## 4. Generare gli Oggetti (Item)
Non creare manualmente gli Item per il blocco. Sfrutta il sistema esistente per generare automaticamente le 4 varianti (cerato, intaccato, ammuffito, marcio):
```java
registerStageItems(vanillaBlock, prefix + "_nomeblocco", nuovoBlocco);
```
Questa funzione creerà in automatico i tooltip corretti e piazzerà le varianti nel menù della Creativa subito dopo l'oggetto Vanilla.

## 5. (Ultimo step) Texture e Data Generation
Dopo aver aggiunto il codice, assicurati di fornire i modelli e le texture necessari. Il progetto usa il data generator, quindi assicurati di aggiornare le classi di data generation (se presenti e attive) per includere il nuovo blocco, o fornire manualmente i file `.json` e `.png` necessari nelle risorse virtuali (Polymer) o fisiche.
