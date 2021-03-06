package minipos.miniproject.com.miniposadndroid.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import minipos.miniproject.com.miniposadndroid.MainActivity;
import minipos.miniproject.com.miniposadndroid.Models.ProductModel;
import minipos.miniproject.com.miniposadndroid.Models.UserModel;
import minipos.miniproject.com.miniposadndroid.R;
import minipos.miniproject.com.miniposadndroid.WSTask.WSTask;

import static xdroid.toaster.Toaster.toast;

public class ListLessProductFragment extends Fragment implements View.OnClickListener{

    private ListView itemListView;
    private ProgressDialog progressDialog;
    private Handler handler;
    private UserModel.User user;
    private List<ProductModel.Product> allLessProducts = new ArrayList<ProductModel.Product>();
    private List<String> productNames = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_less_product, container, false);
        //Set item list view
        itemListView = (ListView) view.findViewById(R.id.itemListView);
        // progress dialog and handler
        progressDialog = new ProgressDialog(getContext());
        handler = new Handler();
        //Get User from MainActivity
        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();
        //Load all less product
        new ListLessProductsController().loadLessProducts();

        return view;
    }

    @Override
    public void onClick(View view) {

    }

        public class ListLessProductsController{

            public void loadLessProducts(){
                progressDialog.setMessage("กำลังโหลดข้อมูลสินค้า...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseData = new WSTask(getContext()).execute("/products/mobile/less?authKey="
                                    +user.getAuthKey(),"GET").get();
                            Type productListType = new TypeToken<ArrayList<ProductModel.Product>>(){}.getType();
                            allLessProducts = new Gson().fromJson(responseData,productListType);
                            setProductNames(allLessProducts);


                            progressDialog.dismiss();
                            if(allLessProducts.size()<1){
                                toast("ไม่พบข้อมูลสินค้า.");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },1000);
            }

            public void setProductNames(final List<ProductModel.Product> products){
            for(ProductModel.Product product : products){
                productNames.add(product.getProductName()+"\n"+"จำนวนสินค้า"+
                        " "+product.getProductQty());
            }
            ArrayAdapter<String> productListViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,productNames);
            itemListView.setAdapter(productListViewAdapter);
            itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new ViewProductDetailsContoller().viewProductDetails(allLessProducts.get(position));
                }
            });
        }

    }

    public class ViewProductDetailsContoller{

        public void viewProductDetails(ProductModel.Product product){
            showViewProductDetailsDialog(product);
        }

        public void showViewProductDetailsDialog(ProductModel.Product product){
            LayoutInflater inflater;
            final View dialogView;
            inflater = LayoutInflater.from(getActivity());

            new AlertDialog.Builder(getActivity())
                    .setTitle("ข้อมูลสินค้า")
                    .setMessage("ชื่อสินค้า : "+product.getProductName().toString()
                            +"\n"+"รหัสบาร์โค้ด : "+product.getProductBarcodeID().toString()
                            +"\n"+"ราคาสินค้า : "+product.getProductSalePrice() + " บาท"
                            +"\n"+"บริษัทนำเข้า : "+product.getCompany().getCompanyName()
                            +"\n"+"ประเภทสินค้า : "+ product.getCategory().getCategoryName().toString()
                            +"\n"+"จำนวนสินค้าคงเหลือ : "+ product.getProductQty() + " ชิ้น"


                    )
                    .setPositiveButton("ตกลง", null)
                    .show();
        }

    }
}

