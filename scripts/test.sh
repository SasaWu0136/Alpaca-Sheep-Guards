#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
mkdir -p build
javac --release 17 -encoding UTF-8 -d build src/*.java tests/*.java
java -cp build RegressionTest
java -cp build WebAdapterTest
printf 'Sample Farm\nVIC\n80\n20\n' | java -Duser.language=en -Duser.country=AU -cp build AlpacaSheepGuards 42 > build/demo.txt
test -s output/alpacaSheepGuardViability.txt
grep -Fq 'Estimated total cost: $2722.28' output/alpacaSheepGuardViability.txt
printf 'PASS: seeded CLI and report export\n'
