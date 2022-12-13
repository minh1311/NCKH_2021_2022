package MFEA;

import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {
        double[] totalTimes = new double[(int)Population.MAX_ITERATIONS];
        int index;

        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(new Task("input\\vm1084.tsp"));
        taskList.add(new Task("input\\lin318.tsp"));
        taskList.add(new Task("input\\berlin52.tsp"));
        taskList.add(new Task("input\\st70.tsp"));
        taskList.add(new Task("input\\bier127.tsp"));
        UnifiedMultitaskingEnvironment ume = new UnifiedMultitaskingEnvironment(taskList);
        try {
        	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    		String timestamp = dateFormatter.format(new Date());
    		String prefix1 = "F:\\1LeNghiem_";
    		String prefix2 = "F:\\2LeNghiem_";
    		String extension = ".txt";
    		String filePath1 = prefix1 + timestamp + extension;
    		String filePath2 = prefix2 + timestamp + extension;
        	File filename1 = new File(filePath1);
        	File filename2 = new File(filePath2);
        	PrintWriter pw1 = new PrintWriter(filename1);
        	PrintWriter pw2 = new PrintWriter(filename2);
        	if(Population.MAX_ITERATIONS==100) {
            	index=10;
            }
        	else index=2;
        for (int i = 0; i < Population.MAX_ITERATIONS; i++) {
            System.out.println("Lần #" + (i + 1) + ":");
            long startTime = System.nanoTime();
            ume.MFEAlgorithm();
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            totalTimes[i] = (double) totalTime / 1000000000;
            System.out.println("Total Time: " + totalTimes[i] + " second(s).");
            

            pw1.print(i+1);
            for (Task task : taskList) {
            	ume.printBestSolution(task); 	
            	double distance = ume.printDistance(task);
            	pw1.print(" "+distance);  

            }	pw1.println();
            if(i==0) {
            	for (Task task : taskList) {	
            		pw2.print(i+1);
                	double distance = ume.printDistance(task);
                	pw2.print(" "+distance);  

                }pw2.println();
            }else
            {  
            	if((i+1)%index==0) {
            
            	for (Task task : taskList) {	
            		pw2.print(i+1);
                	double distance = ume.printDistance(task);
                	pw2.print(" "+distance);  

                }	pw2.println();
            }}
            
            	
            }pw1.close(); pw2.close();
            
        }catch(Exception ex) {
        	System.out.println(ex);
        }
       
    }
}
