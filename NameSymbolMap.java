package stock;

import java.util.*;
import java.io.*;

/*
Maps company name to stock symbols associated with that name
*/
public class NameSymbolMap 
{
    /*
    Creates and returns map
    */
    public static Map<String, List<String>> create()
    {
        Map<String, List<String>> hmSymbols = new HashMap<>();
        
        String resourcesPath = "resources/companylist.csv";
        InputStream input = NameSymbolMap.class.getResourceAsStream(resourcesPath);

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
                    List<String> lstSymbols = hmSymbols.get(companyName);
                    if (lstSymbols == null)
                    {
                        lstSymbols = new ArrayList<>();
                        lstSymbols.add(symbol);
                    }
                    else
                    {
                        lstSymbols.add(symbol);
                    }
                    hmSymbols.put(companyName, lstSymbols);
                }
            }
            else
            {
                String[] array = line.split(",");
                if (array.length >= 2)
                {
                    String company = array[1];
                    String symbol = array[0];
                    List<String> lstSymbols = hmSymbols.get(company);
                    if (lstSymbols == null)
                    {
                        lstSymbols = new ArrayList<>();
                        lstSymbols.add(symbol);
                    }
                    else
                    {
                        lstSymbols.add(symbol);
                    }
                    hmSymbols.put(company, lstSymbols);
                }
            }
        }
        return hmSymbols;
    }
}
