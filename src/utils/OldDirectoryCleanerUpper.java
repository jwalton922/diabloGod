/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import diablodatagatherer.ApiDataRetriever;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * writes files from old way of a single directory of all items, to a directory
 * by time
 *
 * @author Josh
 */
public class OldDirectoryCleanerUpper {

    public static void cleanupFiles() {
        System.out.println("cleanupFiles called");
        File oldItemDir = new File(ApiDataRetriever.ROOT_DIR + "/items");
        File[] oldItemDirFiles = oldItemDir.listFiles();
        System.out.println("Have "+oldItemDirFiles.length+" files");
        for (int i = 0; i < oldItemDirFiles.length; i++) {
            
            String fileName = oldItemDirFiles[i].getName();
            System.out.println("Processing file "+i+". Name: "+fileName);
            String[] fileNameSplit = fileName.split("_");
            String time = fileNameSplit[0];
            boolean validTime = false;
            try {
                Long.parseLong(time);
                validTime = true;
            } catch (Exception e) {
                System.out.println("Error parsign time: " + time);
            }

            if (validTime) {
                try {
                    String newFileDir = ApiDataRetriever.ROOT_DIR+"/"+time+"/";
                    File newDir = new File(newFileDir);
                    if(!newDir.exists()){
                        newDir.mkdir();;
                    }
                    String newFileName = newFileDir+fileName;
                    System.out.println("Writing file to "+newFileName);
                    BufferedReader br = new BufferedReader(new FileReader(oldItemDirFiles[i]));
                    BufferedWriter bw = new BufferedWriter(new FileWriter(newFileName));
                    
                    String line = null;
                    while((line = br.readLine())!= null){
                        bw.write(line);
                    }
                    
                    bw.flush();
                    bw.close();
                    br.close();
                    
                } catch (Exception e) {
                    throw new RuntimeException("Error with file transfer: " + e.getLocalizedMessage());
                }
            }
            System.out.println("Finished writing file "+(i+1)+"/"+oldItemDirFiles.length);
            
        }

    }
    
    public static void main(String[] args){
        System.out.println("Test");
        OldDirectoryCleanerUpper.cleanupFiles();
        System.out.println("Finished");
    }
}
