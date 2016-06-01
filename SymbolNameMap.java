package stock;


import java.io.*;
import java.util.*;
import javafx.scene.control.Alert;

/*
Maps stock symbol to company name associated with symbol
*/
public class SymbolNameMap 
{   
    //Creates and returns map
    public static Map<String, String> create()
    {
        Map<String, String> mSymbolName = new HashMap<>();
        
        String path = "companylist.csv";
        try
        {
            try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(path)))) 
            {
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
            }
        }
        catch(FileNotFoundException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("File IO Error");
            alert.setContentText("File Not Found");
            alert.show();
        }
        return mSymbolName;
    }   
}
