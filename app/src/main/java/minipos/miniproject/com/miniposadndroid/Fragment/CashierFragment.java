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
    private Button testBtn;
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
    //Position for check category
    int positionInCategoryInSpinner = -1;
    //Gson show all @EXPOSE
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

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
        testBtn = (Button) view.findViewById(R.id.testBtn);

        searchButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        saveOrderBtn.setOnClickListener(this);
        searchCategoryBtn.setOnClickListener(this);
        testBtn.setOnClickListener(this);
        //Get User from MainActivity
        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();
        //Set list all products
        try {
            String responseData = new WSTask(getContext()).execute("/products/mobile/?authKey="+user.getAuthKey(),"GET").get();
            Type productListType = new TypeToken<ArrayList<ProductModel.Product>>(){}.getType();
            allProducts = new Gson().fromJson(responseData,productListType);

            for(ProductModel.Product product : allProducts){
                System.out.println("product ID : "+ product.getId());
                System.out.println("product category name : "+ product.getCategory().getCategoryName());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
                new SearchProductController(getContext()).searchProductByBarcode(barcodeResult);
            }
        }
    }

    //Set view on click listener
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.searchButton :
                try {
                    searchProduct();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.scanButton:
                scanBarcode();
                break;
            case R.id.saveOrderBtn:
                showConfirmOrderDialog();
                break;
            case R.id.searchCategoryBtn:
                showCategoryDialoag();
                break;
            case R.id.testBtn:
                    for(ProductModel.Product product : allProducts){
                        System.out.println(product.getProductName()+" Qty : "+product.getProductQty());
                    }
                break;
        }
    }
    // searchProductByName
    public void searchProduct() throws ExecutionException, InterruptedException {
        String searchProductName = searchText.getText().toString();
        SearchProductController searchProductController = new SearchProductController(getContext());
        searchProductController.searchProductByName(searchProductName);

    }
    //Scan barcode
    public void scanBarcode(){
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    public void showCategoryDialoag(){
        new SearchProductController(getContext()).showCategoryDialog();
    }

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

        }else{
            noProductInOrderTextView.setVisibility(View.VISIBLE);
            listItemTableLayout.removeAllViews();
        }


        //loop list table layout
        int index = 0;
        for(final ProductModel.Product product : productsInOrder){
            TableRow tableRowData = new TableRow(getActivity());
            TextView productName = new TextView(getActivity());

            productName.setPadding(14,14,14,14);
            productName.setBackgroundColor(Color.parseColor("#FFFFFF"));

            productName.setText(subProductName(product.getProductName()));

            //Show product details
            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                    new removeOrderController().removeOrder(product);
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

    public void calculateTotal(){
        //Set total price text
        double sumPrice = 0;
        for(ProductModel.Product product : productsInOrder){
            sumPrice += (product.getProductSalePrice()*product.getProductQty());
        }
        totalPrice+=sumPrice;
        totalPriceTextView.setText(totalPrice+" บาท");
        totalPrice = 0;
    }
    public String subProductName(String productName){
        String result = productName;
        if(productName.length()>13){
            result = productName.substring(0,12)+"...";
        }
        return result;
    }

    public void showConfirmOrderDialog(){
        if(productsInOrder.size()<1){
            toast("กรุณาเลือกสินค้าก่อนบันทึกสินค้า");
        }else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            new confirmOrderController().confirmOrder(productsInOrder);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("ยืนยัน")
                    .setMessage("คุณยืนยันที่จะบันทึกออเดอร์ ? ")
                    .setPositiveButton("ตกลง", dialogClickListener)
                    .setNegativeButton("ยกเลิก", dialogClickListener).show();
        }
    }

// SearchProductController
    public class SearchProductController{
       private Context context;

       public SearchProductController(Context context){
           this.context = context;
       }

       public void searchProductByName(final String productName){
           final ProgressDialog progressDialog = new ProgressDialog(this.context);
           progressDialog.setMessage("กำลังโหลด...");
           progressDialog.show();
           final Handler handler = new Handler();
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   try{
                       String responseData = new WSTask(getContext()).execute("/product/mobile/searchName?authKey="+user.getAuthKey()
                               +"&name="+productName,"GET").get();
                       Type productListType = new TypeToken<ArrayList<ProductModel.Product>>(){}.getType();
                       List<ProductModel.Product> productList = new Gson().fromJson(responseData,productListType);
                       searchProducts.clear();
                       searchProducts = productList;
                       if(productList.size()<1){
                           toast("ไม่พบข้อมูลสินค้า");
                       }else{
                           //Update searched product if product already in cart
                           for(ProductModel.Product productFromSearch : searchProducts){
                                for(int i=0; i<allProducts.size();i++){
                                    if(allProducts.get(i).getId() == productFromSearch.getId()){
                                        productFromSearch.setProductQty(allProducts.get(i).getProductQty());
                                    }
                                }
                           }
                           showSelectProductDialog(searchProducts);
                       }
                       progressDialog.dismiss();
                   }catch (InterruptedException e) {
                       e.printStackTrace();
                   } catch (ExecutionException e) {
                       e.printStackTrace();
                   }
               }
           },1000);
       }

       public void searchProductByBarcode(String barcode){
           boolean found = false;
           int index = -1;
           for(int i=0;i<allProducts.size();i++){
               if(allProducts.get(i).getProductBarcodeID().equalsIgnoreCase(barcode)){
                   found = true;
                   index = i;
               }
           }

           if(found){
               new addOrderController().addOrder(allProducts.get(index));
           }else{
               toast("ไม่พบข้อมูล");
           }
       }

        public void showSelectProductDialog(List<ProductModel.Product> productArrayList){
           //Define product name array list to show in dialog
            ArrayList<String> productNameArrayList = new ArrayList<String>();

            productNameArrayList.clear();
            //loop set line string to show in dialog get list from agument
            for(ProductModel.Product product : productArrayList){
                productNameArrayList.add(product.getProductName()+
                        "\n"+"จำนวนสินค้า"+
                        " "+product.getProductQty());
            }
            //defind dialog show list searched product
            LayoutInflater inflater;
            View dialogView;
            ListView productListView;
            inflater = LayoutInflater.from(getActivity());
            dialogView = inflater.inflate(R.layout.product_select_dialog, null);
            productListView = (ListView) dialogView.findViewById(R.id.productListView);
            ArrayAdapter<String> productListViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,productNameArrayList);
            final AlertDialog selectProductdialog = new AlertDialog.Builder(getActivity())
                    .setTitle("กรุณาเลือกสินค้า")
                    .setView(dialogView).create();
            //set list to list view
            productListView.setAdapter(productListViewAdapter);
            //set list on item click
            productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    new addOrderController().addOrder(searchProducts.get(position));
                    selectProductdialog.dismiss();
                }
            });
            selectProductdialog.show();
        }

        public void showCategoryDialog() {
           final ProgressDialog progressDialog = new ProgressDialog(getContext());
           progressDialog.setMessage("กำลังโหลด...");
           progressDialog.show();
           final Handler handler = new Handler();
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   try {
                       // Set all categories
                       allCategories.clear();
                       String responseData = new WSTask(getContext()).execute("/categories?authKey=" + user.getAuthKey(), "GET").get();
                       Type categoriesListType = new TypeToken<ArrayList<CategoryModel.Category>>() {}.getType();
                       allCategories = new Gson().fromJson(responseData, categoriesListType);
                       checkAllCategories();
                       progressDialog.dismiss();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   } catch (ExecutionException e) {
                       e.printStackTrace();
                   }
               }
           },1000);

        }

        public void checkAllCategories(){
            //Check category available
            if (allCategories.size() < 1) {
                toast("ไม่พบรายการประเภทสินค้าในระบบ");
            }else{
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
                for(CategoryModel.Category category : allCategories){
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
                        CategoryModel.Category category = allCategories.get(positionInCategoryInSpinner);
                        List<ProductModel.Product> productsFromCategoy = new ArrayList<ProductModel.Product>();
                        for(ProductModel.Product product : allProducts){
                            if(product.getCategory().getId() == category.getId()){
                                productsFromCategoy.add(product);
                            }
                        }
                        showSelectProductsDialogFromCategory(productsFromCategoy);
                        selectCategorydialog.dismiss();
                    }
                });

            }
        }

        public void showSelectProductsDialogFromCategory(final List<ProductModel.Product> products){
            //Define product name array list to show in dialog
            ArrayList<String> productNameArrayList = new ArrayList<String>();

            //loop set line string to show in dialog get list from agument
            for(ProductModel.Product product : products){
                productNameArrayList.add(product.getProductName()+
                        "\n"+"จำนวนสินค้า"+
                        " "+product.getProductQty());
            }
            //defind dialog show list searched product
            LayoutInflater inflater;
            View dialogView;
            ListView productListView;
            inflater = LayoutInflater.from(getActivity());
            dialogView = inflater.inflate(R.layout.product_select_dialog, null);
            productListView = (ListView) dialogView.findViewById(R.id.productListView);
            ArrayAdapter<String> productListViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,productNameArrayList);

            final AlertDialog selectProductdialogFromCategory = new AlertDialog.Builder(getActivity())
                    .setTitle("กรุณาเลือกสินค้า")
                    .setView(dialogView).create();
            selectProductdialogFromCategory.show();
            //set list to list view
            productListView.setAdapter(productListViewAdapter);
            //set list on item click
            productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new addOrderController().addOrder(products.get(position));
                    selectProductdialogFromCategory.dismiss();
                }
            });

        }

    }

    public class addOrderController{
        //Find same product in array
        public int findSameProduct(ProductModel.Product product){
            //Check same product
            int position = -1;
            for(int i=0;i<productsInOrder.size();i++){
                if(product.getId() == productsInOrder.get(i).getId()){
                    position = i;
                }
            }
            return position;
        }

        public void addOrder(final ProductModel.Product product){
            //Set dialog from layout
            LayoutInflater inflater;
            final View dialogView;
            inflater = LayoutInflater.from(getActivity());
            dialogView = inflater.inflate(R.layout.set_quantity_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("กรุณากรอกจำนวนสินค้า")
                    .setMessage("จำนวนสินค้าคงเหลือ : "+product.getProductQty())
                    .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Check quantitiy
                            int quantity = 0;
                            EditText quantityEditText = (EditText) dialogView.findViewById(R.id.qtyEditText);

                            if(quantityEditText.getText().toString().trim().equals("")){
                                quantity = 1;
                            }else{
                                quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                            }
                            //Check Stock less then quantity
                            if(quantity > product.getProductQty()){
                                Toast.makeText(getActivity(),"จำนวนสินค้าที่คุณกรอกมีมากกว่าจำนวนสินค้าที่อยู่ภายในคลัง",Toast.LENGTH_LONG).show();
                            }else{
                                if(positionInCategoryInSpinner==-1){
                                    //Update list all products stock
                                    for(int i=0;i<allProducts.size();i++){
                                        if(product.getId() == allProducts.get(i).getId()){
                                            int qty = allProducts.get(i).getProductQty() - quantity;
                                            allProducts.get(i).setProductQty(qty);
                                            product.setProductQty(quantity);
                                            break;
                                        }
                                    }
                                }else{
                                    for(int i=0;i<allProducts.size();i++){
                                        if(product.getId() == allProducts.get(i).getId()){
                                            int qty = allProducts.get(i).getProductQty() - quantity;
                                            product.setProductQty(quantity);
                                            toast(product.getProductName());
                                            allProducts.get(i).setProductQty(qty);
                                            break;
                                        }
                                    }
                                }

                                //Find same product if not found return -1
                                int findSameProductPoistion = findSameProduct(product);
                                if(findSameProductPoistion!=-1){
                                    //find same product and replace
                                    int newQty = productsInOrder.get(findSameProductPoistion).getProductQty() + quantity;
                                    product.setProductQty(newQty);
                                    productsInOrder.set(findSameProductPoistion,product);
                                }else{
                                    //Add product into cart
                                    product.setProductQty(quantity);
                                    productsInOrder.add(product);
                                }
                                updateListTableLayout();
                            }

                        }
                    })
                    .setView(dialogView).create();
            dialog.show();
        }
    }

    public class removeOrderController{
        public void removeOrder(final ProductModel.Product product){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Update stock
                            int index = 0;
                            for(int i=0;i<allProducts.size();i++){
                                if(product.getId() == allProducts.get(i).getId()){
                                    int qty = productsInOrder.get(i).getProductQty();
                                    int newQty = allProducts.get(i).getProductQty() + qty;
                                    allProducts.get(i).setProductQty(newQty);
                                    index = i;
                                    break;
                                }
                            }
                            productsInOrder.remove(index);
                            totalPrice = 0;
                            updateListTableLayout();
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

    public class confirmOrderController{

        public void confirmOrder(List<ProductModel.Product> productList){
            OrderModel orderModel = new OrderModel();
            List<OrderDetailModel> orderDetailModels = new ArrayList<OrderDetailModel>();
            for(ProductModel.Product product : productList){
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.setProduct(product);
                orderDetailModel.setProductAmount(product.getProductQty());
                orderDetailModels.add(orderDetailModel);
            }
            orderModel.setOrderdetails(orderDetailModels);
            orderModel.setUser(user);
            try {
                System.out.println("Order = "+new Gson().toJson(orderModel).toString());
                String responseData = new WSTask(getContext()).execute("/create/order?authKey="+user.getAuthKey(),"POST",gson.toJson(orderModel).toString()).get();
                MessageModel messageModel;
                if(responseData!=null){
                        messageModel = new MessageModel(responseData);
                        if(messageModel.getMessage().getMessageText().equalsIgnoreCase("Success")){
                            toast("บันทึกสำเร็จ");
                            productsInOrder.clear();
                            updateListTableLayout();
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
