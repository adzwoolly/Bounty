/**
 * Created by Aaron on 17/02/2016.
 */
public class Cake {

    public static void main(String[] args){
        new DeadCat();
        while(true){
            System.out.println("Beth Cake");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
