package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderModel {
    @Expose
    private Order order = new Order();

    public OrderModel(){}

    public OrderModel(String jsonResponse){
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        this.order = gson.fromJson(jsonResponse,Order.class);
    }

    public class Order{
        @Expose
        private long id;
        private Date orderDates = new Date();
        @Expose
        private UserModel.User user;
        @Expose
        private List<OrderDetailModel> orderDetails = new ArrayList<OrderDetailModel>();
        @Expose
        private double sumPrice;
        @Expose
        private double profit;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Date getOrderDate() {
            return orderDates;
        }

        public void setOrderDate(Date orderDate) {
            this.orderDates = orderDate;
        }

        public UserModel.User getUser() {
            return user;
        }

        public void setUser(UserModel.User user) {
            this.user = user;
        }

        public List<OrderDetailModel> getOrderdetails() {
            return orderDetails;
        }

        public void setOrderdetails(List<OrderDetailModel> orderdetails) {
            this.orderDetails = orderdetails;
        }

        public double getSumPrice() {
            return sumPrice;
        }

        public void setSumPrice(double sumPrice) {
            this.sumPrice = sumPrice;
        }

        public double getProfit() {
            return profit;
        }

        public void setProfit(double profit) {
            this.profit = profit;
        }
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
