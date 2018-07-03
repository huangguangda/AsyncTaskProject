package cn.edu.gdmec.android.asynctaskproject;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 1. 网络上请求数据: 申请网络权限 读写存储权限
 * 2. 布局我们的layout
 * 3. 下载之前我们要做什么?  UI
 * 4. 下载中我们要做什么?   数据
 * 5. 下载后我们要做什么?  UI
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String APK_URL = "http://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";
    public static final int INIT_PROGRESS = 0;
    public static final String FILE_NAME = "huang.apk";

    private Button mDownloadButton;
    private ProgressBar mProgressBar;
    private TextView mResultTextView;
    //private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化视图
        initView();
        // 设置点击监听
        setListener();

        // 初始化UI数据
        setData();
        //new DownloadAsyncTask().execute("huang","good");
        /*DownloadHelper.download(APK_URL, "", new DownloadHelper.OnDownloadListener.SimpleDownloadListener() {
            @Override
            public void onSuccess(int code, File file) {

            }

            @Override
            public void onFail(int code, File file, String message) {

            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onProgress(int progress) {
                super.onProgress(progress);
            }
        });*/

    }

    private void setData() {

        //mResultTextView.setText(R.string.download_text);
        //mDownloadButton.setText(R.string.click_download);

        mResultTextView.setText(R.string.download_text);
        mDownloadButton.setText(R.string.click_download);
        mProgressBar.setProgress(INIT_PROGRESS);

    }

    private void setListener() {

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 16/12/19 下载任务
                DownloadAsyncTask asyncTask = new DownloadAsyncTask();
                asyncTask.execute(APK_URL);
            }
        });

    }
    /**
     * 初始化视图
     */
    private void initView() {
        mResultTextView = findViewById(R.id.textView);
        mDownloadButton = findViewById(R.id.button);
        mProgressBar = findViewById(R.id.progressBar);
    }

    /**
     * String 入参
     * Integer 进度
     * Boolean 返回值
     */

    public class DownloadAsyncTask extends AsyncTask<String,Integer,Boolean>{

        private String mFilePath;
        //在异步任务之前，在主线程中

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //可操作UI 类似淘米 准备工作
            mDownloadButton.setText(R.string.downloading);
            mResultTextView.setText(R.string.downloading);
            mProgressBar.setProgress(INIT_PROGRESS);
        }


        //在另外一个线程中处理事件
        //String... strings 数组表示可变参数
        //可能为空，防御式编程 封装给别人用的
        // 煮米
        @Override
        protected Boolean doInBackground(String... strings) {
            if (strings != null && strings.length>0){
                String apkurl = strings[0];


                try {
                    //首先构造一个url
                    URL url = new URL(apkurl);
                    //构造一个请求 打开连接 构造连接，并打开
                    URLConnection urlConnection = url.openConnection();

                    //输入流
                    InputStream inputStream = urlConnection.getInputStream();
                    //内容的长度 获取了下载内容的总长度
                    //进度条在走动
                    int contentLength = urlConnection.getContentLength();

                    //下载地址准备
                    mFilePath = Environment.getExternalStorageDirectory()
                            + File.separator + FILE_NAME;

                    //对下载地址进行处理

                    File apkFile = new File(mFilePath);
                    if (apkFile.exists()){
                        boolean result = apkFile.delete();
                        if (!result){
                            return  false;
                        }
                    }
                    //已下载的大小
                    int downloadSize=0;

                    //byte数组
                    byte[] bytes = new byte[1024];
                    int length;
                    //创建一个输出管道
                    OutputStream outputStream = new FileOutputStream(mFilePath);

                    //不断的一车一车挖土，走到挖不到为止
                    while ((length = inputStream.read(bytes)) != -1){
                        //挖到的放到我们的文件管道里
                        outputStream.write(bytes,0,length);
                        //累加我们的大小
                        downloadSize+=length;
                        //发送进度
                        publishProgress(downloadSize * 100/contentLength);
                    }
                    //不关掉，可能造成内存溢出
                    inputStream.close();
                    outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }


            }else {
                return false;
            }
            /*for (int i = 0; i < 10000; i++) {
                Log.i(TAG,"doInBackground:"+strings[0]);
                //抛出进度
                publishProgress(i);
            }

            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //进行下载

            return true;
        }

        //执行完，做完了到主线程
        //煮饭好了，通知我可以吃了
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //也是在主线程中，执行结果 处理
            mDownloadButton.setText(aBoolean? getString(R.string.download_finish) : getString(R.string.download_fail));
            mResultTextView.setText(aBoolean? getString(R.string.download_finish) + mFilePath : getString(R.string.download_fail));
        }


        //当我们的进度在变化的时候
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //收到进度，然后处理：也是在UI线程中。
            if (values != null && values.length >0){
                mProgressBar.setProgress(values[0]);
            }

        }


        //取消
        /*@Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }*/

//在执行前onPreExcecute,
// 执行中doInBackground,
// 执行后onPostExecute,
// 处理进度onProgressUpdate



    }
}
