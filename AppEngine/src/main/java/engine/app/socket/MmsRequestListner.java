package engine.app.socket;

public interface MmsRequestListner {

    void onResponseObtained(String imagepath);

    void onErrorObtained(String errormsg);
}
