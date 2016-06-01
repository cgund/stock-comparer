package stock;

import java.util.*;
import java.io.*;
import javafx.scene.control.Alert;

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
        
        String path = "companylist.csv";
        try
        {
            try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(path)))) {
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
            }
        }
        catch(FileNotFoundException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("File IO Error");
            alert.setContentText("File Not Found");
            alert.show();
        }
        return hmSymbols;
    }
}
