package scc.data;

public class Login {
    private String nickname;
    private String password;

    public Login() {}

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getPwd(){
        return password;
    }

    public void setPwd(String password){
        this.password = password;
    }
}
