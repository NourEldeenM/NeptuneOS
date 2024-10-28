package org.os;
import java.io.File;
import java.util.Arrays;

public class Moaz {
    private static Boolean  all=false;
    private static Boolean recursive=false;

    //should be revised
    private static String extractPath(String line) {
        String path = ".";

        int start = line.indexOf("/");
        if (start == -1) {
            return path;
        }

        int end = line.indexOf(" ", start);
        if (end == -1 ) {
            end = line.length();
        }


        if (end > 0 && (line.charAt(end - 1) == '\'' || line.charAt(end - 1) == '\"')) {
            end--;
        }

        path = line.substring(start, end).trim();

        if (path.isEmpty()) {
            return ".";
        }

        return path;
    }
    private static void displayDir(String path, int indent ){
        if(path.charAt(path.length()-1)!='/'){
            path+="/";
        }
        File currentDir=new File(path);
        if(currentDir==null){
            return;
        }
        File[] files=currentDir.listFiles();
        String spaces=" ".repeat(indent*4);
        for (File file:files){
            if(file.isHidden()&&!all){
                continue;
            }

            System.out.print(spaces);
            if(file.isDirectory()){
                System.out.print('/');
            }
            System.out.println( file.getName());

            if(file.isDirectory()&&recursive){
                String nPath=path+file.getName().toString();

                displayDir(nPath,indent+1);
            }
        }

    }
    public static void ls(String line){
        //check that line is for ls
        if (!line.contains("ls ")) {
            throw new IllegalArgumentException("Invalid command: The line must contain 'ls '");
        }

        //set all boolean arguments
        if (line.contains("-")){
            int dashIndex=line.indexOf("-");
            int end=line.indexOf(" ",dashIndex);
            if(end==-1){
                end=line.length();
            }
            String args=line.substring(dashIndex+1,end);
//                System.out.println(args);
            for(char c:args.toCharArray() ){
                if(c=='a'){
                    all=true;
                }
                if(c=='r'){
                    recursive=true;
                }
            }
        }
        String path=extractPath(line);

        System.out.println(path);
        displayDir(path,0);
        all=false;
        recursive=false;
    }
}
