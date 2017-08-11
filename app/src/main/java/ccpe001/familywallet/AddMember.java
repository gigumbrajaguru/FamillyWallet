package ccpe001.familywallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

import static com.google.zxing.BarcodeFormat.QR_CODE;

/**
 * Created by harithaperera on 5/10/17.
 */
public class AddMember extends Fragment  implements View.OnClickListener{

    private ImageView qrImage;
    private TextView cautionText,regenerateQr;
    private Random random;
    private String randomNo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_member, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.addmember_title);
        random = new Random();//security problems
        randomNo = String.valueOf(random.nextInt(1000000));
        cautionText = (TextView) v.findViewById(R.id.editText10);
        regenerateQr = (TextView) v.findViewById(R.id.editText2);
        regenerateQr.setOnClickListener(this);
        qrImage = (ImageView) v.findViewById(R.id.qrCodeImage);
        setQr();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.editText2) {
            random = new Random();
            randomNo = String.valueOf(random.nextInt(1000000));
            setQr();
        }
    }

    private void setQr(){
        MultiFormatWriter mfw = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = mfw.encode(randomNo, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void qrReader(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt(getString(R.string.signin_scan_setpromt));
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if (result.getContents()== null){
                Toast.makeText(getActivity(),R.string.signup_cancel_toast,Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getActivity(),result.getContents(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
