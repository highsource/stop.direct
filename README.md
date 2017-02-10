# Stop.Direct

Shows departure board of the closest stop in Germany.

# Usage

## Start

```
java -jar stopdirect-api-<VERSION>.jar 
```

## Endpoints

TBD

* http://localhost:5000/stop/{lon}/{lat} - given the coordinates, search for the nearest stop.
* http://localhost:5000/stops/{lon}/{lat} - given the coordinates, search for the nearest stop.

# Data

Incorporates [stops data of DB Station&Service AG](http://data.deutschebahn.com/dataset/data-haltestellen), license [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).

> **Lizenzbeschreibung:** Dieser Datensatz wird bereitgestellt unter der Lizenz Creative Commons Attribution 4.0 International (CC BY 4.0). Wenn die Daten der Deutschen Bahn (DB) Bestandteil des OpenStreetMap-Datenbankwerkes werden, genügt eine Nennung der Deutschen Bahn AG in der Liste der Beitragenden. Eine Nennung der DB bei jeder Verwendung der Daten auch durch Lizenznehmer des oben genannten Datenbankwerks ist dann nicht mehr erforderlich. Eine indirekte Nennung (Verweis auf Herausgeber des Datenbankwerks, der wiederum auf die DB verweist) genügt.

> **Haftungsausschluss:** Übersicht Haltestellen DB Station&Service AG Dieser Datenbestand kann Fehler enthalten und/oder unvollständig sein. DB Station&Service AG übernimmt keine Haftung und leistet keinerlei Gewähr.

# License

Code [MIT License](LICENSE).

Data:

* Stops Data of DB Station&Service AG - [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/) (please see [dataset info](http://data.deutschebahn.com/dataset/data-haltestellen)).
* Other included datasets do not have clear licenses.