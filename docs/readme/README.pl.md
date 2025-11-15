# Recycler

**Języki:** [English](../README.md) · [Українська](README.uk.md) · [Русский](README.ru.md) · [Беларуская](README.be.md) · [Polski](README.pl.md) · [Eesti](README.et.md) · [Deutsch](README.de.md)

Przetwarzacz (Recycler) to lekka wtyczka Paper 1.21.9, która pozwala graczom zamieniać surowce na losowe nagrody z osobnymi czasami odnowienia dla każdego materiału.

## Funkcje

- `/recycle` otwiera jednorzędowy GUI **Przetwarzacza** z jedynym dostępnym slotem wejściowym nr 1.
- Każdy stos można wymienić na losową nagrodę blokową; jej ilość nie przekracza maksymalnego rozmiaru stosu wyrolowanego bloku.
- Wrzucenie **pełnego stosu** (dokładne maksimum i co najmniej 16 przedmiotów) zamienia rzut na losowy przedmiot zamiast bloku.
- Nagrody przedmiotowe mają 70% szans na otrzymanie zaklęć według tabeli wag (domyślnie 1–5 zaklęć z naciskiem na 2–3) oraz 5% szans na podwojenie, jeśli przedmiot można stackować.
- Materiały wysokiego tieru (netherite, diament, złoto, żelazo, kamień, kolczuga) korzystają z mnożników sprawiających, że potężny sprzęt wypada rzadziej i trudniej zdobyć ≥3 zaklęcia (netherite ≈ 10× rzadszy).
- Każdy materiał (wejście i nagroda) ma własny 5-minutowy cooldown, więc nie da się natychmiast powtórzyć tej samej kombinacji.
- GUI pozostaje otwarte po każdym rzucie, by można było nieprzerwanie dokładać stosy.
- Przedmioty pozostawione w środku zawsze wracają do gracza przy zamknięciu.

## Lokalizacja

- Dostępne locale: `en_us`, `uk_ua`, `ru_ru`, `be_by`, `pl_pl`, `et_ee`, `de_de`.
- Komunikaty znajdują się w `src/main/resources/lang/*.yml`; aby dodać język, skopiuj te same klucze i przetłumacz wartości.
- Gracze widzą komunikaty w języku klienta, jeśli dana locale istnieje.

## Budowanie

Uruchom Maven w katalogu głównym repozytorium:

```powershell
mvn package
```

Gotowy jar pojawi się w `target/recycler-1.0.0.jar`.

## Testowanie

- Aby uruchomić testy (JUnit 5 + Maven Surefire), użyj:

```powershell
mvn test
```

- Testy znajdują się w `src/test/java`. Logikę zależną od Bukkit warto wydzielać do zwykłych serwisów Java, by łatwiej je było testować bez serwera.
- Po każdej zmianie w kodzie lub testach ponownie uruchom `mvn test`, aby upewnić się, że projekt pozostaje zielony przed pakowaniem.

## Użycie

1. Skopiuj zbudowany JAR do folderu `plugins/` serwera.
2. Uruchom Paper 1.21.9 na Javie 17 (lub zgodnej wersji wymaganej przez serwer).
3. Wpisz `/recycle`, włóż żądany stos w slot 1 i odbierz nagrodę.
4. Jeśli cooldown wciąż trwa, pojawi się komunikat; przedmioty pozostawione w GUI wrócą do ekwipunku po zamknięciu.
