# CSV Processing

A Java command-line tool that turns a client CSV file into JSON, enriches one client with data from several sources, and renders the result as a Markdown report. It started as a take-home midterm exercise and covers CSV parsing, JSON output, REST lookups, a SQL query and basic logging.

## Stack

- Java 17, built with Maven
- OpenCSV for reading the CSV
- Gson and json-simple for JSON
- Log4j 2 for logging
- MariaDB JDBC driver for the demographics query
- Public web APIs: zippopotam.us (ZIP code to latitude/longitude) and floatrates.com (currency exchange rates)

## Data

`clients.csv` is a small extract of the public "US-500" sample dataset (fake names, companies and addresses) with an added `risk_factor` column. `data.csv` is an unrelated statistics file kept in the repo and is not read by the code.

## Running

All commands run from the `CSVProcessing/` subdirectory (the one containing `pom.xml`), because the program opens `clients.csv` from the working directory.

```
cd CSVProcessing
mvn compile
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:$(cat cp.txt)" main query <outFile.json> clients.csv <clientNumber> <currencyCode>
java -cp "target/classes:$(cat cp.txt)" main report <outFile.json>
```

On Windows use `;` instead of `:` in the classpath.

- `query` converts `clients.csv` to `clients.json`, looks up the client with the given number, fetches that client's latitude/longitude from the ZIP code API, queries the demographics table in a MariaDB database, writes everything to `<outFile.json>`, and prints the exchange rate for the currency code (for example `aud` or `jpy`). Example: `query jim.json clients.csv 730 aud`.
- `report` reads the JSON file produced by `query` and writes `clientreports.md`.

### Database credentials

The demographics lookup connects to a MariaDB server hosted by the course this was written for, which is not publicly reachable. The password is read from a `cscPassword.txt` file in the working directory; that file is not in the repository (it is git-ignored). Without access to that database the `query` command will exit at the SQL step, so only the CSV, JSON, ZIP and currency parts can be exercised as-is.

## Project layout

- `CSVProcessing/src/main/java/main.java` - argument handling, CSV/JSON conversion, API and SQL lookups, report writer
- `CSVProcessing/src/main/java/Client.java` - client record built from a CSV row
- `CSVProcessing/jim.json`, `CSVProcessing/clientreports.md` - sample output from a `query` and `report` run

## Status

Coursework, not actively developed. There are no automated tests and the argument handling does not validate input.
