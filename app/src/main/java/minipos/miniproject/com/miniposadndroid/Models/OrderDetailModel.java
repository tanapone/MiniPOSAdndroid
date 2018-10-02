package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.annotations.Expose;

public class OrderDetailModel {
    @Expose
    private ProductModel.Product product;
    @Expose
    private int productAmount;

    public ProductModel.Product getProduct() {
        return product;
    }

    public void setProduct(ProductModel.Product product) {
        this.product = product;
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }
}
