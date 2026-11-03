# Interactive farm edition

Run `sh scripts/web.sh` from the project root, then open http://127.0.0.1:8080.

## Architecture

- `src/FarmWebServer.java`: JDK HTTP server, query validation, JSON serialization and static serving.
- `src/Simulation.java`: existing Java model; defensive snapshots expose trial costs and counts.
- `web/index.html`, `web/style.css`, `web/app.js`: buildless responsive UI, Canvas sprites and browser report export.
- `web/assets/farm.png`: generated pixel-art farm background.

Each request creates its own farm and state, runs the existing model for guard counts 0–2 with `seed + guard count`, and returns all summary and per-trial values. The web adapter does not overwrite the CLI's report. It does not require a database, external API, npm package or Java framework.

## Interactions

Farm inputs are a draft until Run simulation is pressed. Results and exports retain their completed input snapshot; changing inputs displays a stale-results message. Selecting a guard card changes only playback, not the three-way comparison. Sliders keep the flock at a maximum of 100; zero total animals are rejected. Seeds use signed Java long integers and are transferred as strings to avoid JavaScript integer precision loss.

Animation traverses 10 independent trials. A fresh flock appears at each trial; the appropriate number of sheep, lambs and alpacas fades according to returned counts. The decorative fox, animal paths and event timing are illustrations. The engine does not return attack coordinates or time-ordered events. All chart bars are the actual 10 trial costs on a common zero-based scale.

## Verification

`sh scripts/test.sh` compiles for Java 17, runs the original regression suite, checks the web adapter, then verifies the seed-42 CLI report. Web adapter tests run without binding a network port. Browser and end-to-end HTTP checks require a running local server.

## Hosting

The application runs locally with a Java backend. Static-only hosts cannot execute the Java API. A public deployment requires Java-capable hosting or a separately validated portable implementation of the model.
