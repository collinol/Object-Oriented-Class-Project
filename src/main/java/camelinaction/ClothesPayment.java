package camelinaction;
public class ClothesPayment implements PaymentStrategy{

    private double[] payments = new double[2];
    //array to hold [paymentToSeller, paymentToAuctionHouse]
    @Override
    public  double[] payOut(ItemTemplate itemSold){
        payments[0] = .08*(itemSold.getCurrentPrice());
        payments[1] = .92*(itemSold.getCurrentPrice());
        return payments;
    }
}
