package ac.artemis.core.v4.utils.file;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Ghast
 * @since 28-Apr-20
 */
public class JarFileDownloader {
    public boolean downloadFile(String fromUrl, File localFileName) {
        BufferedOutputStream out = null;
        InputStream in = null;
        try {
            File localFile = localFileName;
            if (localFile.exists()) {
                localFile.delete();
                System.out.println(localFileName + " was already found. Overriding.");
            }
            if (localFile.getParentFile() != null) {
                localFile.getParentFile().mkdir();
            } else {
                localFile.getAbsoluteFile().getParentFile().mkdir();
            }
            URL url = new URL(fromUrl);
            out = new BufferedOutputStream(new FileOutputStream(localFileName));
            URLConnection conn = url.openConnection();
            in = conn.getInputStream();
            int size = conn.getContentLength();

            double lastSumCount = 0.0;
            double sumCount = 0.0;
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                sumCount += numRead;
                if ((sumCount / size - lastSumCount / size) * 100.0 > 1) {
                    lastSumCount = sumCount;
                }
                out.write(buffer, 0, numRead);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            //System.out.println("Successfully downloaded dependency " + localFileName + " @ " + fromUrl);
        }

    }
}
