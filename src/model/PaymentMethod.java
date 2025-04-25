package model;

public class PaymentMethod {
    private int paymentMethodID;
    private String paymentMethodName;

    public PaymentMethod() {
        
    }

    public PaymentMethod(int paymentMethodID, String paymentMethodName) {
        this.paymentMethodID = paymentMethodID;
        this.paymentMethodName = paymentMethodName;
    }

    public int getPaymentMethodID() {
        return paymentMethodID;
    }

    public void setPaymentMethodID(int paymentMethodID) {
        this.paymentMethodID = paymentMethodID;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }
}
