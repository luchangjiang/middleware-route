package com.lotstock.eddid.route.util;

import com.alibaba.fastjson.JSONObject;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import org.apache.log4j.Logger;

import java.net.URI;

public class OIDCUtil {

    protected static final Logger logger = Logger.getLogger(OIDCUtil.class);

    public static JSONObject getUserInfo(String urlString, String tokenString) {
        try {
            URI userInfoEndpoint = new URI(urlString);    // The UserInfoEndpoint of the OpenID provider
            BearerAccessToken accessToken = new BearerAccessToken(tokenString); // The access token
            // Get OIDC user info
            HTTPResponse httpResponse = new UserInfoRequest(userInfoEndpoint, accessToken).toHTTPRequest().send();
            UserInfoResponse userinfoResponse = UserInfoResponse.parse(httpResponse);
            //
            if (!userinfoResponse.indicatesSuccess()) {
                UserInfoErrorResponse error = (UserInfoErrorResponse) userinfoResponse;
                logger.info("FAILED error:  url is (" + urlString + ") , and token is (" + tokenString + ")");
                logger.error("FAILED TO GET USER INFO : " + error.getErrorObject());
            } else {
                /**
                 * IF SUCCESS
                 */
                UserInfoSuccessResponse userinfoSuccessResponse = (UserInfoSuccessResponse) userinfoResponse;
                String userInfoString = userinfoSuccessResponse.getUserInfo().toJSONObject().toString();
                JSONObject userInfo = JSONObject.parseObject(userInfoString);
                return userInfo;
            }
        } catch (Exception e) {
            logger.info("token parse url is (" + urlString + ") , and token is (" + tokenString + ")");
            logger.error("access token parse error : " + e.toString());
        }
        return null;
    }


}
