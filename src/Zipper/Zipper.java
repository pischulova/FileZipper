package Zipper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Created by А on 18.12.14.
 */
public class Zipper {
    private File folder;
    private String folderName;
    private List<File> files;
    private FileInputStream fis;
    private FileOutputStream fos;
    private ZipInputStream zis;
    private ZipOutputStream zos;


    public Zipper(String name) {
        folderName = name;
        folder = new File(name);
        files = new ArrayList<File>();
    }

    public void zipFiles() {
        try {
            createFilesList(folder);
            addToZips(files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFilesList(File folder) {
        for (File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory())
                files.add(fileEntry);
        }
    }

    public void addToZips(List<File> list) throws IOException {
        for(File first : list) {
            String firstName = getFileName(first);
            for(File second : list) {
                String secondName = getFileName(second);

                if(firstName.equals(secondName) && extensionsMatch(first, second)) {
                    System.out.println("Writing '" + firstName + "' zip file");
                    fos = new FileOutputStream(firstName+".zip");
                    zos = new ZipOutputStream(fos);
                    addToZipFile(first, zos);
                    addToZipFile(second, zos);

                    File zipF = new File(firstName+".zip");
                    copyFile(zipF, firstName);

                    zipF.delete();
                }
            }
        }
    }

    public void addToZipFile(File file, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        fis = new FileInputStream(file);
        byte[] buffer = new byte[10240];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        zos.closeEntry();
        fis.close();
    }

    private void copyFile(File file, String name) throws IOException {
        fis = new FileInputStream(file);
        zis = new ZipInputStream(fis);
        fos = new FileOutputStream(folderName + "\\" + name + ".zip");
        zos = new ZipOutputStream(fos);

        for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
            zos.putNextEntry(ze);
            byte[] buffer = new byte[1024];
            for (int read = zis.read(buffer); read != -1; read = zis.read(buffer)) {
                zos.write(buffer, 0, read);
            }
            zos.closeEntry();
        }
        zos.close();
        fos.close();
        fos = null;
        System.gc();
        zis.close();
        fis.close();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }

    public static String getFileName(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(0, lastIndexOf);
    }

    public boolean extensionsMatch(File f1, File f2) {
        String firstExt = getFileExtension(f1);
        String secondExt = getFileExtension(f2);

        if(firstExt.equals(".eps") && secondExt.equals(".jpg"))
            return true;
        return false;
    }
}
