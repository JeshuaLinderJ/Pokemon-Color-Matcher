package com.mycompany.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.mycompany.app.service.DocumentPokemon;
import com.mycompany.app.service.ImagePixelParser;

public class App {

    public static void main(String[] args){
        System.out.println(averageList(initiateList(),true));
        System.out.println(averageStack(initiateStack(), true));

        System.out.println(ImagePixelParser.rgbAverage("metapod.png"));

        // Check if documentPokemon has been run before, if not run it once
        try {
            File flagFile = new File("documentPokemon.flag");
            if (!flagFile.exists()) {
                System.out.println("Running documentPokemon() for the first time...");
                DocumentPokemon.initializePokemon();
                
                // Create flag file to indicate it's been run
                flagFile.createNewFile();
                System.out.println("documentPokemon() completed and won't run again.");
            } else {
                System.out.println("documentPokemon() has been run before, skipping...");
            }
        } catch (IOException e) {
            System.err.println("Error managing documentPokemon flag file: " + e.getMessage());
        }

    }


    public static float averageList(ArrayList<Integer> list, boolean round){
        int sum = 0;
        for(Integer num : list){
            sum += num;
        }
        if(round){
            float average = (float) sum / list.size();
            return Math.round(average * 100) / 100.0f;
        }
        return list.isEmpty() ? 0 : sum / list.size();
    }

    public static float averageStack(Stack<Integer> stack, boolean round){
        int sum = 0;
        for(Integer num : stack){
            sum += num;
        }
        if(round){
            float average = (float) sum / stack.size();
            return Math.round(average * 100) / 100.0f;
        }
        return stack.isEmpty() ? 0 : sum / stack.size();
    }

    public static Stack<Integer> initiateStack(){
        Stack<Integer> stack = new Stack<>();
        stack.push(7);
        stack.push(9);
        stack.push(8);
        stack.push(3);
        stack.push(2);
        stack.push(0);
        stack.push(4);
        stack.push(6);
        stack.push(2);
        stack.push(21);
        return stack;
    }

    public static ArrayList<Integer> initiateList(){
        ArrayList<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(8);
        list.add(8);
        list.add(7);
        list.add(3);
        list.add(3);
        list.add(2);
        list.add(9);
        list.add(1);
        list.add(2);
        list.add(8);
        list.add(6);
        list.add(4);
        return list;
    }
}