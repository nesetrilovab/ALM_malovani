# Paint App – Rastrový grafický editor

Grafická aplikace v jazyce Java zaměřená na implementaci základních rastrových algoritmů. Projekt nevyužívá žádné kreslicí knihovny, ale pracuje přímo s pamětí pixelů.

## Funkční přehled
* **Geometrické tvary:** Úsečky, obdélníky, elipsy a dynamické polygony.
* **Interaktivní manipulace:** Režim výběru (**Select**) umožňující posun celých objektů nebo jejich jednotlivých kontrolních bodů.
* **Výplň oblastí:** Implementace algoritmu **Flood Fill** pro barvení uzavřených ploch.
* **Vlastnosti čar:** Možnost volby mezi plnou, tečkovanou a čárkovanou čarou s nastavitelnou tloušťkou v rozmezí 1 až 4 pixely.
* **Doplňující fce:** Guma (maže celé tvary), klávesa Shift pro vykreslování po násobcích 45 stupňů, tlačítko Clear pro kompletní vymazání plátna.
---

## Implementované algoritmy

### 1. Rasterizace úsečky
Vykreslování úseček je realizováno ve třídě `TrivialRasterizer`. Algoritmus počítá souřadnice jednotlivých pixelů pomocí lineární rovnice $y = kx + q$.

* **Detekce směrnice:** Pro zachování vizuální spojitosti čáry algoritmus detekuje směrnici $k$.
* **Iterace:** Pokud je $|k| < 1$, probíhá iterace podél osy $x$. Pokud je $|k| \geq 1$, probíhá iterace podél osy $y$. Tím je zamezeno vzniku mezer v čáře při strmých úhlech.

### 2. Generování tloušťky a stylů
* **Tloušťka čáry:** Při požadavku na tloušťku větší než 1 pixel je v metodě `drawPixel` aplikována logika čtvercového plnění. Kolem vypočítaného středového bodu je vykreslena matice pixelů (např. 2x2, 3x3 nebo 4x4), což simuluje šířku stopy.
* **Styly čar (Dotted/Dashed):** Specializované třídy `DottedRasterizer` a `DashedRasterizer` využívají vnitřní čítač v rámci iteračního cyklu úsečky. Na základě modulo operace algoritmus rozhoduje o vynechání nebo vykreslení pixelu, čímž vzniká požadovaný vzor mezer.

### 3. Dynamický Polygon
Nástroj Polygon se od ostatních tvarů liší svou proměnlivou strukturou a způsobem interakce:

* **Stavová správa:** Na rozdíl od úsečky (která má start a konec) využívá Polygon vnitřní seznam `List<Point>`. Prvním kliknutím se vytvoří nová instance tvaru, každé další kliknutí levou myší přidá nový vrchol. Polygon se uzavře až po určení min. 3 bodů.
* **Vykreslování (Rasterizace):** Algoritmus pro zobrazení polygonu v `redrawAll` prochází celý seznam bodů a postupně volá rasterizaci úsečky mezi každými dvěma sousedními body. Pokud je polygon uzavřený, automaticky se vykreslí i spojnice mezi posledním a prvním bodem.
* **Interaktivní ukončení:** Protože počet bodů není omezen, využívá aplikace událost `MouseEvent.BUTTON3` (pravé tlačítko myši). To slouží jako signál pro "finalizaci" tvaru – ukončí se režim přidávání bodů a polygon se v seznamu `shapes` zafixuje jako hotový objekt.
* **Editace vrcholů:** V režimu **Select** je možné díky dynamickému seznamu bodů uchopit jakýkoliv z vrcholů polygonu a měnit jeho polohu, přičemž se v reálném čase přepočítávají (překreslují) obě sousední úsečky, které do tohoto vrcholu ústí.
* 
### 4. Algoritmus Flood Fill
Plošná výplň je implementována jako nerekurzivní verze algoritmu založená na vlastním zásobníku (`Stack`). 

1. Algoritmus identifikuje barvu pixelu v místě kliknutí (`targetColor`).
2. Následně pomocí prohledávání 4-okolí (sever, jih, východ, západ) nahrazuje sousední pixely stejné barvy barvou novou. 
3. Použití zásobníku namísto rekurze předchází riziku přetečení paměti (*StackOverflowError*) u velkých ploch.

### 5. Správa scény a překreslování
Aplikace využívá objektový přístup k uchování nakreslených prvků v seznamu `List<Drawable>`. 

* **RedrawAll:** Při každé změně (posun bodu, smazání tvaru) dojde k vymazání celého rastru a opětovnému vykreslení všech uložených objektů.
* **Nedestruktivní editace:** Tento proces umožňuje měnit parametry již nakreslených objektů a zajišťuje správné pořadí překrývání.

### 6. Interaktivní nástroje a modifikátory

* **Guma (Erase):** Na rozdíl od běžných rastrových editorů, které pouze přebarvují pixely na bílo, moje "Guma" pracuje s objekty. Při kliknutí v tomto režimu algoritmus prohledá seznam `shapes` od nejnovějších po nejstarší a odstraní první objekt, jehož matematický model obsahuje souřadnice kliknutí. Poté vyvolá `redrawAll`, čímž objekt fyzicky zmizí z plochy.
* **Snapování (Shift modifikátor):** * **Úsečky:** Implementuje funkci "přichytávání" k úhlům. Vypočítá se úhel mezi startovním a koncovým bodem a zaokrouhlí se na nejbližší násobek 45 stupňů. Tím je zajištěno přesné kreslení vodorovných, svislých a diagonálních čar.
    * **Geometrické tvary:** U obdélníku a elipsy vynucuje Shifování shodu délky stran (šířka = výška), což transformuje obdélník na čtverec a elipsu na kruh.
* **Vymazání plátna (Clear):** Tato funkce provede totální reset scény – vyprázdní seznam všech objektů (`shapes.clear()`) a následně vymaže rastr barvou pozadí. Tím se uvolní paměť a uživatel začíná s čistým plátnem.
---

## Ovládání a klávesové zkratky

| Klávesa | Akce |
| :--- | :--- |
| **L, P, R, O** | Přepínání nástrojů: Úsečka, Polygon, Obdélník, Elipsa |
| **S** | Aktivace režimu výběru a editace (Select) |
| **E** | Režim mazání (Erase) – odstraní objekt pod kurzorem |
| **C** | Vymazání celého plátna (Clear) |
| **Shift** | Ortogonální kreslení (45°) a symetrické tvary (čtverce, kruhy) |
| **Pravé tlačítko** | Ukončení sekvence bodů u nástroje Polygon |
