/*
# MIT License
#
# Copyright (c) 2021 LASER-UMASS
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
# ==============================================================================
*/

package mmotwani.rafl.configuration;

public class Config {

	public static String RScriptDirPath = null; // absolute path to the directory storing the intermediate R script and logs files
	public static String rootDirectory = null;  // absolute path to the cloned repository
	public static String resultDirectory = null; // absolute path to the output directory
	public static String defectsFilePath = null;	// path to the d4j-defects.txt file storing 815 defects in Defects4J~v2.0
	public static final int k = 100; // size of the combined list
	public static int seed = 1; // seed specified for reproducibility
	public static final String distance = "Spearman"; // distance metric (use Spearman or Kendall)
	public static final String method = "CE"; // algorithm (use CE for cross entropy monte carlo or GA for genetic algorithm)
	public static final int maxIter = 1000; // max #iterations allowed (default 1000)
	public static final int convIn = 7;  // #consecutive iterations to decide if algorithm has converged (default: 7 for CE, 30 for GA)
	public static int N = 10000; // #samples generated in each iteration. Used only by the CE (default: 10kn, where n is the #unique statements considering all ranked lists)
	public static final Double rho = 0.01; // (ρ · N ) is quantile of candidate lists sorted by the objective function scores. Used only by the CE. (default: 0.01 when N ≥ 100 and 0.1 otherwise)
	public static final int popSize = 100; // population size in each generation for the GA (default 100)
	public static final Double CP = 0.4;  // Cross-over probability for the GA (default 0.4)
	public static final Double MP = 0.01; // Mutation probability for the GA
		
	public static void setParameters(String rootDirectory){
		defectsFilePath = rootDirectory + "/d4j-defects.txt";
		RScriptDirPath = rootDirectory + "/R_scripts";
		resultDirectory = rootDirectory + "/RAFL_results";
	}
}
