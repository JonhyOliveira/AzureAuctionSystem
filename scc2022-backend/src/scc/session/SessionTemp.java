package scc.session;

public class SessionTemp {
    public static final int VALIDITY_SECONDS = 3600;
    public static final String COOKIE_NAME = "scc:session";
    private String nickname;
    private String cookieId;

    public SessionTemp() {}

    public SessionTemp(String cookieId, String nickname) {
        this.cookieId = cookieId;
        this.nickname = nickname;
    }

    public String getNickname(){
        return nickname;
    }
    
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getCookieId(){
        return cookieId;
    }

    public void setCookieId(String cookieId){
        this.cookieId = cookieId;
    }

}
