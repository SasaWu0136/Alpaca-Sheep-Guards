# Alpaca Guard Java updater

`AlpacaGuardWebUpdate.java` is a standalone source-file installer for the web edition. Run it from the Alpaca-Sheep-Guards repository root with JDK 17 or newer:

```sh
java tools/AlpacaGuardWebUpdate.java --check
java tools/AlpacaGuardWebUpdate.java
```

No separate compilation, Python installation, dependency download, network connection or API key is required. The web assets and update manifest are embedded in the Java source.

The target checkout must already contain the portfolio source layout (`src/Simulation.java` and `data/predators.txt`). The tool verifies expected file hashes before applying changes. It refuses to overwrite custom runtime files. If the matching web runtime is already installed, it preserves current documentation and exits without modifying it.

Replaced files are backed up in `.alpaca-web-backups/`. Existing original-project archives are preserved. `--check` validates without writing; the tool does not create commits, push branches or deploy a website.

The embedded update is a fixed installer snapshot. Normal maintenance of an installed checkout should use Git; editing application files does not automatically rebuild this embedded package.
