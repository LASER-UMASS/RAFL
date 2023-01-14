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
package mmotwani.rafl.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import configuration.Config;
import util.Serialize;
import util.ExecuteR;

public class Rafl {

	protected static Logger logger = Logger.getLogger(Rafl.class);

	/**
	 * @param path path to the file listing suspicious statements
	 * @return ArrayList<String> that stores top-100 suspicious statements 
	 */
	private static ArrayList<String> fetchFLResultsFromFile(String path) {
		ArrayList<String> results = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
			while (line != null) {
				String result = line.trim();
				if (line.contains("#")) {
					double score = Double.parseDouble(line.split(",")[1].trim());
					String stmt = line.split(",")[0].trim();
					if (score > 0.0) {
						boolean exists = false;
						for (String res:results){
							String rstmt = res.split(",")[0].trim();
							if (rstmt.contentEquals(stmt)){
								exists = true;
								break;
							}
						}
						if (!exists){
							results.add(result);
						}
					}
				}
				line = reader.readLine();
			}
			reader.close();
			
			if (results.size() < 100){
				reader = new BufferedReader(new FileReader(path));
				line = reader.readLine();
				while (line != null) {
					String result = line.trim();
					if (line.contains("#")) {
						String stmt = line.split(",")[0].trim();
						double score = Double.parseDouble(line.split(",")[1].trim());
						if (score == 0.0) {
							boolean exists = false;
							for (String res:results){
								String rstmt = res.split(",")[0].trim();
								if (rstmt.contentEquals(stmt)){
								//	logger.info("FOUND");
									exists = true;
									break;
								}
							}
							if (!exists){
								results.add(result);
							}
						}
					}
					line = reader.readLine();
					if (results.size() >= 100){
						break;
					}
				}
				reader.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("#statements localized: " + results.size());
		return results;
	}

	/**
	 * @param fl_results_list list storing the suspicious statements with scores > 0
	 * computed using different FL techniques
	 * @return minimum number of statements with score > 0 considering all the lists 
	 */
	private static int computeMinFLResultElements(ArrayList<List<String>> fl_results_list) {
		int min_size = Integer.MAX_VALUE;
		for (List<String> fl_results : fl_results_list) {
			if (min_size > fl_results.size()) {
				min_size = fl_results.size();
			}
		}
		return min_size;
	}

	
	/**
	 * @param fl_results_paths list storing the absolute path of FL results computed 
	 * using different FL techniques. 
	 * @return file_results_list that stores the top-100 suspicious statements for all FL techniques. 
	 */
	private static ArrayList<List<String>> processFLResults(ArrayList<String> fl_results_paths) {

		ArrayList<List<String>> fl_results_list = new ArrayList<List<String>>();

		for (String path : fl_results_paths) {
			logger.info("Extracting localized statements from path: " + path);
			ArrayList<String> fl_results = fetchFLResultsFromFile(path);
			fl_results_list.add(fl_results);
		}
		return fl_results_list;
	}

	/**
	 * @param map storing statements and their suspiciousness scores computed using FL technique
	 * @return sorted map based on the decreasing order of suspiciousness scores. 
	 */
	private static HashMap<String, Double> sortByValues(HashMap<String, Double> map) {
		List<Object> list = new LinkedList<Object>(map.entrySet());

		Collections.sort(list, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Collections.reverse(list);

		double maxscore = Double.parseDouble(list.get(0).toString().split("=")[1].trim());
		double minscore = Double.parseDouble(list.get(list.size() - 1).toString().split("=")[1].trim());

		HashMap<String, Double> sortedHashMap = new LinkedHashMap<String, Double>();
		for (Iterator<Object> it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Double score = (Double) entry.getValue();
			Double normalizedScore = (score - minscore) / (maxscore - minscore);
			sortedHashMap.put((String) entry.getKey(), normalizedScore);
		}
		return sortedHashMap;
	}

	/**
	 * @param defect Defects4J defect for which the FL results are to be combined
	 * @param rPath path to store R script
	 * @param rMatrix MxN matrix storing the top-N suspicious statements obtained by M fault locacalization techniques 
	 * @param rFunction RankAggreg command used to execute RAFL
	 * @throws Exception
	 */
	private static void createRScriptToCombineFL(String defect, String rPath, String rMatrix, String rFunction)
			throws Exception {

		logger.info("Creating R script at " + rPath);
		File rootRScriptDir = new File(Config.RScriptDirPath);
		if (!rootRScriptDir.exists()) {
			logger.info("Creating directory to store R scripts at " + Config.RScriptDirPath);
			rootRScriptDir.mkdir();
		}
		Path rScriptPath = Paths.get(rPath);

		ArrayList<String> Rcommands = new ArrayList<String>();
		Rcommands.add("library(RankAggreg)\n");
		Rcommands.add("sink('" + rPath.replace(".R",".out") + "')\n");
		Rcommands.add("print(\"" + defect + "\")\n");
		Rcommands.add(rMatrix);
		Rcommands.add(rFunction);
		Files.write(rScriptPath, Rcommands, StandardCharsets.UTF_8);
	}

	/**
	 * @param defect Defects4J defect for which the FL results are to be combined
	 * @param N sample size
	 * @param fl_results_list stores the top-100 suspicious statements for all FL techniques
	 * @param rPath path to store R script
	 * @param resultDirPath path to store R script
	 * @throws Exception
	 */
	private static void combineSuspiciousStatements(String defect, int N, ArrayList<List<String>> fl_results_list,
			String rPath, String resultDirPath) throws Exception {

		int k = Math.min(Config.k, N); 
		int colcount = k;
		if (k == 0) {
			logger.error("Cannot combine list with no elements");
			return;
		}

		String RMatrix = "data <- matrix(c(";

		for (List<String> fl_results : fl_results_list) {
			int count = 0;
			HashMap<String, Double> FL = new HashMap<String, Double>();
			HashMap<String, Double> sortedFL = new HashMap<String, Double>();
			for (String result : fl_results) {
				String stmt = result.split(",")[0].trim();
				double score = Double.parseDouble(result.split(",")[1].trim());
				if (!FL.containsKey(stmt)) {
					FL.put(stmt, score);
				} else {
					if (FL.get(stmt) < score) {
						FL.put(stmt, score);
					}
				}
			}

			if (FL.size() > 0) {
				sortedFL = sortByValues(FL);
			}

			count = 0;
			for (String stmt : sortedFL.keySet()) {
				// logger.info("adding stmt to matrix: " + stmt);
				String statement = "\"" + stmt + "\", ";
				RMatrix += statement;
				count++;
				if (count == k)
					break;
			}
		}

		RMatrix = RMatrix.substring(0, RMatrix.length() - 2) + "), ";
		RMatrix += " byrow=TRUE, ncol=" + colcount + ")\n";
		
		String RFunction = "RankAggreg(data, " + Config.k + ", seed=" + Config.seed + ", method=\"" + Config.method
				+ "\", convIn=" + Config.convIn + ", popSize=" + Config.popSize + ", CP=" + Config.CP
				+ ", MP=" + Config.MP + ", maxIter=" + Config.maxIter + ", N=" + Config.N 
				+ ", distance=\"" + Config.distance + "\", rho=" + Config.rho + ", verbose=TRUE)\n";
		if (colcount < Config.k)
			RFunction = "RankAggreg(data, ncol(data), seed=" + Config.seed + ", method=\"" + Config.method
					+ "\", convIn=" + Config.convIn + ", popSize=" + Config.popSize + ", CP=" + Config.CP
					+ ", MP=" + Config.MP + ", maxIter=" + Config.maxIter  + ", N=" + Config.N 
					+ ", distance=\"" + Config.distance + "\", rho=" + Config.rho + ", verbose=TRUE)\n";

		
		createRScriptToCombineFL(defect, rPath, RMatrix, RFunction);
		executeRScriptAndStoreResults(defect, rPath, resultDirPath);

	}

	
	/**
	 * @param defect Defects4J defect for which the FL results are to be combined
	 * @param rPath path to store R script
	 * @param resultDirPath path to store R script
	 * @throws Exception
	 */
	private static void executeRScriptAndStoreResults(String defect, String rPath, String resultDirPath) throws Exception {

		ExecuteR.ExecuteRScript(rPath);
		String final_list = fetchResultList(defect, rPath);

		String project = defect.split("_")[0].trim();
		String bugid = defect.split("_")[1].trim();

		File resultDir = new File(Config.resultDirectory);
		if (!resultDir.exists()) {
			logger.info("Creating directory to store results in " + Config.resultDirectory);
			resultDir.mkdir();
		}
		
		resultDir = new File(resultDirPath);
		if (!resultDir.exists()) {
			logger.info("Creating directory to store result in " + resultDirPath);
			resultDir.mkdir();
		}
		
		resultDir = new File(resultDirPath + "/" + project.toLowerCase());
		if (!resultDir.exists()) {
			logger.info("Creating sub directory to store result in " + resultDirPath + "/" + project.toLowerCase());
			resultDir.mkdir();
		}
		resultDir = new File(resultDirPath + "/" + project.toLowerCase() + "/" + bugid);
		if (!resultDir.exists()) {
			logger.info("Creating sub directory to store result in " + resultDirPath + "/" + project.toLowerCase() + "/" + bugid);
			resultDir.mkdir();
		}

		String resultFilePath = resultDirPath + "/" + project.toLowerCase() + "/" + bugid + "/stmt-susps.txt";

		String str = "Statement,Suspiciousness\n";
		BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath));
		writer.write(str);
		final_list = final_list.replace("Optimal List:", "").trim();
		float i = 0;
		float maxLen = final_list.split(",").length;
		for (String stmt : final_list.split(",")) {
			float score = (maxLen - i) / maxLen;
			writer.write(stmt + "," + score + "\n");
			i++;
		}
		writer.close();
		logger.info("Combined FL result stored in " + resultFilePath);	
	}

	
	/**
	 * @param defect Defects4J defect for which the FL results are to be combined
	 * @param rPath path to store R script
	 * @return combined list of suspicious statements extracted from the R log file
	 * @throws IOException
	 */
	private static String fetchResultList(String defect, String rPath) throws IOException {

		String resultPath = rPath.replace(".R", ".out");

		File file = new File(resultPath);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		String str = new String(data, "UTF-8");
		String[] lists = str.split("Optimal List:");
		String final_list = lists[lists.length - 1].split("The optimal list is:")[0].trim();
		return final_list;
	}

	/**
	 * Method to set the configuration parameters
	 * @throws IOException
	 */
	private static void setParametersFromSettingsFile() throws IOException {
		File settings_file = new File("rafl.settings");
		BufferedReader br = new BufferedReader(new FileReader(settings_file));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("root"))
				Config.rootDirectory = line.split("=")[1].trim();
		}
		br.close();
		logger.info("Setting ROOT DIR as: " + Config.rootDirectory);
		Config.setParameters(Config.rootDirectory);
	}

	
	/**
	 * Method used to combine FL results of all 815 defects in Defects4J. 
	 * @param defect Defects4J defect for which the FL results are to be combined
	 * @param fLResultPaths path to the directories storing FL results computed using different FL techniques
	 * @throws Exception
	 */
	private static void CombineFLResultsAllD4J(String defect, ArrayList<String> fLResultPaths) throws Exception {
		
		String rscript_dir = Config.RScriptDirPath;
		String result_dir = Config.resultDirectory;
		ArrayList<String> fl_results_paths = new ArrayList<String>();
		
		for(String fl_dir:fLResultPaths){
			String result_file = fl_dir + "/" + defect.split("_")[0].toLowerCase() + "/"
					+ defect.split("_")[1] + "/stmt-susps.txt";
			File fl_result = new File(result_file);
			if (!fl_result.exists()) {
				logger.error("FL results not found for defect " + defect + " at " + result_file);
				return;
			}else{
				fl_results_paths.add(result_file);
			}
		}

		File rDir = new File(rscript_dir);
		if (!rDir.exists()) {
			rDir.mkdir();
		}

		String r_path = rscript_dir + "/" + defect + ".R";
		logger.info("Combining fl results stored in :" + fl_results_paths);
		ArrayList<List<String>> fl_results_list = Rafl.processFLResults(fl_results_paths);
		int N = computeMinFLResultElements(fl_results_list);
		combineSuspiciousStatements(defect, N, fl_results_list, r_path, result_dir);
		fl_results_paths.clear();
		fl_results_list.clear();
		logger.info("FL results combined successfully\n");

	}

	/**
	 * Method used to combine FL results of an input defect in Defects4J.
	 * @param defect Defects4J defect for which the FL results are to be combined
	 * @param fLResultPaths path to the directories storing FL results computed using different FL techniques
	 * @throws Exception
	 */
	private static void combineFLResults(String defect, ArrayList<String> fLResultPaths) throws Exception {
		String r_path = Config.RScriptDirPath + "/" + defect + ".R";
		logger.info("Processing fl results stored in :" + fLResultPaths);
		ArrayList<List<String>> fl_results_list = processFLResults(fLResultPaths);
		int N = computeMinFLResultElements(fl_results_list);
		logger.info("Size of fl results list:" + fl_results_list.size());
		logger.info("Min # of elements considering all fl results list:" + N);
		combineSuspiciousStatements(defect, N, fl_results_list, r_path, Config.resultDirectory);

	}	
	
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		setParametersFromSettingsFile();

		// fetch defect info
		String defect = args[0];

		if (defect.contentEquals("all")) {
			
			int numFLTechniques = Integer.parseInt(args[1]);

			// paths of the pre-computed FL results directories
			ArrayList<String> FLResultPaths = new ArrayList<String>();
			for (int i = 0; i < numFLTechniques; i++) {
				File fl_result = new File(args[i + 2]);
				if (!fl_result.exists()) {
					logger.error("ERROR: FL results not found at " + args[i + 2]);
					return;
				}
				FLResultPaths.add(args[i + 2]);
			}
			
			File defectfile = new File(Config.defectsFilePath);
			boolean exists = defectfile.exists();
			if (exists) {
				ArrayList<String> d4jdefects = Serialize.deserializeArrayList(Config.defectsFilePath);
				for(String def: d4jdefects){
					logger.info("\n\nProcessing Defect: " + def);
					long startTime = System.nanoTime();
					CombineFLResultsAllD4J(def, FLResultPaths);
					long endTime = System.nanoTime();
					long duration = (endTime - startTime)/1000000000;
					logger.info("Total execution time for " + defect + " took " + duration + " seconds");
				}
			} else {
				logger.error("Defects file does not exists!");
				return;
			}
		} else {
			// # of FL techniques to combine
			int numFLTechniques = Integer.parseInt(args[1]);

			// paths of the FL results files
			ArrayList<String> FLResultPaths = new ArrayList<String>();
			for (int i = 0; i < numFLTechniques; i++) {
				File fl_result = new File(args[i + 2]);
				if (!fl_result.exists()) {
					logger.error("ERROR: FL results not found at " + args[i + 2]);
					return;
				}
				FLResultPaths.add(args[i + 2]);
			}
			
			Config.seed = Integer.parseInt(args[numFLTechniques + 2]); 
			Config.N = Integer.parseInt(args[numFLTechniques + 3]); 
			
			long startTime = System.nanoTime();
			combineFLResults(defect, FLResultPaths);
			long endTime = System.nanoTime();
			long duration = (endTime - startTime)/1000000000; 
			logger.info("Total execution time for " + defect + " took " + duration + " seconds");
			
		}
	}
}
