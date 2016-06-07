package stock;

import java.io.*;
import java.util.*;

/*
Maps stock symbol to company name associated with symbol
*/
public class SymbolNameMap 
{   
    //Creates and returns map
    public static Map<String, String> create()
    {
        Map<String, String> mSymbolName = new HashMap<>();
        
        String resourcePath = "resources/companylist.csv";
        InputStream input = SymbolNameMap.class.getResourceAsStream(resourcePath);
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(input))); 
        scanner.nextLine();
        while (scanner.hasNext())
        {
            String line = scanner.nextLine();
            int index = line.indexOf("\"");
            if (index >= 0)
            {
                String companyName = line.substring(index + 1, line.indexOf("\"", index + 1));
                String[] array = line.split(",");
                if (array.length >= 2)
                {
                    String symbol = array[0];
                    mSymbolName.put(symbol, companyName);
                }
            }
            else
            {
                String[] array = line.split(",");
                if (array.length >= 2)
                {
                    String symbol = array[0];
                    String company = array[1];
                    mSymbolName.put(symbol, company);
                }
            }
        }
        return mSymbolName;
    }   
}
