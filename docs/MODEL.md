# Model and architecture

## Flow and responsibilities

1. `AlpacaSheepGuards` validates CLI input and orchestrates the comparison.
2. `FileIO` reads state/predator factors from `data/predators.txt`.
3. `Farm`, `State`, `Predator` and the `Animal` subclasses model the domain.
4. `Simulation` creates a fresh population for each of 10 trials per strategy.
5. The CLI compares mean costs and `FileIO` exports the selected strategy.

## Assumptions inherited from the original

| Parameter | Value |
| --- | --- |
| Sheep loss valuation | 150 |
| Lamb loss valuation | 250 |
| Alpaca loss valuation | 1000 |
| Fixed guard cost | 500 per alpaca per trial |
| Maintenance | Uniform random draw from [400, 600) per alpaca |
| Maintenance if a guard dies | 50% |
| Sheep vulnerability | 1 × predator factor |
| Lamb vulnerability | 2 × predator factor |
| Alpaca vulnerability | 0.01 × predator factor |
| Protection multiplier | 1 / 0.5 / 0.25 for 0 / 1 / 2 guards |

Each animal encounters predators in file-column order: fox, dingo, feral pig, wedge-tailed eagle. A random draw below the adjusted death probability kills the animal; remaining predators then skip it. Each death is attributed once. Protection applies to the full trial, including guard alpacas, and does not change when an alpaca dies.

`total cost = livestock loss + alpaca loss + fixed guard cost + maintenance`

Trials have no explicit duration. Cost units are presented as AUD for demonstration; figures are inherited assumptions and do not represent verified current prices. Factors come from the original repository, which supplies no external validation or data citation.

For danger factors up to 1, a lamb's adjusted risk may exceed 1. The existing comparison then implies certain loss. This is inherited behaviour and should be reconsidered if the model becomes configurable.

## Reproducibility and interpretation

The default is 10 trials for each guard count. An optional seed produces independent reproducible pseudorandom sequences using `seed + guard count`. These are not paired trials or common random numbers across strategies.

The recommendation is the strategy with the smallest observed sample mean; ties select the lower guard count. Ten trials provide a demonstration, not a reliable estimate of uncertainty. There are no confidence intervals or proven savings claims. Predator ordering affects attribution; fixed protection and population limits simplify the real system.

## Changes in this portfolio edition

- Repaired the malformed `Animal.setKilledBy` body and unresolved animal-type constants.
- Corrected the cause-of-death constructor to match its documented dead status.
- Added seeded randomness without changing the original default 10-trial model.
- Guarded integer input against overflow and corrected the inclusive 100-animal boundary message.
- Made invalid/missing predator data and report write failures explicit.
- Organized runnable code, data, tests, generated output and the original snapshot.
- Added regression checks, a reproducible CLI demo and Java 17 CI.

## Remaining technical debt

Domain constructors and setters do not consistently enforce invariants; interactive validation remains the principal guard for farms. Predator parsing validates probability values and field count but does not enforce a complete unique set of state rows. Statistics use mutable arrays and predator state. Trial count and cost constants remain hard-coded. Export contains only the selected strategy and does not yet include the seed or full input provenance.
