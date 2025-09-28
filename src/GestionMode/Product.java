package GestionMode;

public class Product {
    private int ProductId;  // Add this field
    private String ProductN;
    private int ProductQtt;
    private int ProductPrice;

    // Constructor
    public Product(int ProductId, String ProductN, int ProductQtt, int ProductPrice) {
        this.ProductId = ProductId;
        this.ProductN = ProductN;
        this.ProductQtt = ProductQtt;
        this.ProductPrice = ProductPrice;
    }

    // Overloaded constructor for use when ID isn't immediately needed
    public Product(String ProductN, int ProductQtt, int ProductPrice) {
        this.ProductN = ProductN;
        this.ProductQtt = ProductQtt;
        this.ProductPrice = ProductPrice;
    }
    public Product(int ProductId,String ProductN, int ProductQtt) {
        this.ProductId = ProductId;
        this.ProductN = ProductN;
        this.ProductQtt = ProductQtt;
    }

    // Getters and Setters
    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    public String getProductN() {
        return ProductN;
    }

    public void setProductN(String ProductN) {
        this.ProductN = ProductN;
    }

    public int getProductQtt() {
        return ProductQtt;
    }

    public void setProductQtt(int ProductQtt) {
        this.ProductQtt = ProductQtt;
    }

    public int getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(int ProductPrice) {
        this.ProductPrice = ProductPrice;
    }
}
