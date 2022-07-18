# RAFL
This repository contains source code for Rank Aggregation-based Fault Localization (RAFL), an unsupervised technique to combine results of multiple fault localization techniques. 

Dependencies:
- Java version version 1.8
- R version version 4.1
- RankAggreg R package version 0.6.6 (https://cran.r-project.org/web/packages/RankAggreg/index.html)
- rJava R package version 1.0-5 (https://cran.r-project.org/web/packages/rJava/index.html)
- Defects4J version version 2.0.0 (https://github.com/rjust/defects4j) 


Before running the main program do the following:
1. Specify the path to the cloned repository as the `root_directory` in the `rafl.settings` file. 
2. Set `JAVA_HOME` to point to Java installation directory (e.g., `export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/`)
3. Set `R_HOME` to point to R installation directory (e.g., `export R_HOME=/usr/lib/R/`)
4. Add JRI native library (available inside the directory where rJava is installed) to the default Java Library Path (depends on OS):

On Windows, it maps to PATH

On Linux, it maps to LD_LIBRARY_PATH

On OS X, it maps to DYLD_LIBRARY_PATH 

(e.g., `export LD_LIBRARY_PATH=<path-to>/R/x86_64-pc-linux-gnu-library/4.1/rJava/jri`) 


RAFL accepts the following command line arguments:

1. `Defect <String>` (e.g., "Chart_1" (to run RAFL on a particular Defects4J defect) or "all" (to run RAFL on all Defects4J defects))
2. `NumOfFlTechniques <Integer>` (the number of FL techniques' results to be combined. This value should be at least 2)
Based on the value specified, the subsequent arguments must specify the path to the files that store the FL results. For example, when combining SBFL and IRFL techniques, the next two arguments will specify: 
3. `Path <String>` to SBFL results (e.g., localized_statements/RAFL_input/sbfl/chart/1/stmt-susps.txt)
4. `Path <String>` to IRFL results (e.g., localized_statements/RAFL_input/blues/blues_m1_stmts/chart/1/stmt-susps.txt)
5. `seed`
6. `sample size (N)`

To run RAFL on a single defect in Defects4J, use the command:

`java -Djava.library.path=.:/usr/local/lib/R/site-library/rJava/jri/ -jar rafl.jar Chart_1 2 <path-to>RAFL/localized_statements/RAFL_input/blues_maxscore_voting/chart/1/stmt-susps.txt <path-to>/RAFL/localized_statements/RAFL_input/sbfl/chart/1/stmt-susps.txt 1 10000`

To run RAFL on all defects in Defects4J, use the command (uses seed 1 and sample size 10,000):

`java -Djava.library.path=.:/usr/local/lib/R/site-library/rJava/jri/ -jar rafl.jar all` 
(Note that executing RAFL on all defects can take a long time as it combines SBFL with IRFL for 815 defects.)

Alternatively, import the project in Eclipse and use the file `RAFL/src/main/Rafl.java` to run the program. 
To experiment with different rank aggregation algorithms and distance metrics used by RAFL, update the parameters in file `RAFL/src/configuration/Config.java`
