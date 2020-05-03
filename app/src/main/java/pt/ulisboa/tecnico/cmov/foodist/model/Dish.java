package pt.ulisboa.tecnico.cmov.foodist.model;

public interface Dish {

    void setCafeteriaId(int i);
    int getCafeteriaId() ;

    void setName (String name);
    String getName() ;

    void setPrice(double price);
    double getPrice() ;

    void setId(int id);
    int getId();

}

