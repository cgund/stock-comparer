package stock;

import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/*
Maps stock exchange trading date to map that maps company symbol to daily stock data
*/
public class YearSymbolDataMap 
{ 
    //Creates and returns map
    public static Map<String, Map<String, List<String>>> create()
    {            
        String path = "NYSE_2014";
        File dir = new File(path);
        File[] arrayFiles = dir.listFiles();
        //Map(Key: FileName(Date) -> Value: Map(Key: CompanyStockSymbol -> Value: StockData))
        Map<String, Map<String, List<String>>> mYearly = new HashMap();
        Scanner scanner = null;
        for (File file: arrayFiles)
        {
            try
            {
                //Key: CompanyStockSymbol -> Value: StockData
                Map<String, List<String>> mDaily = new HashMap();
                scanner = new Scanner(new BufferedReader(new FileReader(file)));
                while (scanner.hasNext())
                {
                    String line = scanner.nextLine();
                    String[] array = line.split(",");

                    String stockSymbol = array[0];
                    List<String> stockData = new ArrayList<>();
                    for (int i = 1; i < array.length; i++)
                    {
                        stockData.add(array[i]);
                    }
                    mDaily.put(stockSymbol, stockData);
                    
                }
                scanner.close();
                String key = file.toString();
                key = key.substring(15, 23);
                String year = key.substring(0, 4);
                String month = key.substring(4, 6);
                String day = key.substring(6, key.length());
                key = year + "-" + month + "-" + day;
                mYearly.put(key, mDaily);
            }
            catch(FileNotFoundException ex)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("File IO Error");
                alert.setContentText("File Not Found");
                alert.show();
            }            
        }
        if (scanner != null)
        {
            scanner.close();
        }
        return mYearly;
    }
}
