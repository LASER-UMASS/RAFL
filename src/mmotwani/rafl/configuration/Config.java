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

package configuration;

public class Config {

	
	public static String RScriptDirPath = null;
	public static String rootDirectory = null;
	public static String resultDirectory = null;

	// parameters used for results computed using Blues for D4J
	public static String raflInputFLResultsDir = null;
	public static String defectsFilePath = null;
	public static final int All = 10000;
	public static final int[] m = new int[]{1, 25, 50, 100, All};
	public static final String[] scoringStrategy = new String[] {"high", "wted"};
	
	// parameters used by RankAggreg R
	public static final int k = 100;
	public static int seed = 1;
	public static final String distance = "Spearman";
	public static final String method = "CE";
	public static final int maxIter = 1000;
	public static final int convIn = 7;
	public static final Double rho = 0.01;
	public static final int popSize = 100;
	public static final Double CP = 0.4;
	public static final Double MP = 0.01;
	public static int N = 10000;
	
	public static void setParameters(String rootDirectory){
		defectsFilePath = rootDirectory + "/d4j-defects.txt";
		raflInputFLResultsDir = rootDirectory + "/localized_statements/RAFL_input"; 
		RScriptDirPath = rootDirectory + "/R_scripts";
		resultDirectory = rootDirectory + "/localized_statements/RAFL_output";
		
	}
}
