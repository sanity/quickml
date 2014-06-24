package quickdt.crossValidation.movingAverages;

import com.google.common.collect.Lists;
import org.apache.mahout.classifier.evaluation.Auc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by alexanderhawk on 6/24/14.
 */
public class WiseAUC {

    static String mayPredPrefix = "/Users/alexanderhawk/Onespot/preds/validation-preds-2014-05-";
    static String junePredPrefix = "/Users/alexanderhawk/Onespot/preds/validation-preds-2014-06-0";
    static String juneTeenPredPrefix = "/Users/alexanderhawk/Onespot/preds/validation-preds-2014-06-1";
    static String mayLabelPrefix  = "/Users/alexanderhawk/Onespot/labels/validation-2014-05-";
    static String juneLabelPrefix = "/Users/alexanderhawk/Onespot/labels/validation-2014-06-0";
    static String juneTeenLabelPrefix = "/Users/alexanderhawk/Onespot/labels/validation-2014-06-1";
    static double totalWeightedLoss = 0;
    static List<Double> lossByDay = Lists.newArrayList();
    static double totalWeight = 0;
    static ArrayList<Double> preds;
    static ArrayList<Double> labels;

    public static void main(String[] args) {
        String predFile, labelFile;

        for (int i = 26; i <= 31; i++) {
            predFile = mayPredPrefix + i + ".txt";
            labelFile = mayLabelPrefix + i + ".csv";
            incrementDailyAUC(labelFile, predFile);
        }
        for (int i = 1; i <= 9; i++) {
            predFile = junePredPrefix + i + ".txt";
            labelFile = juneLabelPrefix + i + ".csv";
            incrementDailyAUC(labelFile, predFile);

        }

        for (int i = 0; i <= 4; i++) {
            predFile = juneTeenPredPrefix + i + ".txt";
            labelFile = juneTeenLabelPrefix + i + ".csv";
            incrementDailyAUC(labelFile, predFile);
        }
        System.out.println("weighted auc " + totalWeightedLoss / totalWeight);
        for (Double val : lossByDay)
            System.out.println(val);
    }

    private static void incrementDailyAUC(String labelFile, String predFile) {
        preds = Lists.newArrayList();
        labels = Lists.newArrayList();

        readIntoArrayList(preds, predFile);
        readIntoArrayList(labels, labelFile);
        Auc auc = new Auc();
        for (int i = 0; i < preds.size(); i++) {
            auc.add((int) labels.get(i).doubleValue(), preds.get(i));
        }
        totalWeightedLoss += auc.auc() * preds.size();
        totalWeight += preds.size();
        lossByDay.add(auc.auc());
    }

    private static void readIntoArrayList(ArrayList<Double> arrayList, String fileName) {
        Scanner scan;
        File file = new File(fileName);
        try {
            scan = new Scanner(file);
            if (!fileName.contains("preds"))
                scan.next();
            while (scan.hasNextDouble()) {
                arrayList.add(scan.nextDouble());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
