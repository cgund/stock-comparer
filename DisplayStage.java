package stock;

import java.text.*;
import java.time.*;
import java.util.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/*
Displays closing prices in line chart format for a single stock or a single 
stock compared to a market index over the specified date range
*/
public class DisplayStage 
{
    private String symbol;
    private final Map<String,Map<String, List<String>>> mYearly;
    private List<String> alDates; //NYSE trading dates
    private final ArrayList<MarketIndex> alMarketIndexes = new ArrayList<>();
    private static final Double INIT_INVESTMENT = 100000.00;
    private final int startDate;
    private final int endDate;
    private final XYChart.Series seriesStock = new XYChart.Series();
    private final CategoryAxis xAxis;
    private final NumberAxis yAxis;
    private final String companyName;
    private final LineChart<String, Number> lineChart;
    private final BorderPane root;
    private final Stage stage;
    
    /*
    Four argument constructor
    @param symbol Stock symbol of company
    @param mYearly NYSE trading dates mapped to map of company symbols and daily
    stock data
    @param sDate The start date of the query
    @param eDate The end date of the query
    */
    public DisplayStage(String symbol, Map<String,Map<String, List<String>>> mYearly, 
                                        LocalDate sDate, LocalDate eDate)
    {
        this.symbol = symbol;
        this.mYearly = mYearly;
        
        sortDates(mYearly);
        
        startDate = alDates.indexOf(sDate.toString());
        endDate = alDates.indexOf(eDate.toString());
            
        xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        
        yAxis = new NumberAxis();
        companyName = MainStage.mSymbolName.get(symbol);
        lineChart = new LineChart<>(xAxis, yAxis);
        
        root = new BorderPane();
        
        stage = new Stage();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }
    
    /*
    Sort dates so that when they are iterated over the dates are in chronological
    order
    */
    private void sortDates(Map<String,Map<String, List<String>>> mYearly)
    {
        Set<String> dates = mYearly.keySet(); //Trading dates
        String[] arrayDates = new String[dates.size()];
        arrayDates = dates.toArray(arrayDates);
        alDates = Arrays.asList(arrayDates);
        Collections.sort(alDates);        
    }
    
    /*
    Display individual stock series data points compared to market index series 
    data points in line chart format
    */
    public void displayStockVsIndex(MarketIndex mi)
    {   
        XYChart.Series seriesMarketIndex = new XYChart.Series();
        seriesMarketIndex.setName(mi.toString());
        seriesStock.setName(companyName);
        yAxis.setLabel("Investment Value($)");
        
        String strDate = alDates.get(startDate); //Start trading date
        //Retrieve collection of symbols mapped to daily stock dataPoint
        Map<String, List<String>> mapDaily = mYearly.get(strDate);
        symbol = symbol.trim();
        List<String> sData = mapDaily.get(symbol); //Company stock dataPoint for that day
        String strClosingPrice = sData.get(sData.size() - 2);
        //Closing price for that day
        Double dblClosingPrice = Double.parseDouble(strClosingPrice); 
        
        Double numStocks = INIT_INVESTMENT/dblClosingPrice; //Number of stocks purchased
        
        Map<String, Double> mapMI = mapMarketIndex(mi);
        List<String> alIndexDates = sortMarketIndexDates(mapMI);
            
        String startIndexDate = alIndexDates.get(startDate);
        Double startIndexPrice = mapMI.get(startIndexDate);
        
        final Double numIndexStocks = INIT_INVESTMENT/startIndexPrice; //Number of index stocks
       
        /*
        Iterate over specified date range, retrieve date, stock closing price 
        for that date, the updated value of the stock investment, plot the 
        data point, set Circle object as data point node, add data point
        to series data property
        */
        for (int i = startDate; i <= endDate; i++)
        {
            String stockDate = alDates.get(i);
            Map<String, List<String>> mDaily = mYearly.get(stockDate);
            symbol = symbol.trim();
            List<String> stockData = mDaily.get(symbol);
            String date = stockData.get(0);
            String closePrice = stockData.get(stockData.size() - 2);
            Double dblClosePrice = Double.parseDouble(closePrice);
            
            double amountMoney = numStocks * dblClosePrice;            
            
            XYChart.Data dataPoint = new XYChart.Data(date, amountMoney);
            dataPoint.setNode(new Circle());
            seriesStock.getData().add(dataPoint);           
                    
            /*
            If Market Index map contains the date as a key, retrieve the index
            price for that day, calculate the value of the invesment, plot
            the dataPoint point, add the dataPoint point to the market index series dataPoint
            propery
            */

            if (mapMI.containsKey(stockDate))
            {
                Double dailyIndexPrice = mapMI.get(stockDate);
                double w = numIndexStocks * dailyIndexPrice;
                XYChart.Data dataP = new XYChart.Data(date, w);
                dataP.setNode(new Circle()); 
                seriesMarketIndex.getData().add(dataP);                
            }               
        }    
        
        /*
        Adds the stock and market index series dataPoint to the line chart dataPoint property
        */
        lineChart.getData().add(seriesMarketIndex);
        lineChart.getData().add(seriesStock);
        
        lineChart.setLegendVisible(true);
        lineChart.setLegendSide(Side.TOP);
        root.setCenter(lineChart);
        
        stage.show();
    }
    
    /*
    Creates map of market index, mapping trading date to closing price.  If the
    user has previously viewed the market index dataPoint, then the map representing 
    that dataPoint is retrieved from the list collection, otherwise the market index 
    is added to the list and the market index is mapped
    */
    public Map<String, Double> mapMarketIndex(MarketIndex mi)
    {
        Map<String, Double> mapMI;
        if (alMarketIndexes.contains(mi))
        {
            int index = alMarketIndexes.indexOf(mi);
            MarketIndex marketIndex = alMarketIndexes.get(index);
            mapMI = marketIndex.getMap();
        }
        else
        {
            alMarketIndexes.add(mi);
            mapMI = mi.map();
        }
        return mapMI;
    }
    
    /*
    Retrieves a set of keys representing the market index dates, converts set
    to closingPrices and closingPrices to list, sorts list, and returns list
    */
    public List<String> sortMarketIndexDates(Map<String, Double> mapMI)
    {
        Set<String> indexDates = mapMI.keySet();
        String[] aIndexDates = new String[indexDates.size()];
        aIndexDates = indexDates.toArray(aIndexDates);
        List<String> alIndexDates = Arrays.asList(aIndexDates);
        Collections.sort(alIndexDates);
        return alIndexDates;
    }
    
    /*
    Display individual stock series dataPoint in line chart format
    */    
    public void displaySingleStock()
    {
        yAxis.setLabel("Closing Price");
        
        //Set chartTitle
        if (companyName != null)
        {
            lineChart.setTitle(companyName + "    " + symbol);
        }

        //Variables for computing average and median stock price
        Double totalPrice = 0.0;
        int totalDays = endDate - startDate;
        
        ArrayList<Double> closePrices = new ArrayList<>();
        
        /*
        Iterate over specified date range, retrieve date, stock closing price 
        for that date, plot the 
        data point, set Circle object as data point node, add data point
        to series data property, increment total price variable for use later
        in calculating median and average
        */
        for (int i = startDate; i <= endDate; i++)
        {
            String day = alDates.get(i);
            Map<String, List<String>> mDaily = mYearly.get(day);
            symbol = symbol.trim();
            List<String> stockData = mDaily.get(symbol);
            String date = stockData.get(0);
            String closePrice = stockData.get(stockData.size() - 2);
            Double dblClosePrice = Double.parseDouble(closePrice);
            closePrices.add(dblClosePrice);
            XYChart.Data dataPoint = new XYChart.Data(date, dblClosePrice);
            dataPoint.setNode(new Circle());
            seriesStock.getData().add(dataPoint);
            
            totalPrice += dblClosePrice;
        }
        
        Double avgPrice = totalPrice / (double) totalDays;
        Double medianPrice = calcMedian(closePrices);
        
        root.setCenter(lineChart);
        root.setRight(createStatsPane(avgPrice, medianPrice));
        
        lineChart.getData().add(seriesStock);
        
        stage.show();
    }
    
    /*
    Calculates the median closing price for the stock
    */
    private double calcMedian(List<Double> closePrices)
    {
        Double[] closingPrices = new Double[closePrices.size()];
        closingPrices = closePrices.toArray(closingPrices);
        Arrays.sort(closingPrices);
        Double median;
        if (closingPrices.length % 2 == 0) //Even number of elements
        {
            int index = closingPrices.length/2;
            median = (closingPrices[index] + closingPrices[index - 1]) / 2;
        }
        else //Odd number of elements
        {
            median = closingPrices[closingPrices.length/2];
        }
        return median;
    }
    
    /*
    Return a pane to display the median and mean average price of the stock
    */
    private VBox createStatsPane(Double meanPrice, Double medianPrice)
    {
        NumberFormat numFormatter = NumberFormat.getCurrencyInstance();
        String strMeanPrice = numFormatter.format(meanPrice);
        String strMedianPrice = numFormatter.format(medianPrice);
        
        VBox vBoxStats = new VBox(10);
        vBoxStats.setAlignment(Pos.CENTER);
        vBoxStats.setPadding(new Insets(5, 10, 10, 5));
        vBoxStats.getChildren().add(new Label("Mean Price: " + strMeanPrice));
        vBoxStats.getChildren().add(new Label("Median Price: " + strMedianPrice));
        
        return vBoxStats;
    }
    
}
