package stock;

import java.io.*;
import java.util.*;

/*
Encapsulates stock market index data
*/
public class MarketIndex 
{
    private final String name; //name of market index
    private final String path; //file path
    private Map<String, Double> mMarketIndex; //Date -> Aggregate Daily Value
    
    public MarketIndex(String name, String path)
    {
        this.name = name;
        this.path = path;
    }
    
    /*
    Maps market index data from file with format DATE, AGGREGATE VALUE
    */
    public Map<String, Double> map()
    {        
        mMarketIndex = new HashMap<>();
        try
        {
            try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(path)))) 
            {
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
            }
        }
        catch(FileNotFoundException ex)
        {
            System.err.println(ex);
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
