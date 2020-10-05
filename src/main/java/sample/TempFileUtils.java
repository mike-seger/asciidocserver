package sample;

import java.io.File;

public class TempFileUtils {
	private final static File sysTempDir = new File(System.getProperty("java.io.tmpdir"));

    public File createTempDir(String dirname) {
    	if(!sysTempDir.exists()) {
    		throw new RuntimeException("Could not find system temp dir: "+sysTempDir.getAbsolutePath());
	    }
    	File tempDir =  new File(sysTempDir, dirname);
//    	if(tempDir.exists()) {
//		    deleteDir(tempDir);
//	    }
    	if(!tempDir.exists() && !tempDir.mkdir()) {
		    throw new RuntimeException("Could not create temp dir: "+tempDir.getAbsolutePath());
	    }

    	return tempDir;
    }

	private void deleteDir(File dir) {
		File[] files = dir.listFiles();
		if(files != null) {
			for (final File file : files) {
				deleteDir(file);
			}
		}
		if(!dir.delete()) {
			throw new RuntimeException("Could not delete dir: "+dir.getAbsolutePath());
		}
	}
}
