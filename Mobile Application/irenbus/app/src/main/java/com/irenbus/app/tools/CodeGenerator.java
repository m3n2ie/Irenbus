package com.irenbus.app.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CodeGenerator {
    private String code ="";
    ArrayList<Character> charsList;

    public CodeGenerator(){
        charsList = new ArrayList<>();
        for(int i = 65; i<=90; i++){
            charsList.add((char)i);
        }
        for(int i = 97; i<=122; i++){
            charsList.add((char)i);
        }
        for(int i = 48; i<=57; i++){
            charsList.add((char)i);
        }
    }

    public String getCode(){
        code = "";
        Collections.shuffle(charsList);
        for(int i=1; i<=21; i++){
            code += i%4==0 ? "-" : "";
            code += ""+charsList.get((int)(Math.random()*charsList.size()));
        }
        System.out.println("Code: "+code);
        return code;
    }

}
