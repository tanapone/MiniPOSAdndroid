package minipos.miniproject.com.miniposadndroid.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import minipos.miniproject.com.miniposadndroid.R;

import static xdroid.toaster.Toaster.toast;

public class CashierFragment extends Fragment  implements View.OnClickListener {

    private String barcodeResult;
    private ImageButton searchButton;
    private ImageButton scanButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cashier, container, false);
        searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        scanButton = (ImageButton) view.findViewById(R.id.scanButton);
        searchButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(),"Canceled scan",Toast.LENGTH_SHORT).show();
            } else {
                barcodeResult = result.getContents();
                toast(barcodeResult);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.searchButton :
                System.out.println("hello");
                break;
            case R.id.scanButton:
                scanBarcode();
                break;
        }
    }

    public void scanBarcode(){
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }
}
