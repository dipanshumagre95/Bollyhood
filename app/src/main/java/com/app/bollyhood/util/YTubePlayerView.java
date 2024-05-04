package com.app.bollyhood.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.bollyhood.R;

import java.util.ArrayList;
import java.util.Iterator;

public class YTubePlayerView extends WebView {
    /* access modifiers changed from: private */
    public Activity activity;
    private ArrayList<String> classes = new ArrayList<>();
    /* access modifiers changed from: private */
    public Handler handler = new Handler();
    private WebView webView;

    public YTubePlayerView(Context context) {
        super(context);
        initView(context);
    }

    public YTubePlayerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.webView = new WebView(context.getApplicationContext());
        initView(context);
    }

    public void setInstanseOfActivity(Activity activity2) {
        this.activity = activity2;
        MyStorage.getInstance().storage.put("myActivity", this.activity);
    }


    private void initView(Context context) {
        initialList();
        hideSomeSectionOfBlog(this);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);
        setWebChromeClient(new MyChrome());
        //     getSettings().setUserAgentString("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0");
        getSettings().setDisplayZoomControls(true);
        getSettings().setAllowContentAccess(false);

     /*   ProgressDialog loading = new ProgressDialog(context);
        loading.setCancelable(true);
        loading.setMessage("Loading...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();
*/
        setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                return true;
            }

            public void onPageCommitVisible(WebView webView, String str) {
                super.onPageCommitVisible(webView, str);
            }

            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                super.onPageStarted(webView, str, bitmap);
            }

            public void onPageFinished(WebView webView, String str) {
                YTubePlayerView.this.hideSomeSectionOfBlog(webView);
                YTubePlayerView.this.scheduleHideContent(webView);
              /*  if (loading!=null && loading.isShowing()){
                    loading.dismiss();
                }*/
            }

            public void onReceivedError(WebView webView, int i, String str, String str2) {
                webView.getSettings();
                webView.loadData("<h1 style = 'text-align: center' >Something went wrong, try again.</h1>", "text/html", "UTF-8");
            }

            public boolean onRenderProcessGone(WebView webView, RenderProcessGoneDetail renderProcessGoneDetail) {
                return super.onRenderProcessGone(webView, renderProcessGoneDetail);
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {

                Toast.makeText(activity, "ffff", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initialList() {
        this.classes.add("ytp-chrome-top-buttons");
        this.classes.add("ytp-title");
        this.classes.add("ytp-youtube-button ytp-button yt-uix-sessionlink");
        this.classes.add("ytp-button ytp-endscreen-next");
        this.classes.add("ytp-button ytp-endscreen-previous");
        this.classes.add("ytp-show-cards-title");
        this.classes.add("ytp-endscreen-content");
        this.classes.add("ytp-chrome-top");
        this.classes.add("ytp-share-button");
        this.classes.add("ytp-watch-later-button");
        this.classes.add("ytp-pause-overlay");

    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        try {
            super.onConfigurationChanged(configuration);
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                hideFullScreen();
            } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                goFullScreenVideo();
            }
        } catch (Exception e) {
            Log.e("onConfig error", e.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        if (getLayoutParams().height == -2) {
            if (Build.VERSION.SDK_INT > 21) {
                i3 = MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(i) * 9) / 24,  MeasureSpec.EXACTLY);
            } else {
                i3 = MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(i) * 9) / 16, MeasureSpec.EXACTLY);
            }
            super.onMeasure(i, i3);
            return;
        }
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: private */
    public void hideSomeSectionOfBlog(final WebView webView2) {
        try {
            Iterator<String> it = this.classes.iterator();
            while (it.hasNext()) {
                String next = it.next();
                hideElementByClassName(webView2, next);
                removeElementByClassName(webView2, next);
            }
            hideContextMenu(webView2);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    webView2.loadUrl("javascript:(function() { document.getElementsByClassName('ytp-button ytp-settings-button')[0].style.display='inline'; })()");
                    webView2.loadUrl("javascript:(function() { document.getElementsByClassName('ytp-fullscreen-button ytp-button')[0].style.display='inline'; })()");
                    webView2.loadUrl("javascript:(function() { document.getElementsByClassName('branding-img iv-click-target')[0].style.display='none'; })()");
                }
            }, 2000);
        } catch (Exception e) {
            Log.e("error player", e.toString());
        }
    }

    private void hideElementByClassName(WebView webView2, String str) {
        webView2.loadUrl("javascript:(function() { document.getElementsByClassName('" + str + "')[0].style.display='none'; })()");
    }

    private void removeElementByClassName(WebView webView2, String str) {
        webView2.loadUrl("javascript:(function() {  var elements = document.getElementsByClassName('" + str + "');    while(elements.length > 0){        elements[0].parentNode.removeChild(elements[0]);    } })()");
    }

    /* access modifiers changed from: private */
    public void scheduleHideContent(final WebView webView2) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                YTubePlayerView.this.hideSomeSectionOfBlog(webView2);
                YTubePlayerView.this.handler.postDelayed(this,3000L);
            }
        }, 3000L);
    }

    /* access modifiers changed from: private */
    public void goFullScreenVideo() {
        //Timber.m91e("Landscape", new Object[0]);
        Activity activity2 = (Activity) MyStorage.getInstance().storage.get("myActivity");
        this.activity = activity2;
        activity2.getWindow().addFlags(1024);
        hideSomeSectionOfBlog(this.webView);
    }

    /* access modifiers changed from: private */
    public void hideFullScreen() {
        // Timber.m91e("Portait", new Object[0]);
        Activity activity2 = (Activity) MyStorage.getInstance().storage.get("myActivity");
        this.activity = activity2;
        activity2.getWindow().clearFlags(1024);
        hideSomeSectionOfBlog(this.webView);
        setHeightOfVideo();
    }

    private void setHeightOfVideo() {
        try {
       /*     DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            int buttonWidth = width/2;*/
            setLayoutParams(new LinearLayout.LayoutParams(-1, getResources().getDimensionPixelSize(R.dimen.potrairtsize2)));

        } catch (Exception e) {
            Log.e("error portrait", e.toString());
        }
    }

    private void hideContextMenu(WebView webView2) {
        webView2.loadUrl("javascript:(function() { var css = document.createElement('style');  css.type = 'text/css'; var styles = '.ytp-contextmenu { width: 0px !important}';if (css.styleSheet) css.styleSheet.cssText = styles; else css.appendChild(document.createTextNode(styles));document.getElementsByTagName('head')[0].appendChild(css); })()");
    }


    public void seekToMillis(double mil) {
        Toast.makeText(activity, ">> COUNT", Toast.LENGTH_SHORT).show();
        this.loadUrl("javascript:onSeekTo(" + mil + ")");
    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (this.mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(YTubePlayerView.this.activity.getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) YTubePlayerView.this.activity.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            YTubePlayerView.this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            this.mCustomViewCallback = null;
            YTubePlayerView.this.hideFullScreen();
        }

        public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
            Activity unused = YTubePlayerView.this.activity = (Activity) MyStorage.getInstance().storage.get("myActivity");
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = view;
            view.setLongClickable(true);
            view.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    return true;
                }
            });
            FrameLayout frameLayout = (FrameLayout) view;
            int childCount = frameLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = frameLayout.getChildAt(i);
                if (childAt != null) {
                    childAt.setLongClickable(true);
                    childAt.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View view) {
                            return true;
                        }
                    });
                }
            }
            this.mOriginalSystemUiVisibility = YTubePlayerView.this.activity.getWindow().getDecorView().getSystemUiVisibility();
            this.mCustomViewCallback = customViewCallback;
            ((FrameLayout) YTubePlayerView.this.activity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            YTubePlayerView.this.activity.getWindow().getDecorView().setSystemUiVisibility(3846);
            YTubePlayerView.this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            YTubePlayerView.this.goFullScreenVideo();
        }
    }
}

