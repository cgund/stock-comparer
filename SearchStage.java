package stock;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
Class for searching for company stock symbol by company name
*/
public class SearchStage 
{
    private static TextField tfSymbol; //Reference to MainStage TextField
    private static TextField tfSearch;
    private static final Map<String, List<String>> mNameSymbol = MainStage.mNameSymbol;
    private static final double MIN_MATCH = .80;
    private static TableView<Company> tableView; //Items list contains Company objects
    private static TableColumn colSymbol;
    private static TableColumn colName;
    
    private static final ObservableList<Company> tableData = 
            FXCollections.observableArrayList();
    
    private static final Stage stage = new Stage();
    
    /*
    Initiliazes form, registers search button event handler, constructs TableView
    */
    public static void load(TextField tf)
    {    
        tfSymbol = tf;
        stage.setOnCloseRequest(e ->
        {
            tableData.clear();
        });
        
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(createSearchPane(), createResultsPane());
        
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        show();
    }
    
    public static void show()
    {
        tfSearch.clear();
        stage.show();
    }
    
    private static Node createSearchPane()
    {
        Label lblInput = new Label("Enter company name: ");
        tfSearch = new TextField();
        Button btnGo = new Button("Go");
        btnGo.setOnAction(e ->
        {
            tableView.getItems().clear();
            search(tfSearch.getText());           
        });        
        HBox hbInput = new HBox(10);
        hbInput.setAlignment(Pos.CENTER);
        hbInput.setPadding(new Insets(10, 10, 10, 10));
        hbInput.getChildren().addAll(lblInput, tfSearch, btnGo);
        return hbInput;
    }
    
    private static Node createResultsPane()
    {        
        Label lblOutput = new Label("Search Results");
        
        colSymbol = new TableColumn("Symbol");
        colName = new TableColumn("Company Name");
        //Extracts property values from Company objects
        colSymbol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        tableView = new TableView<>();
        tableView.setPlaceholder(new Label(""));
        tableView.getColumns().addAll(colSymbol, colName);
        
        colSymbol.prefWidthProperty().bind(tableView.widthProperty().divide(4));
        colName.prefWidthProperty().bind(tableView.widthProperty().divide(1.33));

        VBox vbOutput = new VBox(10);
        vbOutput.setAlignment(Pos.CENTER);
        vbOutput.getChildren().addAll(lblOutput, tableView); 
        return vbOutput;
    }
    
    /*
    Searches for stock symbol matches
    */
    public static void search(String searchInput)
    {
        if (!searchInput.isEmpty())
        {
            searchInput = searchInput.trim();
            if (mNameSymbol.containsKey(searchInput))
            {
                //Returns list of stock symbols associated with company name
                List<String> lstTickerSymbols = mNameSymbol.get(searchInput);
                displayResults(lstTickerSymbols, searchInput);
            }
            else
            {
                Set<String> setNames = mNameSymbol.keySet();
                Iterator iterator = setNames.iterator();
                boolean matchMade = false;
                while (iterator.hasNext())
                {
                    String storedName = (String)iterator.next();
                    Float normalizedMatch = fuzzyScore(storedName, searchInput);
                    if (normalizedMatch >= MIN_MATCH) 
                    {
                        //Returns list of stock symbols associated with company name
                        List<String> lstTickerSymbols = mNameSymbol.get(storedName);
                        displayResults(lstTickerSymbols, storedName);
                        matchMade = true;
                    }
                }
                if (matchMade == false)
                {
                    displayNoResults();
                }
            }
        }
        else
        {
            displayNoResults();
        }
    }
    
    /*
    No results found
    */
    public static void displayNoResults()
    {
        tableData.add(new Company(null, "No results found"));
        tableView.setItems(tableData);         
    }
    
    /*
    Displays results in TableView
    */
    public static void displayResults(List<String> list, String companyName)
    {
        List<Company> lstCompany = new ArrayList<>();
        for (String tickerSymbol: list)
        {
            Hyperlink symbol = new Hyperlink(tickerSymbol);
            symbol.setOnAction(ev -> //Registers event handler 
            {
                tfSymbol.setText(symbol.getText()); //Sets TextField Text property in MainStage
                tableData.clear();
                colSymbol.getColumns().clear();
                colName.getColumns().clear();
                stage.hide();
            });

            lstCompany.add(new Company(symbol, companyName));
        }
                 
        tableData.addAll(lstCompany); //Adds collection of symbols to ObservableList
        tableView.setItems(tableData); 
    }
    
    /*
    Adapted code:
    https://commons.apache.org/sandbox/commons-text/xref/org/apache/commons/text/similarity/FuzzyScore.html
    */
    private static Float fuzzyScore(String term, String query) 
    {
        if (term == null || query == null) 
        {
            throw new IllegalArgumentException("Strings must not be null");
        }

        // fuzzy logic is case insensitive. We normalize the Strings to lower
        // case right from the start. Turning characters to lower case
        // via Character.toLowerCase(char) is unfortunately insufficient
        // as it does not accept a locale.
        final String termLowerCase = term.toLowerCase();
        final String queryLowerCase = query.toLowerCase();

        // the resulting score
        int score = 0;

        // the position in the term which will be scanned next for potential
        // query character matches
        int termIndex = 0;

        // index of the previously matched character in the term
        int previousMatchingCharacterIndex = Integer.MIN_VALUE;

        for (int queryIndex = 0; queryIndex < queryLowerCase.length(); queryIndex++) 
        {
            final char queryChar = queryLowerCase.charAt(queryIndex);

            boolean termCharacterMatchFound = false;
            for (; termIndex < termLowerCase.length() && !termCharacterMatchFound; termIndex++) 
            {
                final char termChar = termLowerCase.charAt(termIndex);

                if (queryChar == termChar) 
                {
                    // simple character matches result in one point
                    score++;

                    // subsequent character matches further improve
                    // the score.
                    if (previousMatchingCharacterIndex + 1 == termIndex) 
                    {
                        score += 2;
                    }

                    previousMatchingCharacterIndex = termIndex;

                    // we can leave the nested loop. Every character in the
                    // query can match at most one character in the term.
                    termCharacterMatchFound = true;
                }
            }
        }
        return (float)(score) / (float)(termLowerCase.length());
    }
}
