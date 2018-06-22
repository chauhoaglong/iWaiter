package vn.thientf.iwaiter;

import android.*;
import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import vn.thientf.iwaiter.Fragment.FragmentMenu;


public class FragmentScan extends Fragment {
    SurfaceView cameraPreview;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    TextView txtResult;
    final int RequestCameraPermissionID = 1001;
    View rootView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_scan,null);
        cameraPreview = (SurfaceView) rootView.findViewById(R.id.cameraPreview);
        txtResult = (TextView) rootView.findViewById(R.id.txtResult);

        barcodeDetector = new BarcodeDetector.Builder(this.getActivity())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this.getActivity(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        //Add Event
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    String tmp = qrcodes.valueAt(0).displayValue;
                    GlobalData.getInstance().setCurrRes(tmp.substring(0,3));
                    GlobalData.getInstance().setCurrTable(tmp.substring(5));
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Create vibrate
                            Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            txtResult.setText(qrcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }


        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }



}
