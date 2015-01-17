package ua.naiksoftware.util;

import java.io.*;

import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import filelog.Log;

/**
 *
 * @author Naik
 */
public class FileUtils {

    private static final String tag = "FileUtils";
 
    private static final long MIN_LOCAL_MEMORY = 1024 * 100;// 100 Kb

	public static void moveFiles(String src, String dest, FilenameFilter filter) {
		File fsrc = new File(src);
		File fdest = new File(dest);
		fdest.mkdirs();
		String to;
		File[] list = fsrc.listFiles(filter);
		for(File entry: list) {
			to =  entry.getPath().replace(src, dest);
			if (entry.isDirectory()) {
				moveFiles(entry.getPath(), to, filter);
			} else {
				entry.renameTo(new File(to));
			}
		}
	}
	
    public static boolean unzip(InputStream is, File folderToUnzip) {
        //Log.d("Unzip", "method unzip started");
        ZipInputStream zip = new ZipInputStream(new BufferedInputStream(is));
        FileOutputStream fos = null;
        String fileName = null;
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zip.getNextEntry()) != null) {
                //long free = folderToUnzip.getFreeSpace();
                fileName = zipEntry.getName();
                final File outputFile = new File(folderToUnzip, fileName);
                outputFile.getParentFile().mkdirs();
                //Log.d("Unzip", "Zip entry: " + fileName + ", extract to: " + outputFile.getPath());
                if (fileName.endsWith("/")) {
                    //Log.d("Unzip", fileName+ " is directory");
                    outputFile.mkdirs();
                    continue;
                } else {
                    outputFile.createNewFile();
                    //if (zipEntry.getSize() == outputFile.length()) {
                    //    continue;
                    //}
                    //free = free - zipEntry.getSize() + outputFile.length();
                    //if (free < MIN_LOCAL_MEMORY) {
                        // error
                    //    return false;
                    //}
                    fos = new FileOutputStream(outputFile, false);
                    byte[] bytes = new byte[2048];
                    int c;
                    try {
                        while ((c = zip.read(bytes)) != -1) {
                            fos.write(bytes, 0, c);
                        }
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        Log.d("Unzip", "IOErr in readFromStream (zip.read(bytes)): " + e.getMessage());
						return false;
                    }
                }
                zip.closeEntry();
            }
        } catch (IOException ioe) {
            Log.d("Unzip err", ioe.getMessage());
            return false;
        } finally {
            try {
                zip.close();
                zip = null;
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            for (File file : listFiles) {
                deleteDirectory(file);
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

    public static TreeMap<String, String> loadManifest(File mf) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mf)));
            String line;
            int index;
            while ((line = br.readLine()) != null) {
                index = line.indexOf(':');
                if (index > 0) {
                    params.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
                }
            }
            br.close();
        } catch (Throwable t) {
            System.out.println("getAppProperty() will not be available due to " + t.toString());
        }
        return params;
    }
}
