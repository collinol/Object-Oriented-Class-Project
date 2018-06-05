package camelinaction;
public class ElectronicsPayment implements PaymentStrategy{

    private double[] payments = new double[2];
    //array to hold [paymentToSeller, paymentToAuctionHouse]
    @Override
    public  double[] payOut(ItemTemplate itemSold){

        payments[0] = .05*(itemSold.getCurrentPrice());
        payments[1] = .95*(itemSold.getCurrentPrice());
        return payments;
    }
}
