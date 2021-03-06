package com.rawalinfocom.rcontact.webservice;

import android.app.Activity;
import android.content.ContentValues;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * A Class to invoke Get or Post Request
 */

public class RequestWs {

    /**
     * Get Request
     **/
    public <CLS> CLS getGetRequest(Activity activity, String url, Class<CLS> cls) throws Exception {
//        return new WebServiceRequestGet(url).execute(cls);
        return new WebServiceGet(activity, url).execute(cls);
    }


    /**
     * Post HttpUrlConnection Request
     **/
    public <CLS> CLS getPostRequest(Activity activity, String url, int requestType, Object reqCls,
                                    Class<CLS> cls, ContentValues contentValues, boolean
                                            setHeader) throws Exception {
        return new WebServicePost(activity, url, requestType, setHeader).execute(cls, reqCls,
                contentValues);
    }

}
