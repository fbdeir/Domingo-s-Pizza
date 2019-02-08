import java.util.ArrayList;

public class Pizza {
    PizzaFragment type;
    PizzaFragment dough;
    ArrayList<PizzaFragment> toppings;
    int price;

    public Pizza(PizzaFragment type, PizzaFragment dough, ArrayList<PizzaFragment> toppings, int price) {
        this.type = type;
        this.dough = dough;
        this.toppings = toppings;
        this.price=price;
    }
}
