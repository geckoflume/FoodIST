package pt.ulisboa.tecnico.cmov.foodist.model;

public interface Dish {

    int getCafeteriaId();

    void setCafeteriaId(int i);

    String getName();

    void setName(String name);

    double getPrice();

    void setPrice(double price);

    int getId();

    void setId(int id);

    boolean getHaveInfo();

    void setHaveInfo(boolean haveInfo);

    boolean getHaveMeat();

    void setHaveMeat(boolean haveMeat);

    boolean getHaveFish();

    void setHaveFish(boolean haveFish);

    boolean getIsVegetarian();

    void setIsVegetarian(boolean isVegetarian);

    boolean getIsVegan();

    void setIsVegan(boolean isVegan);

    String getData();

    void setData(String data);

}

