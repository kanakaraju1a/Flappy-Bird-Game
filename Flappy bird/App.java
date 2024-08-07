import javax.swing.*;
  
public class App
{
    public static void main(String args[])
    {
        int bwidth=300;
        int bheight=600;
        JFrame frame=new JFrame("FLY bird");
        frame.setVisible(true);
        frame.setSize(bwidth,bheight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  Flybirds flybird=new Flybirds();
  frame.add(flybird);
  frame.pack();
  flybird.requestFocus();
  frame.setVisible(true);

    }
}
