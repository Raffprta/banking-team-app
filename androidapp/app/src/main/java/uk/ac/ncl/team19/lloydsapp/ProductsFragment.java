package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @Author Raffaello Perrotta
 *
 * A Web View showing the official LLoyds Product section, in which the further products
 * offered by the banking company can be viewed by the user.
 */
public class ProductsFragment extends Fragment{

    private final String LLOYDS_URL = "http://www.lloydsbank.com/M/default.asp";
    private ProgressDialog loading = new ProgressDialog();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View  webView = inflater.inflate(R.layout.products_layout, container, false);
        // Set the view to the webview XML
        WebView productsPage = (WebView) webView.findViewById(R.id.webView);
        // Load the Lloyds official product page and show loading animation

        // This loads the web view.
        productsPage.loadUrl(LLOYDS_URL);

        // Set the web client, i.e. all hyperlinks will continue to open within the view.
        productsPage.setWebViewClient(new WebViewClient() {
            // Handles URL opening to default browser client
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        // Listen for when the back button is pressed by the user.
        productsPage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;
                    // Force the view to go back in the web history.
                    if(keyCode == KeyEvent.KEYCODE_BACK)
                        if (webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                    }

                return false;
            }
        });

        return webView;

    }



}
