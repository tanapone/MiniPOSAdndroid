package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class ProductModel {
    @Expose
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
        @Expose
        private int id = 0;
        @Expose
        private String productName;
        @Expose
        private String productBarcodeID;
        @Expose
        private double productCapitalPrice;
        @Expose
        private double productSalePrice;
        @Expose
        private int productMinimum;
        @Expose
        private int productQty;
        @Expose
        private CategoryModel.Category category;
        @Expose
        private CompanyModel.Company company;

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

        public void setCategory(CategoryModel.Category category) {
            this.category = category;
        }

        public CompanyModel.Company getCompany() {
            return company;
        }

        public void setCompany(CompanyModel.Company company) {
            this.company = company;
        }

        public CategoryModel.Category getCategory() {
            return category;
        }
    }
}
