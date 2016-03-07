package util;

public interface HttpCallbackListener {

	void onError(Exception e);

	void onFinish(String string);

}
