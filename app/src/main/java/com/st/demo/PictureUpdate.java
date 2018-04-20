// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.st.MB.FTMHeaderBuilder.MBFct;
import com.st.MB.FTMHeaderBuilder.MBcmd;
import com.st.MB.FTMUseCaseGen;
import com.st.MB.FTMUseCaseHostAnswerAcknowledge;
import com.st.MB.FTMUseCaseHostRequest;
import com.st.MB.FTMUseCaseImageUpdate;
import com.st.MB.MBFWUListenerDataSent;
import com.st.MB.MBTransferListenerDataReceived;
import com.st.MB.MBTransferListenerFWU;
import com.st.MB.MBTransferListenerHostAcknowledge;
import com.st.MB.MBTransferListenerHostRequest;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.nfcv.BasicOperation;
import com.st.nfcv.Helper;
import com.st.nfcv.MBCommandV;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler.ST25DVRegisterTable;
import com.st.util.DebugUtility;
import com.st.util.IOUtil;
import com.st.util.crc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
//import android.util.Log;

public class PictureUpdate extends FragmentActivity {

    static final String TAG = "PictureUpdate";

    TextView sendLog;
    TextView receiveLog;
    Button launchCapture;
    Button launchGetImage;
    Button launchSend;
    Button launchReceive;

    public TextView sizeETView;
    public SeekBar sizeSBView;
    public TextView sizeETinfoView;
    public TextView sizeETinfoWHView;

    private CheckBox enableRatioPictureFeatures;


    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;


    private int currentCfgSelection = 0; // static by default = 0
    private boolean currentCfgMBEN = true; // On


    int m_AndroidLoopThreadCpt = 50;

    FTMUseCaseGen imageDownloadUC;

    eventLogInfoCallBack MBuCLog; // For 3 previous object display

    FTMUseCaseHostRequest simpleCmdReceivedFromHost;
    FTMUseCaseHostAnswerAcknowledge mMBGenericUseCaseHostAnswerAcknowledge;

    eventLogInfoCallBackHostCmd MBuCHostCmdLog; // Call back for a Host message cmd received
    eventLogInfoCallBackHostAcknowledge MBuCHostAckCmdLog;

    private int defaultPictureWsize = 320;
    private int defaultPictureHsize = 240;
    private final String mFileNameForPictureReceived = new String("ST25PictureDumpBytes.hex");

    private int SEEKBAR_RED;
    private static int SEEKBAR_GREEN;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    public enum EnumPictureDemo {
        PICTURE_DEMO_UNKNOWN,
        PICTURE_DOWNLOAD_EXCHANGE,
        PICTURE_UPLOAD_EXCHANGE
    }

    private EnumPictureDemo pictureAction;
    private int FWArraySize = 0;

    private ImageView readerImgView;
    private ImageView hostImgView;

    public byte[] bufferFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_update);

        SEEKBAR_RED = 0xFFffd300; //getResources().getColor(R.color.st_dark_orange);
        SEEKBAR_GREEN = 0xFFbbcc00; //getResources().getColor(R.color.st_dark_green);

        sendLog = (TextView) findViewById(R.id.Text_log_send_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());
        receiveLog = (TextView) findViewById(R.id.Text_log_receive_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());

        // Logs
        MBuCLog = new eventLogInfoCallBack(receiveLog, sendLog);
        MBuCHostCmdLog = new eventLogInfoCallBackHostCmd(receiveLog, sendLog);
        MBuCHostAckCmdLog = new eventLogInfoCallBackHostAcknowledge(receiveLog, sendLog);


        // Config Static Dynamic
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.RadioCFGStaticDyn);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.RB_cfg_static:
                        currentCfgSelection = 0;
                        break;
                    case R.id.RB_cfg_Dyn:
                        currentCfgSelection = 1;
                        break;

                    default:
                        currentCfgSelection = 0;
                        break;
                }

            }
        });

        // Config MB Enable
        RadioGroup radioGroupMBEN = (RadioGroup) findViewById(R.id.RadioCFGMBEN);

        radioGroupMBEN.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.RB_cfg_MBOff:
                        currentCfgMBEN = false;
                        break;
                    case R.id.RB_cfg_MBOn:
                        currentCfgMBEN = true;
                        break;

                    default:
                        currentCfgMBEN = true;
                        break;
                }

            }
        });


        sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
        sizeETView.setText(String.valueOf(this.FWArraySize));
        sizeETView.setVisibility(View.GONE);


        sizeSBView = (SeekBar) findViewById(R.id.valueseekbar1);
        sizeSBView.setVisibility(View.GONE);
        // Set Default value
        sizeSBView.setProgress(FWArraySize);
        sizeSBView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                int download = ((int) Math.round(progress / 4)) * 4;

                sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
                sizeETView.setVisibility(View.VISIBLE);
                sizeETView.setText(String.valueOf(download));
            }
        });

        enableRatioPictureFeatures = (CheckBox) findViewById(R.id.checkEnableRatioPicture);

        initListenerActionCfg();
        initListener();

        readerImgView = (ImageView) findViewById(R.id.photoViewReader);
        readerImgView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //sizeETinfoView = (TextView) findViewById(R.id.TVbininfoSizeValue);
                //sizeETinfoView.setText(String.valueOf(FWArraySize));

                sizeETinfoWHView = (TextView) findViewById(R.id.TVbininfoSizeHWValue);
                sizeETinfoWHView.setText(String.valueOf(readerImgView.getDrawable().getIntrinsicWidth() + "x" + String.valueOf(readerImgView.getDrawable().getIntrinsicHeight())));
            }
        });

        hostImgView = (ImageView) findViewById(R.id.photoViewHost);
        hostImgView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //sizeETinfoView = (TextView) findViewById(R.id.TVbininfoSizeValue);
                //sizeETinfoView.setText(String.valueOf(FWArraySize));

                sizeETinfoWHView = (TextView) findViewById(R.id.TVbininfoSizeHWValue);
                sizeETinfoWHView.setText(String.valueOf(hostImgView.getDrawable().getIntrinsicWidth() + "x" + String.valueOf(hostImgView.getDrawable().getIntrinsicHeight())));
            }
        });

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[]{ndef,};
        mTechLists = new String[][]{new String[]{NfcV.class.getName()}};

    }


    private static final int FILE_SELECT_CODE = 0;
    private static final int PICK_GALLERY_IMAGE_CODE = 1;
    private static final int PICK_CAMERA_IMAGE_CODE = 2;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    break;
                case PICK_GALLERY_IMAGE_CODE:
                    onSelectFromGalleryResult(data);
                    break;
                case PICK_CAMERA_IMAGE_CODE:
                    onCaptureImageResult(data);
                    break;
                default:
            }

        }
    }
    public void onBackPressed() {
        if (simpleCmdReceivedFromHost != null)  simpleCmdReceivedFromHost.stopUCThreadingLoop(0);
        if (imageDownloadUC != null)  imageDownloadUC.stopUCThreadingLoop(0);

        super.onBackPressed();
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap pic = null;
        if (data != null) {
            try {

                if (data.getExtras() == null) {
                    Uri contentURI = Uri.parse(data.getDataString());
                    ContentResolver cr = getContentResolver();
                    InputStream in = cr.openInputStream(contentURI);
                    pic = BitmapFactory.decodeStream(in, null, null);
                } else {
                    pic = (Bitmap) data.getExtras().get("data");
                }
                pic = bitMapResize(pic);

            } catch (IOException e) {
                e.printStackTrace();
            }
            setPhotoInView(pic);

        }
    }

    private Bitmap bitMapResize(Bitmap pic) {
        Boolean ratioPictureEnable = true;
        if (enableRatioPictureFeatures.isChecked()) {
            //
            ratioPictureEnable = true;
        } else {
            //
            ratioPictureEnable = false;
        }
        /*
        int maxHeight = defaultPictureHsize;
        int maxWidth = defaultPictureWsize;
        float scale = Math.min(((float) maxWidth / pic.getWidth()), ((float) maxHeight / pic.getHeight()));

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        pic = Bitmap.createBitmap(pic, 0, 0, pic.getWidth(), pic.getHeight(), matrix, true);

        */
        if (ratioPictureEnable) {
            /*
            int ScaleSize = 320;//max Height or width to Scale
            int width = pic.getWidth();
            int height = pic.getHeight();
            float excessSizeRatio = width > height ? width / ScaleSize : height / ScaleSize;
            pic = Bitmap.createBitmap(
                    pic, 0, 0,(int) (width/excessSizeRatio),(int) (height/excessSizeRatio));
                    */
            pic = getProportionalBitmap(pic, defaultPictureWsize, "x");

        } else {
            pic = Bitmap.createScaledBitmap(
                    pic, defaultPictureWsize, defaultPictureHsize, false);

        }

        return pic;
    }

    private Bitmap getProportionalBitmap(Bitmap bitmap,
                                         int newDimensionXorY,
                                         String XorY) {
        if (bitmap == null) {
            return null;
        }

        float xyRatio = 0;
        int newWidth = 0;
        int newHeight = 0;

        if (XorY.toLowerCase().equals("x")) {
            xyRatio = (float) newDimensionXorY / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newDimensionXorY, newHeight, true);
        } else if (XorY.toLowerCase().equals("y")) {
            xyRatio = (float) newDimensionXorY / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, newDimensionXorY, true);
        }
        return bitmap;
    }

    public void onCheckboxEnableRatioReaderPictureClicked(View v) {
        NFCApplication currentApp = (NFCApplication) getApplication();

    }

    private void onCaptureImageResult(Intent data) {
        if (data != null) {
            Bitmap thePic = (Bitmap) data.getExtras().get("data");
            thePic = bitMapResize(thePic);
            setPhotoInView(thePic);

        }
    }

    private void setPhotoInView(Bitmap thePic) {
        readerImgView.setImageBitmap(thePic);
        FWArraySize = computeAndSetEstimedImageSize(readerImgView);

        sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
        sizeETView.setText(String.valueOf(FWArraySize));

        sizeETinfoView = (TextView) findViewById(R.id.TVbininfoSizeValue);
        sizeETinfoView.setText(String.valueOf(FWArraySize));

        sizeETinfoWHView = (TextView) findViewById(R.id.TVbininfoSizeHWValue);
        sizeETinfoWHView.setText(String.valueOf(thePic.getWidth() + "x" + String.valueOf(thePic.getHeight())));

    }


    private void savePhotoJPGReceivedPictureBytes(byte[] data, String fileName) {
        BufferedOutputStream bs = null;
        try {

            FileOutputStream fs = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),
                    fileName));
            bs = new BufferedOutputStream(fs);
            bs.write(data);
            bs.close();
            bs = null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bs != null) try {
            bs.close();
        } catch (Exception e) {
        }
    }

    private void setReceivedPhotoInView(Bitmap thePic) {
        if (thePic != null) {
            thePic = bitMapResize(thePic);
            hostImgView.setImageBitmap(thePic);
        }

    }

    private void setReceivedPhotoInView(String FileName) {
        Bitmap thePic = null;
        File photo =
                new File(Environment.getExternalStorageDirectory(),
                        FileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.outWidth = defaultPictureWsize;
        options.outHeight = defaultPictureHsize;
        options.inScaled = true;

        if (photo.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(photo);
                thePic = BitmapFactory.decodeStream(fis, null, options);
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (thePic != null) {
                thePic = bitMapResize(thePic);
                hostImgView.setImageBitmap(thePic);
            } else {
                // Not a known picture - displayable
                thePic = bitMapResize(BitmapFactory.decodeResource(getResources(), R.drawable.ftm_image_not_available));
                hostImgView.setImageBitmap(thePic);
            }
        }

    }

    private long calculateCRCofByteArray(byte[] data) {
        long mcrc = 0;
        try {
            mcrc = crc.CRC(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            mcrc = -1;
            e.printStackTrace();
        }
        return mcrc;
    }

    private int computeAndSetEstimedImageSize(ImageView imageView) {
        byte[] initialPicData;
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, (int) 100, baos); // bm

        initialPicData = baos.toByteArray();
        int modPicture = initialPicData.length % 4;
        if (modPicture != 0 ) {
            bufferFile = new byte[initialPicData.length + (4-modPicture)];
            System.arraycopy(initialPicData, 0, bufferFile, 0, initialPicData.length);
        } else {
            bufferFile = baos.toByteArray();
        }
        // for debug
        //savePhotoJPGReceivedPictureBytes(bufferFile,"readerFTM_PictureFile.hex");
        return bufferFile.length;
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", defaultPictureWsize);
        intent.putExtra("outputY", defaultPictureHsize);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_GALLERY_IMAGE_CODE);
        }

    }

    private void pickImageFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up camera to select a photo
            startActivityForResult(intent, PICK_CAMERA_IMAGE_CODE);
        }


    }

    private void initListener() {

        launchCapture = (Button) findViewById(R.id.CaptureFromFileButton);
        launchCapture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = true;
                //boolean result=Utility.checkPermission(MainActivity.this);
                if (result) pickImageFromCamera();
            }
        });

        launchGetImage = (Button) findViewById(R.id.GetFromFileButton);
        launchGetImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        launchSend = (Button) findViewById(R.id.DownloadButton);
        launchReceive = (Button) findViewById(R.id.UploadButton);


        launchSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureAction = EnumPictureDemo.PICTURE_DOWNLOAD_EXCHANGE;

                sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
                sizeETView.setVisibility(View.GONE);
                sizeSBView = (SeekBar) findViewById(R.id.valueseekbar1);
                sizeSBView.setVisibility(View.GONE);

                resetProgressColorSBUiUpdates();

                if (bufferFile != null) {
                    long m_CRC;
                    m_CRC = -1;
                    m_CRC = calculateCRCofByteArray(bufferFile);
                    imageDownloadUC = new FTMUseCaseImageUpdate(bufferFile, m_CRC, MBFct.MB_FCT_IMAGE_DOWNLOAD);

                    MBuCLog.llogClearinfo(false);
                    MBuCLog.llogClearinfo(true);
                    MBuCLog.lloginfo("starting Image Download : " + FWArraySize, true);
                    imageDownloadUC.addListener(MBuCLog);
                    // Display info
                    sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
                    sizeETView.setVisibility(View.VISIBLE);
                    sizeSBView = (SeekBar) findViewById(R.id.valueseekbar1);
                    sizeSBView.setVisibility(View.VISIBLE);

                    sizeSBView.setMax(FWArraySize);
                    sizeSBView.setProgress(0);
                    sizeETView.setText(String.valueOf(FWArraySize));
                    ((FTMUseCaseImageUpdate) imageDownloadUC).addListenerFWUDataSent(MBuCLog);

                    imageDownloadUC.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                    imageDownloadUC.setUCThreadingSleepTime(30);
                    imageDownloadUC.execute();
                    // end test implementation

                } else {
                    MBuCLog.llogClearinfo(false);
                    MBuCLog.llogClearinfo(true);
                    MBuCLog.lloginfo("Please, select a file first  ", true);
                    MBuCLog.lloginfo("Please, use Capture/File buttons  ", true);
                }

            }
        });

        launchReceive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureAction = EnumPictureDemo.PICTURE_UPLOAD_EXCHANGE;
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);
                resetProgressColorSBUiUpdates();

                sizeETView = (TextView)  findViewById(R.id.TVbinSizeValue);
                sizeETView.setVisibility(View.VISIBLE);
                sizeSBView = (SeekBar)  findViewById(R.id.valueseekbar1);
                sizeSBView.setVisibility(View.VISIBLE);
                sizeETView.setText(String.valueOf("n/a"));

                eventLogInfoCallBackHostCmdWithoutAction eventLogWithoutAction = new eventLogInfoCallBackHostCmdWithoutAction(receiveLog, sendLog);
                simpleCmdReceivedFromHost = new FTMUseCaseHostRequest();
                MBuCHostCmdLog.llogClearinfo(false);
                MBuCHostCmdLog.llogClearinfo(true);
                MBuCHostCmdLog.lloginfo("Starting waiting Host Cmd ... ", true);
                simpleCmdReceivedFromHost.addListener((MBTransferListenerHostRequest) eventLogWithoutAction);
                simpleCmdReceivedFromHost.addListener((MBTransferListenerDataReceived) eventLogWithoutAction);
                simpleCmdReceivedFromHost.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                simpleCmdReceivedFromHost.setUCThreadingSleepTime(200);
                simpleCmdReceivedFromHost.execute();
                beginProgressColorSBUiUpdates();

            }
        });
    }

    private void initListenerActionCfg() {
        Button launchCfg = (Button) findViewById(R.id.BMailBoxRLReadCfg);
        launchCfg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);

                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                if (currentCfgSelection == 0 || currentCfgSelection == 1) {
                    if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysHDL = (SysFileLRHandler) currentTag.getSYSHandler();
                        boolean staticregister = currentCfgSelection == 0 ? true : false;
                        MBCommandV MBcmd = new MBCommandV(sysHDL.getMaxTransceiveLength());
                        BasicOperation bop = new BasicOperation(sysHDL.getMaxTransceiveLength());
                        lloginfo("Read MB Config", sendLog);
                        if (MBcmd.MBReadCfgBasicOp(staticregister) == 0) {
                            // ok
                            MBcfgUpdate(MBcmd.getBlockAnswer());
                            // Get Watch Dog register value
                            //SysFileLRHandler sfh = (SysFileLRHandler) currentTag.getSYSHandler();
                            if (bop.readRegister(ST25DVRegisterTable.Reg_MB_WDG, true) == 0) {
                                MBWDcfgUpdate(bop.getMBBlockAnswer());
                            } else {
                                MBWDcfgUpdate(null);
                            }
                        } else {
                            // ko
                            MBcfgUpdate(null);

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid parameters, please select Static/Dynamic Cfg", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button writeCfg = (Button) findViewById(R.id.BMailBoxEnableDisable);
        writeCfg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);

                if (currentCfgSelection == 0 || currentCfgSelection == 1) {
                    NFCApplication currentApp = NFCApplication.getApplication();
                    NFCTag currentTag = currentApp.getCurrentTag();
                    if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysLRH = (SysFileLRHandler) currentTag.getSYSHandler();
                        boolean staticregister = currentCfgSelection == 0 ? true : false;
                        MBCommandV bop = new MBCommandV(sysLRH.getMaxTransceiveLength());
                        lloginfo("configureMB MBEN", sendLog);
                        boolean mb = true; // otherwise EH
                        boolean enable = currentCfgMBEN;
                        if (bop.configureMB(staticregister, enable) == 0) {
                            // ok
                            Toast.makeText(getApplicationContext(), "configureMB succeed ....", Toast.LENGTH_LONG).show();
                            //logGenericEventInformation("configureMB succeed  :" + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);
                            lloginfo("configureMB succeed Max TCV :" + sysLRH.getMaxTransceiveLength(), receiveLog);

                        } else {
                            // ko
                            Toast.makeText(getApplicationContext(), "configureMB failed ....", Toast.LENGTH_LONG).show();
                            if (bop.getBlockAnswer() != null)
                                lloginfo("configureMB failed : " + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "configureMB Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "configureMB Invalid parameters, please select Static/Dynamic Cfg", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button writeWDG = (Button) findViewById(R.id.BMailBoxWWatchDog);
        writeWDG.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);

                TextView textWDG = (TextView) findViewById(R.id.myNumberWatchDog);
                byte ValueWDG = (byte) Byte.valueOf(textWDG.getText().toString());
                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                    SysFileLRHandler sysLRH = (SysFileLRHandler) currentTag.getSYSHandler();
                    boolean staticregister = currentCfgSelection == 0 ? true : false;
                    BasicOperation bop = new BasicOperation(sysLRH.getMaxTransceiveLength());
                    lloginfo("Write WDG ", sendLog);
                    boolean staticRegister = true;
                    if (bop.writeRegister(ST25DVRegisterTable.Reg_MB_WDG, ValueWDG, staticRegister) == 0) {
                        // ok
                        Toast.makeText(getApplicationContext(), "Write WDG succeed ....", Toast.LENGTH_LONG).show();
                        lloginfo("Write WDG succeed :" + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()), receiveLog);

                    } else {
                        // ko
                        Toast.makeText(getApplicationContext(), "Write WDG failed ....", Toast.LENGTH_LONG).show();
                        byte[] answer = bop.getMBBlockAnswer();
                        if (answer != null)
                            lloginfo("Write WDG failed : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Write WDG Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                }


            }
        });

        Button writeAndroidLoopThreadCpt = (Button) findViewById(R.id.BMailBoxThreadLoop);
        TextView textThreadLoop = (TextView) findViewById(R.id.myNumberThreadLoop);
        textThreadLoop.setText(new Integer(m_AndroidLoopThreadCpt).toString());

        writeAndroidLoopThreadCpt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView textThreadLoop = (TextView) findViewById(R.id.myNumberThreadLoop);
                m_AndroidLoopThreadCpt = Integer.valueOf(textThreadLoop.getText().toString());

            }
        });

    }


    private abstract class eventLogInfoCallBackGeneric {
        public eventLogInfoCallBackGeneric(TextView tv_in, TextView tv_out) {
            super();
            this.tv_in = tv_in;
            this.tv_out = tv_out;
        }

        TextView tv_in;
        TextView tv_out;

        protected void lloginfo(String text, boolean in_out) {
            if (in_out == true) {
                this.tv_out.append(text);
                this.tv_out.append("\n");

            } else {
                this.tv_in.append(text);
                this.tv_in.append("\n");
            }

        }
        protected void lloginfo(String text, boolean in_out,int color) {
            if (in_out == true) {
                int currentColor = this.tv_out.getCurrentTextColor();
                this.tv_out.setTextColor(color);
                this.tv_out.append(text);
                this.tv_out.append("\n");
                this.tv_out.setTextColor(currentColor);

            } else {
                int currentColor = this.tv_in.getCurrentTextColor();
                this.tv_out.setTextColor(color);
                this.tv_in.append(text);
                this.tv_in.append("\n");
                this.tv_in.setTextColor(currentColor);
            }

        }
        protected void llogClearinfo(boolean in_out) {
            if (in_out == true) {
                this.tv_out.setText("");
            } else {
                this.tv_in.setText("");

            }

        }

    }

    private class eventLogInfoCallBack extends eventLogInfoCallBackGeneric implements MBTransferListenerFWU, MBFWUListenerDataSent {
        public eventLogInfoCallBack(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);

        }

        private void display_result(int error, FTMUseCaseGen uc) {
            lloginfo("Error status " + error, false);
            lloginfo("Error code " + Helper.ConvertIntToHexFormatString(uc.getErrorCode()), false);
            lloginfo("Transfer time " + uc.getEllapsedTime()+ " ms", false);
            lloginfo("Sent data " + uc.getTotalBytesProcessed(), false);

            // get and display Payload received
            /*
            byte[] payload_received = uc.getPayloadReceived();
            if (payload_received != null) {
                lloginfo("Rcv data " + payload_received.length, false);
                byte[] spld;
                byte[] epld;
                if (payload_received.length > 20) {
                    int start_pp = 10;
                    int end_pp = 10;
                    spld = new byte[start_pp];
                    epld = new byte[end_pp];
                    System.arraycopy(payload_received, 0, spld, 0, start_pp);
                    System.arraycopy(payload_received, payload_received.length - end_pp, epld, 0, end_pp);
                    lloginfo("Rcv data start ...: " + Helper.ConvertHexByteArrayToString(spld), false);
                    lloginfo("Rcv data end .....: " + Helper.ConvertHexByteArrayToString(epld), false);

                } else {
                    lloginfo("Rcv data overall: " + Helper.ConvertHexByteArrayToString(payload_received), false);
                }
            } else {
                lloginfo("Rcv data : No Data", false);

            }
            */
        }

        @Override
        public void endOfTransfer(int error) {
            // TODO Auto-generated method stub
            if (pictureAction == EnumPictureDemo.PICTURE_DEMO_UNKNOWN) {
                lloginfo("No action selected ", false);
            } else if (pictureAction == EnumPictureDemo.PICTURE_UPLOAD_EXCHANGE) {
                display_result(error, simpleCmdReceivedFromHost);
                //lloginfo("No yet implemented " , false);
            } else if (pictureAction == EnumPictureDemo.PICTURE_DOWNLOAD_EXCHANGE) {
                display_result(error, imageDownloadUC);
            } else
                lloginfo("Transfer xxxx status ", false);

            pictureAction = EnumPictureDemo.PICTURE_DEMO_UNKNOWN;
        }

        @Override
        public void FWUdataSent(long size) {
            sizeSBView.setProgress((int) size);
        }
    }

    private class eventLogInfoCallBackHostCmd extends eventLogInfoCallBackGeneric implements MBTransferListenerHostRequest {
        public eventLogInfoCallBackHostCmd(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);
        }


        protected void display_result(int error, byte[] data, MBFct fct) {
            lloginfo("Reader receive cmd " + fct, false);
            lloginfo("Reader receive error code " + error, false);
            /*
            if (data != null) {
                lloginfo("Reader receive full data size" + data.length, false);

                // get and display Payload received
                byte[] payload_received = data;
                lloginfo("data PL size : " + payload_received.length, false);
                byte[] spld;
                byte[] epld;
                if (payload_received.length > 20) {
                    int start_pp = 10;
                    int end_pp = 10;
                    spld = new byte[start_pp];
                    epld = new byte[end_pp];
                    System.arraycopy(payload_received, 0, spld, 0, start_pp);
                    System.arraycopy(payload_received, payload_received.length - end_pp, epld, 0, end_pp);
                    lloginfo("data PL Start ...: " + Helper.ConvertHexByteArrayToString(spld), false);
                    lloginfo("data PL end .....: " + Helper.ConvertHexByteArrayToString(epld), false);

                } else {
                    lloginfo("data PL overall: " + Helper.ConvertHexByteArrayToString(payload_received), false);
                }
            } else {
                lloginfo("data PL overall: No data", false);
            }
            */
        }

        @Override
        public void hostRequestAvailable(int error, byte[] data, MBFct fct, MBcmd cmd) {
            // TODO Auto-generated method stub
            display_result(error, data, fct);
            // Manage here the actions needed according to fct
            lloginfo("Cmd not yet implemented " + fct.toString(), true);
        }

    }

    private Handler progressHandler; // init with new Handler(getMainLooper())
    private static AtomicInteger progressColor = new AtomicInteger(SEEKBAR_GREEN);

    private static AtomicInteger progressLostChunck = new AtomicInteger(0);
    private static AtomicInteger progressCurrentChunck = new AtomicInteger(0);

    private static AtomicInteger progressFullSize = new AtomicInteger(0);
    private static AtomicInteger progressCurrentSize = new AtomicInteger(0);

    private int PROGRESS_POLL_PERIOD_MILLIS = 2000;
    private void beginProgressColorSBUiUpdates() {
        progressColor.set(SEEKBAR_GREEN);
        progressLostChunck.set(0);
        progressCurrentChunck.set(0);
        progressFullSize.set(0);
        progressCurrentSize.set(0);
        displayColorProgress();
        progressHandler.postDelayed(pollProgress, PROGRESS_POLL_PERIOD_MILLIS);
    }
    private void resetProgressColorSBUiUpdates() {
        progressColor.set(SEEKBAR_GREEN);
        progressLostChunck.set(0);
        progressCurrentChunck.set(0);
        progressFullSize.set(0);
        progressCurrentSize.set(0);
        displayColorProgress();
        sizeSBView.setProgress(0);
        sizeSBView.setMax(0);

    }

    private Runnable pollProgress = new Runnable() {
        public void run() {
            if (sizeSBView.getVisibility() == View.VISIBLE) {
                displayColorProgress();
                progressHandler.postDelayed(pollProgress, PROGRESS_POLL_PERIOD_MILLIS);
            }
        }
    };
    private void displayColorProgress() {
        sizeSBView.setBackgroundColor(progressColor.get());
    }



    private class eventLogInfoCallBackHostCmdWithoutAction extends eventLogInfoCallBackHostCmd implements MBTransferListenerDataReceived {
        public eventLogInfoCallBackHostCmdWithoutAction(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);
            progressHandler= new Handler(getMainLooper());

        }

        @Override
        public void hostRequestAvailable(int error, byte[] data, MBFct fct, MBcmd cmd) {
            // TODO Auto-generated method stub
            if (fct != MBFct.MB_FCT_DUMMY) {
                display_result(error, data, fct);
                if (error == 0) {
                    if (fct == MBFct.MB_FCT_IMAGE_UPLOAD) {
                        lloginfo("Duration : " + simpleCmdReceivedFromHost.getEllapsedTime() + "ms", true);
                        lloginfo("Full data size : " + simpleCmdReceivedFromHost.getTotalBytesProcessed(), true);

                        savePhotoJPGReceivedPictureBytes(data, mFileNameForPictureReceived);
                        //new SavePhotoTask().execute(data);
                        long crc = calculateCRCofByteArray(data);
                        lloginfo("CRC " + String.format("0x%08X", crc), true);
                        mMBGenericUseCaseHostAnswerAcknowledge = new FTMUseCaseHostAnswerAcknowledge(MBFct.MB_FCT_IMAGE_UPLOAD, IOUtil.longToBytes(crc));
                        mMBGenericUseCaseHostAnswerAcknowledge.addListener(MBuCHostAckCmdLog);
                        mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                        mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingSleepTime(300);
                        mMBGenericUseCaseHostAnswerAcknowledge.execute();
                    } else {
                        lloginfo("WARNING - Not a Cmd for a Picture transfer...." + fct.toString(), true,Color.RED);
                    }
                } else {
                    if (fct == MBFct.MB_FCT_IMAGE_UPLOAD) {
                        if (simpleCmdReceivedFromHost.getPayloadReceived() != null) {
                            lloginfo("Protocol error received .. " + fct.toString(), true);
                            long crc = calculateCRCofByteArray(data);
                            lloginfo("CRC " + String.format("0x%08X", crc), true, Color.RED);
                            mMBGenericUseCaseHostAnswerAcknowledge = new FTMUseCaseHostAnswerAcknowledge(MBFct.MB_FCT_IMAGE_UPLOAD, IOUtil.longToBytes(crc));
                            mMBGenericUseCaseHostAnswerAcknowledge.addListener(MBuCHostAckCmdLog);
                            mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                            mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingSleepTime(300);
                            mMBGenericUseCaseHostAnswerAcknowledge.execute();
                            // cmd terminate to be implemented

                        } else {
                            lloginfo("Protocol error received .. No request " + fct.toString(), true, Color.RED);
                        }
                    } else {
                        lloginfo("WARNING - Not a Cmd for a Picture transfer...." + fct.toString(), true, Color.RED);
                    }

                }

            } else {
                sizeSBView.setBackgroundColor(SEEKBAR_RED);
                lloginfo("No cmd received or Cmd not yet implemented " + fct.toString(), true);
            }
        }

        @Override
        public void dataReceived(long size, long expectedSize, long currentChunck, long expectedChunck) {
            // do not manipulate log on display
            //sizeSBView.setMax((int) expectedSize);
            //sizeSBView.setProgress((int) size);
            progressFullSize.set((int) expectedSize);
            progressCurrentSize.set((int) size);

            sizeSBView.setMax((int) expectedSize);
            sizeSBView.setProgress((int) size);

            // do not manipulate log on display
            if (currentChunck > expectedChunck) {
                // Update the progress bar
                progressColor.set(SEEKBAR_RED);
                if (DebugUtility.printMBExecutionInformation)
                    Log.v(TAG, "Lost Data Received chunck : " + currentChunck +
                            " size : " + size +
                            " Tsize : " + expectedSize +
                            " exp. chunck : " + (expectedChunck)
                    );
            } else {
                progressColor.set(SEEKBAR_GREEN);
                if (DebugUtility.printMBExecutionInformation)
                    Log.v(TAG, "Data Received chunck : " + currentChunck +
                            " size : " +  size +
                            " Tsize : " +  expectedSize +
                            " exp. chunck : " +  (expectedChunck)
                    );
            }

        }
    }


    private class eventLogInfoCallBackHostAcknowledge extends eventLogInfoCallBackGeneric implements MBTransferListenerHostAcknowledge {
        public eventLogInfoCallBackHostAcknowledge(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);
            // TODO Auto-generated constructor stub
        }

        private void display_result(int error, byte[] data, MBFct fct, MBcmd cmd) {
            lloginfo("Reader receive Ack fct " + fct, false);
            lloginfo("Reader receive Ack cmd " + cmd, false);
            lloginfo("Reader receive Ack err " + error, false);

        }

        @Override
        public void hostAcknowledgeAvailable(int error, byte[] data, MBFct fct, MBcmd cmd) {
            // TODO Auto-generated method stub
            display_result(error,data,fct,cmd);
            if (error == 0) {
                if (fct == MBFct.MB_FCT_IMAGE_UPLOAD) {
                    setReceivedPhotoInView(mFileNameForPictureReceived);
                    lloginfo("Image uploaded ... ", true);
                } else {
                    setReceivedPhotoInView(BitmapFactory.decodeResource(getResources(), R.drawable.ftm_image_not_available));
                    lloginfo("Fct not yet implemented " + fct.toString(), true);
                }

            } else {
                setReceivedPhotoInView(BitmapFactory.decodeResource(getResources(), R.drawable.ftm_image_not_available));
                lloginfo("Error received on : " + fct.toString(), true);
            }
        }

    }


    private void lloginfo(String text, TextView tv) {
        tv.append(text);
        tv.append("\n");
    }
    private void lloginfo(String text, TextView tv, int color) {
        int currentColor = tv.getCurrentTextColor();
        tv.setTextColor(color);
        tv.append(text);
        tv.append("\n");
        tv.setTextColor(currentColor);

    }
    private void llogClearinfo(TextView tv) {
        tv.setText("");
    }

    private void MBcfgUpdate(byte[] answer) {
        if (answer != null) {
            Toast.makeText(getApplicationContext(),
                    "Read MB Cfg : " + Helper.ConvertHexByteArrayToString(answer), Toast.LENGTH_LONG).show();
            lloginfo("Read MB Cfg : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
            if (answer.length == 1) {
                lloginfo("Read MB Cfg : " + "No data", receiveLog);

            } else {
                // update the register text values .....MBEN
                int mcolorzero = Color.RED;
                int id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView06 : R.id.DynCCfgRow1TextView06;
                if ((answer[1] & 0x01) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....HostPutMsg
                id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView05 : R.id.DynCCfgRow1TextView05;
                if ((answer[1] & 0x02) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....RFPutMsg
                id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView04 : R.id.DynCCfgRow1TextView04;
                if ((answer[1] & 0x04) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....HostMissMsg
                id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView03 : R.id.DynCCfgRow1TextView03;
                if ((answer[1] & 0x10) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....RFMissMsg
                id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView02 : R.id.DynCCfgRow1TextView02;
                if ((answer[1] & 0x20) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....CurrentMsg
                id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView01 : R.id.DynCCfgRow1TextView01;
                if ((answer[1] & 0xC0) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                    if ((answer[1] & 0xC0) == 1) {
                        // I2C
                    } else if ((answer[1] & 0xC0) == 2) {
                        // RF
                    } else {
                        // issue .....??
                    }
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Read MB Cfg : " + "Error cmd answer", Toast.LENGTH_LONG)
                    .show();
            lloginfo("Read MB Cfg : " + "Error cmd answer", receiveLog);
            // Upodate text in TAB
            // update the register text values .....MBEN
            int id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView06 : R.id.DynCCfgRow1TextView06;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....HostPutMsg
            id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView05 : R.id.DynCCfgRow1TextView05;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....RFPutMsg
            id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView04 : R.id.DynCCfgRow1TextView04;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....HostMissMsg
            id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView03 : R.id.DynCCfgRow1TextView03;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....RFMissMsg
            id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView02 : R.id.DynCCfgRow1TextView02;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....CurrentMsg
            id_tt = this.currentCfgSelection == 0 ? R.id.StaticCfgRow1TextView01 : R.id.DynCCfgRow1TextView01;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);

        }
    }

    private void MBWDcfgUpdate(byte[] answer) {
        int id_tt = R.id.myNumberWatchDog;
        if (answer != null) {
            Toast.makeText(getApplicationContext(),
                    "WD Cfg : " + Helper.ConvertHexByteArrayToString(answer), Toast.LENGTH_LONG).show();
            lloginfo("WD Cfg : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
            if (answer.length == 1) {
                lloginfo("WD Cfg : " + "No data", receiveLog);
                ((TextView) this.findViewById(id_tt)).setText("X");
                ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);

            } else {
                // update the register text values .....MBEN

                //((TextView) this.findViewById(id_tt)).setText(Helper.ConvertHexByteToString(answer[1]));
                ((TextView) this.findViewById(id_tt)).setText(new Integer(answer[1]).toString());
                ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
            }

        } else {
            ((TextView) this.findViewById(id_tt)).setText("X");
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), "WD Cfg : " + "Error cmd answer", Toast.LENGTH_LONG)
                    .show();
            lloginfo("WD Cfg : " + "Error cmd answer", receiveLog);
            // Upodate text in TAB
            // update the register text values .....MBEN

        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        String action = intent.getAction();
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
                || (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
                || (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))) {
            Tag rawTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NFCTag tmpTag = null;

            if (rawMsgs != null) {
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                tmpTag = new NFCTag(rawTag, msgs);
            } else {
                tmpTag = new NFCTag(rawTag);
            }

            NFCApplication.getApplication().setCurrentTag(tmpTag);
            NFCAppHeaderFragment mHeadFrag = (NFCAppHeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.WcActNFCAppHeaderFragmentId);
            mHeadFrag.onTagChanged(tmpTag);

        }

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}