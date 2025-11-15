# Recycler

**Sprachen:** [English](../README.md) · [Українська](README.uk.md) · [Русский](README.ru.md) · [Беларуская](README.be.md) · [Polski](README.pl.md) · [Eesti](README.et.md) · [Deutsch](README.de.md)

Das Recyclinggerät (Recycler) ist ein leichtes Paper-1.21.9-Plugin, mit dem Spieler Rohmaterialien in zufällige Belohnungen mit individuellen Abklingzeiten je Material umwandeln können.

## Funktionen

- `/recycle` öffnet ein einreihiges **Recyclinggerät**-GUI, in dem nur Slot 1 als Eingabe verfügbar ist.
- Jeder Stack lässt sich gegen eine zufällige Blockbelohnung eintauschen; deren Menge überschreitet nie die maximale Stackgröße des gezogenen Blocks.
- Ein **voller Stack** (exakter Maximalwert und mindestens 16 Items) verwandelt den Wurf in eine zufällige Itembelohnung statt eines Blocks.
- Itembelohnungen haben 70 % Chance auf Verzauberungen aus einer gewichteten Tabelle (standardmäßig 1–5 Verzauberungen mit Fokus auf 2–3) und 5 % Chance auf Verdopplung, wenn das Item stapelbar ist.
- Hochwertige Materialien (Netherit, Diamant, Gold, Eisen, Stein, Kettenrüstung) nutzen Multiplikatoren, die mächtige Ausrüstung seltener machen und ≥3 Verzauberungen erschweren (Netherit ≈ zehnmal seltener als neutrale Drops).
- Jedes Material (Input und Belohnung) besitzt einen eigenen 5-Minuten-Cooldown, daher kann man Kombinationen nicht sofort wiederholen.
- Das GUI bleibt nach jedem Wurf geöffnet, sodass weitere Stacks ohne Unterbrechung eingezahlt werden können.
- Zurückgelassene Items werden beim Schließen automatisch an den Spieler zurückgegeben.

## Lokalisierung

- Unterstützte Locales: `en_us`, `uk_ua`, `ru_ru`, `be_by`, `pl_pl`, `et_ee`, `de_de`.
- Nachrichten liegen unter `src/main/resources/lang/*.yml`; füge neue Sprachen hinzu, indem du die Schlüssel kopierst und übersetzt.
- Spieler sehen Nachrichten in der Sprache ihres Clients, sofern ein passendes Locale existiert.

## Build

Führe Maven im Repository-Stamm aus:

```powershell
mvn package
```

Das fertige Jar liegt anschließend unter `target/recycler-1.0.0.jar`.

## Tests

- Um die automatisierten Tests (JUnit 5 + Maven Surefire) zu starten, verwende:

```powershell
mvn test
```

- Tests liegen in `src/test/java`. Lagere Bukkit-spezifische Logik in normale Java-Services aus, damit sie ohne Server testbar sind.
- Nach Änderungen stets `mvn test` laufen lassen, bevor du das Plugin packst.

## Verwendung

1. Kopiere das gebaute JAR in den `plugins/`-Ordner deines Servers.
2. Starte Paper 1.21.9 unter Java 17 (oder einer kompatiblen Version laut Server-Anforderung).
3. Nutze `/recycle`, lege den gewünschten Stack in Slot 1 und kassiere die Belohnung.
4. Läuft noch ein Cooldown, erscheint eine Meldung; Items, die im GUI verbleiben, landen beim Schließen wieder im Inventar.
