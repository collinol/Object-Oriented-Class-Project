package camelinaction;
public class FoodPayment implements PaymentStrategy{

    private double[] payments = new double[2];
    //array to hold [paymentToSeller, paymentToAuctionHouse]
    @Override
    public  double[] payOut(ItemTemplate itemSold){
        payments[0] = .03*(itemSold.getCurrentPrice());

        payments[1] = .97*(itemSold.getCurrentPrice());
        return payments;
    }
}
