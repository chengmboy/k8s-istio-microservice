package io.github.chengmboy.kim.common.exception;

/**
 * 403 授权拒绝
 */
public class KimDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public KimDeniedException() {
    }

    public KimDeniedException(String message) {
        super(message);
    }

    public KimDeniedException(Throwable cause) {
        super(cause);
    }

    public KimDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public KimDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
