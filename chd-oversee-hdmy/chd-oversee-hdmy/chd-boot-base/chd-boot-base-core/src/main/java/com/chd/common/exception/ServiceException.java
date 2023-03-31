package com.chd.common.exception;

/**
 * 服务异常,不对外
 */
public class ServiceException extends RuntimeException {

    private Integer code ;

	/**  */
	private static final long serialVersionUID = 1L;

    public ServiceException(String message){
		super(message);
	}
    
    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    public ServiceException(ExceptionCode exCode){
        super(exCode.getMessage());
        this.code=exCode.getCode();
    }
	
	
	public ServiceException(Throwable cause)
	{
		super(cause);
	}
	
	public ServiceException(String message,Throwable cause)
	{
		super(message,cause);
	}

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
