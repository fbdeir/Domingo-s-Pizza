import java.util.ArrayList;

public class PizzaPage {
    public String Title;
    public ArrayList<PizzaFragment> pizzas= new ArrayList<>();
    public java.awt.Image img;
    PizzaFragment objs;

    public PizzaPage(java.awt.Image img, PizzaFragment... objs){
        this.img=img;
    }
}
