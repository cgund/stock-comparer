package stock;

import java.io.*;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/*
Maps stock exchange trading date to map that maps company symbol to daily stock data
*/
public class YearSymbolDataMap
{
    //Creates and returns map
    public static Map<String, Map<String, List<String>>> create() throws IOException
    {  
        Map<String, Map<String, List<String>>> mYearly = new HashMap();        

        JarFile jar = new JarFile("StockComparer.jar");
        Enumeration<JarEntry> entries = jar.entries();
        int counter = 0;
        while (entries.hasMoreElements() && counter < 146) 
        {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.contains("NYSE"))
            {
                int index = name.indexOf('N');
                String resource = name.substring(index, name.length());
                //Key: CompanyStockSymbol -> Value: StockData
                Map<String, List<String>> mDaily = new HashMap();
                InputStream inStream = YearSymbolDataMap.class.getResourceAsStream("resources/" + resource);
                try (Scanner scanner = new Scanner(new BufferedReader(
                        new InputStreamReader(inStream)))) 
                {
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
                }
                String key = resource;
                key = key.substring(5, 13);
                String year = key.substring(0, 4);
                String month = key.substring(4, 6);
                String day = key.substring(6, key.length());
                key = year + "-" + month + "-" + day;
                mYearly.put(key, mDaily);
            }
            counter++;
        }
        return mYearly;
    }
}