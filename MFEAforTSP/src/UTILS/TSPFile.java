package UTILS;

import MFEA.Task;
import TSP.EuclidDistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class TSPFile {
    //Đọc file
    public static void readFile(String filename, Task cp) {
        File file = new File(filename);
        Scanner sc;
        StringTokenizer st;
        try {
            sc = new Scanner(file);
            System.out.println(sc.nextLine()); //File name.
            System.out.println(sc.nextLine()); //File type: TSP.
            System.out.println(sc.nextLine()); //Comment for file.

            st = new StringTokenizer(sc.nextLine());
            while(st.hasMoreTokens()) {
                st.nextToken(": ");
                cp.setDimension(Integer.parseInt(st.nextToken()));
            }

            st = new StringTokenizer(sc.nextLine());
            while(st.hasMoreTokens()) {
                st.nextToken(": ");
                cp.setDistanceType(st.nextToken());
                cp.setObjectiveFunction(new EuclidDistance());         
            }
            if(sc.nextLine().equals(ConstantString.NODE_COORD_SECTION)) {
                //Đọc tọa độ
                for (int i = 0; i < cp.getDimension(); i++) {
                    sc.nextInt();
                    cp.setaX(i, Double.parseDouble(sc.next()));
                    cp.setaY(i, Double.parseDouble(sc.next()));
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
