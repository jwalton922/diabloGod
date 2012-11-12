/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablodatagatherer;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import diablo.analysis.DiabloFileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Josh
 */
public class InvalidFileDeleter {
    
    public static void deleteInvalidFiles(){
        File profileDir = new File(DiabloFileReader.PROFILE_DIR);
        File heroDir = new File(DiabloFileReader.HERO_DIR);
        File itemDir = new File(DiabloFileReader.ITEMS_DIR);
        //File[] profileFiles = profileDir.listFiles();
        File[] heroFiles = heroDir.listFiles();
        File[] itemFiles = itemDir.listFiles();
        System.out.println("Listed all files");
        ArrayList<File> filesToDelete = new ArrayList<File>();
        
       // filesToDelete.addAll(processFiles(profileFiles));
        System.out.println("Finished Finding profile files to delete");
        filesToDelete.addAll(processFiles(heroFiles));
        System.out.println("Finished Finding hero files to delete");
        filesToDelete.addAll(processFiles(itemFiles));
        System.out.println("Finished Finding item files to delete");
        
        System.out.println("There are "+filesToDelete.size()+" files to delete");
        
        
        
        
    }
    
    public static ArrayList<File> processFiles(File[] files){
        ArrayList<File> filesToDelete = new ArrayList<File>();
        for(int i = 0; i < files.length; i++){
            DBObject object = readFile(files[i]);
            if(!(ApiDataRetriever.isValidAPIData(object))){
                System.out.println("Found file to delete: "+files[i].getName());
                filesToDelete.add(files[i]);
                files[i].delete();
            }
            
        }
        return filesToDelete;
    }
    
    public static DBObject readFile(File f){
        DBObject object = null;
        String objectData = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            
            while((line = br.readLine()) != null){
                objectData+=line;
            }
            
            br.close();
        } catch(Exception e){
            System.out.println("Error reading file");
            e.printStackTrace();
        }
        //System.out.println("Finished reading file: "+f.getName());
        if(objectData.length() > 0){
            object = (DBObject) JSON.parse(objectData);
        }
        //System.out.println("Finished parsing data");
        return object;
    }
    
    public static void main(String[] args){
        InvalidFileDeleter.deleteInvalidFiles();
    }
}
