<div align="center">

# 🦙 Alpaca Guard

### A little farm. A few good guards. A decision backed by simulation.

**An interactive pixel-art farm powered by a Java livestock protection simulator.**

[Get started](#run-it-locally) · [Model details](docs/MODEL.md) · [中文介紹](docs/PROJECT_ZH.md) · [Architecture](docs/WEB.md)

![Pixel-art Australian farm background used in Alpaca Guard](web/assets/farm.png)

*Farm artwork used in the playground. Animals and interface controls are rendered separately.*

</div>

## Can you keep your flock safe?

Guard alpacas can reduce livestock losses, but they also cost money to keep. Alpaca Guard turns that trade-off into an explorable farm: choose a flock, select an Australian state, and compare the estimated cost of **zero, one or two guard alpacas**.

The project began as a Java learning exercise. This edition adds a browser playground, reproducible runs, automated checks and a clearer separation between simulation and presentation.

## Explore the farm

| Feature | What you can do |
| --- | --- |
| Build a flock | Adjust sheep and lambs with sliders, up to 100 animals in total. |
| Pick a location | Explore supplied predator factors for VIC, NSW, SA and WA. |
| Compare protection | See mean costs, cost ranges and average losses for all three strategies. |
| Watch trial playback | Preview each strategy across 10 independent trials with animated Canvas animals. |
| Control the replay | Pause, replay, or watch at 1×, 2× or 4× speed. |
| Investigate predators | Inspect average losses attributed to foxes, dingoes, feral pigs and eagles. |
| Repeat an experiment | Use the same seed to reproduce the same Java results. |
| Export the comparison | Download every strategy, trial costs and the seed in a text report. |

The layout adapts to desktop and phone screens. Controls support keyboard use. Reduced-motion preferences disable automatic playback and animal wandering.

## Run it locally

**Requirements:** JDK 17 or newer, Git, and a modern browser. Shell scripts work on macOS and Linux. No npm packages, database or external API key are required.

```sh
git clone https://github.com/SasaWu0136/Alpaca-Sheep-Guards.git
cd Alpaca-Sheep-Guards
sh scripts/web.sh
```

Open **http://127.0.0.1:8080**. Keep the terminal running and press `Ctrl+C` to stop.

If port 8080 is busy, run `sh scripts/web.sh 8081` and open http://127.0.0.1:8081 instead. The server listens on your computer's loopback address. A hosted public demo is not currently included.

### Windows / PowerShell

```powershell
New-Item -ItemType Directory -Force build
javac --release 17 -encoding UTF-8 -d build src/*.java
java -cp build FarmWebServer
```

Run commands from the repository root so the application can find `web/` and `data/predators.txt`.

### Prefer the terminal?

```sh
sh scripts/run.sh 42
```

Enter `Sample Farm`, `VIC`, `80`, and `20`. Omit `42` for a fresh seed, which is printed at startup. The CLI writes to `output/alpacaSheepGuardViability.txt`; web reports download through your browser.

## A repeatable example

**Sample Farm · VIC · 80 sheep · 20 lambs · seed 42 · 10 trials per strategy**

| Guard alpacas | Mean total cost | Mean sheep lost | Mean lambs lost |
| --- | ---: | ---: | ---: |
| 0 | $3,220.00 | 11.8 | 5.8 |
| 1 | $2,900.82 | 7.0 | 3.4 |
| 2 | **$2,722.28** | **3.3** | **0.8** |

Two alpacas produce the lowest observed mean cost in this sample: **$497.72 below the no-guard strategy**. Another seed, flock or state can change the outcome. Costs use AUD model units and inherited price assumptions.

## Under the hood

The browser sends farm inputs to a local JDK HTTP server. After validation, the server runs the existing `Simulation` class for each guard count and returns summary statistics and per-trial values as JSON. The browser draws charts, replays the results and exports the report.

| Layer | Implementation |
| --- | --- |
| Domain | Abstract `Animal` class, `Sheep`, `Lamb` and `Alpaca` subclasses; `Farm`, `State` and `Predator`. |
| Simulation | Java `Random`, repeated trials, loss attribution and cost aggregation. |
| Web adapter | JDK `HttpServer`, validated inputs, JSON serialization and static serving. |
| Interface | HTML, CSS, vanilla JavaScript and Canvas pixel sprites. |
| Data | Supplied text file with state-specific predator factors. |
| Verification | Dependency-free Java checks and GitHub Actions on Java 17. |

The web edition preserves the CLI's cost model and random sequence. Defensive copies expose trial snapshots. Each request has its own farm and state; web exports do not overwrite the CLI report.

## What the model assumes

```text
Total model cost = animal losses + fixed guard fees + guard maintenance
```

- Sheep are valued at 150, lambs at 250 and alpacas at 1,000.
- Each guard adds a 500 fixed fee and maintenance in [400, 600); maintenance is halved if that guard dies.
- One guard multiplies modelled risk by 0.5; two multiply it by 0.25.
- Predators are evaluated in a fixed order. Each animal is counted as lost at most once.
- The lowest sample mean wins; ties favour fewer guards.

These are **educational assumptions inherited from the original project**, with no field validation or defined trial duration. Ten trials do not establish reliable real-world savings. See [MODEL.md](docs/MODEL.md) for the full rules and limitations.

Animation uses actual per-trial loss counts. Positions, movement, the decorative fox and timing are illustrative. Each trial begins with a fresh flock; playback does not represent consecutive days or a time-ordered attack log.

## Check the project

```sh
sh scripts/test.sh
```

The suite currently runs **23 simulation regression checks**, **21 web adapter checks**, and a seeded CLI/report export check. It covers reproducibility, risk boundaries, cost and kill accounting, malformed input, JSON escaping and web/CLI consistency. Adapter tests run without opening a network port.

GitHub Actions runs the same command on Java 17 for pushes and pull requests. Browser layout and end-to-end HTTP behaviour are separate from these checks.

## Repository map

| Path | Contents |
| --- | --- |
| `src/` | Java domain classes, simulation, CLI and HTTP adapter. |
| `web/` | Browser interface, Canvas rendering and farm artwork. |
| `data/` | Supplied state and predator factors. |
| `scripts/` | Web, CLI and test entry points. |
| `tests/` | Simulation and web adapter checks. |
| `docs/` | Model, architecture, Chinese introduction and project case study. |
| `archive/` | Original source and assessment materials preserved for provenance. |
| `build/`, `output/` | Generated files, excluded from version control. |

## Where it could go next

- Configurable trial counts and confidence intervals.
- Sensitivity analysis for fees, livestock values and predator factors.
- CSV export and comparison history.
- Time-ordered model events for more faithful animation.
- Java-capable hosting for a public demo.

These are planned improvements. The current application runs locally with supplied data.

## Credits

Original Java author: **Hsin-i Wu**. Author attribution is retained in the source and archived snapshot. The pixel-art landscape was generated with AI for this interface; animal sprites and controls are rendered in code.

[中文介紹](docs/PROJECT_ZH.md) · [Project case study](docs/PORTFOLIO.md) · [Model details](docs/MODEL.md) · [Web architecture](docs/WEB.md)
