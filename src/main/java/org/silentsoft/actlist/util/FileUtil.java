package org.silentsoft.actlist.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    
    public static List<String> readFileByLine(Path path, boolean trim) {
    	ArrayList<String> content = new ArrayList<String>();
    	
    	if (Files.isReadable(path)) {
    		BufferedReader reader = null;
        	try {
        		reader = Files.newBufferedReader(path, CHARSET);
            	
                String line = null;
                while ((line = reader.readLine()) != null) {
                    content.add(trim ? line.trim() : line);
                }
        	} catch (Exception e) {
        		
        	} finally {
        		if (reader != null) {
        			try {
    					reader.close();
    				} catch (IOException e) {
    					
    				}
        		}
        	}
    	}
    	
    	return content;
    }
    
    public static void saveFile(Path path, String content) throws IOException {
    	if (Files.isWritable(path)) {
    		BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
        	writer.write(content, 0, content.length());
            writer.close();
    	}
    }
	
}
