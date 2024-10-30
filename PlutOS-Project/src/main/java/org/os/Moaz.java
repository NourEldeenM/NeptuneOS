package org.os;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Moaz {
    private static Boolean  all=false;
    private static Boolean recursive=false;

   private static String displayDir(String path, int indent ){
        if(path.charAt(path.length()-1)!='/'){
            path+="/";
        }

        File currentDir=new File(path);
       if (!currentDir.exists() || !currentDir.isDirectory()) {
            return "";
        }

        File[] files=currentDir.listFiles();

       if (files == null) {
           return "";
       }


       StringBuilder ans = new StringBuilder();
       String spaces=" ".repeat(indent*4);
        for (File file:files){
            if(file.isHidden()&&!all){
                continue;
            }

            ans.append(spaces).append(file.getName());
            if(file.isDirectory()){
                ans.append('/');
            }

            ans.append("\n");
            if(file.isDirectory()&&recursive){
                String nPath=path+file.getName();


                ans.append(displayDir(nPath,indent+1));
            }
        }
        return ans.toString();

    }


    public static String ls(String[] tokens){

//        set all booleans to flase
        all=false;
        recursive=false;

//        check that line is for ls
        if (!tokens[0].contains("ls")) {
            throw new IllegalArgumentException("Invalid command: The line must contain 'ls'");
        }

//        set all boolean arguments
        if (tokens.length>1&&tokens[1].contains("-")){
            for(char c:tokens[1].toCharArray() ){
                if(c=='-'){
                    continue;
                }
                if(c=='a'){
                    all=true;
                    continue;
                }
                if(c=='r'){
                    recursive=true;
                    continue;
                }
                throw new IllegalArgumentException("This "+"argument didn't supported\n");
            }
        }

        //extract path
        String path=".";
        if(tokens.length>=3){
            int start=0;
            int end=tokens[2].length();
            if(tokens[2].contains("\"")||tokens[2].contains("'")){
                start++;
                if(tokens[2].substring(start).contains("\"")||tokens[2].substring(start).contains("'")){
                    end--;
                }else{
                    throw new IllegalArgumentException("your path is not valid");
                }
            }
            path=tokens[2].substring(start,end);
        }

//        call display dir function that loop over files in given path
        String ans=displayDir(path,0);


//      return ans
        return ans;
    }


    public static void appendOutputToFile(String[] tokens){
       if(tokens.length<3){
           throw new IllegalArgumentException("output redirection shuld be on format\n command >> file \n");
       }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tokens[2], true))) {
            writer.write(tokens[0]);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
