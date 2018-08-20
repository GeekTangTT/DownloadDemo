package com.example.t.downloaddemo;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import static android.app.DownloadManager.COLUMN_LOCAL_FILENAME;

/*
* DownloadManager是处理长时间HTTP下载的系统服务。
* 客户端可以将指定内容下载到某一特定的目录。
* DownloadManager会在后台进行下载工作，自己会处理下载失败、网络变换或系统重启等问题。
* 这是android提供的系统服务，我们通过这个服务完成文件下载。整个下载 过程全部交给系统负责，不需要我们过多的处理。
* DownLoadManager包含两个内部类：
* DownLoadManager.Query:主要用于查询下载信息。
  DownLoadManager.Request:主要用于发起一个下载请求。
* */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PIC_URL = "http://pic11.nipic.com/20101119/3320946_221711832717_2.jpg";
    public static final String APK_URL = "http://118.190.70.77:8085/thirdparty/通达OA/ispirit_for_Android2017.apk";
    public static final String DOC_URL = "http://118.190.70.77:8085/testdocs/doc.doc";
    public static final String DOCX_URL = "http://118.190.70.77:8085/testdocs/docx.docx";
    public static final String PDF_URL = "http://118.190.70.77:8085/testdocs/pdf.pdf";
    public static final String PPT_URL = "http://118.190.70.77:8085/testdocs/ppt.ppt";
    public static final String PPTX_URL = "http://118.190.70.77:8085/testdocs/pptx.pptx";
    public static final String TXT_URL = "http://118.190.70.77:8085/testdocs/txt.txt";
    public static final String XLS_URL = "http://118.190.70.77:8085/testdocs/xls.xls";
    public static final String XLSX_URL = "http://118.190.70.77:8085/testdocs/xlsx.xlsx";

    private Button downImage, downAPK, downDOC, downDOCX, downPDF, downPPT, downPPTX, downTXT, downXLS, downXLSX;

    private static final String TAG = "MainActivity";
    private DownloadManager mDownloadManager;
    private long id;
    private ImageView imageView;
    private DownloadReceiver mReceiver;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            query();
        }
    };

    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
    BroadcastReceiver receiver;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        isCall();
        downImage = (Button) findViewById(R.id.downImage);
        downAPK = findViewById(R.id.downAPK);
        downDOC = findViewById(R.id.downDOC);
        downDOCX = findViewById(R.id.downDOCX);
        downPDF = findViewById(R.id.downPDF);
        downPPT = findViewById(R.id.downPPT);
        downPPTX = findViewById(R.id.downPPTX);
        downTXT = findViewById(R.id.downTXT);
        downXLS = findViewById(R.id.downXLS);
        downXLSX = findViewById(R.id.downXLSX);

        downImage.setOnClickListener(this);
        downAPK.setOnClickListener(this);
        downDOC.setOnClickListener(this);
        downDOCX.setOnClickListener(this);
        downPDF.setOnClickListener(this);
        downPPT.setOnClickListener(this);
        downPPTX.setOnClickListener(this);
        downTXT.setOnClickListener(this);
        downXLS.setOnClickListener(this);
        downXLSX.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.imageview);
        text = (TextView) findViewById(R.id.text);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        mReceiver = new DownloadReceiver();
        registerReceiver(mReceiver, intentFilter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downImage:
                download(PIC_URL);
                break;
            case R.id.downAPK:
                download(APK_URL);
                break;
            case R.id.downDOC:
                download(DOC_URL);
                break;
            case R.id.downDOCX:
                download(DOCX_URL);
                break;
            case R.id.downPDF:
                download(PDF_URL);
                break;
            case R.id.downPPT:
                download(PPT_URL);
                break;
            case R.id.downPPTX:
                download(PPTX_URL);
                break;
            case R.id.downTXT:
                download(TXT_URL);
                break;
            case R.id.downXLS:
                download(XLS_URL);
                break;
            case R.id.downXLSX:
                download(XLSX_URL);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }
//    public void downImage(View view){
//        download(PIC_URL);
//    }
//    public void downAPK(View view){
//        download(APK_URL);
//    }
//    public void downDOC(View view){
//        download(DOC_URL);
//    }
//    public void downDOCX(View view){
//        download(DOCX_URL);
//    }
//    public void downPDF(View view){
//        download(PDF_URL);
//    }
//    public void downPPT(View view){
//        download(PPT_URL);
//    }
//    public void downPPTX(View view){
//        download(PPTX_URL);
//    }
//    public void downTXT(View view){
//        download(TXT_URL);
//    }
//    public void downXLS(View view){
//        download(XLS_URL);
//    }
//    public void downXLSX(View view){
//        download(XLSX_URL);
//    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void download(String uri) {
        //获取系统服务
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //发送请求的下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        //设置下载时的网络类型WiFi
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //设置下载时状态栏显示通知，可见可视通知
        //在下载过程中通知栏会一直显示该下载的Notification。在下载完毕后该Notification会继续显示。
        // 直到用户点击该Notification或者消除该Notification。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置为可被媒体扫描器找到
        //request.allowScanningByMediaScanner();
        //下载的文件能够被系统的Downloads应用扫描到并管理
        request.setVisibleInDownloadsUi(true);


        //取得系统服务后，调用downloadmanager对象的enqueue方法进行下载，此方法返回一个编号用于标示此下载任务
        //id = mDownloadManager.enqueue(request);
        File destFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "11");
        if (destFile.exists()) {
            destFile.delete();
        }
//        request.setDestinationUri(Uri.parse("file://"+destFile.getAbsolutePath()));
        request.setDestinationInExternalPublicDir("", "sec.jpg");
        id = mDownloadManager.enqueue(request);

        //下载任务和已下载文件同时删除
        //mDownloadManager.remove(id);
        //延时是为了能查到数据
        handler.sendEmptyMessageDelayed(1, 2000);

    }


    /**
     * 查询操作
     */

    private void query() {

        Log.d(TAG, "query: 客户端已执行0");
        //根据任务编号查询下载任务信息。setFilterByStatus(int flags)：根据下载状态查询下载任务
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
        Cursor cursor = mDownloadManager.query(query);

        Log.d(TAG, "query: 客户端已执行1");

        if (cursor != null) {
            Log.d(TAG, "cursor不为空null");
            while (cursor.moveToNext()) {
                Log.d(TAG, "query: 客户端已执行2");
                String bytesDownload = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                String descrition = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                String id = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                String status = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String totalSize = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                Log.d(TAG, "query: " + cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                Log.i("MainActivity", "bytesDownload:" + bytesDownload);
                Log.i("MainActivity", "descrition:" + descrition);
                Log.i("MainActivity", "id:" + id);
                Log.i("MainActivity", "localUri:" + localUri);
                Log.i("MainActivity", "mimeType:" + mimeType);
                Log.i("MainActivity", "title:" + title);
                Log.i("MainActivity", "status:" + status);
                Log.i("MainActivity", "totalSize:" + totalSize);
                text.setText("查询到的数据：bytesDownload:" + bytesDownload + "；descrition:" + descrition + "；id:" + id +
                        "；localUri:" + localUri + "；mimeType:" + mimeType + "；title:" + title + "；status:" + status + "；totalSize:" + totalSize);
            }
            cursor.close();
        }
    }

    private void isCall() {
        //检查是否获得了权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //若没有获得权限，申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
                //  如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
                // 如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                // 弹窗需要解释为何需要该权限，再次请求授权
                Toast.makeText(this, "请授权", Toast.LENGTH_LONG).show();
                //跳转到设置界面，用户手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                //不需要解释为何需要该权限，直接请求授权
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            //已经获得授权，可以打电话
            //callPhone();
        }
    }

    private void callPhone() {
        //通过intent跳转到拨打电话界面
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        //str=et.getText().toString().trim();
        Uri phoneNum = Uri.parse("tel:");
        intent.setData(phoneNum);
        Log.i(TAG, "callPhone: " + phoneNum.toString());
        startActivity(intent);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    Log.i(TAG, "onRequestPermissionsResult: " + "ok");
                    callPhone();
                } else {
                    // 授权失败！
                    Log.i(TAG, "onRequestPermissionsResult: " + "授权失败");
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }


    /**
     * 广播接收器，接受ACTION_DOWNLOAD_COMPLETE和ACTION_NOTICATION_CLICKED
     * 处理用户点击通知栏中的下载界面
     */
    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //是否下载完成
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Log.d(TAG, "下载完成");
                Uri uri = mDownloadManager.getUriForDownloadedFile(id);
                Log.d(TAG, "uri地址: " + uri);

                imageView.setImageURI(uri);

                try {
                    mDownloadManager.openDownloadedFile(id);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                mDownloadManager.getMimeTypeForDownloadedFile(id);

            }

            if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                Log.d(TAG, "点击通知栏");
            }


        }
    }
}



