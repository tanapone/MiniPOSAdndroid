package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProductModel {

    private Product product;

    public ProductModel(){}

    public Product getProduct(){
        return this.product;
    }

    public ProductModel(String jsonResponse){
        Gson gson  = new GsonBuilder().create();
        this.product = gson.fromJson(jsonResponse,Product.class);
    }

    public class Product{
        private int id = 0;
        private String productName;
        private String productBarcodeID;
        private double productCapitalPrice;
        private double productSalePrice;
        private int productMinimum;
        private int productQty;
        private CategoryModel category;
        private CompanyModel company;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductBarcodeID() {
            return productBarcodeID;
        }

        public void setProductBarcodeID(String productBarcodeID) {
            this.productBarcodeID = productBarcodeID;
        }

        public double getProductCapitalPrice() {
            return productCapitalPrice;
        }

        public void setProductCapitalPrice(double productCapitalPrice) {
            this.productCapitalPrice = productCapitalPrice;
        }

        public double getProductSalePrice() {
            return productSalePrice;
        }

        public void setProductSalePrice(double productSalePrice) {
            this.productSalePrice = productSalePrice;
        }

        public int getProductMinimum() {
            return productMinimum;
        }

        public void setProductMinimum(int productMinimum) {
            this.productMinimum = productMinimum;
        }

        public int getProductQty() {
            return productQty;
        }

        public void setProductQty(int productQty) {
            this.productQty = productQty;
        }

        public CategoryModel getCategory() {
            return category;
        }

        public void setCategory(CategoryModel category) {
            this.category = category;
        }

        public CompanyModel getCompany() {
            return company;
        }

        public void setCompany(CompanyModel company) {
            this.company = company;
        }
    }
}
