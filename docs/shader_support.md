# Valutazione Supporto Shader (Spores & Shadows)

## Pipeline di Rendering Attuale
Attualmente, `Spores & Shadows` utilizza le API di rendering di Fabric (FRAPI) tramite `ForwardingBakedModel`. 
La muffa viene renderizzata emettendo un secondo set di quad (poligoni) leggermente spostati verso l'esterno (`offset = 0.002f`) lungo la normale della faccia. Questo offset serve a prevenire lo Z-fighting con il blocco sottostante.

## Problemi di Compatibilità con gli Shader
1. **Z-Fighting / Offset Z:** Molti shader ri-proiettano la profondità (depth buffer) o calcolano le ombre in base alla geometria base. L'offset minimo di 0.002f potrebbe non essere sufficiente con alcune risoluzioni della shadow map, causando sfarfallii (Z-fighting) o artefatti d'ombra (self-shadowing).
2. **Supporto FRAPI (Fabric Rendering API):** Shader mod come **Iris** (su Fabric) storicamente bypassavano parzialmente le FRAPI. È spesso richiesto l'uso della mod **Indium** per garantire che i `ForwardingBakedModel` vengano renderizzati correttamente. Senza Indium, il layer di muffa potrebbe non essere disegnato affatto.
3. **Canvas Renderer:** Canvas implementa una sua pipeline di rendering molto diversa. Spesso supporta meglio le FRAPI rispetto a Iris+Sodium nativo, ma gestisce materiali e PBR (Physically Based Rendering) in modo proprietario. I quad extra per la muffa potrebbero non ereditare correttamente le proprietà PBR (normal map, specular map) del blocco base, risultando visivamente "piatti".

## Fattibilità Tecnica & Soluzioni Proposte
Per garantire una compatibilità ottimale con Iris/Oculus/Canvas:

- **Approccio 1: Texture Generata a Runtime (Resource Pack / Atlante)**
  Invece di aggiungere geometria extra (quad offset), si potrebbero fondere (blend) dinamicamente in memoria le texture del blocco base con la texture della muffa durante la fase di caricamento degli asset (Texture Stitching). Questo eliminerebbe completamente l'offset geometrico, garantendo perfetta compatibilità con shader (e PBR, se unita correttamente) e prestazioni migliori.
  
- **Approccio 2: Shader Core Custom / Mixin**
  Intervenire tramite mixin direttamente nei vertex/fragment shader di Minecraft o Iris (RenderType, ChunkBuilder), passando un attributo extra (es. un lightmap channel o un vertex color modificato) per indicare il livello di infezione, calcolando l'overlay della texture lato GPU. Questo è estremamente complesso e fragile ad ogni aggiornamento o cambio di shader.

- **Approccio 3 (attuale migliorato): Dipendenza da Indium e tuning Z-Offset**
  Aggiungere `Indium` come dipendenza suggerita/raccomandata. Aggiungere al file di configurazione (`ModConfig`) un parametro `mold_z_offset` che permette agli utenti di aumentare l'offset (es. a 0.005f) nel caso riscontrino problemi di Z-fighting con specifici shader.

## Conclusione
L'approccio più rapido è mantenere la pipeline attuale ma **raccomandare l'uso di Indium** e magari esporre l'offset nella configurazione per il debugging. 
Per la soluzione definitiva e più solida (soprattutto per pacchetti PBR), l'ideale sarebbe passare alla fusione delle texture in fase di caricamento (Approccio 1), sebbene richieda una riscrittura significativa del gestore dei modelli.
