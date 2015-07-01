package com.sinapsi.webservice.utility;

import java.io.BufferedReader;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class that read the body from a post request
 *
 */
public class BodyReader {
	
	/**
	 * Read the body from post request and return the jsoned string
	 * 
	 * @param request http servlet request
	 * @return jsoned string
	 */
	public static String read(HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) { 
			e.printStackTrace();
		}

		return jb.toString();
	}
}
