# Recycler

**Keeled:** [English](../README.md) · [Українська](README.uk.md) · [Русский](README.ru.md) · [Беларуская](README.be.md) · [Polski](README.pl.md) · [Eesti](README.et.md) · [Deutsch](README.de.md)

Taaskasutaja (Recycler) on kerge Paper 1.21.9 plugina, mis laseb mängijatel muuta toormaterjalid juhuslikeks auhindadeks, hoides iga materjali jaoks eraldi jahtumisaega.

## Võimalused

- `/recycle` avab üherealise **Taaskasutaja** GUI, kus ainult pesa 1 on sisendiks.
- Iga virn saab vahetada juhusliku plokiauhinna vastu; kogus ei ületa saadud ploki maksimaalset virnasuurust.
- Täpse **täisvirna** (maksimum ja vähemalt 16 eset) sisestamine muudab viske ploki asemel juhuslikuks esemeks.
- Esemepreemiad saavad 70% tõenäosusega loitsud kaalutud tabelist (vaikimisi 1–5 loitsu rõhuga 2–3) ning 5% tõenäosusega kahekordistuvad, kui ese on virnastatav.
- Kõrgema tieri materjalid (netheriidi, teemant, kuld, raud, kivi, rõngasrüü) kasutavad kordajaid, mis muudavad võimsad esemed haruldasemaks ja vähendavad ≥3 loitsu tõenäosust (netheriidi ≈ 10× harvem).
- Igal materjalil (sisend ja auhind) on oma 5-minutiline jahtumisaeg, seega ei saa sama kombinatsiooni kohe korrata.
- GUI jääb pärast iga viset avatuks, et saaksid pidevalt virnu lisada.
- GUI-sse jäänud esemed tagastatakse alati sulgemisel mängijale.

## Lokaliseering

- Sisseehitatud locale'id: `en_us`, `uk_ua`, `ru_ru`, `be_by`, `pl_pl`, `et_ee`, `de_de`.
- Sõnumid asuvad `src/main/resources/lang/*.yml`; lisa uus keel, kopeerides võtmed ja tõlkides väärtused.
- Mängijad näevad sõnumeid oma kliendi keeles, kui vastav locale on olemas.

| Locale | Kaanoniline nimi |
| --- | --- |
| `en_us` | Recycler |
| `uk_ua` | Перероблювач |
| `ru_ru` | Переработчик |
| `be_by` | Перапрацоўшчык |
| `pl_pl` | Przetwarzacz |
| `et_ee` | Taaskasutaja |
| `de_de` | Recyclinggerät |

## Ehitamine

Käivita Maven repositooriumi juurkaustas:

```powershell
mvn package
```

Valmis jar fail on `target/recycler-1.0.0.jar` kataloogis.

## Testimine

- Automatiseeritud testide (JUnit 5 + Maven Surefire) käivitamiseks kasuta:

```powershell
mvn test
```

- Testid paiknevad kaustas `src/test/java`. Hoia Bukkit-spetsiifiline loogika eraldatud tavalistesse Java-teenustesse, et neid saaks ilma serverita testida.
- Pärast muudatusi käivita `mvn test`, et veenduda rohelises olekus enne pakendamist.

## Kasutamine

1. Kopeeri valminud JAR serveri `plugins/` kausta.
2. Käivita Paper 1.21.9 Java 17 (või serveri nõutava ühilduva versiooniga).
3. Kasuta käsku `/recycle`, aseta soovitud virn pessa 1 ja kogu auhind.
4. Kui jahtumisaeg kestab, kuvatakse sõnum; GUI-sse jäänud esemed tagastatakse inventari sulgemisel.
