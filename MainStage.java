package stock;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

/*
Primary input form of application.  The user inputs a stock symbol on the NYSE,
selects a data range, and selects a market index for comparison purposes (optional).
The user can also select the "Lookup Symbol" option that opens another window
that provides functionality for searching for a stock symbol.  The display 
button opens another stage for displaying stock data in linechart format (if
inputs are valid).
*/
public class MainStage extends Application 
{            
    private TextField txtSymbol;
    private Callback<DatePicker, DateCell> callBack;
    private DatePicker dpStart;
    private DatePicker dpEnd;
    private ComboBox<MarketIndex> cbCompare;
    private final LocalDate MIN_DATE = LocalDate.of(2014, Month.JANUARY, 1);
    private final LocalDate MAX_DATE = LocalDate.of(2014, Month.JUNE, 30);
    private boolean searchStageLoaded = false;  
    //Maps to hold stock data
    protected static Map<String,Map<String, List<String>>> mYear_mSymbolData;
    protected static Map<String, List<String>> mNameSymbol;
    protected static Map<String, String> mSymbolName;

    @Override
    public void start(Stage primaryStage) 
    {    
        BorderPane root = new BorderPane();
        
        initCallBack();
        root.setTop(createSearchSymbolPane());
        root.setCenter(createGridPane());
        root.setBottom(createButtonPane());
        
        Scene scene = new Scene(root, 400, 250);

        primaryStage.setTitle("Stock Comparer");
        primaryStage.setScene(scene);
        primaryStage.show();
        try
        {
            mYear_mSymbolData = YearSymbolDataMap.create(); //Primary map
        }
        catch (IOException ex)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("IO Error");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.show();
        }
        mNameSymbol = NameSymbolMap.create(); //Company name -> symbol map
        mSymbolName = SymbolNameMap.create(); //Company symbol -> name map      
    }
    
    private void initCallBack()
    {
        //Restricts DatePickers to a single year(2014)
        callBack = new Callback<DatePicker, DateCell>() 
        {
            @Override
            public DateCell call(final DatePicker datePicker) 
            {
                return new DateCell() 
                {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) 
                    {
                        super.updateItem(item, empty);
                        
                        if (!empty && item != null)
                        {
                            if (item.isBefore(MIN_DATE) || item.isAfter(MAX_DATE)) 
                            {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            }   
                        }
                    }
                };
            }
        };         
    }
    
    private Node createStartLabel()
    {
        Label lblStart = new Label("Start Date:");
        lblStart.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 12));
        lblStart.setPadding(new Insets(0, 0, 0, 10));
        lblStart.setAlignment(Pos.CENTER_LEFT);
        return lblStart;
    }
    
    private Node createEndLabel()
    {                        
        Label lblEnd = new Label("End date:");
        lblEnd.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 12));
        lblEnd.setPadding(new Insets(0, 0, 0, 10));
        lblEnd.setAlignment(Pos.CENTER_LEFT);
        return lblEnd;
    }
    
    private Node createDatePickerStart()
    { 
        dpStart = new DatePicker(MIN_DATE);
        dpStart.setShowWeekNumbers(false); 
        dpStart.setDayCellFactory(callBack);
        return dpStart;
    }
    
    private Node createDatePickerEnd()
    {
        dpEnd = new DatePicker(MAX_DATE);
        dpEnd.setShowWeekNumbers(false);   
        dpEnd.setDayCellFactory(callBack);
        return dpEnd;       
    }
    
    private Node createSearchSymbolPane()
    {
        Label lblSymbol = new Label("Enter Stock Symbol: ");
        txtSymbol = new TextField();
        txtSymbol.setMaxWidth(100);
        Hyperlink hlLookupSymbol = new Hyperlink("Lookup Symbol");
        hlLookupSymbol.setPadding(new Insets(0, 0, 0, 50));
        hlLookupSymbol.setOnAction(e ->
        {
            if (searchStageLoaded)
            {
                SearchStage.show();
            }
            else
            {
                SearchStage.load(txtSymbol);
                searchStageLoaded = true;
            }
        });
        
        HBox hbSymbol = new HBox(10);
        hbSymbol.setAlignment(Pos.CENTER);
        hbSymbol.setPadding(new Insets(10, 0, 0, 0));        
        hbSymbol.getChildren().addAll(lblSymbol, txtSymbol, hlLookupSymbol);
        
        return hbSymbol;
    }
    
    private Node createCompareLabel()
    {     
        Label lblCompare = new Label("Compare to:");
        lblCompare.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 12));
        lblCompare.setPadding(new Insets(0, 0, 0, 10));
        lblCompare.setAlignment(Pos.CENTER_LEFT);    
        return lblCompare;
    }
    
    private Node createComboBoxPane()
    {
        cbCompare = new ComboBox<>();
        //Sets the list of MarketIndex items to show within the ComboBox popup
        cbCompare.getItems().addAll(
                new MarketIndex("---", null),
                new MarketIndex("Dow Jones Industrial Average", "resources/DJIA.csv"),
                new MarketIndex("S&P 500", "resources/SP500.csv"),
                new MarketIndex("NASDAQ Composite", "resources/NASDAQ_COMP.csv"));
        cbCompare.setValue(cbCompare.getItems().get(0));
        cbCompare.setEditable(false);
        return cbCompare;
    }
    
    private Node createGridPane()
    {
        GridPane gpOptions = new GridPane();
        gpOptions.setAlignment(Pos.CENTER);
        gpOptions.setHgap(20);
        gpOptions.setVgap(20);      
        
        gpOptions.add(createStartLabel(), 0, 0);
        gpOptions.add(createDatePickerStart(), 1, 0);
        gpOptions.add(createEndLabel(), 0, 1);
        gpOptions.add(createDatePickerEnd(), 1, 1);
        gpOptions.add(createCompareLabel(), 0, 2);
        gpOptions.add(createComboBoxPane(), 1, 2);
        return gpOptions;        
    }
    
    private Node createButtonPane()
    {
        Button btnDisplay = new Button("Display");
        //Registers EventHandler with button onAction property
        btnDisplay.setOnAction(e ->
        {                
            LocalDate startDate = dpStart.getValue();
            LocalDate endDate = dpEnd.getValue();

            long interval = ChronoUnit.DAYS.between(startDate, endDate);
            if (interval > 0) //Valid date range
            {
                String symbol = txtSymbol.getText().toUpperCase().trim();
                if (mSymbolName.containsKey(symbol)) //Map contains symbol
                {
                    if (cbCompare.getValue().getName().equals("---")) 
                    { //No MarketIndex selected
                        DisplayStage ds = new DisplayStage(symbol, 
                                mYear_mSymbolData, startDate, endDate);
                        ds.displaySingleStock();
                    }
                    else //MarketIndex selected
                    {
                        MarketIndex mi = cbCompare.getValue();                       
                        DisplayStage ds = new DisplayStage(symbol, 
                                mYear_mSymbolData, startDate, endDate);
                        ds.displayStockVsIndex(mi);                           
                    }                       
                }
                else //Map does not contain symbol
                {
                    displayAlert("Error", "Symbol nout found");                       
                }
            }
            else //Invalid date range
            {
                displayAlert("Date Range Error", "End date must be later than start date");
            }                        
        });
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.setPadding(new Insets(0, 0, 10, 0));
        hbBtn.getChildren().addAll(btnDisplay);
        return hbBtn;
    }
    
    public void displayAlert(String title, String content)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();        
    }
    
    public static void main(String[] args) 
    {
        launch(args);
    }
}
