package stock;

import javafx.scene.control.Hyperlink;

/*
Encapsulates company listed on NYSE
*/
public class Company 
{
    private Hyperlink symbol;
    private String name;
    
    public Company(Hyperlink symbol, String name)
    {
        this.symbol = symbol;
        this.name = name;
    }

    public Hyperlink getSymbol() {
        return symbol;
    }

    public void setSymbol(Hyperlink symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
