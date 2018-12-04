package com.example.hiroki.myapplication;
/**
 * Created by T.Higurashi on 2017/11/10.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;

import java.io.Serializable;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ImageAnalyzeIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    //public static final String ACTION_CREATE_REQUEST = "kensyu.nsco.miehiyo.action.create_request";
    //Todo：以下は何を示しているのかを調べる。
    public static final String ACTION_JSON = "kensyu.nsco.miehiyo.action.json";
    public static final String ACTION_REST = "kensyu.nsco.miehiyo.action.rest";
    public static final String ACTION_PARSE = "kensyu.nsco.miehiyo.action.parse";
    public static final String ACTION_RESULT = "kensyu.nsco.miehiyo.action.result";
    public static final String ACTION_RESULTPARSE = "kensyu.nsco.miehiyo.action.parse";
    public static final String ACTION_RESULTJSON = "kensyu.nsco.miehiyo.action.resultjson";
    public static final String ACTION_RESULTHTTP = "kensyu.nsco.miehiyo.action.resulthttp";
    public static final String ACTION_RESULTSHOW = "kensyu.nsco.miehiyo.action.resultshow";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "kensyu.nsco.miehiyo.extra.PARAM1";
    //private static final feelingResult EXTRA_PARAM2 = "kensyu.nsco.miehiyo.extra.PARAM2";


    AnnotateImageRequest jsonData;
    String Http_result;
    BatchAnnotateImagesResponse parseData;
    String[] Sresult;//宮西さんから受け取る用の配列
    double[] dresult;//日暮さんへ送るための変換後の配列
    //画像の変換からリクエスト作成までを行おうとしている関係で、
    //ストレージにアクセスするためにアプリのコンテキストが必要
    Context context = this.getApplicationContext();

    public ImageAnalyzeIntentService() {
        // ActivityのstartService(intent);で呼び出されるコンストラクタはこちら
        //引数なしは必ず用意する。
        super("ImageAnalyzeIntentService");
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionJson(Context c, String uri/*, String param2*/) {
        Intent intent = new Intent(c, ImageAnalyzeIntentService.class);
        intent.setAction(ACTION_JSON);
        intent.putExtra(EXTRA_PARAM1, uri);
        //intent.putExtra(EXTRA_PARAM2, param2);
        c.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRest(Context c /*,String param1, String param2*/) {
        Intent intent = new Intent(c, ImageAnalyzeIntentService.class);
        intent.setAction(ACTION_REST);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        c.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionParse(Context c/*, String param1, String param2*/) {
        Intent intent = new Intent(c, ImageAnalyzeIntentService.class);
        intent.setAction(ACTION_PARSE);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        c.startService(intent);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionResult(Context c/*, String param1, String param2*/) {
        Intent intent = new Intent(c, ImageAnalyzeIntentService.class);
        intent.setAction(ACTION_RESULT);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        c.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //IntentServiceで送られてきたActionをString型に変換する
            final String action = intent.getAction();
            //画像をJsonへパースする場合(case①)
            if (ACTION_JSON.equals(action)) {
                //putExtraでURIが来るからそれをString型で取る
                final String uri = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                case_one(uri/*, String param2*/);
            }
            //JSON用意出来たら送信処理を行わせる(case②)
            else if (ACTION_REST.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                case_two(/*param1, param2*/);
            }
            //返ってきた結果をパース(case③)
            else if (ACTION_PARSE.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                case_three(/*param1, param2*/);
            }
            //実行結果を結果表示用に整理(case④)

            else if (ACTION_RESULT.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                case_four(/*param1, param2*/);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void case_one(String param1/*, String param2*/) {
        // 送られてきたURIをString型からURI型にする
        Uri uri = Uri.parse(param1);

        //URIを宮西さんのところへ送る(返ってくるのはAnnotateImageRequest型)
        jsonData = JsonDataProcessor.createRequest(uri,context);

        Intent intent = new Intent(context, ProgressActivity.class);
        intent.setAction(ACTION_RESULTJSON);
        //intent.putExtra(EXTRA_PARAM2, param2);
        sendBroadcast(intent);

        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    //Todo:江見さんの処理終了の検知方法
    private void case_two(/*String param1, String param2*/) {
        //case①でJsonデータが来たらそれをRestHttpに送る
        Http_result = HttpRest.callCloudVision(jsonData);

        //MainActivityに処理完了をBroadcastで教える
        Intent intent = new Intent(context, ProgressActivity.class);
        intent.setAction(ACTION_RESULTHTTP);
        //intent.putExtra(EXTRA_PARAM2, param2);
        sendBroadcast(intent);

        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void case_three(/*String param1, String param2*/) {
        //case②の江見さんからの戻り値をそのまま渡す
        Sresult = JsonDataProcessor.parseResponseToString(parseData);

        Intent intent = new Intent(context, ProgressActivity.class);
        intent.setAction(ACTION_RESULTPARSE);
        //intent.putExtra(EXTRA_PARAM2, param2);
        sendBroadcast(intent);
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    //Todo:整理方法を知り、反映(String型で来るのを表示しやすいようにString型とdouble型の構造体にする)
    //感情は4つ：Joy・Sorrow・Anger・Surprise
    //Todo：どういうデータで来るのかを知る。
    private void case_four(/*String param1, String param2*/) {
        dresult = new double[4];
        for( int i=0 ; i < 4 ; i++){
            dresult[i] = Double.parseDouble(Sresult[i]);
        }

        Intent intent = new Intent(context, ResultActivity.class);
        intent.setAction(ACTION_RESULTSHOW);
        intent.putExtra("feeling_Result", dresult);
        //intent.putExtra(EXTRA_PARAM2, param2);
        sendBroadcast(intent);

        //throw new UnsupportedOperationException("Not yet implemented");
    }

    //Todo:各処理終了後にMainActivityに処理終了を通知する方法
}

