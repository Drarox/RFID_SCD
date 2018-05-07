// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo;

import com.st.TCP.TCPClient;
import com.st.provider.SharedInformation.Livre;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import com.st.nfcv.DataRead;
import com.st.nfcv.Helper;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcm24LRBasicOperation;
import com.st.provider.AndroidProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;
//import android.util.Log;

public class ScanReadLRActivity extends FragmentActivity
{
    static final String TAG = "ScanReadLRActivity";

    Button launchRead, buttonCo;
    ImageView imageWifi;
    TextView textFrom;
    TextView type, titre, editeur, annee, annee2, labelTitre;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    String[] catBlocks = null;
    String[] catValueBlocks = null;

    int nbblocks = 0;

    String sNbOfBlock = null;
    // o byte numberOfBlockToRead;
    byte [] numberOfBlockToRead = null;

    String startAddressString = null;
    byte [] addressStart = null;

    byte [] data = null;

    List<DataRead> listOfData = null;

    String codeLivre = null;

    private TCPClient mTcpClient;
    String adresseIp = "192.168.137.1"; //ip du serveur socket
    int portTcp      = 2502; //port socket

    final Handler handler = new Handler();
    final Handler handlerRead = new Handler();

    MediaPlayer success = null;
    MediaPlayer error = null;

    String lecture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_read);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[] {ndef,};
        mTechLists = new String[][] { new String[] { android.nfc.tech.NfcV.class.getName() } };

        initListener();

        insertRecords();

        new connectTask().execute("");

        //detection wifi
        String getSSID = getWifiName(getApplicationContext());
        String testSSID = "\"RFID_SCD\"";
        if (getSSID != null) {
            if (getSSID.equals(testSSID))
                imageWifi.setImageResource(R.drawable.wifi);
            else
                imageWifi.setImageResource(R.drawable.wifioff);

        }
        else
            imageWifi.setImageResource(R.drawable.wifioff);

        // new timer detection wifi
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(myTimerTask, 200, 200);

        success = MediaPlayer.create(this, R.raw.success);
        error = MediaPlayer.create(this, R.raw.error);
    }


    private void initListener()
    {
        textFrom = (TextView) findViewById(R.id.textFrom);
        type = (TextView) findViewById(R.id.type);
        titre = (TextView) findViewById(R.id.titre);
        editeur = (TextView) findViewById(R.id.editeur);
        annee = (TextView) findViewById(R.id.annee);
        annee2 = (TextView) findViewById(R.id.annee2);
        imageWifi = (ImageView) findViewById(R.id.imageWifi);
        launchRead = (Button) findViewById(R.id.button_read);
        buttonCo = (Button) findViewById(R.id.buttonCo);
        labelTitre = (TextView) findViewById(R.id.labelTitre);

        type.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) { //lorsque que le text est actualisé
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            String trame = textFrom.getText().toString() + " - " + lecture + " - ";
                            EnvoiMessage(trame); //envoi tcp
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mTcpClient.stopClient();
                    }
                }).start();
            }

        });

        buttonCo.setOnClickListener(new OnClickListener() //Lors du click sur bouton
        {
            @Override
            public void onClick(View v) { //button co
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                new connectTask().execute("");

                String getSSID = getWifiName(getApplicationContext());
                String testSSID = "\"RFID_SCD\"";
                if (getSSID != null) {
                    if (getSSID.equals(testSSID))
                        imageWifi.setImageResource(R.drawable.wifi);
                    else
                        imageWifi.setImageResource(R.drawable.wifioff);

                }
                else
                    imageWifi.setImageResource(R.drawable.wifioff);
            }
        });

        imageWifi.setOnClickListener(new OnClickListener() //Lors du click sur bouton
        {
            @Override
            public void onClick(View v) { //clique sur image wifi
                new connectTask().execute("");
                String getSSID = getWifiName(getApplicationContext());
                String testSSID = "\"RFID_SCD\"";
                if (getSSID != null) {
                    if (getSSID.equals(testSSID))
                        imageWifi.setImageResource(R.drawable.wifi);
                    else
                        imageWifi.setImageResource(R.drawable.wifioff);

                }
                else
                    imageWifi.setImageResource(R.drawable.wifioff);
            }
        });

        launchRead.setOnClickListener(new OnClickListener() //Lors du click sur bouton
        {
            @Override
            public void onClick(View v) { //appui sur bouton read
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub

                try {
                    //new timer pour lecture en boucle
                    Timer timerRead = new Timer();
                    timerRead.scheduleAtFixedRate(TimerRead, 600, 600);

                    new connectTask().execute("");
                    NFCApplication currentApp = NFCApplication.getApplication();
                    NFCTag currentTag = currentApp.getCurrentTag();
                    if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
                        if (sysHDL.getMemorySize() != null) {
                            String tmpmemsize = sysHDL.getMemorySize();
                            processPreExecute(sysHDL.getMemorySize());
                            stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHDL.getMaxTransceiveLength());
                            if (bop.m24LRReadBasicOp(addressStart, numberOfBlockToRead, tmpmemsize) == 0) {
                                // ok
                                processPostExecute(bop.getReadMultipleBlockAnswer()); //Extrait les données raw de bop
                            } else {
                                // ko
                                processPostExecute(null);

                            }
                        } else {   }

                    } else {  }
                }
                catch (Exception e) {  }
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        Log.v(TAG, "onNewIntent");

        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        String action = intent.getAction();
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
            || (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
            || (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)))
        {
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
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        new connectTask().execute("");

        //detection wifi
        String getSSID = getWifiName(getApplicationContext());
        String testSSID = "\"RFID_SCD\"";
        if (getSSID != null) {
            if (getSSID.equals(testSSID))
                imageWifi.setImageResource(R.drawable.wifi);
            else
                imageWifi.setImageResource(R.drawable.wifioff);

        }
        else
            imageWifi.setImageResource(R.drawable.wifioff);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter!=null) mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onDestroy(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (error != null) error.release();
        if (success != null) success.release();
        super.onDestroy();
    }

//paramètrage lecture nfc
private void processPreExecute (String memorySize) {
      startAddressString = "0000";
      startAddressString = Helper.castHexKeyboard(startAddressString);
      startAddressString = Helper.FormatStringAddressStart(startAddressString, memorySize);
      addressStart = Helper.ConvertStringToHexBytes(startAddressString);

      sNbOfBlock = "0008"; //lecture de 8 blocs
      sNbOfBlock = Helper.FormatStringNbBlockInteger(sNbOfBlock, startAddressString ,memorySize);
      numberOfBlockToRead = Helper.ConvertIntTo2bytesHexaFormat(Integer.parseInt(sNbOfBlock));
}

//Formatage pour récup code puis écriture
private void processPostExecute (byte[] ReadMultipleBlockAnswer) { //données hexa dans un tableau
    nbblocks = Integer.parseInt(sNbOfBlock); // ici 8
    if (ReadMultipleBlockAnswer != null && ReadMultipleBlockAnswer.length - 1 > 0) // si données non null et > 0
    {
        if (ReadMultipleBlockAnswer[0] == 0x00) // si premier octect a 00
        {
            String value = new String(ReadMultipleBlockAnswer, 0, 33); //formatage ascii vers string
            codeLivre = value.substring(22); //récup code barre
            textFrom.setText(codeLivre); //écritue du code barre
            displayContentProvider();
        }
    }
}


    //lecture provider et affichage des infos
    private void displayContentProvider() {
        String columns[] = new String[] { Livre.ID, Livre.CODEBARRE, Livre.TITRE, Livre.RECOLEMENT };
        Uri mContacts = AndroidProvider.CONTENT_URI;
        Cursor cur = managedQuery(mContacts, columns, "CODEBARRE = ?", new String[] {codeLivre}, null);

        if (cur.getCount() == 0) //si livre pas dans le provider
        {
            labelTitre.setText("");
            type.setText("");
            titre.setTextColor(Color.RED);
            titre.setText("Livre non trouvé");
            editeur.setText("");
            annee.setText("");
            annee2.setText("");
            error.start();
            lecture = "Livre non trouvé"; //pour trame
        }

        if (cur.moveToFirst()) { //si livre trouvé
            String name = null;
            String desc = null;
            do {
                name = cur.getString(cur.getColumnIndex(Livre.ID)) + " " +
                        cur.getString(cur.getColumnIndex(Livre.CODEBARRE)) + " " +
                        cur.getString(cur.getColumnIndex(Livre.TITRE));
                if (cur.getString(cur.getColumnIndex(Livre.TITRE))!= null) {
                    desc = cur.getString(cur.getColumnIndex(Livre.TITRE));

                    labelTitre.setText("Titre :");
                    titre.setTextColor(Color.BLACK);
                    String[] SplitArgs = desc.split(" - ");
                    type.setText("Type : " + SplitArgs[0]);
                    titre.setText(SplitArgs[1]);
                    String an = SplitArgs[3].replaceAll("[^0-9]", "");
                    if(an.matches("^[0-9]{4}$")){ //sans auteur
                        editeur.setText("Editeur : " + SplitArgs[2]);
                        annee.setText("Année : " + SplitArgs[3]);
                        annee2.setText("");
                    }
                    else { //si auteur présent
                        editeur.setText("Auteur : " + SplitArgs[2]);
                        annee.setText("Editeur : " + SplitArgs[3]); //decalage pour auteur
                        annee2.setText("Année : " + SplitArgs[4]);
                    }

                    ContentValues values = new ContentValues();
                    values.put(Livre.RECOLEMENT, "OK");
                    String id = cur.getString(cur.getColumnIndex(Livre.ID));
                    long noUpdated = getContentResolver().update(mContacts,values,Livre.ID+"=?",new String[] {String.valueOf(id)}); //Mise à jour provider si livre trouvé

                    lecture = desc; //pour trame
                }
                success.start();
            } while (cur.moveToNext());
        }

    }

    //insertion provider
    private void insertRecords() {
        Uri mContacts = AndroidProvider.CONTENT_URI;
        int nombre = getContentResolver().delete(mContacts, null, null);

        ContentValues contact = new ContentValues();
        contact.put(Livre.CODEBARRE, "0340761154");
        contact.put(Livre.TITRE, "Livre - Mécanique 2 - Dunod - 1998");
        contact.put(Livre.RECOLEMENT, "");
        getContentResolver().insert(AndroidProvider.CONTENT_URI, contact);

        contact.clear();
        contact.put(Livre.CODEBARRE, "0341134964");
        contact.put(Livre.TITRE, "Livre - Sciences industrielles - Bronsard , Françoise - Ellipses - 2003");
        contact.put(Livre.RECOLEMENT, "");
        getContentResolver().insert(AndroidProvider.CONTENT_URI, contact);

    }

    //Connexion TCPClient
    public class connectTask extends AsyncTask<String,String,TCPClient> {
        @Override
        protected TCPClient doInBackground(String... message) {
            mTcpClient = new TCPClient(adresseIp, portTcp, new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                    Log.d("doInbackGround", "recu " + message);
                    //    reponse.setText(message);
                }
            });
            mTcpClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }//Fin classe connectTask

    //Méthode d'envoi de message TCP
    public void EnvoiMessage (String msg) {
        if (mTcpClient != null) {
            try {
                mTcpClient.sendMessage(msg);
                Log.e("envoiMessage", "EnvoiMessage: Trame envoyée : " + msg);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("envoiMessage", "EnvoiMessage: ECHEC");
            }
        }
    }

    //récup le nom du wifi
    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }

    //Detecter wifi
    TimerTask myTimerTask = new TimerTask() {
        @Override
        public void run() {
            // post a runnable to the handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String getSSID = getWifiName(getApplicationContext());
                    String testSSID = "\"RFID_SCD\"";
                    if (getSSID != null) {
                        if (getSSID.equals(testSSID))
                            imageWifi.setImageResource(R.drawable.wifi);
                        else
                            imageWifi.setImageResource(R.drawable.wifioff);

                    }
                    else
                        imageWifi.setImageResource(R.drawable.wifioff);
                }
            });
        }
    };

    //Scan en boucle
    TimerTask TimerRead = new TimerTask() {
        @Override
        public void run() {
            // post a runnable to the handler
            handlerRead.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        new connectTask().execute("");
                        NFCApplication currentApp = NFCApplication.getApplication();
                        NFCTag currentTag = currentApp.getCurrentTag(); //Récup du tag
                        if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
                            if (sysHDL.getMemorySize() != null) {
                                String tmpmemsize = sysHDL.getMemorySize(); //Récup la taille
                                processPreExecute(sysHDL.getMemorySize()); //Réduit à la taille souhaiter
                                stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHDL.getMaxTransceiveLength()); //Récup la mémoire
                                if (bop.m24LRReadBasicOp(addressStart, numberOfBlockToRead, tmpmemsize) == 0) {
                                    // ok
                                    processPostExecute(bop.getReadMultipleBlockAnswer()); //Formatage de la mémoire
                                } else {
                                    // ko
                                    processPostExecute(null);

                                }
                            } else { }

                        } else { }
                    }
                    catch (Exception e) { }
                }
            });
        }
    };


}


