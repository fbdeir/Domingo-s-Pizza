import com.google.gson.Gson;
import okhttp3.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DomingosPizza extends JFrame implements ActionListener{
    public final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public final int FRAME_HEIGHT= screenSize.height * 2 / 3 -20;
    public final int  FRAME_WIDTH = screenSize.width * 1 / 2;
    public int totalPrice;
    public int previousPrice;
    public int currentPrice;
    public JButton nextButton;
    public int pageNumber=0;
    public JPanel bigPanel;
    JLabel label;
    JPanel p;
    Component comp;
    public ArrayList<Image> imageList = new ArrayList<>();
    public ArrayList<PizzaFragment> pizzas= new ArrayList<>();
    public ArrayList<AbstractButton> crumblist;
    BackgroundPanel back;
    List<JComponent> contentList= new ArrayList<>();
    public PizzaFragment pizzaType=new PizzaFragment("",0);
    public PizzaFragment doughType=new PizzaFragment("",0);
    public ArrayList<PizzaFragment> toppings= new ArrayList<>();
    public DomingosPizza() {
        totalPrice=0;
        previousPrice=0;
        pageNumber=0;
        currentPrice=0;
        initUI();
    }
    private void calculateTotalPrice(){
        int sum=0;
        for(int i=0; i<toppings.size(); i++){
            sum+=toppings.get(i).price;
        }
        totalPrice =sum+pizzaType.price+doughType.price;
    }
    private void initUI() {
        setTitle("Simple example");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        back=new BackgroundPanel(new javax.swing.ImageIcon(getClass().getResource("full-pizza.png")).getImage());
        back.setTransparentAdd(true);
        back.setOpaque(false);
        BreadCrumbList b=new BreadCrumbList();
        comp=b.makeBreadcrumbList(Arrays.asList("Choose Pizza Type", "Choose Dough", "Add Toppings", "Confirm Order"));
        back.add(comp, BorderLayout.NORTH);
        crumblist=b.buttons;
        breadcrumblisteners();
        loadpage0();
        add(back);
        pack();
        setLocationRelativeTo(null);
    }
    public void loadpage0(){
        if(bigPanel!=null) {
            back.remove(bigPanel);
        }
        contentList = new ArrayList<>();
        PizzaFragment chicken=new PizzaFragment("Chicken Pizza: $10", 10);
        PizzaFragment pepperoni= new PizzaFragment("Pepperoni Pizza: $9", 9);
        PizzaFragment vegetarian= new PizzaFragment("Vegetarian Pizza: $8", 8);
        ArrayList<PizzaFragment> list=new ArrayList<>();
        pizzas.add(chicken);
        pizzas.add(pepperoni);
        pizzas.add(vegetarian);
        list.add(chicken);
        list.add(pepperoni);
        list.add(vegetarian);
        back.setImage(new javax.swing.ImageIcon(getClass().getResource("pizza-full.jpg")).getImage());
        back.add(addRadioButtons("Choose Pizza Type", list, "next"),BorderLayout.NORTH);
        JPanel panel=new JPanel(new BorderLayout());
        panel.setBackground(new Color(0,0,0,80));
        JLabel label=new JLabel(""+totalPrice);
        panel.add(label);
        panel.setOpaque(true);
        back.add(panel);
        pageNumber=0;
        validate();
        repaint();
    }

    public JPanel bottomBar(){
        JPanel panel=new JPanel(new BorderLayout());
        panel.setBackground(new Color(0,0,0,80));
        JLabel label=new JLabel(""+totalPrice);
        //panel.setPreferredSize(new Dimension(FRAME_WIDTH, 10));
        panel.add(label);
        return panel;
    }

    public JPanel addRadioButtons(String title, List<PizzaFragment> list, String buttonName){
        bigPanel=new JPanel(new GridLayout(2,1));
        ButtonGroup buttonGroup=new ButtonGroup();
        p=new JPanel(new GridLayout(list.size()+1, 1));
        label= new JLabel(title);
        label.setFont(new Font("times new roman", Font.CENTER_BASELINE, 38));
        label.setBackground(new Color(51,51,51));
        label.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/18,0,(FRAME_WIDTH-bigPanel.getWidth())/18));
        bigPanel.add(label);
        bigPanel.setOpaque(true);
        p.setOpaque(false);
        for(PizzaFragment pizza: list){
            String s=pizza.name;
            JRadioButton radioButton= new JRadioButton(s);
            radioButton.setBackground(new Color(51,51,51));
            radioButton.setFont(new Font("times new roman", Font.PLAIN, 30));
            radioButton.setSize(new Dimension(40,40));
            radioButton.setForeground(Color.WHITE);
            buttonGroup.add(radioButton);
            radioButton.addActionListener(this::actionPerformed);
            p.add(radioButton);
            contentList.add(radioButton);
        }
        nextButton=new JButton(buttonName);
        nextButton.addActionListener(this::actionPerformed);
        contentList.add(nextButton);
        nextButton.setFont(new Font("times new roman", Font.CENTER_BASELINE, 20));
        //nextButton.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/4+20,0,(FRAME_WIDTH-bigPanel.getWidth())/4+20));
        nextButton.setBackground(Color.BLACK);
        nextButton.setForeground(new Color(225,225,0));
        p.add(nextButton);
        bigPanel.add(p);
        bigPanel.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/4,0,(FRAME_WIDTH-bigPanel.getWidth())/4));
        return bigPanel;
    }
    public void loadpage4(){
        back.remove(bigPanel);
        back.remove(comp);
        back.setImage(new javax.swing.ImageIcon(getClass().getResource("Pizza-Delivery.jpg")).getImage());
        JLabel titl=new JLabel("Your Order Has Been Placed!");
        titl.setFont(new Font("times new roman", Font.CENTER_BASELINE, 38));

        JPanel temp=new JPanel();
        temp.add(titl);
        back.add(temp);
        pageNumber=0;
        validate();
        repaint();
    }
    @Override
    public void actionPerformed(ActionEvent e){
        for(JComponent button : contentList){
            if(button instanceof JRadioButton && !crumblist.contains(button)){
                if(((JRadioButton) button).isSelected()) {
                    if (pageNumber == 0) {
                            pizzaType = new PizzaFragment(((JRadioButton) button).getText(),getPrice(((JRadioButton) button).getText()));
                    }
                    else if (pageNumber == 1) {
                        doughType = new PizzaFragment(((JRadioButton) button).getText(),0);
                    }
                }
            }
            else if(e.getSource() ==nextButton){

                if(pageNumber==0) {
                    loadpage1();
                }
                else if(pageNumber==1){
                    loadpage2();
                }
                else if (pageNumber==2){
                    loadpage3();
                }
                else if(pageNumber==3){
                    //load last page
                    Pizza lastpizza= new Pizza(pizzaType, doughType, toppings, totalPrice);
                    new Thread(() -> {
                        try {
                            writeTheData(lastpizza);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }).start();
                    loadpage4();
                }
            }
        }
        if(e.getSource() instanceof AbstractButton){
            if(( ((AbstractButton) e.getSource()).getText()).equals("Choose Dough")){
                loadpage1();
            }
            else if(( ((AbstractButton) e.getSource()).getText()).equals("Add Toppings")){
                loadpage2();
            }else if ((((AbstractButton) e.getSource()).getText()).equals("Confirm Order")){
                loadpage3();
            }else if((((AbstractButton) e.getSource()).getText()).equals("Choose Pizza Type")){
                loadpage0();
            }

        }
    }
    public void breadcrumblisteners(){
        for(AbstractButton b: crumblist){
            b.addActionListener(this::actionPerformed);
        }
    }
    public  void loadpage1(){
        back.remove(bigPanel);
        back.setImage(new javax.swing.ImageIcon(getClass().getResource("dough2.jpg")).getImage());
        PizzaFragment flatbread = new PizzaFragment("Flat Bread", 0);
        PizzaFragment thinCrust = new PizzaFragment("Thin Crust", 0);
        contentList = new ArrayList<>();
        ArrayList<PizzaFragment> pizzas=new ArrayList<>();
        pizzas.add(flatbread);
        pizzas.add(thinCrust);
        back.add(addRadioButtons("Choose Dough Type",pizzas, "next"),BorderLayout.NORTH);
        validate();
        repaint();
        pageNumber=1;
    }
    public void loadpage2(){
        back.remove(bigPanel);
        contentList=new ArrayList<>();
        back.setImage(new javax.swing.ImageIcon(getClass().getResource("toppings.jpg")).getImage());
        back.remove(bigPanel);
        PizzaFragment cheese = new PizzaFragment("Extra Cheese: $2", 2);
        PizzaFragment mushrooms= new PizzaFragment("Mushrooms: $1", 1);
        PizzaFragment pineapple= new PizzaFragment("Pineapple: $2", 2);
        PizzaFragment sausage= new PizzaFragment("Sausage: $2", 2);
        ArrayList<PizzaFragment> pizzas=new ArrayList<>();
        pizzas.add(cheese);
        pizzas.add(mushrooms);
        pizzas.add(pineapple);
        pizzas.add(sausage);
        back.add(addCheckBoxes("Choose Toppings",pizzas, "next"),BorderLayout.NORTH);
        validate();
        repaint();
        pageNumber=2;
    }

    public boolean check_if_topping_exists(String topping){
        for(PizzaFragment i: toppings){
            if(topping.equals(i.name)){
                return true;
            }
        }return false;
    }
    public String printToppings(){
        String s="";
        for(PizzaFragment i: toppings){
            s+=i.name+"\n";
        }
        return s;
    }
    public void loadpage3(){
        calculateTotalPrice();
        back.remove(bigPanel);
        contentList=new ArrayList<>();
        back.remove(bigPanel);
        back.setImage(new javax.swing.ImageIcon(getClass().getResource("confirm.jpg")).getImage());
        bigPanel=new JPanel(new GridLayout(2,1));
        p=new JPanel(new GridLayout(4, 1));
        label= new JLabel("Your Order:");
        label.setFont(new Font("times new roman", Font.CENTER_BASELINE, 38));
        label.setBackground(new Color(51,51,51));
        bigPanel.add(label);
        p.setOpaque(false);
        JTextArea confirmpizza=new JTextArea(pizzaType.name+"\n"+doughType.name+"\n"+printToppings()+"Total: $"+totalPrice);
        JScrollPane pane=new JScrollPane(confirmpizza);
        System.out.println(convertToMultiline(pizzaType+"\n"+doughType+"\n"+toppings+"\n-------\n"+"Total: $"+totalPrice));
        confirmpizza.setBackground(new Color(51,51,51));
        confirmpizza.setOpaque(true);
        confirmpizza.setForeground(Color.WHITE);
        confirmpizza.setFont(new Font("times new roman", Font.CENTER_BASELINE, 22));
        p.add(pane);
        nextButton=new JButton("Confirm");
        nextButton.addActionListener(this::actionPerformed);
        contentList.add(nextButton);
        nextButton.setFont(new Font("times new roman", Font.CENTER_BASELINE, 20));
        //nextButton.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/4+20,0,(FRAME_WIDTH-bigPanel.getWidth())/4+20));
        nextButton.setBackground(Color.BLACK);
        nextButton.setForeground(new Color(225,225,0));
        p.add(nextButton);
        bigPanel.add(p);
        bigPanel.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/4,0,(FRAME_WIDTH-bigPanel.getWidth())/4));
        back.add(bigPanel);
        validate();
        back.repaint();
        pageNumber=3;
    }
    public static String convertToMultiline(String orig)
    {
        return "<html>" + orig.replaceAll("\n", "<br>");
    }
    public JPanel addCheckBoxes(String title, List<PizzaFragment> list, String buttonName){
        bigPanel=new JPanel(new GridLayout(2,1));
        p=new JPanel(new GridLayout(list.size()+1, 1));
        label= new JLabel(title);
        label.setFont(new Font("times new roman", Font.CENTER_BASELINE, 38));
        label.setBackground(new Color(51,51,51));
        label.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/18,0,(FRAME_WIDTH-bigPanel.getWidth())/18));
        bigPanel.add(label);

        bigPanel.setOpaque(true);
        p.setOpaque(false);

        for(PizzaFragment pizza: list){
            pizzas.add(pizza);
            String s=pizza.name;
            JCheckBox radioButton= new JCheckBox(s);
            radioButton.setBackground(new Color(51,51,51));
            radioButton.setFont(new Font("times new roman", Font.PLAIN, 30));
            radioButton.setSize(new Dimension(40,40));
            radioButton.setForeground(Color.WHITE);
            radioButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == 1){
                        totalPrice+=getPrice(radioButton.getText());

                        if(!check_if_topping_exists(radioButton.getText())){
                            toppings.add(new PizzaFragment(radioButton.getText(), getPrice(radioButton.getText())));
                        }

                    }else{
                        totalPrice-=getPrice(radioButton.getText());
                        toppings.remove(radioButton.getText());
                    }
                }
            });
            p.add(radioButton);
            contentList.add(radioButton);
        }
        //p.add((JComponent) buttonGroup);
        nextButton=new JButton(buttonName);
        nextButton.addActionListener(this::actionPerformed);
        contentList.add(nextButton);
        nextButton.setFont(new Font("times new roman", Font.CENTER_BASELINE, 20));
        //nextButton.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/4+20,0,(FRAME_WIDTH-bigPanel.getWidth())/4+20));
        nextButton.setBackground(Color.BLACK);
        nextButton.setForeground(new Color(225,225,0));
        p.add(nextButton);
        //p.setPreferredSize(new Dimension(max*30+20,300));

        //bigPanel.setPreferredSize(bigPanel.getPreferredSize());
        bigPanel.add(p);
        bigPanel.setBorder(new EmptyBorder(0,(FRAME_WIDTH-bigPanel.getWidth())/4,0,(FRAME_WIDTH-bigPanel.getWidth())/4));

        return bigPanel;
    }

    public void clearComponents(){
        back.remove(bigPanel);
    }
    public int getPrice(String s){
        for(PizzaFragment p: pizzas){
            if(p.name.equals(s)){
                return p.price;
            }
        }
        return 0;
    }
    public static void main (String[] args)  {

            EventQueue.invokeLater(() -> {
                new DomingosPizza().setVisible(true);
            });

    }

    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private void writeTheData(Pizza pizza) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(pizza);
        post("https://fatima-s-stuff.firebaseio.com/Pizzas/.json",json);
    }

    //does a POST request, which in Firebase's API creates a random key as a parent to what we're going to create.
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }

}
