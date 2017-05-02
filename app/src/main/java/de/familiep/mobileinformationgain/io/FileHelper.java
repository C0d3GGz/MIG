package de.familiep.mobileinformationgain.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHelper {

    private static String ERRORLOG_FILENAME = "error.txt";
    private File contextDir;

    public FileHelper(File externalDir) {
        contextDir = externalDir;
    }

    public void createFileWithContent(String filename, String content){
        PrintWriter writer = null;
        File outputFile = new File(contextDir, filename);

        try {
            writer = new PrintWriter(new FileOutputStream(outputFile, false));
            writer.print(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }
    }

    public void appendErrorlog(String stacktrace) {
        PrintWriter writer = null;
        File outputFile = new File(contextDir, ERRORLOG_FILENAME);

        try {
            writer = new PrintWriter(new FileOutputStream(outputFile, true));
            writer.print(stacktrace);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }
    }

    public File getErrorLog() {
        return new File(contextDir, ERRORLOG_FILENAME);
    }

    /**
     * returns a csv helper which overrides existing csv files
     * @param fileName name of the csv file (suffix is optional)
     * @param csvHeaderData first line of csv data
     * @return the csvHelper to operate on
     */
    public CsvHelper getCsvHelper(String fileName, String csvHeaderData) {
        return new CsvHelper(fileName, csvHeaderData);
    }

    /**
     * no recursion: zips only files, no folders
     * @return all files zipped
     */
    public File exportAllGeneratedDataAsZip(){
        File files[] = contextDir.listFiles();
        File tmpDir = new File(contextDir + "/tmp");
        tmpDir.mkdirs();
        File zipOut = new File(tmpDir, "data.zip");

        if(zipOut.exists()) zipOut.delete();

        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        FileInputStream fis = null;

        try{
            zipOut.createNewFile();

            fos = new FileOutputStream(zipOut);
            zos = new ZipOutputStream(fos);

            for(File file : files){
                byte buffer[] = new byte[1024];
                fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0){
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (zos != null) zos.close();
                if (fis != null) fis.close();;
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return zipOut;
    }

    public class CsvHelper {

        private File csvFile;
        private PrintWriter writer;
        private String header;
        private boolean append;

        /**
         *
         * @param fileName filename, may already exist to append to file
         * @param csvHeaderData if new file set csv head, otherwise ignored
         */
        private CsvHelper(String fileName, String csvHeaderData) {

            this.header = csvHeaderData;

            if(!fileName.endsWith(".csv"))
                fileName += ".csv";

            csvFile = new File(contextDir, fileName);

            if(csvFile.exists()){
                append = true;
            }

            try {
                csvFile.createNewFile();
                writer = new PrintWriter(new FileOutputStream(csvFile, append));
                if(!append)
                    writer.println(csvHeaderData);
            } catch (IOException e){e.printStackTrace();}
        }

        public void addEntriesToCsv(String entryWithoutNewline){
            if(writer != null) writer.println(entryWithoutNewline);
        }

        public File finishCsv(){
            if(writer != null) writer.close();
            return csvFile;
        }
    }
}
