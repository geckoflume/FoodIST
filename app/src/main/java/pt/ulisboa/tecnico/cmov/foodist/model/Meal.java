package pt.ulisboa.tecnico.cmov.foodist.model;

public class Meal {
    private String Name;
    private String Price;
    private int CafetId;

    public Meal(String n, String p, int i){
        this.Name = n;
        this.Price = p;
        this.CafetId = i;
    }

    public int getCafetId() {
        return CafetId;
    }

    public String getName() {
        return Name;
    }

    public String getPrice() {
        return Price;
    }

}

