# Alpaca Guard — Project Case Study

## Overview

Alpaca Guard is an interactive livestock protection simulator. A pixel-art browser interface connects to a Java engine to compare the estimated costs of keeping zero, one or two guard alpacas.

## Problem

Reducing predator losses introduces its own costs. The application makes this trade-off visible by evaluating livestock losses, fixed guard fees and maintenance under the same farm inputs.

## Experience

Users build a flock, select a state and run all three protection strategies. Comparison cards show mean costs, cost ranges and average losses. Selecting a card changes the farm playback; pause, replay and speed controls allow each trial to be explored. Text exports include the completed inputs, seed and strategy results.

## Implementation

| Component | Responsibility |
| --- | --- |
| Animal hierarchy | Encapsulates animal values and vulnerability through an abstract base class and three subclasses. |
| Simulation engine | Samples predator outcomes and maintenance costs across 10 trials per strategy. |
| Java HTTP adapter | Validates inputs, runs the existing engine and returns summary and trial data as JSON. |
| Browser interface | Renders responsive controls, comparison charts, Canvas animals and downloadable reports. |
| Verification | Checks deterministic replay, boundary cases, accounting, input validation and CLI/web consistency. |

The frontend has no package dependencies. The backend uses the JDK HTTP server and runs locally on the loopback interface.

## Reproducible result

With 80 sheep, 20 lambs in VIC and seed 42, mean costs are $3,220.00, $2,900.82 and $2,722.28 for zero, one and two guards. The two-guard strategy has the lowest sample mean, $497.72 below the no-guard strategy. These values describe this simulated sample rather than measured farm savings.

## Design decisions

- **One calculation engine:** both the CLI and browser use the same Java model.
- **Seeded experiments:** identical inputs reproduce identical results.
- **Independent requests:** each web request creates its own farm and state.
- **Result snapshots:** editing draft inputs does not silently alter completed results or exports.
- **Illustrative playback:** returned trial loss counts drive the animation; movement and timing are decorative.
- **Small runtime footprint:** Java 17 and a browser are sufficient to run the application.

## Constraints and future development

The supplied factors and prices are educational assumptions without field validation. Trial count is fixed at 10 and uncertainty is not quantified. Predator order and fixed protection multipliers simplify real behaviour.

Further development includes configurable trial counts, confidence intervals, sensitivity analysis, CSV export and Java-capable public hosting.

## References

[Application overview](../README.md) · [Model rules](MODEL.md) · [Web architecture](WEB.md) · [中文介紹](PROJECT_ZH.md)
