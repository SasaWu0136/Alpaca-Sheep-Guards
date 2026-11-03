# Portfolio case study

## Alpaca Sheep Guards — Livestock Protection Cost Simulator

**Type:** Java command-line simulation, extended from an existing learning project.
**Stack:** Java 17+, Java Collections, Random, text file I/O, shell scripts, GitHub Actions.

### Problem
Guard animals reduce modelled predator losses but introduce their own costs. The project makes this trade-off visible by comparing three protection scenarios for the same flock.

### Implementation
The domain uses an abstract Animal class with sheep, lamb and alpaca subclasses. A simulation service samples predator outcomes and maintenance costs across 10 trials per strategy. The CLI compares cost summaries and exports the selected strategy.

### Evidence
With 80 sheep, 20 lambs in VIC and seed 42, the observed mean costs are 3220.00, 2900.82 and 2722.28 for zero, one and two guards respectively. This is a reproducible example, not measured real-world savings.

### Quality and trade-offs
The portfolio edition fixes compilation blockers, makes randomness injectable, rejects invalid probability data and introduces automatic regression checks. It retains the original small model so the logic can be inspected without a framework. More trials, uncertainty reporting and validated data are needed for practical decisions.

### Suggested CV entry
**Alpaca Sheep Guards | Java, OOP, Stochastic Simulation**
- Modelled livestock and predator interactions using an abstract animal hierarchy and state-specific input data.
- Compared three guard strategies through repeated simulations, reporting estimated costs, animal losses and predator attribution.
- Extended the project with reproducible seeded runs, regression checks and GitHub Actions verification.

Use these bullets only for work you can explain and accurately attribute. Do not claim production deployment, real farm adoption, AI functionality or verified cost reductions.

### Two-minute demo
1. Explain the cost-versus-loss question.
2. Run `sh scripts/run.sh 42`; enter Sample Farm, VIC, 80 and 20.
3. Compare the three result blocks and open the exported report.
4. Show the animal hierarchy and the cost formula.
5. Run `sh scripts/test.sh`.
6. Explain the 10-trial limitation and the next step of uncertainty analysis.
