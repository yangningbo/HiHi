package com.gaopai.guiren.net;


/**
 * 处理应用中网络超时或者Token过期的异常
 *
 */
public class DamiException extends Exception {

	private static final long serialVersionUID = 475022994858770424L;
	private int statusCode = -1;
	private int msgId = -1;
	
	
    public DamiException(String msg) {
        super(msg);
    }

    public DamiException(Exception cause) {
        super(cause);
    }

    public DamiException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public DamiException(String msg, Exception cause) {
        super(msg, cause);
    }

    public DamiException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    
	public DamiException() {
		super(); 
	}

	public DamiException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DamiException(Throwable throwable) {
		super(throwable);
	}

	public DamiException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
