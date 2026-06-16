# Upute za pokretanje projekta SeriesManager

**SeriesManager** je JavaFX desktop aplikacija za upravljanje serijama, korisnicima, glumcima, redateljima i korisnickom watchlistom. Aplikacija koristi PostgreSQL bazu podataka, Maven za dependency-je i JavaFX za graficko sucelje.

## 1. Potrebni programi

Prije pokretanja potrebno je instalirati:

- **JDK 21**
- **Apache Maven**
- **PostgreSQL**

Projekt je radjen kao Maven projekt. JavaFX, PostgreSQL driver, Jackson XML i Logback dependency-ji definirani su u `pom.xml`.

## 2. Vazno o lozinkama

Postoje dvije razlicite vrste korisnika i lozinki:

- PostgreSQL korisnik i lozinka
- korisnici unutar SeriesManager aplikacije

`DB_USER` i `DB_PASSWORD` su podaci za spajanje na PostgreSQL bazu na racunalu osobe koja pokrece projekt.

Primjer:

```text
Ako na svome racunalu imate PostgreSQL korisnika:
username: postgres
password: 1234

onda environment varijable kod vas trebaju biti:
DB_USER=postgres
DB_PASSWORD=1234
```

Korisnici aplikacije su druga stvar. To su korisnicki racuni s kojima se korisnik prijavljuje u samu SeriesManager aplikaciju nakon pokretanja.

Ovo su lozinke u bazi nakon SQL dump-a:

```text
Administrator:
username: admin
password: admin123

Korisnik:
username: macak
password: macak1
```

## 3. Postavljanje PostgreSQL baze

U PostgreSQL-u je potrebno napraviti praznu bazu podataka s nazivom:

```text
series_manager_db
```

Primjer kroz `psql`:

```sql
CREATE DATABASE series_manager_db;
```

Nakon toga treba pokrenuti SQL dump:

```text
src/main/resources/SQL/create_database_full.sql
```

Ova skripta je kompletan dump baze. Ona kreira:

- tablice
- sekvence
- primarne i strane kljuceve
- unique constraint-e
- procedure
- funkcije
- pocetne podatke
- korisnike aplikacije

Zbog toga nije potrebno posebno pokretati `createtable.sql`.

Vazno: bazu treba napraviti praznu i onda pokrenuti `create_database_full.sql`. Ako se prvo pokrene `createtable.sql`, a zatim `create_database_full.sql`, mogu se pojaviti greske jer tablice i procedure vec postoje.

## 4. Pokretanje SQL dumpa kroz terminal

Otvoriti terminal u root direktoriju projekta.

Primjer:

```powershell
cd putanja/do/SeriesManager
```

Pokrenuti dump:

```powershell
psql -U postgres -d series_manager_db -f src/main/resources/SQL/create_database_full.sql
```

Ako PostgreSQL korisnik nije `postgres`, umjesto `postgres` treba upisati korisnicko ime koje postoji na tom racunalu.

Primjer:

```powershell
psql -U moj_korisnik -d series_manager_db -f src/main/resources/SQL/create_database_full.sql
```

Nakon pokretanja komande, `psql` moze traziti PostgreSQL lozinku. Tu korisnik upisuje lozinku svog PostgreSQL korisnika.

## 5. Pokretanje SQL dumpa kroz pgAdmin

Ako se koristi pgAdmin:

1. Otvoriti pgAdmin.
2. Napraviti bazu `series_manager_db`.
3. Desni klik na bazu `series_manager_db`.
4. Odabrati **Query Tool**.
5. Otvoriti datoteku `src/main/resources/SQL/create_database_full.sql`.
6. Pokrenuti cijelu skriptu.

Nakon izvrsavanja skripte baza treba sadrzavati sve tablice, funkcije i procedure potrebne za rad aplikacije.

## 6. Konfiguracija spajanja na bazu

Datoteka `config.xml` u root direktoriju projekta sadrzi JDBC adresu baze:

```xml
<dbUrl>jdbc:postgresql://localhost:5432/series_manager_db</dbUrl>
```

Ako je PostgreSQL na drugom portu ili drugom racunalu, potrebno je promijeniti ovu vrijednost.

Aplikacija korisnicko ime i lozinku PostgreSQL baze cita iz environment varijabli:

```text
DB_USER
DB_PASSWORD
```

Primjer za Windows PowerShell:

```powershell
$env:DB_USER="postgres"
$env:DB_PASSWORD="lozinka_od_postgres_korisnika"
```

Ako korisnik ima drugu lozinku, upisuje svoju lozinku. Ne upisuje moju lokalnu PostgreSQL lozinku.

## 7. Pokretanje preko IntelliJ IDEA

1. Otvoriti IntelliJ IDEA.
2. Odabrati **Open** i otvoriti root direktorij projekta.
3. Pricekati da IntelliJ ucita Maven dependency-je iz `pom.xml`.
4. Provjeriti da projekt koristi **JDK 21**.
5. Otvoriti **Run > Edit Configurations**.
6. U environment variables dodati `DB_USER` i `DB_PASSWORD`.
7. Pokrenuti klasu `src/main/java/hr/algebra/main/Main.java`.

Primjer environment varijabli u IntelliJ IDEA:

```text
DB_USER=postgres;DB_PASSWORD=lozinka_od_postgres_korisnika
```

Ako profesorov PostgreSQL korisnik nije `postgres`, treba upisati svog korisnika.

Primjer:

```text
DB_USER=moj_korisnik;DB_PASSWORD=moja_postgres_lozinka
```

Ako IDE ponudi Maven/JavaFX run konfiguraciju, moze se koristiti i ona.

## 8. Pokretanje preko terminala

U terminalu se treba pozicionirati u root direktorij projekta:

```powershell
cd putanja/do/SeriesManager
```

Zatim postaviti environment varijable:

```powershell
$env:DB_USER="postgres"
$env:DB_PASSWORD="lozinka_od_postgres_korisnika"
```

Pokretanje aplikacije:

```powershell
mvn clean javafx:run
```

Maven ce automatski preuzeti potrebne dependency-je definirane u `pom.xml`.

## 9. Prijava u aplikaciju

Nakon pokretanja aplikacije postoji mogucnost prijave korisnikom koji postoji u bazi.

```text
Administrator:
username: admin
password: admin123

Korisnik:
username: macak
password: macak1
```

Ovo su podaci za prijavu u aplikaciju SeriesManager.

## 10. Uvoz podataka

Aplikacija koristi XML podatke za import serija. Za uvoz je potrebna internetska veza jer se podaci citaju s udaljenog XML izvora.

Ako internet nije dostupan, osnovna aplikacija se i dalje moze pokrenuti, ali uvoz vanjskih podataka nece raditi.

## 11. Ceste greske

Ako se pojavi greska da `DB_USER` ili `DB_PASSWORD` nisu postavljeni, potrebno je provjeriti environment varijable.

Ako se aplikacija ne moze spojiti na bazu, provjeriti:

- radi li PostgreSQL servis
- postoji li baza `series_manager_db`
- je li pokrenuta skripta `create_database_full.sql`
- odgovara li port u `config.xml`
- jesu li `DB_USER` i `DB_PASSWORD` tocni za PostgreSQL korisnika na tom racunalu

Ako se prilikom pokretanja `create_database_full.sql` pojavi greska da objekt vec postoji, baza vjerojatno nije bila prazna. U tom slucaju treba napraviti novu praznu bazu ili obrisati postojece objekte pa ponovno pokrenuti skriptu.

Ako Maven javlja gresku za Java verziju, potrebno je provjeriti da je instaliran i odabran **JDK 21**.

## 12. Napomena o PostgreSQL verziji

Dump `create_database_full.sql` napravljen je iz PostgreSQL baze verzije 18.3.

Ako se koristi starija PostgreSQL verzija i dobije se greska na naredbama kao sto su:

```text
\restrict
\unrestrict
SET transaction_timeout = 0;
```

te linije se mogu ukloniti iz `create_database_full.sql`, zatim ponovno pokrenuti skriptu.

## 13. Napomena

Direktorij `assets` treba ostati uz projekt jer se u njemu nalaze slike koje aplikacija koristi.

Datoteka `user_activity.xml` sluzi za spremanje aktivnosti korisnika i nalazi se u root direktoriju projekta.

Ukoliko postoje određeni problemi prilikom postavljanja aplikacije,
slobodno se možete javiti na brodic@algebra.hr.
