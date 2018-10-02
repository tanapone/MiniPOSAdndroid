package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderModel {
    @Expose
    private long id;
    private Date orderDate = new Date();
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
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
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
