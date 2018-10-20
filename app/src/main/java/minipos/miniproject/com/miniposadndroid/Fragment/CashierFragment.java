package minipos.miniproject.com.miniposadndroid.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import minipos.miniproject.com.miniposadndroid.LoginActivity;
import minipos.miniproject.com.miniposadndroid.MainActivity;
import minipos.miniproject.com.miniposadndroid.Models.CategoryModel;
import minipos.miniproject.com.miniposadndroid.Models.MessageModel;
import minipos.miniproject.com.miniposadndroid.Models.OrderDetailModel;
import minipos.miniproject.com.miniposadndroid.Models.OrderModel;
import minipos.miniproject.com.miniposadndroid.Models.ProductModel;
import minipos.miniproject.com.miniposadndroid.Models.UserModel;
import minipos.miniproject.com.miniposadndroid.Printer.PrinterHelper;
import minipos.miniproject.com.miniposadndroid.R;
import minipos.miniproject.com.miniposadndroid.WSTask.WSTask;

import static xdroid.toaster.Toaster.toast;

public class CashierFragment extends Fragment  implements View.OnClickListener {

    private String barcodeResult;
    private ImageButton searchButton;
    private ImageButton scanButton;
    private EditText searchText;
    private ScrollView scrollViewLayout;
    private UserModel.User user;
    private TableLayout listItemTableLayout;
    private TextView totalPriceTextView;
    private TextView noProductInOrderTextView;
    private Button saveOrderBtn;
    private Button searchCategoryBtn;
    private Button moneyChangeBtn;
    private PrinterHelper printerHelper;
    private Button printResceiptBtn;
    // list all products
    private List<ProductModel.Product> allProducts = new ArrayList<ProductModel.Product>();
    // list by search product
    private List<ProductModel.Product> searchProducts = new ArrayList<ProductModel.Product>();
    // list products in cart
    private List<ProductModel.Product> productsInOrder = new ArrayList<ProductModel.Product>();
    // list all categories
    private List<CategoryModel.Category> allCategories = new ArrayList<CategoryModel.Category>();
    //Sum price to calculate
    private double sumPrice =0;
    //Total price
    private double totalPrice = 0;
    //TotalPrice for moneyChange
    private double totalPriceForMoneyChange = 0;
    //Position for check category
    int positionInCategoryInSpinner = -1;
    //Gson show all @EXPOSE
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    //ProgressDialog
    private ProgressDialog progressDialog;
    //Hander
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Set view from layout
        View view = inflater.inflate(R.layout.fragment_cashier, container, false);
        searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        scanButton = (ImageButton) view.findViewById(R.id.scanButton);
        scrollViewLayout = (ScrollView) view.findViewById(R.id.scrollViewLayout);
        listItemTableLayout = (TableLayout) view.findViewById(R.id.listItemTableLayout);
        searchText = (EditText) view.findViewById(R.id.searchText);
        totalPriceTextView = (TextView) view.findViewById(R.id.totalPriceTextView);
        noProductInOrderTextView = (TextView) view.findViewById(R.id.noProductInOrderTextView);
        saveOrderBtn = (Button) view.findViewById(R.id.saveOrderBtn);
        searchCategoryBtn = (Button) view.findViewById(R.id.searchCategoryBtn);
        printResceiptBtn = (Button) view.findViewById(R.id.printResceiptBtn);
        moneyChangeBtn = (Button) view.findViewById(R.id.moneyChangeBtn);

        searchButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        saveOrderBtn.setOnClickListener(this);
        searchCategoryBtn.setOnClickListener(this);
        printResceiptBtn.setOnClickListener(this);
        moneyChangeBtn.setOnClickListener(this);
        //Get User from MainActivity
        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();
        //Set ProgessDialog and Hander
        progressDialog = new ProgressDialog(getContext());
        handler = new Handler();

        //Set printerHelper
        printerHelper = new PrinterHelper(getContext());

        //Set list all products
        progressDialog.setMessage("กำลังโหลดข้อมูลสินค้า...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String responseData = null;
                try {
                    responseData = new WSTask(getContext()).execute("/products/mobile/?authKey="+user.getAuthKey(),"GET").get();
                    Type productListType = new TypeToken<ArrayList<ProductModel.Product>>(){}.getType();
                    allProducts = new Gson().fromJson(responseData,productListType);
                    progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        },1000);

        return view;
    }

    //Activity when scanned
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(),"ยกเลิกการ แสกนบาร์โค้ด",Toast.LENGTH_SHORT).show();
            } else {
                barcodeResult = result.getContents();
                new SearchProductController().searchByCriteria("Barcode");

            }
        }
    }

    //Set view on click listener
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.searchButton :
                    new SearchProductController().searchByCriteria("Name");
                break;
            case R.id.scanButton:
                scanBarcode();
                break;
            case R.id.searchCategoryBtn:
                    new SearchProductController().searchByCriteria("Category");
                break;
            case R.id.saveOrderBtn:
                new ConfirmOrderController().confirmOrder(productsInOrder);
                break;
            case R.id.printResceiptBtn:
                new PrintReceiptController().settingUpOrder();
                break;
            case R.id.moneyChangeBtn:
                showChangeMoneyDialog();
                break;
        }
    }

    //Scan barcode
    public void scanBarcode(){
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    //Show change money dialog
    public void showChangeMoneyDialog(){
        LayoutInflater inflater;
        View dialogView;
        inflater = LayoutInflater.from(getActivity());
        dialogView = inflater.inflate(R.layout.money_change_dialog, null);
        final EditText moneyChangeEditText = (EditText) dialogView.findViewById(R.id.moneyChangeEditText);
        final TextView moneyChangeTextView = (TextView) dialogView.findViewById(R.id.moneyChangeTextView);
        Button calMoneyChangeBtn = (Button) dialogView.findViewById(R.id.calMoneyChangeBtn);

        calMoneyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double moneyInput = Double.valueOf(moneyChangeEditText.getText().toString());
                if(moneyInput < totalPriceForMoneyChange) {
                toast("จำนวนเงินที่กรอกน้อยกว่าเงินรวมของรายการขาย");
                }else {
                    double moneyChange = (moneyInput - totalPriceForMoneyChange);
                    moneyChangeTextView.setText(moneyChange + " บาท");
                }
            }
        });

        final AlertDialog moneyChangeDialog = new AlertDialog.Builder(getActivity())
                .setTitle("คำนวณเงินทอนให้แก่ลูกค้า")
                .setMessage("ราคารวม : "+totalPriceForMoneyChange + " บาท")
                .setView(dialogView).create();
        moneyChangeDialog.show();
    }


    public void calculateTotal(){
        //Set total price text
        double sumPrice = 0;
        for(ProductModel.Product product : productsInOrder){
            sumPrice += (product.getProductSalePrice()*product.getProductQty());
        }
        totalPrice+=sumPrice;
        totalPriceTextView.setText(totalPrice+" บาท");
        totalPriceForMoneyChange = totalPrice;
        totalPrice = 0;
    }
    public String subProductName(String productName){
        String result = productName;
        if(productName.length()>13){
            result = productName.substring(0,12)+"...";
        }
        return result;
    }




    //Controllers
    //SearchProductController
    public class SearchProductController{

        public void searchByCriteria(String criteria){
            if(criteria.equalsIgnoreCase("Name")){
                searchProducts.clear();
                final String productSearchName = searchText.getText().toString();
                progressDialog.setMessage("กำลังโหลด...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseData = new WSTask(getContext()).execute("/product/mobile/searchName?authKey="+user.getAuthKey()
                                    +"&name="+productSearchName,"GET").get();
                            Type productListType = new TypeToken<ArrayList<ProductModel.Product>>(){}.getType();
                            searchProducts = new Gson().fromJson(responseData,productListType);
                            progressDialog.dismiss();
                            //Set searchProduct
                            for(int i=0;i<allProducts.size();i++){
                                for(int j=searchProducts.size()-1;j>=0;j--){
                                    if(searchProducts.get(j).getId() == allProducts.get(i).getId()){
                                        searchProducts.get(j).setProductQty(allProducts.get(i).getProductQty());
                                    }
                                }
                            }
                            showSelectProductDialog(searchProducts);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }



                    }
                },1000);

            }else if(criteria.equalsIgnoreCase("Barcode")){
                searchProducts.clear();
                progressDialog.setMessage("กำลังโหลด...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseData = new WSTask(getContext()).execute("/product/mobile/searchBarcode?authKey="+user.getAuthKey()
                                    +"&barcodeID="+barcodeResult,"GET").get();
                            ProductModel product = new ProductModel(responseData);
                            if(product.getProduct()==null){
                                toast("ไม่พบข้อมูลสินค้า");
                            }else{
                                //Set searchProduct by barcode
                                for(int i=0;i<allProducts.size();i++){
                                    if(product.getProduct().getId() == allProducts.get(i).getId()){
                                        product.getProduct().setProductQty(allProducts.get(i).getProductQty());
                                    }
                                }
                                showSetQuantityDialog(product.getProduct());
                            }
                            progressDialog.dismiss();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                },1000);

            }else{
                searchProducts.clear();
                //Search by category
                progressDialog.setMessage("กำลังโหลดข้อมูลประเภทสินค้า...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Set all categories
                            allCategories.clear();
                            String responseData = new WSTask(getContext()).execute("/categories/mobile?authKey=" + user.getAuthKey(), "GET").get();
                            Type categoriesListType = new TypeToken<ArrayList<CategoryModel.Category>>() {}.getType();
                            allCategories = new Gson().fromJson(responseData, categoriesListType);
                            showSelectCategoryDialog(allCategories);
                            progressDialog.dismiss();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                },1000);
            }
        }

        //Show select category dialog
        public void showSelectCategoryDialog(List<CategoryModel.Category> categories){
            //Define select category dialog
            LayoutInflater inflater;
            View dialogView;
            inflater = LayoutInflater.from(getActivity());
            dialogView = inflater.inflate(R.layout.select_category_dialog, null);
            final Spinner categorySpinner = (Spinner) dialogView.findViewById(R.id.categorySpinner);
            final Button searchProductsByCategoryBtn = (Button) dialogView.findViewById(R.id.searchProductsByCategoryBtn);
            //Define category name list
            List<String> categoriesNamesArray = new ArrayList<String>();
            //Set categories names
            for(CategoryModel.Category category : categories){
                categoriesNamesArray.add(category.getCategoryName());
            }
            //Define arrayAdapter for category spinner
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item,categoriesNamesArray);
            //Set adaptor for category spinner
            categorySpinner.setAdapter(arrayAdapter);

            //Check when select position
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    positionInCategoryInSpinner = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            final AlertDialog selectCategorydialog = new AlertDialog.Builder(getActivity())
                    .setTitle("กรุณาเลือกประเภทสินค้า")
                    .setView(dialogView).create();
            selectCategorydialog.show();

            searchProductsByCategoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CategoryModel.Category category = allCategories.get(positionInCategoryInSpinner);
                    searchProducts.clear();


                    //
                   progressDialog.setMessage("กำลังโหลดข้อมูลสินค้า...");
                   progressDialog.setCanceledOnTouchOutside(false);
                   progressDialog.show();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               String responseData = new WSTask(getContext()).execute("/product/mobile/category/"
                                       +category.getId()+"?authKey="+user.getAuthKey(),"GET").get();
                               Type productListType = new TypeToken<ArrayList<ProductModel.Product>>(){}.getType();
                               searchProducts = new Gson().fromJson(responseData,productListType);
                               progressDialog.dismiss();
                               //Set searchProduct
                               for(int i=0;i<allProducts.size();i++){
                                   for(int j=searchProducts.size()-1;j>=0;j--){
                                       if(searchProducts.get(j).getId() == allProducts.get(i).getId()){
                                           searchProducts.get(j).setProductQty(allProducts.get(i).getProductQty());
                                       }
                                   }
                               }
                               showSelectProductDialog(searchProducts);
                               selectCategorydialog.dismiss();
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           } catch (ExecutionException e) {
                               e.printStackTrace();
                           }
                       }
                   },1000);

                }
            });

        }

        //Show select product dialog
        public void showSelectProductDialog(final List<ProductModel.Product> productArrayList) {
            if (productArrayList.size() < 1) {
                toast("ไม่พบข้อมูลสินค้า");
            } else {
                //Define product name array list to show in dialog
                ArrayList<String> productNameArrayList = new ArrayList<String>();

                productNameArrayList.clear();
                //loop set line string to show in dialog get list from agument
                for (ProductModel.Product product : productArrayList) {
                    productNameArrayList.add(product.getProductName() +
                            "\n" + "จำนวนสินค้า" +
                            " " + product.getProductQty());
                }
                //defind dialog show list searched product
                LayoutInflater inflater;
                View dialogView;
                ListView productListView;
                inflater = LayoutInflater.from(getActivity());
                dialogView = inflater.inflate(R.layout.product_select_dialog, null);
                productListView = (ListView) dialogView.findViewById(R.id.productListView);
                ArrayAdapter<String> productListViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, productNameArrayList);
                final AlertDialog selectProductdialog = new AlertDialog.Builder(getActivity())
                        .setTitle("กรุณาเลือกสินค้า")
                        .setView(dialogView).create();
                //set list to list view
                productListView.setAdapter(productListViewAdapter);
                //set list on item click
                productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showSetQuantityDialog(productArrayList.get(position));
                        selectProductdialog.dismiss();
                    }
                });
                selectProductdialog.show();
            }
        }
        public void showSetQuantityDialog(final ProductModel.Product product){
            //Set SetQuantityDialog
            LayoutInflater inflater;
            final View dialogView;
            ListView productListView;
            inflater = LayoutInflater.from(getActivity());
            dialogView = inflater.inflate(R.layout.set_quantity_dialog, null);
            final AlertDialog setQuantityDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("กรุณาเลือกสินค้า")
                    .setMessage("จำนวนสินค้าในคลัง : "+product.getProductQty()+" ชิ้น")
                    .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                            int quantity = 0;
                            EditText quantityEditText = (EditText) dialogView.findViewById(R.id.qtyEditText);

                            if(quantityEditText.getText().toString().trim().equals("")){
                                quantity = 1;
                            }else{
                                quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                            }
                            //Check Stock less then quantity
                            if(quantity > product.getProductQty()){
                                toast("จำนวนสินค้าที่คุณกรอกมีมากกว่าจำนวนสินค้าที่อยู่ภายในคลัง");
                            }else{
                                //Update stock
                                for(int i =0;i<allProducts.size();i++){
                                    if(product.getId() == allProducts.get(i).getId()){
                                        int qty = allProducts.get(i).getProductQty() - quantity;
                                        allProducts.get(i).setProductQty(qty);
                                        break;
                                    }
                                }
                                //Use add order
                                product.setProductQty(quantity);
                                new AddOrderController().addOrder(product);
                            }
                        }
                    })
                    .setView(dialogView).create();
            setQuantityDialog.show();


        }

    }

    //AddOrderController
    public class AddOrderController{

        public int getSameProductIndexInOrder(ProductModel.Product product){
            int index = -1;
            for(int i=0;i<productsInOrder.size();i++){
                if(product.getId() == productsInOrder.get(i).getId()){
                    index = i;
                    break;
                }
            }
            return index;
        }

        public void addOrder(ProductModel.Product product){
            //if not found same product in order will return -1
            int sameProductInOrderIndex = getSameProductIndexInOrder(product);
            if(sameProductInOrderIndex!=-1){
                int newQty = (product.getProductQty() + productsInOrder.get(sameProductInOrderIndex).getProductQty());
                product.setProductQty(newQty);
                //Update product in order
                productsInOrder.set(sameProductInOrderIndex,product);
            }else{
                productsInOrder.add(product);
            }

            new ListOrderController().updateListTableLayout();
        }
    }

    //ListOrderController
    public class ListOrderController{

        public void clearListTableLayout(){
            listItemTableLayout.removeAllViews();
            TextView headerName = new TextView(getContext());
            TextView headerQty = new TextView(getContext());
            TextView headerPrice = new TextView(getContext());
            TextView headerDelete = new TextView(getContext());

            headerName.setPadding(10,10,10,10);
            headerName.setBackgroundColor(Color.parseColor("#009688"));
            headerName.setText("ชื่อสินค้า");
            headerName.setTextColor(Color.WHITE);
            headerName.setGravity(Gravity.CENTER);

            headerQty.setPadding(10,10,10,10);
            headerQty.setBackgroundColor(Color.parseColor("#009688"));
            headerQty.setText("จำนวนสินค้า");
            headerQty.setTextColor(Color.WHITE);
            headerQty.setGravity(Gravity.CENTER);

            headerPrice.setPadding(10,10,10,10);
            headerPrice.setBackgroundColor(Color.parseColor("#009688"));
            headerPrice.setText("ราคา");
            headerPrice.setTextColor(Color.WHITE);
            headerPrice.setGravity(Gravity.CENTER);

            headerDelete.setPadding(10,10,10,10);
            headerDelete.setBackgroundColor(Color.parseColor("#009688"));
            headerDelete.setText("ลบ");
            headerDelete.setTextColor(Color.WHITE);
            headerDelete.setGravity(Gravity.CENTER);

            TableRow tableRow = new TableRow(getContext());
            tableRow.addView(headerName);
            tableRow.addView(headerQty);
            tableRow.addView(headerPrice);
            tableRow.addView(headerDelete);

            listItemTableLayout.addView(tableRow);
        }

        public void updateListTableLayout(){
            //Check product in order to show no product in order text
            if(productsInOrder.size()>0){
                noProductInOrderTextView.setVisibility(View.INVISIBLE);
                //Clear list table layout
                clearListTableLayout();
                //Set moneyChangeBtn able to click
                moneyChangeBtn.setEnabled(true);

            }else{
                noProductInOrderTextView.setVisibility(View.VISIBLE);
                listItemTableLayout.removeAllViews();
                //Set moneyChangeBtn Unable to click
                moneyChangeBtn.setEnabled(false);
            }


            //loop list table layout
            int index = 0;
            for(final ProductModel.Product product : productsInOrder){
                TableRow tableRowData = new TableRow(getActivity());
                final TextView productName = new TextView(getActivity());

                productName.setPadding(14,14,14,14);
                productName.setBackgroundColor(Color.parseColor("#FFFFFF"));

                productName.setText(subProductName(product.getProductName()));

                //Show product details
                productName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast(product.getProductName());
                    }
                });

                productName.setGravity(Gravity.CENTER);
                TextView productQuantity = new TextView(getActivity());
                productQuantity.setPadding(14,14,14,14);
                productQuantity.setBackgroundColor(Color.parseColor("#FFFFFF"));
                productQuantity.setText(String.valueOf(product.getProductQty()));
                productQuantity.setGravity(Gravity.CENTER);

                TextView productPrice = new TextView(getActivity());
                productPrice.setText(String.valueOf(product.getProductSalePrice()*product.getProductQty()));
                productPrice.setPadding(14,14,14,14);
                productPrice.setBackgroundColor(Color.parseColor("#FFFFFF"));

                productPrice.setGravity(Gravity.CENTER);
                ImageView trash = new ImageView(getActivity());
                trash.setImageResource(R.drawable.ic_delete_black_24dp);
                trash.setPadding(6,6,6,6);
                trash.setBackgroundColor(Color.parseColor("#FFFFFF"));

                final int finalIndex = index;
                trash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RemoveOrderController().removeOrder(product);
                    }
                });


                tableRowData.addView(productName);
                tableRowData.addView(productQuantity);
                tableRowData.addView(productPrice);
                tableRowData.addView(trash);

                listItemTableLayout.addView(tableRowData);

                index++;
            }
            //Calculate and setTotal text
            calculateTotal();
        }
    }

    //RemoveOrderController
    public class RemoveOrderController{
        public void removeOrder(final ProductModel.Product product){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Update stock
                            int index = 0;
                            for(int i=0;i<allProducts.size();i++){
                                if(allProducts.get(i).getId() == product.getId()){
                                    int qty = product.getProductQty();
                                    int newQty = allProducts.get(i).getProductQty() + qty;
                                    allProducts.get(i).setProductQty(newQty);
                                    break;
                                }
                            }
                            // Get position for remove
                            for(int i=0;i<productsInOrder.size();i++){
                                if(product.getId() == productsInOrder.get(i).getId()){
                                    index = i;
                                    break;
                                }
                            }
                            productsInOrder.remove(index);
                            totalPrice = 0;
                            new ListOrderController().updateListTableLayout();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("คุณต้องการลบสินค้านี้ ?")
                    .setMessage(product.getProductName())
                    .setPositiveButton("ตกลง", dialogClickListener)
                    .setNegativeButton("ยกเลิก", dialogClickListener).show();
        }
    }

    //ConfrimOrderController
    public class ConfirmOrderController{

        public void confirmOrder(List<ProductModel.Product> productList) {
            if (productList.size() < 1) {
                toast("กรุณาเลือกสินค้าก่อนทำการบันทึกการขาย");
            } else {
                OrderModel orderModel = new OrderModel();
                List<OrderDetailModel> orderDetailModels = new ArrayList<OrderDetailModel>();
                for (ProductModel.Product product : productList) {
                    OrderDetailModel orderDetailModel = new OrderDetailModel();
                    orderDetailModel.setProduct(product);
                    orderDetailModel.setProductAmount(product.getProductQty());
                    //
                    orderDetailModels.add(orderDetailModel);
                }
                System.out.println(new Gson().toJson(orderDetailModels));
                orderModel.getOrder().setOrderdetails(orderDetailModels);
                orderModel.getOrder().setUser(user);
                try {
                    System.out.println("Order = " + new Gson().toJson(orderModel.getOrder()).toString());
                    String responseData = new WSTask(getContext()).execute("/create/order?authKey=" + user.getAuthKey(), "POST", gson.toJson(orderModel.getOrder()).toString()).get();
                    MessageModel messageModel;
                    if (responseData != null) {
                        messageModel = new MessageModel(responseData);
                        if (messageModel.getMessage().getMessageText().equalsIgnoreCase("Success")) {
                            toast("บันทึกสำเร็จ");
                            if(printResceiptBtn.isEnabled()==false){
                                printResceiptBtn.setEnabled(true);
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //PrintReceiptController
    public class PrintReceiptController {
        //define order for getting order id
        OrderModel order;

        public void settingUpOrder() {
            progressDialog.setMessage("กำลังตั้งค่าหมายเลขออเดอร์...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        String responseData = new WSTask(getContext()).execute("/order/last?authKey=" + user.getAuthKey(), "GET").get();
                        order = new OrderModel(responseData);
                        progressDialog.dismiss();
                        if (order.getOrder().getId() == 0) {
                            toast("เกิดข้อผิดพลาดในการตั้งค่าออเดอร์");
                        } else {
                            checkBluetoothConnected();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }

        public void checkBluetoothConnected() {
            if (printerHelper.connectedBluetooth() == false) {
                printerHelper.findBuletooth();
                toast("หลังจากเลือกเครื่องปริ้นท์บลูธูทเสร็จ กรุณากดปุ่มพิมพ์ใบเสร็จอีกครั้ง");
            } else {
                printReceipt();
            }
        }

        public void printReceipt(){
            printerHelper.resetPrint();
            printerHelper.setCenter();
            printerHelper.printText("MiniPOS",25,"center");
            printerHelper.printText("-----------------------------------------", 20, "center");
            printerHelper.printText("หมายเลขออเดอร์ : "+order.getOrder().getId(),25,"center");
            printerHelper.printText("รายการสินค้าทั้งหมด",25,"left");
            for (ProductModel.Product product : productsInOrder) {
                printerHelper.printText(product.getProductName() + " x" + product.getProductQty(), 20, "left");
                printerHelper.printText("ราคาสินค้า" + " " + product.getProductSalePrice() + " " + "ต่อชิ้น", 20, "opppsite");
                printerHelper.printNewLine();
                printerHelper.printNewLine();
            }
            printerHelper.printText("-----------------------------------------", 20, "center");
            printerHelper.printText("ราคารวม : "+String.valueOf(totalPriceTextView.getText().toString().trim()), 25, "opppsite");
            printerHelper.printNewLine();
            printerHelper.printText("วันที่ทำรายการ"+ " " + printerHelper.getDate(), 23, "center");
            printResceiptBtn.setEnabled(false);
            productsInOrder.clear();
            new ListOrderController().updateListTableLayout();
        }
    }


}
