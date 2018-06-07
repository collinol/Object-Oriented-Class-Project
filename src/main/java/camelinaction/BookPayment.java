package camelinaction;
public class BookPayment implements PaymentStrategy{

    private double[] payments = new double[2];
    //array to hold [paymentToSeller, paymentToAuctionHouse]
    @Override
    public  double[] payOut(ItemTemplate itemSold){
        payments[0] = .02*(itemSold.getCurrentPrice());
        payments[1] = .98*(itemSold.getCurrentPrice());
        return payments;
    }
}
