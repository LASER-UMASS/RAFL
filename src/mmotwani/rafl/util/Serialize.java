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
package mmotwani.rafl.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;


public class Serialize {

	protected static Logger logger = Logger.getLogger(Serialize.class);

	public static void serializeArrayList(ArrayList<String> list, String filepath){
		try
		{
			FileOutputStream fos = new FileOutputStream(filepath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.close();
			fos.close();
		} 
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}

	public static ArrayList<String> deserializeArrayList(String filepath){

		ArrayList<String> list = new ArrayList<String>();
		try
		{
			FileInputStream fis = new FileInputStream(filepath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			list = (ArrayList) ois.readObject();
			ois.close();
			fis.close();
		} 
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
			return list;
		} 
		catch (ClassNotFoundException c) 
		{
			logger.error("Class not found");
			c.printStackTrace();
			return list;
		}

		return list; 
	}

	public static void serializeHashMap(HashMap<String, ArrayList<String>> hmap, String filename)
	{
		try
		{
			FileOutputStream fos =
					new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hmap);
			oos.close();
			fos.close();
			logger.info("Serialized HashMap data is saved in " + filename);
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}


	public static HashMap<String, ArrayList<String>> deserializeHashMap(String filename)
	{
		HashMap<String, ArrayList<String>> map = null;
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (HashMap) ois.readObject();
			ois.close();
			fis.close();
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
			return map;
		}catch(ClassNotFoundException c)
		{
			logger.error("Class not found");
			c.printStackTrace();
			return map;
		}
		logger.info("Dserialized HashMap data is saved in " + filename);
		// Display content using Iterator
		Set set = map.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
		}
		return map;
	}
}