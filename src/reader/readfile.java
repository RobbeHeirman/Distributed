package reader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class readfile {

    private Scanner x;

   private void openFile(String file){
        try{

            x = new Scanner(new File(file));

        }catch (Exception e){
            System.err.println("Could not open file");
        }
    }

    public Map<String,String> readFile(@SuppressWarnings("SameParameterValue") String file){
        System.out.println("Reading " + file + "...");
        this.openFile(file);
        Map<String, String> ret = new HashMap<>();
        while(x.hasNext()){
            String key = x.next();
            String value = x.next();
            System.out.println(key + " is " + value );
            ret.put(key, value);
        }
        x.close();
        return ret;
    }
}
