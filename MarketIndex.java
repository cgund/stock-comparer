package stock;

import java.io.*;
import java.util.*;

/*
Encapsulates stock market index data
*/
public class MarketIndex 
{
    private final String name; //name of market index
    private Map<String, Double> mMarketIndex; //Date -> Aggregate Daily Value
    private final InputStream input;
    
    public MarketIndex(String name, String resourcePath)
    {
        this.name = name;
        if (resourcePath != null)
            input = this.getClass().getResourceAsStream(resourcePath);            
        else
            this.input = null;
    }
    
    /*
    Maps market index data from file with format DATE, AGGREGATE VALUE
    */
    public Map<String, Double> map()
    {        
        mMarketIndex = new HashMap<>();
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(input)));
        while (scanner.hasNext())
        {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            if (parts.length == 2)
            {
                String date = parts[0];
                String[] array = date.split("/");
                if (array.length == 3)
                {
                    String year = array[2];
                    String day = array[1];
                    if (day.length() == 1)
                    {
                        day = "0" + day;
                    }
                    String month = array[0];
                    if (month.length() == 1)
                    {
                        month = "0" + month;
                    }
                    date = year + "-" + month + "-" + day;
                }

                String average = parts[1];
                Double dblAverage = Double.parseDouble(average);
                mMarketIndex.put(date, dblAverage);                        
            }
        }
        return mMarketIndex;
    }
    
    public Map<String, Double> getMap()
    {
        return mMarketIndex;
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
