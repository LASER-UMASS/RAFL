# Rank Aggregation-based Fault Localization (RAFL)

This repository contains source code for Rank Aggregation-based Fault Localization (RAFL), an unsupervised technique to combine results of multiple fault localization techniques. 

If you use RAFL, please include the following citation:

Manish Motwani and Yuriy Brun, Better Automatic Program Repair by Using Bug Reports and Tests Together, in Proceedings of the 45th International Conference on Software Engineering (ICSE), 2023

## Installation
### Dependencies:
- Java version version 8
- R version version 4.1
- RankAggreg R package version 0.6.6 (https://cran.r-project.org/web/packages/RankAggreg/index.html)
- rJava R package version 1.0-5 (https://cran.r-project.org/web/packages/rJava/index.html)
- Defects4J version version 2.0.0 (https://github.com/rjust/defects4j) 

### How to run:
1. After cloaning this repository, specify the absolute path to the cloned repository as the `root_directory` in the [`rafl.settings`](https://github.com/LASER-UMASS/RAFL/blob/main/rafl.settings) file. 
2. Set `JAVA_HOME` to point to Java-8 installation directory (e.g., `export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/`)
3. Set `R_HOME` to point to R installation directory (e.g., `export R_HOME=/usr/lib/R/`)
4. Add JRI native library (available inside the directory where rJava is installed) to the default Java Library Path (depends on OS):

On Windows, it maps to PATH

On Linux, it maps to LD_LIBRARY_PATH

On OS X, it maps to DYLD_LIBRARY_PATH 

(e.g., use `export LD_LIBRARY_PATH=<path-to>/R/x86_64-pc-linux-gnu-library/4.1/rJava/jri` for Linux) 

5. RAFL accepts the following command line arguments:

- `<Defects4J project>_<bugid>` (e.g., `Chart_1` (to run RAFL on a particular Defects4J defect) or `all` (to run RAFL on all Defects4J defects))
- `NumOfFLTechniques` (the number of FL techniques' whose results are to be combined. This value should be at least 2)
Based on the value specified, the following arguments must specify the path to the files that store the FL results. 
For example, when combining the results of SBFL and Blues FL techniques, the next two arguments will be: 
- `<path-to-SBFL_results>` 
- `<path-to-Blues_results>`
- `seed`
- `sample size`

For example, to run RAFL on a Chart 1 defect in Defects4J, use the command:

`java -Djava.library.path=.:/usr/local/lib/R/site-library/rJava/jri/ -jar rafl.jar Chart_1 2 <path-to-SBFL_results>/chart/1/stmt-susps.txt <path-to-Blues_results>/chart/1/stmt-susps.txt 1 10000`

To run RAFL on all the 815 defects in Defects4J~v2.0, use the following command. 

`java -Djava.library.path=.:/usr/local/lib/R/site-library/rJava/jri/ -jar rafl.jar all 2 <path-to-SBFL_results> <path-to-Blues_results> 1 10000` 
(**Note:** Executing RAFL on all defects can take a long time as it combines SBFL with IRFL for 815 defects.)

## How to experiment using different configuration parameters

- Import the project in Eclipse and use the file `RAFL/src/main/Rafl.java` to run the program. 

- To experiment with different rank aggregation algorithms and distance metrics update the parameters 
in file [`RAFL/src/configuration/Config.java`](https://github.com/LASER-UMASS/RAFL/blob/main/src/configuration/Config.java). 
