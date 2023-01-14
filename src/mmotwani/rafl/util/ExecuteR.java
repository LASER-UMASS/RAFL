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

package util;

import java.io.*;
import java.awt.Frame;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.rosuda.JRI.*;

import main.Rafl;

public class ExecuteR {

	protected static Logger logger = Logger.getLogger(ExecuteR.class);

	public static  void ExecuteRScript(String Rpath) {
		if (!Rengine.versionCheck()) {
			logger.error("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		
		Rengine re = Rengine.getMainEngine();
		if(re == null){
		    re = new Rengine(new String[] { "--vanilla" }, false, new TextConsole());
		    logger.info("Rengine created, waiting for R");
		    if (!re.waitForR()) {
		    	logger.error("Cannot load R");
		    }
		}
		String cmd = "source('" + Rpath + "',echo=TRUE)";
		re.eval(cmd).asString();
		re.end();
	}
}
