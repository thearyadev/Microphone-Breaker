package dev.aryankothari;

import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;

public class Config {
    public int break_point;
    public String path;
    public Config(String path){
        this.path = path;
        try{
            Scanner reader = new Scanner(new File(path));
            String bp = reader.nextLine();
            bp = bp.split("=")[1];
            this.break_point = Integer.parseInt(bp);

        }catch(Exception e){
            System.out.println(e);
            this.break_point = 25;

        }

    }
    public void set_break_point(int newbp){
        try{

            FileWriter writer = new FileWriter(this.path);
            writer.write("breakpoint=" + newbp);
            writer.close();

        }catch (Exception e){
            System.out.println(e);
        }finally{
            this.break_point = newbp;
        }
    }

}
