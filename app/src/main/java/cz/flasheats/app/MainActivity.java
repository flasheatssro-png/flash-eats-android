package cz.flasheats.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private static final String HOME = "https://flash-eats-production.vercel.app/?source=android-apk";
    private static final String HOST = "flash-eats-production.vercel.app";
    private static final int FILE_CHOOSER = 1001;
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xFF05070B);
        getWindow().setNavigationBarColor(0xFF05070B);
        webView = new WebView(this);
        webView.setBackgroundColor(0xFF05070B);
        setContentView(webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true); s.setAllowContentAccess(true); s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false); s.setBuiltInZoomControls(false); s.setDisplayZoomControls(false);
        s.setUserAgentString(s.getUserAgentString() + " FlashEatsAndroid/1.0");
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri=request.getUrl(); String scheme=uri.getScheme(); String host=uri.getHost();
                if (("https".equalsIgnoreCase(scheme)||"http".equalsIgnoreCase(scheme)) && HOST.equalsIgnoreCase(host)) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch(Exception e) { Toast.makeText(MainActivity.this,"Cannot open link",Toast.LENGTH_SHORT).show(); }
                return true;
            }
            @Override public void onPageStarted(WebView view, String url, Bitmap favicon) { super.onPageStarted(view,url,favicon); }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if(fileCallback!=null) fileCallback.onReceiveValue(null); fileCallback=callback;
                Intent intent; try { intent=params.createIntent(); } catch(Exception e) { intent=new Intent(Intent.ACTION_OPEN_DOCUMENT); intent.setType("*/*"); intent.addCategory(Intent.CATEGORY_OPENABLE); }
                try { startActivityForResult(intent,FILE_CHOOSER); return true; } catch(ActivityNotFoundException e) { fileCallback=null; return false; }
            }
        });
        String initial=HOME; Intent in=getIntent(); if(in!=null&&in.getData()!=null&&HOST.equalsIgnoreCase(in.getData().getHost())) initial=in.getData().toString();
        if(savedInstanceState==null) webView.loadUrl(initial); else webView.restoreState(savedInstanceState);
    }
    @Override protected void onNewIntent(Intent intent){ super.onNewIntent(intent); setIntent(intent); Uri uri=intent.getData(); if(uri!=null&&HOST.equalsIgnoreCase(uri.getHost())&&webView!=null) webView.loadUrl(uri.toString()); }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){ super.onActivityResult(requestCode,resultCode,data); if(requestCode==FILE_CHOOSER&&fileCallback!=null){ fileCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,data)); fileCallback=null; } }
    @Override public void onBackPressed(){ if(webView!=null&&webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
    @Override protected void onSaveInstanceState(Bundle outState){ if(webView!=null) webView.saveState(outState); super.onSaveInstanceState(outState); }
    @Override protected void onDestroy(){ if(webView!=null){ webView.loadUrl("about:blank"); webView.stopLoading(); webView.destroy(); webView=null; } super.onDestroy(); }
}
