package org.smartregister.chw.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtilities {

    public static boolean hasExternalDisk() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean canWriteToExternalDisk() {
        if (!hasExternalDisk())
            return false;

        return !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    public static File createDirectory(String directoryPath, boolean onSdCard) throws Exception {
        File location = onSdCard ? Environment.getExternalStorageDirectory() : Environment.getDataDirectory();
        File dir = new File(location + File.separator + directoryPath);
        if (dir.exists())
            return dir;

        if (!dir.mkdirs())
            throw new Exception("Directory was not created successfully");

        return dir;
    }

    public static boolean writeToExternalDisk(String directoryPath, byte[] bytes, String fileName) throws Exception {
        File dir = createDirectory(directoryPath, canWriteToExternalDisk());
        boolean created = dir.exists();
        if (created) {
            File file = new File(dir, fileName);
            FileOutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        } else {
            throw new Exception("cannot write to sd card");
        }

        return true;
    }
}
