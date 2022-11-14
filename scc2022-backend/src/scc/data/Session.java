public class Session {
    private String nickname;
    private String cookieId;

    public Session() {}

    public Session( String cookieId, String nickname) {
        this.nickname = nickname;
        this.cookieId = cookieId;
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
