package com.chd.common.exception;

/**
 * 业务异常对外
 */
public class BizException extends RuntimeException {

    private Integer code ;
	/**  */
	private static final long serialVersionUID = 1L;

    public BizException(String message){
		super(message);
	}
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ExceptionCode exCode){
        super(exCode.getMessage());
        this.code=exCode.getCode();
    }
	
	public BizException(Throwable cause)
	{
		super(cause);
	}
	
	public BizException(String message,Throwable cause)
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