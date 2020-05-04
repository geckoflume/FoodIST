package pt.ulisboa.tecnico.cmov.foodist.model;

public interface Picture {
    int getId();

    void setId(int id);

    int getDishId();

    void setDishId(int dishId);

    String getFilename();

    void setFilename(String filename);
}
