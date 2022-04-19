package con.chin.controller;

import jp.co.yahoo.yconnect.YConnectExplicit;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.oauth2.AuthorizationException;
import jp.co.yahoo.yconnect.core.oauth2.OAuth2ResponseType;
import jp.co.yahoo.yconnect.core.oauth2.TokenException;
import jp.co.yahoo.yconnect.core.oidc.*;
import jp.co.yahoo.yconnect.core.util.YConnectLogger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URI;

@Controller
public class YahooImportController {

    private static final long serialVersionUID = 1L;

    // アプリケーションID, シークレット
    private final static String clientId = "dj00aiZpPWthUW4xbFQ0Q2gwWiZzPWNvbnN1bWVyc2VjcmV0Jng9ZDM-";
    private final static String clientSecret = "0LL0flPVL7yfMbCy95RAiNAGqB1XrGCAJfK7GSX6";

    // コールバックURL
    // (アプリケーションID発行時に登録したURL)
    private static final String redirectUri = "http://localhost:8080/showYahoo";

    @GetMapping("/showYahoo1")
    public String show(){

        return "yahooimport";
    }


    @GetMapping("/showYahoo")
    public String show(HttpServletRequest request, HttpServletResponse response){
// ログ出力レベル設定ファイルが配置されている任意のパスを指定してください
//        YConnectLogger.setFilePath("./yconnect_log_conf.xml");

        response.setContentType("text/html; charset=UTF-8");

        // state, nonceにランダムな値を初期化
        String state = "5Ye65oi744KKT0vjgafjgZnjgojjgIHlhYjovKnjg4M"; // リクエストとコールバック間の検証用のランダムな文字列を指定してください
        String nonce = "SUTljqjjga8uLi7jgrrjg4Plj4vjgaDjgoc"; // リプレイアタック対策のランダムな文字列を指定してください

        // YConnectインスタンス生成
        YConnectExplicit yconnect = new YConnectExplicit();

        // SSL証明書チェック無効 ※Production環境では必ず有効にすること
        // YConnectExplicit.disableSSLCheck();

        try {
            // コールバックURLから各パラメーターを抽出
            String fullUri = request.getRequestURL().toString() + "?" + request.getQueryString();
            URI requestUri = new URI(fullUri);

            if (yconnect.hasAuthorizationCode(requestUri)) {
                /*********************************************************
                 Parse the Callback URI and Save the Access Token.
                 *********************************************************/

                StringBuffer sb = new StringBuffer();

                // 認可コードを取得
                String code = yconnect.getAuthorizationCode(state);

                sb.append("<h1>Authorization Request</h1>");
                sb.append("Authorization Code: " + code + "<br/><br/>");

                /***********************************************
                 Request Access Token adn Refresh Token.
                 ***********************************************/

                // Tokenエンドポイントにリクエスト
                yconnect.requestToken(code, clientId, clientSecret, redirectUri);
                // アクセストークン, リフレッシュトークン, IDトークンを取得
                String accessTokenString = yconnect.getAccessToken();
                long expiration = yconnect.getAccessTokenExpiration();
                String refreshToken = yconnect.getRefreshToken();
                String idTokenString = yconnect.getIdToken();

                sb.append("<h1>Access Token Request</h1>");
                sb.append("Access Token: " + accessTokenString + "<br/><br/>");
                sb.append("Expiration: " + Long.toString(expiration) + "<br/><br/>");
                sb.append("Refresh Token: " + refreshToken + "<br/><br/>");

                /************************
                 Decode ID Token.
                 ************************/

                //IDトークンの検証
                if (yconnect.verifyIdToken(nonce, clientId, clientSecret, idTokenString)){
                    //IDトークンの復号
                    IdTokenObject idTokenObject = yconnect.decodeIdToken(idTokenString);
                    sb.append("<h1>ID Token</h1>");
                    sb.append("ID Token: " + idTokenObject.toString() + "<br/><br/>");
                } else {
                    //検証に失敗したのでエラー文を出力
                    sb.append("<h1>ID Token</h1>");
                    sb.append("ID Token error: " + yconnect.getIdTokenErrorMessage() + "<br/><br/>");
                    sb.append("ID Token error description: " + yconnect.getIdTokenErrorDescriptionMessage() + "<br/><br/>");
                }

                /*************************
                 Request UserInfo.
                 *************************/

                // UserInfoエンドポイントへリクエスト
                yconnect.requestUserInfo(accessTokenString);
                // UserInfo情報を取得
                UserInfoObject userInfoObject = yconnect.getUserInfoObject();
                sb.append("<h1>UserInfo Request</h1>");
                sb.append("UserInfo: <pre>" + userInfoObject + "</pre><br/>");
                sb.append("user_id: <pre>" + userInfoObject.getAdditionalValue("user_id") + "</pre><br/>");
                // JsonObjectから任意のkeyを指定して値を取得する方法
                //String userId = userInfoObject.getJsonObject().getString("user_id");
                /*************************************************
                 Request Access Token Using Refresh Token.
                 *************************************************/

                // Tokenエンドポイントにリクエストしてアクセストークンを更新
                yconnect.refreshToken(refreshToken, clientId, clientSecret);
                accessTokenString = yconnect.getAccessToken();
                expiration = yconnect.getAccessTokenExpiration();

                sb.append("<h1>Refresh Token</h1>");
                sb.append("Access Token: " + accessTokenString + "<br/><br/>");
                sb.append("Expiration: " + Long.toString(expiration) + "<br/><br/>");

                PrintWriter out = response.getWriter();
                out.println(new String(sb));
                out.close();

            } else {

                /****************************************************************
                 Request Authorization Endpoint for getting Access Token.
                 ****************************************************************/

                // 各パラメーター初期化
                String responseType = OAuth2ResponseType.CODE_IDTOKEN;
                String display = OIDCDisplay.DEFAULT;
                String[] prompt = { OIDCPrompt.DEFAULT };
                String[] scope = { OIDCScope.OPENID, OIDCScope.PROFILE,
                        OIDCScope.EMAIL, OIDCScope.ADDRESS };

                // 各パラメーターを設定
                yconnect.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);
                URI uri = yconnect.generateAuthorizationUri();

                // Authorizationエンドポイントにリダイレクト(同意画面を表示)
                response.sendRedirect(uri.toString());
            }

        } catch (ApiClientException ace) {

            // エラーレスポンスが"Invalid_Token"であるかチェック
            if(ace.isInvalidToken()) {

                /*****************************
                 Refresh Access Token.
                 *****************************/

                try {

                    // 保存していたリフレッシュトークンを指定してください
                    String refreshToken = "STORED_REFRESH_TOKEN";

                    // Tokenエンドポイントにリクエストしてアクセストークンを更新
                    yconnect.refreshToken(refreshToken, clientId, clientSecret);
                    String accessTokenString = yconnect.getAccessToken();
                    long expiration = yconnect.getAccessTokenExpiration();

                    StringBuffer sb = new StringBuffer();
                    sb.append("<h1>Refresh Token</h1>");
                    sb.append("Access Token: " + accessTokenString + "<br/><br/>");
                    sb.append("Expiration: " + Long.toString(expiration) + "<br/><br/>");

                    PrintWriter out = response.getWriter();
                    out.println(new String(sb));
                    out.close();

                } catch (TokenException te) {

                    // リフレッシュトークンの有効期限切れチェック
                    if(te.isInvalidGrant()) {
                        // はじめのAuthorizationエンドポイントリクエストからやり直してください
                    }

                    te.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            ace.printStackTrace();
        } catch (AuthorizationException e) {
            e.printStackTrace();
        } catch (TokenException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }




        return "yahooimport";
    }




}
